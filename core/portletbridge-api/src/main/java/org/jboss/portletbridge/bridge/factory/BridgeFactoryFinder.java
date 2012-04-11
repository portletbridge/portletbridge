/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.bridge.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.faces.BridgeException;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;

public final class BridgeFactoryFinder {

    private static final Logger logger = Logger.getLogger(BridgeFactoryFinder.class.getName(), BridgeLogger.LOGGING_BUNDLE);
    private static Map<Class<?>, List<String>> factoryDefinitions = new HashMap<Class<?>, List<String>>(6);
    private static Map<Class<?>, BridgeFactory<?>> factoryInstances = new HashMap<Class<?>, BridgeFactory<?>>(6);
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public static void addFactoryDefinition(Class<? extends BridgeFactory<?>> type, String factoryImplClassName) {
        lock.writeLock().lock();
        try {
            List<String> defs = factoryDefinitions.get(type);
            if (defs == null) {
                defs = (List<String>) new ArrayList<String>(4);
                factoryDefinitions.put(type, defs);
            }
            if (!defs.contains(factoryImplClassName)) {
                defs.add(factoryImplClassName);
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static List<String> getFactoryDefinition(Class<? extends BridgeFactory<?>> type) {
        lock.readLock().lock();
        try {
            return factoryDefinitions.get(type);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void addFactoryInstance(Class<? extends BridgeFactory<?>> type, BridgeFactory<?> factoryInstance) {
        lock.writeLock().lock();
        try {
            factoryInstances.put(type, factoryInstance);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static BridgeFactory<?> getFactoryInstance(Class<? extends BridgeFactory<?>> type) {
        lock.readLock().lock();
        try {
            BridgeFactory<?> instance = factoryInstances.get(type);
            if (instance != null) {
                return instance;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        // Check that instance wasn't created between readLock().unlock() and writeLock().lock()
        try {
            BridgeFactory<?> instance = factoryInstances.get(type);
            if (instance != null) {
                return instance;
            }

            List<String> defs = getFactoryDefinition(type);

            if (defs == null) {
                logger.log(Level.SEVERE, "No Factory Implementation class specified in faces-config for " + type);
                return null;
            }
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            for (String factoryImplClassName : defs) {
                instance = getImplGivenPreviousImpl(cl, factoryImplClassName, type, instance);
            }

            // Now store instance in cache
            addFactoryInstance(type, instance);
            return instance;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static BridgeFactory<?> getImplGivenPreviousImpl(ClassLoader classLoader, String implName,
            Class<? extends BridgeFactory<?>> type, BridgeFactory<?> previousImpl) {

        Class<? extends BridgeFactory<?>> implClass = null;

        try {
            implClass = (Class<? extends BridgeFactory<?>>) classLoader.loadClass(implName).asSubclass(type);
            try {
                Constructor<? extends BridgeFactory<?>> constructor = implClass.getConstructor(BridgeFactory.class);
                previousImpl = constructor.newInstance(previousImpl);
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("New factory instance " + implName + " has been created with parent implementation");
                }
            } catch (NoSuchMethodException e) {
                Constructor<? extends BridgeFactory<?>> constructor = implClass.getConstructor();
                previousImpl = constructor.newInstance();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("New factory instance " + implName + " has been created");
                }
            }
        } catch (ClassNotFoundException e) {
            throw new BridgeException("Bridge Factory class " + implName + " not found");
        } catch (NoSuchMethodException e) {
            throw new BridgeException("Bridge Factory " + implName
                    + " has neither BridgeFactory() nor BridgeFactory(BridgeFactory) constructor");
        } catch (IllegalArgumentException e) {
            throw new BridgeException("Illegal argument for Bridge Factory " + implName + " constructor", e);
        } catch (InstantiationException e) {
            throw new BridgeException("Can't instantiate Bridge Factory class " + implName, e);
        } catch (IllegalAccessException e) {
            throw new BridgeException("Illegal access to Bridge Factory constructor", e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof NoClassDefFoundError) {
                logger.info("Bridge Factory was not instantiated due to " + targetException.getMessage());
            } else {
                throw new BridgeException("Can't instantiate Bridge Factory class " + implName, targetException);
            }
        } catch (NoClassDefFoundError e) {
            logger.info("Bridge Factory was not instantiated due to " + e.getMessage());
        }
        return previousImpl;
    }
}
