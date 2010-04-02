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

package net.neilcsmith.praxis.video.components.test;

import net.neilcsmith.praxis.impl.AbstractComponent;
import net.neilcsmith.praxis.impl.FloatProperty;
import net.neilcsmith.praxis.impl.StringProperty;
import net.neilcsmith.praxis.video.DefaultVideoInputPort;
import net.neilcsmith.praxis.video.DefaultVideoOutputPort;
import net.neilcsmith.ripl.components.temporal.Difference.Mode;

/**
 *
 * @author Neil C Smith
 */
public class Difference extends AbstractComponent {
    
    private net.neilcsmith.ripl.components.temporal.Difference diff;
    
    public Difference() {
        diff = new net.neilcsmith.ripl.components.temporal.Difference();
        registerPort("input", new DefaultVideoInputPort(this, diff));
        registerPort("output", new DefaultVideoOutputPort(this, diff));
        StringProperty mode = StringProperty.create(this, new ModeBinding(),
                getModeStrings(), diff.getMode().name());
        registerControl("mode", mode);
        FloatProperty threshold = FloatProperty.create(this, new ThresholdBinding(), 0, 1, 0);
        registerControl("threshold", threshold);
        registerPort("threshold", threshold.createPort());
    }
    
    private String[] getModeStrings() {
        Mode[] modes = Mode.values();
        String[] strings = new String[modes.length];
        for (int i=0; i < modes.length; i++) {
            strings[i] = modes[i].name();
        }
        return strings;
    }
    
    private class ModeBinding implements StringProperty.Binding {

        public void setBoundValue(long time, String value) {
            diff.setMode(Mode.valueOf(value));
        }

        public String getBoundValue() {
            return diff.getMode().name();
        }
        
    }
    
    private class ThresholdBinding implements FloatProperty.Binding {

        public void setBoundValue(long time, double value) {
            diff.setThreshold(value);
        }

        public double getBoundValue() {
            return diff.getThreshold();
        }
        
    }

}
