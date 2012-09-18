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
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class RichFacesResourceHandler extends ResourceWrapper {

    public static final String RICHFACES_PATH_TOKEN = "/rfRes/";

    // Private Data Members
    private Resource wrappedResource;

    public RichFacesResourceHandler(Resource resource) {
        this.wrappedResource = resource;
    }

    @Override
    public String getRequestPath() {

        String requestPath = super.getRequestPath();

        if (requestPath != null) {

            // If the /rfRes/ token is found in the request path, then RichFaces has likely added a dynamic resource.
            // Such resources have not had the request path processed by ExternalContext.encodeResourceURL(String) and
            // are therefore incompatible with a portlet environment.
            int pos = requestPath.indexOf(RICHFACES_PATH_TOKEN);

            if (pos > 0) {

                // Some resources like fileUploadProgress will have an extension like ".xhtml" appended to them which
                // must be removed.
                requestPath = requestPath.replaceAll("[.]faces", "");
                requestPath = requestPath.replaceAll("[.]jsf", "");
                requestPath = requestPath.replaceAll("[.]xhtml", "");

                // Encode the request path as a portlet ResourceURL.
                FacesContext facesContext = FacesContext.getCurrentInstance();
                ExternalContext externalContext = facesContext.getExternalContext();
                StringBuilder buf = new StringBuilder();
                buf.append("/javax.faces.resource/");
                buf.append(requestPath.substring(pos + RICHFACES_PATH_TOKEN.length()));
                requestPath = externalContext.encodeResourceURL(buf.toString());
            }
        }

        return requestPath;
    }

    @Override
    public Resource getWrapped() {
        return wrappedResource;
    }

}
