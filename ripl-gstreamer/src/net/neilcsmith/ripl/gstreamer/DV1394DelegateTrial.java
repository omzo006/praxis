/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008/09 - Neil C Smith. All rights reserved.
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
package net.neilcsmith.ripl.gstreamer;

import java.net.URI;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.RGBDataSink;
import org.gstreamer.elements.RGBDataSink.Listener;

/**
 *
 * @author Neil C Smith
 */
public class DV1394DelegateTrial extends AbstractGstDelegate {

    private DV1394DelegateTrial() {

    }

    @Override
    protected Pipeline buildPipeline(Listener listener) {
        Pipeline pipe = new Pipeline();
        Element src = ElementFactory.make("dv1394src", "source0");
        Element demux = ElementFactory.make("dvdemux", "dvdemux0");
        Element dvdec = ElementFactory.make("dvdec", "dvdec0");
        Element color0 = ElementFactory.make("ffmpegcolorspace", "color0");
        Element deint = ElementFactory.make("deinterlace", "deint0");
        Element color1 = ElementFactory.make("ffmpegcolorspace", "color1");
        RGBDataSink sink = new RGBDataSink("sink", listener);
        sink.getSinkElement().set("sync", "false");
        sink.setPassDirectBuffer(true);
        pipe.addMany(src, demux, dvdec, color0, sink);
        Pipeline.linkMany(src, demux, dvdec, color0, sink);
        return pipe;
    }

    public static DV1394DelegateTrial create(URI resource) {
        return new DV1394DelegateTrial();
    }
}
