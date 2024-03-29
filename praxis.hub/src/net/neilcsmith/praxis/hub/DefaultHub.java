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
 */
package net.neilcsmith.praxis.hub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.neilcsmith.praxis.core.Argument;
import net.neilcsmith.praxis.core.Call;
import net.neilcsmith.praxis.core.CallArguments;
import net.neilcsmith.praxis.core.Component;
import net.neilcsmith.praxis.core.ComponentAddress;
import net.neilcsmith.praxis.core.ComponentFactory;
import net.neilcsmith.praxis.core.ComponentType;
import net.neilcsmith.praxis.core.IllegalRootStateException;
import net.neilcsmith.praxis.core.InterfaceDefinition;
import net.neilcsmith.praxis.core.InvalidAddressException;
import net.neilcsmith.praxis.core.Lookup;
import net.neilcsmith.praxis.core.Packet;
import net.neilcsmith.praxis.core.Root;
import net.neilcsmith.praxis.core.RootHub;
import net.neilcsmith.praxis.core.info.ControlInfo;
import net.neilcsmith.praxis.core.interfaces.ComponentFactoryService;
import net.neilcsmith.praxis.core.interfaces.RootManagerService;
import net.neilcsmith.praxis.core.interfaces.ServiceManager;
import net.neilcsmith.praxis.core.interfaces.ServiceUnavailableException;
import net.neilcsmith.praxis.core.types.PArray;
import net.neilcsmith.praxis.core.types.PReference;
import net.neilcsmith.praxis.core.types.PString;
import net.neilcsmith.praxis.impl.AbstractRoot;
import net.neilcsmith.praxis.impl.BasicControl;
import net.neilcsmith.praxis.impl.InstanceLookup;
import net.neilcsmith.praxis.impl.SimpleControl;

/**
 *
 * @author Neil C Smith
 */
public class DefaultHub extends AbstractRoot {

    private static final Logger LOG = Logger.getLogger(DefaultHub.class.getName());
    private RootHubImpl hub;
    private ServiceManagerImpl serviceManager;
    private Root.Controller controller;
    private final String EXT_PREFIX = "_sys_";
    private final String HUB_ID = "praxis";
    private ConcurrentMap<String, Root.Controller> roots;
    private List<PString> rootList;
    private Thread hubThread;
    private ComponentFactory factory;
    private Lookup lookup;
    private Map<InterfaceDefinition, ComponentAddress[]> services;
    private Root[] extensions;

    public DefaultHub(Root... exts) {
        this(null, exts);
    }
    
//    public DefaultHub(ComponentFactory factory, Root... exts) {
//        this(null, factory, exts);
//    }
    
    public DefaultHub(ComponentFactory factory, Root... exts) {
        super(EnumSet.of(AbstractRoot.Caps.Component));
        roots = new ConcurrentHashMap<String, Root.Controller>();
        rootList = new ArrayList<PString>();
        hub = new RootHubImpl();
        serviceManager = new ServiceManagerImpl();
        services = new ConcurrentHashMap<InterfaceDefinition, ComponentAddress[]>();
        extensions = exts;
        lookup = InstanceLookup.create(serviceManager);
        if (factory == null) {
            factory = LookupComponentFactory.getInstance();
        }
        this.factory = factory;
    }

    public void activate() throws IllegalRootStateException {

        controller = initialize(HUB_ID, hub);
        Runnable runner = new Runnable() {

            public void run() {
                try {
                    controller.run();
                } catch (IllegalRootStateException ex) {
                    Logger.getLogger(DefaultHub.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        hubThread = new Thread(runner, "PRAXIS_HUB_THREAD");
        hubThread.start();

    }

    public void shutdown() {
        Root.Controller ctrl = controller;
        if (ctrl != null) {
            ctrl.shutdown();
        }

    }

    @Override
    protected void activating() {
        super.activating();
        createDefaultControls();
        createDefaultServices();
        installExtensions();
    }

    @Override
    protected void terminating() {
        super.terminating();
        String[] ids = roots.keySet().toArray(new String[roots.size()]);
        for (String id : ids) {
            uninstallRoot(id);
        }

    }

//    @Override
//    protected void stopping() {
//        System.exit(0);
//    }
    private void createDefaultControls() {
        registerControl("clear", new ClearControl());
        registerControl("log", new LogControl());
        registerControl(RootManagerService.ADD_ROOT, new AddRootControl());
        registerControl(RootManagerService.REMOVE_ROOT, new RemoveRootControl());
        registerControl(RootManagerService.ROOTS, new RootsControl());
        registerControl(ComponentFactoryService.NEW_INSTANCE, new NewInstanceControl());
    }

    private void createDefaultServices() {
        ComponentAddress address = ComponentAddress.create("/" + HUB_ID);
        installService(RootManagerService.INSTANCE, address);
        installService(ComponentFactoryService.INSTANCE, address);
    }

    private void installExtensions() {
        for (Root ext : extensions) {
            // get before we activate install - thread safety
            InterfaceDefinition[] servs = ext.getInterfaces();
            String extID = EXT_PREFIX + Integer.toHexString(ext.hashCode());
            try {
                installRoot(extID, "sysex", ext);
            } catch (InvalidAddressException ex) {
                LOG.log(Level.SEVERE, "Failed to install extension\n{0} to /{1}\n{2}",
                        new Object[]{ext.getClass(), extID, ex});
                continue;
            } catch (IllegalRootStateException ex) {
                LOG.log(Level.SEVERE, "Failed to install extension\n{0} to /{1}\n{2}",
                        new Object[]{ext.getClass(), extID, ex});
                continue;
            }
            // safe to install services - thread safe at this point
            ComponentAddress root = ComponentAddress.create("/" + extID);
            for (InterfaceDefinition serv : servs) {
                installService(serv, root);
            }
        }
    }

    private void installService(InterfaceDefinition serv, ComponentAddress root) {
        if (serv == null) {
            return;
        }
        ComponentAddress[] provs = services.get(serv);
        if (provs == null) {
            services.put(serv, new ComponentAddress[]{root});
        } else {
            ComponentAddress[] nprovs = new ComponentAddress[provs.length + 1];
            nprovs[0] = root;
            System.arraycopy(provs, 0, nprovs, 1, provs.length);
            services.put(serv, nprovs);
        }

    }

    // NOT threadsafe - should only be called from single thread
    // don't use to activate main hub
    protected void installRoot(String id, String typeID, Root root)
            throws InvalidAddressException, IllegalRootStateException {
        if (id == null || root == null) {
            throw new NullPointerException();
        }
        if (!ComponentAddress.isValidID(id) || roots.get(id) != null) {
            throw new InvalidAddressException();
        }
        Root.Controller ctrl = root.initialize(id, hub);
        roots.put(id, ctrl);
        startRoot(id, typeID, ctrl);

        rootList.add(PString.valueOf(id));
    }

    // NOT threadsafe - should only be called from single thread
    protected void uninstallRoot(String id) {
        Root.Controller ctrl = roots.get(id);
        if (ctrl != null) {
            ctrl.shutdown();
            roots.remove(id);
            rootList.remove(PString.valueOf(id));
        }
    }

    protected void clear() {
        String[] ids = roots.keySet().toArray(new String[roots.size()]);
        for (String id : ids) {
            if (id.startsWith(EXT_PREFIX)) {
                continue;
            }
            uninstallRoot(id);
        }
    }

    private void startRoot(final String rootID, String typeID, final Root.Controller ctrl) {
        Thread thr = new Thread(new Runnable() {

            public void run() {
                try {
                    ctrl.run();
                } catch (IllegalRootStateException ex) {
                    LOG.severe("Root " + rootID + " threw root state exception");
                } finally {
                    if (roots.remove(rootID, ctrl)) {
                        LOG.warning("Root " + rootID + " terminated unexpectedly");
                    }
                }
            }
        }, rootID);
        if ("root:audio".equals(typeID)) {
            thr.setPriority(Thread.MAX_PRIORITY);
        } else {
            thr.setPriority(7); // @TODO work out priority scheme
        }

        thr.start();
    }

    void route(Packet packet) {
        getPacketRouter().route(packet);
    }

    private class RootHubImpl implements RootHub {

        private final Logger logger = Logger.getLogger(RootHubImpl.class.getName());

        private RootHubImpl() {
        }

        // THREAD SAFE
        public void dispatch(Packet packet) throws InvalidAddressException {
//            logger.info(packet.toString());
            Root.Controller dest = roots.get(packet.getRootID());
            if (dest != null) {
                dest.submitPacket(packet);
            } else if (packet.getRootID().equals(HUB_ID)) {
                // hub is not in roots!
                controller.submitPacket(packet);
            } else {
                logger.info("hub throwing Invalid Address Exception - no root found for id : " + packet);
                throw new InvalidAddressException();
            }
        }

        // THREAD SAFE
        public Lookup getLookup() {
            return lookup;
        }
    }

    private class ServiceManagerImpl implements ServiceManager {

        // THREAD SAFE
        public ComponentAddress findService(InterfaceDefinition info) throws ServiceUnavailableException {
            return findServicesImpl(info)[0];
        }

        // THREAD SAFE
        public ComponentAddress[] findAllServices(InterfaceDefinition info) throws ServiceUnavailableException {
            ComponentAddress[] provs = findServicesImpl(info);
            return Arrays.copyOf(provs, provs.length);
        }

        private ComponentAddress[] findServicesImpl(InterfaceDefinition info) throws ServiceUnavailableException {
            ComponentAddress[] provs = services.get(info);
            if (provs == null) {
                throw new ServiceUnavailableException();
            } else {
                return provs;
            }
        }
    }

    private class ClearControl extends BasicControl {

        private ClearControl() {
            super(DefaultHub.this);
        }

        @Override
        protected Call processInvoke(Call call, boolean quiet) throws Exception {
            clear();
            if (quiet) {
                return null;
            } else {
                return Call.createReturnCall(call, CallArguments.EMPTY);
            }
        }

        public ControlInfo getInfo() {
            return null;
        }
    }

    private class LogControl extends BasicControl {

        private Logger logger = Logger.getLogger(LogControl.class.getName());

        private LogControl() {
            super(DefaultHub.this);
        }

        @Override
        protected Call processInvoke(Call call, boolean quiet) throws Exception {
            logger.info(call.toString());
            if (!quiet) {
                return Call.createReturnCall(call, CallArguments.EMPTY);
            }
            return null;
        }

        @Override
        protected void processError(Call call) {
            CallArguments args = call.getArgs();
            if (args.getSize() == 1) {
                Argument arg = args.get(0);
                if (arg instanceof PReference) {
                    Object o = ((PReference) arg).getReference();
                    if (o instanceof Exception) {
                        logger.log(Level.SEVERE, call.toString(), (Exception) o);
                        return;
                    }
                }
            }
            logger.severe(call.toString());
        }

        @Override
        protected void processReturn(Call call) {
            logger.info(call.toString());
        }

        public ControlInfo getInfo() {
            return null;
        }
    }

    private class NewInstanceControl extends SimpleControl {

        private NewInstanceControl() {
            super(ComponentFactoryService.NEW_INSTANCE_INFO);
        }

        @Override
        protected CallArguments process(long time, CallArguments args, boolean quiet) throws Exception {
            Component c = factory.createComponent(ComponentType.coerce(args.get(0)));
            return CallArguments.create(PReference.wrap(c));
        }
    }

    private class AddRootControl extends SimpleControl {

        private AddRootControl() {
            super(RootManagerService.ADD_ROOT_INFO);
        }

        @Override
        protected CallArguments process(long time, CallArguments args, boolean quiet) throws Exception {
            String id = args.get(0).toString();
            Root r = factory.createRootComponent(ComponentType.coerce(args.get(1)));
            installRoot(id, args.get(1).toString(), r);
            return CallArguments.EMPTY;
        }
    }

    private class RemoveRootControl extends SimpleControl {

        private RemoveRootControl() {
            super(RootManagerService.REMOVE_ROOT_INFO);
        }

        @Override
        protected CallArguments process(long time, CallArguments args, boolean quiet) throws Exception {
            String id = args.get(0).toString();
            uninstallRoot(id);
            return CallArguments.EMPTY;
        }
    }

    private class RootsControl extends SimpleControl {

        private RootsControl() {
            super(RootManagerService.ROOTS_INFO);
        }

        @Override
        protected CallArguments process(long time, CallArguments args, boolean quiet) throws Exception {
            return CallArguments.create(PArray.valueOf(rootList));
        }



    }
}
