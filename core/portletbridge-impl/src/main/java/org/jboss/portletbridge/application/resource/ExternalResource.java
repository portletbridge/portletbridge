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
import java.net.URL;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

/**
 * @author leo
 *
 */
public class ExternalResource extends Resource {

    private final String path;

    public ExternalResource(String path) {
        this.path = path;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.application.Resource#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // this resource is never served.
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.application.Resource#getRequestPath()
     */
    @Override
    public String getRequestPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.application.Resource#getResponseHeaders()
     */
    @Override
    public Map<String, String> getResponseHeaders() {
        // this resource is never served.
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.application.Resource#getURL()
     */
    @Override
    public URL getURL() {
        // this resource is never used for composite components.
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.application.Resource#userAgentNeedsUpdate(javax.faces.context.FacesContext)
     */
    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        // this resource is never served.
        return false;
    }

}
