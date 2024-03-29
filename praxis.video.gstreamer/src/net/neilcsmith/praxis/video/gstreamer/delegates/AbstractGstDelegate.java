/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 Neil C Smith.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 * 
 * You should have received a copy of the GNU General Public License version 3
 * along with this work; if not, see http://www.gnu.org/licenses/
 * 
 * 
 * Please visit http://neilcsmith.net if you need additional information or
 * have any questions.
 */
package net.neilcsmith.praxis.video.gstreamer.delegates;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.video.gstreamer.components.VideoDelegate;
import net.neilcsmith.praxis.video.render.NativePixelData;
import net.neilcsmith.praxis.video.render.PixelData;
import net.neilcsmith.praxis.video.render.Surface;
import net.neilcsmith.praxis.video.render.SurfaceOp;
import net.neilcsmith.praxis.video.render.ops.ScaledBlit;
import net.neilcsmith.praxis.video.utils.ResizeMode;
import net.neilcsmith.praxis.video.utils.ResizeUtils;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.BufferDataAppSink;

/**
 *
 * @author Neil C Smith
 */
public abstract class AbstractGstDelegate extends VideoDelegate {

    private static Logger logger = Logger.getLogger(AbstractGstDelegate.class.getName());
    private final AtomicReference<State> state;
    private GStreamerSurface surface;
    private final Lock surfaceLock;
    private Pipeline pipe;
    private Rectangle srcRegion;
    private int srcWidth;
    private int srcHeight;
    private Rectangle destRegion;
    private int destWidth;
    private int destHeight;
    private volatile boolean waitOnFrame;
    private volatile boolean newFrameAvailable = true;
    private volatile boolean looping;

    protected AbstractGstDelegate() {
        Gst.init();
        state = new AtomicReference<VideoDelegate.State>(State.New);
        surfaceLock = new ReentrantLock();
    }

    @Override
    public State initialize() throws StateException {
        // this is where we call down to build pipeline
        if (state.compareAndSet(State.New, State.Ready)) {
            try {
                BufferDataAppSink sink = new BufferDataAppSink("sink", new BufferListener());
                sink.setAutoDisposeBuffer(false);
                pipe = buildPipeline(sink);
                makeBusConnections(pipe.getBus());
                pipe.setState(org.gstreamer.State.READY); // in gst thread?
                return State.Ready;
            } catch (Exception ex) {
                error("Error building pipeline", ex);
                return State.Error;
            }
        } else {
            throw new StateException();
        }
    }

    @Override
    public final void play() throws StateException {
        State s;
        do {
            s = state.get();
            if (s == State.Playing) {
                return;
            }
            if (s != State.Paused && s != State.Ready) {
                throw new StateException("Illegal call to play when state is " + s);
            }
        } while (!state.compareAndSet(s, State.Playing));
        try {
            doPlay();
        } catch (Exception ex) {
            error("Error while attempting Play", ex);
        }
    }

    protected void doPlay() throws Exception {
        Gst.getExecutor().execute(new Runnable() {

            public void run() {
                pipe.play();
            }
        });
    }

    @Override
    public final void pause() throws StateException {
        State s;
        do {
            s = state.get();
            if (s == State.Paused) {
                return;
            }
            if (s != State.Playing && s != State.Ready) {
                throw new StateException("Illegal call to pause when state is " + s);
            }
        } while (!state.compareAndSet(s, State.Paused));
        try {
            doPause();
        } catch (Exception ex) {
            error("Error while attempting Pause", ex);
        }
    }

    protected void doPause() throws Exception {
        Gst.getExecutor().execute(new Runnable() {

            public void run() {
                pipe.pause();

            }
        });
    }

    @Override
    public final void stop() throws StateException {
        State s;
        do {
            s = state.get();
            if (s == State.Ready) {
                return;
            }
            if (s != State.Playing && s != State.Paused) {
                throw new StateException("Illegal call to stop when state is " + s);
            }
        } while (!state.compareAndSet(s, State.Ready));
        try {
            doStop();
        } catch (Exception ex) {
            error("Error while attempting Stop", ex);
        }
    }

    protected void doStop() throws Exception {
        Gst.getExecutor().execute(new Runnable() {

            public void run() {
                pipe.stop();
            }
        });
    }

    @Override
    public final void dispose() {
        State s;
        do {
            s = state.get();
            if (s == State.Disposed) {
                return;
            }
        } while (!state.compareAndSet(s, State.Disposed));
        doDispose();
    }

    protected void doDispose() {
        pipe.setState(org.gstreamer.State.NULL);
        pipe.dispose();
//        Gst.deinit();

    }

    protected void error(String message, Exception ex) {
        State s;
        do {
            s = state.get();
            if (s == State.Disposed) {
                break;
            }
        } while (!state.compareAndSet(s, State.Error));
        logger.log(Level.WARNING, message);
        if (ex != null && logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Detailed error log :", ex);
        }
    }

    @Override
    public State getState() {
        return state.get();
    }

    @Override
    public boolean canWaitOnFrame() {
        return true;
    }

    @Override
    public void setWaitOnFrame(boolean wait) {
        waitOnFrame = wait;
    }

    @Override
    public boolean getWaitOnFrame() {
        return waitOnFrame;
    }

    @Override
    public void setLooping(boolean loop) {
        if (loop) {
            if (isLoopable()) {
                looping = true;
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public boolean isLooping() {
        return looping;
    }

    @Override
    public long getDuration() {
        return pipe.queryDuration(TimeUnit.NANOSECONDS);
    }

    @Override
    public long getPosition() {
        return pipe.queryPosition(TimeUnit.NANOSECONDS);
    }

    public void process( Surface output) {
        State s = state.get();
        if (s == State.Playing || s == State.Paused) {
            drawVideo(output);
        }

    }

    private void drawVideo(Surface output) {
        if (waitOnFrame) {
            while (state.get() == State.Playing && !newFrameAvailable) {
                Thread.yield();
            }
        }

        surfaceLock.lock();

        try {
            if (surface != null && surface.buffer != null) {
                checkRegions(output);
//                output.process(new GraphicsOp(new GraphicsOp.Callback() {
//                    public void draw(Graphics2D g2d, Image[] images) {
//                        
//                        int dx1 = destRegion.x;
//                        int dy1 = destRegion.y;
//                        int dx2 = dx1 + destRegion.width;
//                        int dy2 = dy1 + destRegion.height;
//                        int sx1 = srcRegion.x;
//                        int sy1 = srcRegion.y;
//                        int sx2 = sx1 + srcRegion.width;
//                        int sy2 = sy1 + srcRegion.height;
//                        g2d.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
//                    }
//                }));
                output.process(new ScaledBlit().setSourceRegion(srcRegion).setDestinationRegion(destRegion),
                        surface);
                newFrameAvailable = false;
            }

        } finally {
            surfaceLock.unlock();
        }
    }

    private void checkRegions(Surface output) {
        if (srcRegion == null || destRegion == null
                || srcWidth != surface.getWidth() || srcHeight != surface.getHeight()
                || destWidth != output.getWidth() || destHeight != output.getHeight()) {
            Rectangle src = new Rectangle();
            Rectangle dest = new Rectangle();
            Dimension srcDim = new Dimension(surface.getWidth(), surface.getHeight());
            Dimension destDim = new Dimension(output.getWidth(), output.getHeight());
            ResizeUtils.calculateBounds(srcDim, destDim, getResizeMode(), src, dest);
            srcRegion = src;
            destRegion = dest;
        }

    }

    @Override
    public void setResizeMode(ResizeMode mode) {
        super.setResizeMode(mode);
        srcRegion = null;
        destRegion = null;
    }

//    private BufferedImageSurface getImage(int width, int height) {
//        if (image != null && image.getWidth() == width && image.getHeight() == height) {
//            return image;
//        }
//
//        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
////        image.setAccelerationPriority(0.0f);
//        return image;
//    }

    private void makeBusConnections(Bus bus) {
        bus.connect(new Bus.ERROR() {

            public void errorMessage(GstObject arg0, int arg1, String arg2) {
                error(arg0 + " : " + arg2, null);
            }
        });
        bus.connect(new Bus.EOS() {

            public void endOfStream(GstObject arg0) {
                try {
                    if (isLooping()) {
                        pipe.seek(0, TimeUnit.NANOSECONDS);
                    } else {
                        stop();
                    }
                } catch (Exception ex) {
                    error("", ex);
                }
            }
        });
    }
    
    protected abstract Pipeline buildPipeline(Element sink) throws Exception;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }
//
//    private class RGBListener implements RGBDataSink.Listener {
//
//        public void rgbFrame(boolean preroll, int width, int height, IntBuffer rgb) {
//
//            if (!surfaceLock.tryLock()) {
//                return;
//            }
//
//            try {
//                image = getImage(width, height);
//                int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
//                rgb.get(pixels, 0, width * height);
//                newFrameAvailable = true;
//            } finally {
//                surfaceLock.unlock();
//            }
//
//
//        }
//    }
    
    private class BufferListener implements BufferDataAppSink.Listener {

        public void bufferFrame(int width, int height, Buffer buffer) {
            surfaceLock.lock();
            try {
                if (surface == null || surface.getWidth() != width || surface.getHeight() != height) {
                    if (surface != null && surface.buffer != null) {
                        surface.buffer.dispose();
                    }
                    surface = new GStreamerSurface(width, height);
                } else {
                    if (surface.buffer != null) {
                        surface.buffer.dispose();
                    }
                }
                surface.buffer = buffer;
                surface.modCount++;
            } finally {
                surfaceLock.unlock();
            }
        }
        
    }
    
    private static class GStreamerSurface extends Surface implements NativePixelData {
        
        private static PixelData[] EMPTY = new PixelData[0];
        
        private Buffer buffer;
        private int[] data;
        private int modCount;
        
        private GStreamerSurface(int width, int height) {
            super(width, height, false);
        }

        @Override
        public int getModCount() {
            return modCount;
        }

        @Override
        public void process(SurfaceOp op, Surface... inputs) {
            if (inputs.length > 0) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            modCount++;
            op.process(this, EMPTY);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isClear() {
            return false;
        }

        @Override
        public void release() {
            // no op
        }

        @Override
        public void copy(Surface source) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean checkCompatible(Surface surface, boolean checkDimensions, boolean checkAlpha) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Surface createSurface(int width, int height, boolean alpha) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int[] getData() {
            if (data == null) {
                data = new int[width * height];
            }
            IntBuffer ib = buffer.getByteBuffer().asIntBuffer();
            ib.rewind();
            ib.get(data);
            ib.rewind();
            return data;
        }

        public int getOffset() {
            return 0;
        }

        public int getScanline() {
            return width;
        }

        public ByteBuffer getNativeData() {
            ByteBuffer bb = buffer.getByteBuffer();
            bb.rewind();
            return bb;
        }

        public Format getFormat() {
            return Format.INT_RGB;
        }
        
    }

    
}
