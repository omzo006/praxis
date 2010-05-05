/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 - Neil C Smith. All rights reserved.
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

package net.neilcsmith.praxis.script.commands;


import net.neilcsmith.praxis.core.CallArguments;
import net.neilcsmith.praxis.core.syntax.InvalidSyntaxException;
import net.neilcsmith.praxis.script.Command;
import net.neilcsmith.praxis.script.ExecutionException;
import net.neilcsmith.praxis.script.Namespace;
import net.neilcsmith.praxis.script.StackFrame;
import net.neilcsmith.praxis.script.ast.RootNode;
import net.neilcsmith.praxis.script.ast.ScriptParser;

/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
public class EvalCommand implements Command {

    private final boolean inline;

    public EvalCommand(boolean inline) {
        this.inline = inline;
    }

    public StackFrame createStackFrame(Namespace namespace, CallArguments args)
            throws ExecutionException {
        if (args.getCount() != 1) {
            throw new ExecutionException();
        }
        String script = args.getArg(0).toString();
        try {
            RootNode astRoot = ScriptParser.getInstance().parse(script);
//            astRoot.init(namespace);
            if (inline) {
                return new EvalStackFrame(namespace, astRoot);
            } else {
                return new EvalStackFrame(namespace.createChild(), astRoot);
            }
        } catch (InvalidSyntaxException ex) {
            throw new ExecutionException(ex);
        }
    }

    

}
