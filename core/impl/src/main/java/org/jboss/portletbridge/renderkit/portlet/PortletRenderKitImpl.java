/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.renderkit.portlet;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import java.io.Writer;

/**
 * Override the {@link ResponseWriter} returned when we're processing a partial request.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletRenderKitImpl extends RenderKitWrapper {

    private RenderKit wrappedRenderKit;

    public PortletRenderKitImpl(RenderKit parent) {
        this.wrappedRenderKit = parent;
    }

    @Override
    public RenderKit getWrapped() {
        return wrappedRenderKit;
    }

    @Override
    public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
        PartialViewContext partialViewContext = FacesContext.getCurrentInstance().getPartialViewContext();
        boolean isPartialRequest = partialViewContext.isPartialRequest();

        if (isPartialRequest) {
            return new PortletPartialResponseWriter(getWrapped().createResponseWriter(writer, contentTypeList, characterEncoding));
        } else {
            return getWrapped().createResponseWriter(writer, contentTypeList, characterEncoding);
        }
    }
}
