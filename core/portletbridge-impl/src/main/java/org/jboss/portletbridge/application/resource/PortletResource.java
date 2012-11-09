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

import java.util.List;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * @author leo, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletResource extends ResourceWrapper {

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

    @Override
    public String getLibraryName() {
        return wrapped.getLibraryName();
    }

    @Override
    public String getResourceName() {
        return wrapped.getResourceName();
    }

    @Override
    public void setContentType(String contentType) {
        wrapped.setContentType(contentType);
    }

    @Override
    public void setLibraryName(String libraryName) {
        wrapped.setLibraryName(libraryName);
    }

    @Override
    public void setResourceName(String resourceName) {
        wrapped.setResourceName(resourceName);
    }

    @Override
    public String getContentType() {
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

        if (null != wrappedPath) {
            if (wrappedPath.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
                List<String> servletMappings = BridgeContext.getCurrentInstance().getBridgeConfig().getFacesServletMappings();
                //TODO Handle this nicer? Maybe cache extension mappings on startup
                for (String mapping : servletMappings) {
                    if (mapping.startsWith("*.")) {
                        mapping = mapping.substring(1);
                        String libraryToken = mapping + "?ln";
                        int pos = wrappedPath.indexOf(libraryToken);
                        if (pos > 0) {
                            wrappedPath = wrappedPath.substring(0, pos) + wrappedPath.substring(pos + mapping.length());
                        }
                    }
                }
            }
        }
        return externalContext.encodeResourceURL(wrappedPath);
    }

}
