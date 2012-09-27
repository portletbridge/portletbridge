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
package org.jboss.portletbridge.richfaces.application.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeUtil;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portletbridge.application.resource.PortletResource;
import org.jboss.portletbridge.application.resource.PortletResourceHandler;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class RichFacesPortletResourceHandler extends PortletResourceHandler {

    private static final BridgeLogger logger = new JULLoggerImpl(RichFacesPortletResourceHandler.class.getName());

    private final ResourceHandler parent;

    public RichFacesPortletResourceHandler(ResourceHandler parent) {
        super(parent);
        this.parent = parent;
    }

    /**
     * @see javax.faces.application.ResourceHandlerWrapper#getWrapped()
     */
    @Override
    public ResourceHandler getWrapped() {
        return this.parent;
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        if (BridgeUtil.isPortletRequest()) {
            ExternalContext externalContext = context.getExternalContext();
            String resourceName = externalContext.getRequestParameterMap().get(RESOURCE_IDENTIFIER.substring(1));
            if (null != resourceName) {
                String libraryId = externalContext.getRequestParameterMap().get(LIBRARY_ID);
                ResourceHandler handler = context.getApplication().getResourceHandler();
                Resource resource = handler.createResource(resourceName, libraryId);
                if (null != resource) {
                    if (!isPortletResource(resource)) {
                        resource = new PortletResource(resource);
                    }
                    handleResourceRequest(context, resource);
                } else {
                    send404(context, resourceName, libraryId);
                }
            } else {
                send404(context, resourceName, null);
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
            InputStream in = null;

            int bufferSize = extContext.getResponseBufferSize();
            if (0 == bufferSize)
                bufferSize = 2048;
            ByteBuffer buf = ByteBuffer.allocate(bufferSize);
            extContext.setResponseBufferSize(buf.capacity());

            try {
                in = resource.getInputStream();
                if (in == null) {
                    send404(context, resource.getResourceName(), resource.getLibraryName());
                    return;
                }
                resourceChannel = Channels.newChannel(in);
                String contentType = resource.getContentType();
                if (contentType != null) {
                    extContext.setResponseContentType(resource.getContentType());
                }
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream(bufferSize);
                out = Channels.newChannel(byteArray);
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

                // Fix RichFaces URLs in Resources
                if (resource.getResourceName().indexOf(".css") > 0) {
                    String updatedCss = updateCssUrls(context, byteArray.toString());
                    size = updatedCss.length();
                    byteArray = new ByteArrayOutputStream(size);
                    byteArray.write(updatedCss.getBytes());
                }

                extContext.setResponseContentLength(size);
                extContext.setResponseStatus(HttpServletResponse.SC_OK);

                // Write out data
                byteArray.writeTo(extContext.getResponseOutputStream());
                byteArray.flush();
                byteArray.close();
            } finally {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
                if (null != resourceChannel) {
                    resourceChannel.close();
                }
            }
        } else {
            send304(context);
        }
    }

    protected String updateCssUrls(FacesContext context, String cssContent) {
        Map<String, String> urlCache = new HashMap<String, String>();
        ResourceHandler handler = context.getApplication().getResourceHandler();

        for (RichFacesUrlType rfUrlType : RichFacesUrlType.values()) {
            int urlStart = cssContent.indexOf(rfUrlType.getPathPrefix());

            while (urlStart > 0) {
                int fileNamePosStart = urlStart + rfUrlType.getPathPrefix().length();
                int period = cssContent.indexOf('.', fileNamePosStart);

                if (period > 0) {
                    int extEnd = cssContent.indexOf(')', period + 1);

                    String relPath = cssContent.substring(urlStart, extEnd);
                    String imageUrl = urlCache.get(relPath);
                    if (null == imageUrl) {
                        String resourceName = cssContent.substring(fileNamePosStart, extEnd);
                        String libraryName = rfUrlType.getLibraryName();
                        Resource imageResource = handler.createResource(resourceName, libraryName);
                        if (!isPortletResource(imageResource)) {
                            imageResource = new PortletResource(imageResource);
                        }
                        if (null != imageResource) {
                            imageUrl = imageResource.getRequestPath();
                            imageUrl = imageUrl.replaceAll(libraryName, rfUrlType.getToken());
                            urlCache.put(relPath, imageUrl);
                        } else {
                            // Shouldn't happen, but can when there are errors in resource mappings
                            imageUrl = relPath;
                            logger.log(Level.ERROR, "Unable to retrieve resource " + resourceName + " from library "
                                    + libraryName);
                        }
                    }

                    StringBuilder buf = new StringBuilder();
                    buf.append(cssContent.substring(0, urlStart));
                    buf.append(imageUrl);
                    buf.append(cssContent.substring(extEnd));
                    cssContent = buf.toString();
                } else {
                    // Filename not found
                }

                urlStart = cssContent.indexOf(rfUrlType.getPathPrefix(), fileNamePosStart);
            }
        }

        for (RichFacesUrlType rfUrlType : RichFacesUrlType.values()) {
            cssContent = cssContent.replaceAll(rfUrlType.getToken(), rfUrlType.getLibraryName());
        }

        return cssContent;
    }

    protected enum RichFacesUrlType {
        ONE("org.richfaces", "../../org.richfaces.images/", "rf-one"),
        TWO("org.richfaces", "../../", "rf-two"),
        THREE("org.richfaces.images", "../org.richfaces.images/", "rf-three"),
        FOUR("org.richfaces.images", "org.richfaces.images/", "rf-four");

        private String libraryName;
        private String pathPrefix;
        private String token;

        private RichFacesUrlType(String libraryName, String pathPrefix, String token) {
            this.libraryName = libraryName;
            this.pathPrefix = pathPrefix;
            this.token = token;
        }

        public String getLibraryName() {
            return libraryName;
        }

        public String getPathPrefix() {
            return pathPrefix;
        }

        public String getToken() {
            return token;
        }
    }

}
