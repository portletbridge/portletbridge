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

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.context.PortletExternalContextImpl;

/**
 * @author leo
 */
public class PortletResource extends ResourceWrapper {

    public static final String LIBRARY_NAME = "ln";
    private final Resource wrapped;

    public PortletResource(Resource wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * @see javax.faces.application.ResourceWrapper#getWrapped()
     */
    @Override
    public Resource getWrapped() {
        return wrapped;
    }

    public String getLibraryName() {
        return wrapped.getLibraryName();
    }

    public String getResourceName() {
        return wrapped.getResourceName();
    }

    public void setContentType(String contentType) {
        wrapped.setContentType(contentType);
    }

    public void setLibraryName(String libraryName) {
        wrapped.setLibraryName(libraryName);
    }

    public void setResourceName(String resourceName) {
        wrapped.setResourceName(resourceName);
    }

    @Override
    public String getContentType() {
        // ResourceWrapper does not delegate this method
        return wrapped.getContentType();
    }

    /**
     * @see javax.faces.application.Resource#getRequestPath()
     */
    @Override
    public String getRequestPath() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        String wrappedPath = wrapped.getRequestPath();

        if (externalContext.getRequestMap().containsKey(Bridge.PORTLET_LIFECYCLE_PHASE)) {
            if (externalContext.getResponse() instanceof MimeResponse) {
                MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
                ResourceURL resourceURL = mimeResponse.createResourceURL();
                resourceURL.setResourceID(ResourceHandler.RESOURCE_IDENTIFIER);
                resourceURL.setParameter(PortletResourceHandler.RESOURCE_ID, getWrapped().getResourceName());
                String libraryName = getWrapped().getLibraryName();
                if (null != libraryName) {
                    resourceURL.setParameter(PortletResourceHandler.LIBRARY_ID, libraryName);
                }
                String contentType = getWrapped().getContentType();
                if (null != contentType) {
                    resourceURL.setParameter(PortletResourceHandler.MIME_PARAM, contentType);
                }
                return resourceURL.toString();
            } else {
                return PortletExternalContextImpl.RESOURCE_URL_DO_NOTHITG;
            }
        } else {
            return wrappedPath;
        }
    }

}
