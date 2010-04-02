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

package net.neilcsmith.praxis.hub;

import java.util.Iterator;
import java.util.ServiceLoader;
import net.neilcsmith.praxis.core.Lookup;

/**
 *
 * @author Neil C Smith
 */
public class ServiceLoaderLookup implements Lookup {

    public <T> Result<T> lookup(Class<T> type) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
        return new ServiceLoaderLookup.ServiceLoaderWrapper<T>(serviceLoader);
    }
    
    private class ServiceLoaderWrapper<T> implements Lookup.Result<T> {
        
        private ServiceLoader<T> serviceLoader;
        
        ServiceLoaderWrapper(ServiceLoader<T> serviceLoader) {
            this.serviceLoader = serviceLoader;
        }

        public Iterator<T> iterator() {
            return serviceLoader.iterator();
        }
        
    }

}
