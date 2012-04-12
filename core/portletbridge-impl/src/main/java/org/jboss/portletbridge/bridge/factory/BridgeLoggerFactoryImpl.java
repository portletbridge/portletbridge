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

import javax.faces.FacesException;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

/**
 * @author kenfinnigan
 */
public class BridgeLoggerFactoryImpl extends BridgeLoggerFactory {

    public static BridgeLogger getLogger(String className) {
        return ((BridgeLoggerFactory) BridgeFactoryFinder.getFactoryInstance(BridgeLoggerFactory.class))
                .getBridgeLogger(className);
    }

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeLoggerFactory#getBridgeLogger(org.jboss.portletbridge.bridge.config.BridgeConfig)
     */
    @Override
    public BridgeLogger getBridgeLogger(BridgeConfig config) throws FacesException {
        // TODO Config for setting logger impl? ie. log4j, slf4j, logback, jul
        return new JULLoggerImpl();
    }

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeLoggerFactory#getBridgeLogger(String)
     */
    @Override
    public BridgeLogger getBridgeLogger(String className) {
        return new JULLoggerImpl(className);
    }
}
