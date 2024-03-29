/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 Neil C Smith.
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
package net.neilcsmith.praxis.video;

import net.neilcsmith.praxis.settings.Settings;

/**
 *
 * @author Neil C Smith <http://neilcsmith.net>
 */
public class VideoSettings {

    public final static String KEY_RENDERER = "video.renderer";
    private final static String KEY_CAPTURE_PREFIX = "video.capture";
    private final static String DEFAULT_RENDERER = "Software";
    private static String DEFAULT_CAPTURE_SCHEME = "v4l2";

    static {
        String os = System.getProperty("os.name");
        if (os != null) {
            if (os.contains("Windows")) {
                DEFAULT_CAPTURE_SCHEME = "ks";
            } else if (os.contains("Mac") || os.contains("Darwin")) {
                DEFAULT_CAPTURE_SCHEME = "qtkit";
            }
        }
    }

    private VideoSettings() {
    }

    public static String getRenderer() {
        return Settings.get(KEY_RENDERER, DEFAULT_RENDERER);
    }

    public static void setRenderer(String renderer) {
        Settings.put(KEY_RENDERER, renderer);
    }

    public static String getCaptureDevice(int idx) {
        if (idx < 0) {
            throw new IllegalArgumentException();
        }
        return Settings.get(KEY_CAPTURE_PREFIX + idx, DEFAULT_CAPTURE_SCHEME + "://" + idx);
    }

    public static void setCaptureDevice(int idx, String device) {
        if (idx < 0) {
            throw new IllegalArgumentException();
        }
        Settings.put(KEY_CAPTURE_PREFIX + idx, device);
    }
}
