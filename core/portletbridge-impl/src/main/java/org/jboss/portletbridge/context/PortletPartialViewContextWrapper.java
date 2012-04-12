/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.portletbridge.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

public class PortletPartialViewContextWrapper extends PartialViewContextWrapper {

    private final FacesContext ctx;
    private final PartialViewContext parent;
    private boolean responseInitialized = false;

    public PortletPartialViewContextWrapper(FacesContext ctx, PartialViewContext parent) {
        this.ctx = ctx;
        this.parent = parent;
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        if (!responseInitialized) {
            // In portal, content-type has to be set before getting response writer.
            ExternalContext extContext = ctx.getExternalContext();
            extContext.setResponseContentType("text/xml");
            extContext.addResponseHeader("Cache-Control", "no-cache");
            responseInitialized = true;
        }
        return super.getPartialResponseWriter();
    }

    @Override
    public PartialViewContext getWrapped() {
        return parent;
    }

    @Override
    public void setPartialRequest(boolean isPartialRequest) {
        getWrapped().setPartialRequest(isPartialRequest);
    }

}
