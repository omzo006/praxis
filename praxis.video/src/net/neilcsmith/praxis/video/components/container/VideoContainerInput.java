/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2013 Neil C Smith.
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
package net.neilcsmith.praxis.video.components.container;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.ContainerContext;
import net.neilcsmith.praxis.core.Port;
import net.neilcsmith.praxis.core.RegistrationException;
import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.video.VideoPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoInputPort;
import net.neilcsmith.praxis.video.impl.DefaultVideoOutputPort;
import net.neilcsmith.praxis.video.pipes.impl.Placeholder;

/**
 *
 * @author Neil C Smith
 */
public class VideoContainerInput extends AbstractComponent {
    
    private ContainerContext context;
    private String id;
    private VideoPort.Input containerPort;

    public VideoContainerInput() {
        Placeholder pl = new Placeholder();
        VideoPort.Output output = new DefaultVideoOutputPort(pl);
        containerPort = new DefaultVideoInputPort(pl);
        registerPort(Port.OUT, output);
    }

    @Override
    public void hierarchyChanged() {
        super.hierarchyChanged();
        ContainerContext ctxt = getLookup().get(ContainerContext.class);
        if (context != ctxt) {
            if (context != null) {
                context.unregisterPort(id, containerPort);
            }
            if (ctxt != null) {
                id = getAddress().getID();
                try {
                    ctxt.registerPort(id, containerPort);
                } catch (RegistrationException ex) {
                    Logger.getLogger(VideoContainerInput.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            context = ctxt;
        }
    }
    
}
