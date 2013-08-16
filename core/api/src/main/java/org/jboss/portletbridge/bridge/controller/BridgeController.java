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
package org.jboss.portletbridge.bridge.controller;

import javax.portlet.faces.BridgeException;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.context.BridgeContext;

public interface BridgeController {
    String IGNORE_BRIDGE_SCOPE = "org.jboss.portletbridge.ignoreBridgeScope";
    String VIEW_ROOT = "org.jboss.portletbridge.viewRoot";
    String ACTION_PARAMETERS = "org.jboss.portletbridge.actionParameters";

    void init(BridgeConfig config) throws BridgeException;

    void destroy();

    void processPortletAction(BridgeContext ctx) throws BridgeException;

    void handlePortletEvent(BridgeContext ctx) throws BridgeException;

    void renderPortletHead(BridgeContext ctx) throws BridgeException;

    void renderPortletBody(BridgeContext ctx) throws BridgeException;

    void renderResource(BridgeContext ctx) throws BridgeException;

}