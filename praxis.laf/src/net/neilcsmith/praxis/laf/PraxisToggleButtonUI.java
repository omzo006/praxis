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
package net.neilcsmith.praxis.laf;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODToggleButtonUI;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Neil C Smith
 */
public class PraxisToggleButtonUI extends NimRODToggleButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new PraxisButtonUI();

    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b,
            Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        if (!b.isFocusPainted() || !oldOpaque) {
            return;
        }
        if (b.getParent() instanceof JToolBar) {
            return;  // No se pintael foco cuando estamos en una barra
        }

        PraxisThemeUtils.paintFocus(g, 3, 3, b.getWidth() - 6, b.getHeight() - 6, 2, 2, 1, NimRODLookAndFeel.getFocusColor());
    }
}
