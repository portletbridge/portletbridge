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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.portletbridge.application.resource.PortletResource;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class RichFacesPortletResource extends ResourceWrapper implements Externalizable {
    public static final String RICHFACES_PATH_TOKEN = "/rfRes/";

    private Resource wrapped;

    /**
     * Necessary for serialization.
     */
    public RichFacesPortletResource() {
    }

    public RichFacesPortletResource(Resource resource) {
        wrapped = resource;
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

    @Override
    public String getRequestPath() {
        String path = wrapped.getRequestPath();
        if (null != path) {
            int pos = path.indexOf(RICHFACES_PATH_TOKEN);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            if (pos >= 0) {
                StringBuilder buf = new StringBuilder(150);
                if (path.contains("MediaOutputResource")) {
                    buf.append(path);
                } else {
                    buf.append(path.substring(0, pos));
                    buf.append(ResourceHandler.RESOURCE_IDENTIFIER);
                    buf.append("/");
                    buf.append(path.substring(pos + RICHFACES_PATH_TOKEN.length()));
                }
                path = buf.toString();
            }
            path = externalContext.encodeResourceURL(path);
        }
        return path;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(wrapped.getClass().getName());
        if (wrapped instanceof PortletResource) {
            ((PortletResource) wrapped).writeExternal(out);
        } else {
            out.writeObject(getResourceName());
            out.writeObject(getLibraryName());
            out.writeObject(getContentType());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Class<?> clazz = Class.forName((String)in.readObject());
        try {
            wrapped = (Resource) clazz.newInstance();
            if (wrapped instanceof PortletResource) {
                ((PortletResource) wrapped).readExternal(in);
            } else {
                setResourceName((String) in.readObject());
                setLibraryName((String) in.readObject());
                setContentType((String) in.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
