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
package net.neilcsmith.praxis.tinkerforge.components;

import com.tinkerforge.BrickletDistanceIR;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.Argument;
import net.neilcsmith.praxis.core.ControlPort;
import net.neilcsmith.praxis.core.types.PNumber;
import net.neilcsmith.praxis.impl.ArgumentProperty;
import net.neilcsmith.praxis.impl.DefaultControlOutputPort;

/**
 *
 * @author Neil C Smith
 */
public class DistanceIR extends AbstractTFComponent<BrickletDistanceIR> {

    private BrickletDistanceIR device;
    private int value;
    private ControlPort.Output out;
    private DistanceListener dListener;

    public DistanceIR() {
        super(BrickletDistanceIR.class);
        registerControl("distance", ArgumentProperty.createReadOnly(PNumber.info(), new ValueBinding()));
        out = new DefaultControlOutputPort();
        registerPort("distance", out);
    }

    @Override
    protected void initDevice(BrickletDistanceIR device) {
        this.device = device;
        dListener = new DistanceListener();
        device.addDistanceListener(dListener);
        try {
            device.setDistanceCallbackPeriod(getCallbackPeriod());
        } catch (Exception ex) {
            Logger.getLogger(DistanceIR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void disposeDevice(BrickletDistanceIR device) {
        device.removeDistanceListener(dListener);
        dListener = null;
        try {
            device.setDistanceCallbackPeriod(0);
        } catch (Exception ex) {
            Logger.getLogger(DistanceIR.class.getName()).log(Level.FINE, null, ex);
        }
        this.device = null;
    }

    private class DistanceListener implements BrickletDistanceIR.DistanceListener {

        @Override
        public void distance(final int dist) {
            final long time = getTime();
            invokeLater(new Runnable() {
                
                @Override
                public void run() {
                    if (dListener == DistanceListener.this) {
                        value = dist;
                        out.send(time, value);
                    }
                }
            });
        }
    }

    private class ValueBinding implements ArgumentProperty.ReadBinding {

        @Override
        public Argument getBoundValue() {
            return PNumber.valueOf(value);
        }
    }
}
