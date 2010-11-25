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
 *
 */

package net.neilcsmith.praxis.video.gstreamer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.neilcsmith.praxis.video.VideoDelegateFactory;
import net.neilcsmith.praxis.video.VideoDelegateFactoryProvider;

/**
 *
 * @author Neil C Smith
 */
public class GStreamerFactoryProvider implements VideoDelegateFactoryProvider {
    
    private static String[] schemes = { "file", "http", "v4l", "v4l2", "ipcam", "dv1394"};

    public Set<String> getSupportedSchemes() {
        return new LinkedHashSet<String>(Arrays.asList(schemes)); 
    }

    public String getLibraryName() {
        return "gstreamer";
    }

    public VideoDelegateFactory getFactory() {
        return GStreamerFactory.getInstance();
    }

}
