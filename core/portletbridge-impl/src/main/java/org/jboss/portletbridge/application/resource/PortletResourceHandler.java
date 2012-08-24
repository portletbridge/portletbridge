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
package org.jboss.portletbridge.application.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ResourceRequest;
import javax.portlet.faces.BridgeUtil;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leo
 *
 */
public class PortletResourceHandler extends ResourceHandlerWrapper {

    public static final String RESOURCE_ID = "id";

    public static final String LIBRARY_ID = "ln";

    public static final String MIME_PARAM = "type";

    private final ResourceHandler parent;

    public PortletResourceHandler(ResourceHandler parent) {
        this.parent = parent;
    }

    /**
     * @see javax.faces.application.ResourceHandlerWrapper#getWrapped()
     */
    @Override
    public ResourceHandler getWrapped() {
        return parent;
    }

    @Override
    public boolean isResourceRequest(FacesContext facesContext) {
        ExternalContext extContext = facesContext.getExternalContext();
        Object request = extContext.getRequest();

        if (request instanceof ResourceRequest) {
            String resourceIdentifier = ((ResourceRequest)request).getResourceID();

            if (null != resourceIdentifier) {
                return true;
            }
        }
        return super.isResourceRequest(facesContext);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);
        if (!isPortletResource(resource)) {
            resource = new PortletResource(resource);
        }
        return resource;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource resource = super.createResource(resourceName, libraryName, contentType);
        if (!isPortletResource(resource)) {
            resource = new PortletResource(resource);
        }
        return resource;
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {

        if (BridgeUtil.isPortletRequest()) {
            ExternalContext externalContext = context.getExternalContext();
            String resourceId = externalContext.getRequestParameterMap().get(RESOURCE_ID);
            if (null != resourceId) {
                String libraryId = externalContext.getRequestParameterMap().get(LIBRARY_ID);
                String contentType = externalContext.getRequestParameterMap().get(MIME_PARAM);
                Resource resource = createResource(resourceId, libraryId, contentType);
                if (null != resource) {
                    handleResourceRequest(context, resource);
                } else {
                    send404(context, resourceId, libraryId);
                }
            } else {
                send404(context, resourceId, null);
            }
        } else {
            super.handleResourceRequest(context);
        }
    }

    protected void handleResourceRequest(FacesContext context, Resource resource) throws IOException {
        if (resource.userAgentNeedsUpdate(context)) {
            ExternalContext extContext = context.getExternalContext();
            ReadableByteChannel resourceChannel = null;
            WritableByteChannel out = null;
            int bufferSize = extContext.getResponseBufferSize();
            if (0 == bufferSize)
                bufferSize = 2048;
            ByteBuffer buf = ByteBuffer.allocate(bufferSize);
            extContext.setResponseBufferSize(buf.capacity());
            try {
                InputStream in = resource.getInputStream();
                if (in == null) {
                    send404(context, resource.getResourceName(), resource.getLibraryName());
                    return;
                }
                resourceChannel = Channels.newChannel(in);
                String contentType = resource.getContentType();
                if (contentType != null) {
                    extContext.setResponseContentType(resource.getContentType());
                }
                out = Channels.newChannel(extContext.getResponseOutputStream());
                extContext.setResponseBufferSize(buf.capacity());
                handleHeaders(context, resource);

                int size = 0;
                for (int thisRead = resourceChannel.read(buf), totalWritten = 0; thisRead != -1; thisRead = resourceChannel
                    .read(buf)) {

                    buf.rewind();
                    buf.limit(thisRead);
                    do {
                        totalWritten += out.write(buf);
                    } while (totalWritten < size);
                    buf.clear();
                    size += thisRead;

                }

                extContext.setResponseContentLength(size);

            } finally {
                if (out != null) {
                    out.close();
                }
                if (resourceChannel != null) {
                    resourceChannel.close();
                }
            }
        } else {
            send304(context);
        }
    }

    private boolean isPortletResource(Resource res) {
        if (null == res || res instanceof PortletResource) {
            return true;
        } else {
            return false;
        }
    }

    protected void send404(FacesContext ctx, String resourceName, String libraryName) {

        ctx.getExternalContext().setResponseStatus(HttpServletResponse.SC_NOT_FOUND);

    }

    protected void send304(FacesContext ctx) {

        ctx.getExternalContext().setResponseStatus(HttpServletResponse.SC_NOT_MODIFIED);

    }

    protected void handleHeaders(FacesContext ctx, Resource resource) {

        ExternalContext extContext = ctx.getExternalContext();
        for (Map.Entry<String, String> cur : resource.getResponseHeaders().entrySet()) {
            extContext.setResponseHeader(cur.getKey(), cur.getValue());
        }

    }

}
