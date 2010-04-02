/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Neil C Smith. All rights reserved.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please visit http://neilcsmith.net if you need additional information or
 * have any questions.
 *
 */

package net.neilcsmith.jnajack;

import java.nio.ByteBuffer;
import net.neilcsmith.jnajack.lowlevel.JackLibrary;

/**
 * Wraps a native Jack port.
 * @author Neil C Smith
 */
public class JackPort {

    JackLibrary._jack_port portPtr;
    ByteBuffer buffer;
    private JackClient client;
    private JackLibrary jackLib;
    private String shortName;

    JackPort(String shortName, JackClient client, JackLibrary jackLib, JackLibrary._jack_port portPtr) {
        this.shortName = shortName;
        this.portPtr = portPtr;
        this.client = client;
        this.jackLib = jackLib;
    }

    /**
     * Get the buffer associated with this port. Do not cache this value between
     * process calls - this buffer reference is only valid inside the process
     * callback.
     *
     * The buffer will be returned as a direct ByteBuffer.
     *
     * For audio use <code>getBuffer().asFloatBuffer()</code>
     *
     * @return buffer associated with this port.
     */
    // @TODO should we create this lazily in call to client. MIDI ports won't require this.
    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Get the full name for this port including the "client_name:" prefix.
     * @return full name
     */
    public String getName() {
        return client.getName() + ":" + shortName;
    }

    /**
     * Get the short name for this port (without client name prefix).
     * @return short name
     */
    public String getShortName() {
        return shortName;
    }

}
