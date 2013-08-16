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
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after acquiring the
 * <code>BridgeContext</code> (and before doing anything else). Gives listeners the ability to do their own request
 * initialization and/or to augment the <code>BridgeContext</code>. For example a listener could get the request or
 * response from the <code>BridgeContext</code>, wrap it to add their own overrides, and then put back into the
 * <code>BridgeContext</code>. Such use would ensure the wrapped request or response is what is used throughout the
 * bridge's request processing.
 */
public class BridgeInitializeRequestSystemEvent extends BridgeSystemEvent {
    private static final long serialVersionUID = -4648289862503176422L;

    public BridgeInitializeRequestSystemEvent(BridgeContext ctx) {
        super(ctx);
    }
}