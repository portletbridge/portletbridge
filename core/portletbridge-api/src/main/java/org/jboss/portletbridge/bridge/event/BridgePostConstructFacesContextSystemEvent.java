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
package org.jboss.portletbridge.bridge.event;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after the bridge has acquired
 * the <code>FacesContext</code> but before it runs the <code>Lifecyle</code>. Gives listeners the ability to do work at a point
 * in which the entire request processing environment has been established but before actually executing the request. One use
 * allows <code>ExternalContext</code> implementations to be notified prior to use to do any initializations that couldn't be
 * done in its constructor because it lacked access to the <code>BridgeContext</code>.
 */
public class BridgePostConstructFacesContextSystemEvent extends BridgeSystemEvent {
    private static final long serialVersionUID = -4648253075591280844L;

    public BridgePostConstructFacesContextSystemEvent(BridgeContext ctx) {
        super(ctx);
    }
}