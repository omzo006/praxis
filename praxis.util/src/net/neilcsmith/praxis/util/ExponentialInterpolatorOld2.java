/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Neil C Smith. All rights reserved.
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
 *
 */
package net.neilcsmith.praxis.util;

/**
 *
 * @author Neil C Smith
 */
public class ExponentialInterpolatorOld2 implements Interpolator {

    private final static ExponentialInterpolatorOld2 instance = new ExponentialInterpolatorOld2();
//    private final double Em1 = Math.E - 1;
    private final double log2 = Math.log(2);

    private ExponentialInterpolatorOld2() {
    }

    public double interpolate(double value) {
        if (value <= 0) {
            return 0;
        } else if (value >= 1) {
            return 1;
        } else {
            return ((Math.pow(10, value)) - 1) / 9;
//            return ((Math.pow(2, value)) - 1);
        }
    }

    public double reverseInterpolate(double value) {
        if (value <= 0) {
            return 0;
        } else if (value >= 1) {
            return 1;
        } else {
            return Math.log10((value * 9) + 1);
//            return Math.log(value + 1) / log2;
        }
    }

    public static ExponentialInterpolatorOld2 getInstance() {
        return instance;
    }
}
