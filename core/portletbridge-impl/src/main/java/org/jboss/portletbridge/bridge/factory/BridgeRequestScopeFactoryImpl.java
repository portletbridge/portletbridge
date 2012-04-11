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

import java.util.Map;

import javax.faces.FacesException;

import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeImpl;

/**
 * @author kenfinnigan
 */
public class BridgeRequestScopeFactoryImpl extends BridgeRequestScopeFactory {

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory#getBridgeRequestScope(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId, String portletMode)
            throws FacesException {
        return new BridgeRequestScopeImpl(portletName, sessionId, viewId, portletMode);
    }

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory#getBridgeRequestScope(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId, String portletMode,
            int initialCapacity) throws FacesException {
        return new BridgeRequestScopeImpl(portletName, sessionId, viewId, portletMode, initialCapacity);
    }

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory#getBridgeRequestScope(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, int, float, int)
     */
    @Override
    public BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId, String portletMode,
            int initialCapacity, float loadFactor, int concurrencyLevel) throws FacesException {
        return new BridgeRequestScopeImpl(portletName, sessionId, viewId, portletMode, initialCapacity, loadFactor,
                concurrencyLevel);
    }

    /**
     * @see org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory#getBridgeRequestScope(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId, String portletMode,
            Map<String, Object> requestScopeDataMap) throws FacesException {
        return new BridgeRequestScopeImpl(portletName, sessionId, viewId, portletMode, requestScopeDataMap);
    }

}
