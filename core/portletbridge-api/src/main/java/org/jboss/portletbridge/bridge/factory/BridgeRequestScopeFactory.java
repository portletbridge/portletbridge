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

/**
 * This abstract class provides a contract for defining a factory that knows how to create instances of type
 * {@link BridgeRequestScope}.
 */
public abstract class BridgeRequestScopeFactory extends BridgeFactory<BridgeRequestScopeFactory> {

    public abstract BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId,
            String portletMode) throws FacesException;

    public abstract BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId,
            String portletMode, int initialCapacity) throws FacesException;

    public abstract BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId,
            String portletMode, int initialCapacity, float loadFactor, int concurrencyLevel) throws FacesException;

    public abstract BridgeRequestScope getBridgeRequestScope(String portletName, String sessionId, String viewId,
            String portletMode, Map<String, Object> requestScopeDataMap) throws FacesException;

    public BridgeRequestScopeFactory getWrapped() {
        return null;
    }
}
