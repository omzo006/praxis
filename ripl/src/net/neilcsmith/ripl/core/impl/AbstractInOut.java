/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 - Neil C Smith. All rights reserved.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details.
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please visit http://neilcsmith.net if you need additional information or
 * have any questions.
 */
package net.neilcsmith.ripl.core.impl;

import java.util.ArrayList;
import java.util.List;
import net.neilcsmith.ripl.core.Sink;
import net.neilcsmith.ripl.core.SinkIsFullException;
import net.neilcsmith.ripl.core.Source;
import net.neilcsmith.ripl.core.SourceIsFullException;
import net.neilcsmith.ripl.core.Surface;

/**
 *
 * @author Neil C Smith
 */
public abstract class AbstractInOut extends AbstractSource implements Sink {

    private int maxSources;
    List<Source> sources;
    private long time = 0;
    private long renderReqTime;
    private boolean renderReqCache;
    private boolean clearSurface;

    public AbstractInOut(int maxSources, int maxSinks) {
        this(maxSources, maxSinks, true);
    }
    
    public AbstractInOut(int maxSources, int maxSinks, boolean clearSurface) {
        super(maxSinks);
        if (maxSources < 1) {
            throw new IllegalArgumentException();
        }
        this.maxSources = maxSources;
        this.sources = new ArrayList<Source>(maxSources);
        this.clearSurface = clearSurface;
    }

    public void process(Surface surface, Sink sink, long time) {
        if (!validateSink(sink)) {
            return;
        }
        boolean rendering = isRendering(time);
        if (this.time != time) {
            this.time = time;
            callSources(surface, time, rendering);
        }
        if (rendering) {
            if (clearSurface) {
                surface.clear();
            }
            process(surface, true);
        } else {
            process(surface, false);
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time, boolean recurse) {
        this.time = time;
        if (recurse) {
            for (Source source : sources) {
                source.setTime(time, true);
            }
        }
    }


    public void addSource(Source source) throws SinkIsFullException, SourceIsFullException {
        if (source == null) {
            throw new NullPointerException();
        }
        if (sources.contains(source)) {
            return;
        }
        if (sources.size() == maxSources) {
            throw new SourceIsFullException();
        }
        source.registerSink(this);
        sources.add(source);
    }

    public void removeSource(Source source) {
        if (sources.contains(source)) {
            source.unregisterSink(this);
            sources.remove(source);
        }
    }

    public boolean isRenderRequired(Source source, long time) {
        return isRendering(time);
    }

    public Source[] getSources() {
        return sources.toArray(new Source[sources.size()]);
    }

    protected boolean isRendering(long time) {
        if (sinks.size() == 1) {
            return simpleRenderingCheck(time);
        } else {
            return protectedRenderingCheck(time);
        }
    }

    private boolean simpleRenderingCheck(long time) {
        if (time != renderReqTime) {
            renderReqTime = time;
            renderReqCache = sinks.get(0).isRenderRequired(this, time);
        }
        return renderReqCache;
    }
    private int renderIdx = 0;

    private boolean protectedRenderingCheck(long time) {
        if (renderIdx > 0) {
            while (renderIdx < sinks.size()) {
                if (sinks.get(renderIdx++).isRenderRequired(this, time)) {
                    renderIdx = 0;
                    return true;
                }
            }
            return false;
        } else {
            if (renderReqTime != time) {
                renderReqTime = time;
                renderReqCache = false;
                while (renderIdx < sinks.size()) {
                    if (sinks.get(renderIdx++).isRenderRequired(this, time)) {
                        renderReqCache = true;
                        break;
                    }
                }
                renderIdx = 0;
            }
            return renderReqCache;
        }
    }

    protected int getSourceCount() {
        return sources.size();
    }

    protected Source getSource(int index) {
        return sources.get(index);
    }

    protected int getIndexOf(Source source) {
        return sources.indexOf(source);
    }

    protected abstract void callSources(Surface surface, long time, boolean rendering);

    protected abstract void process(Surface surface, boolean rendering);
}
