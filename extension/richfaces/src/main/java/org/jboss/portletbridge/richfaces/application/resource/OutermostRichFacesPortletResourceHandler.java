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

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class OutermostRichFacesPortletResourceHandler extends ResourceHandlerWrapper {
    private static final String ORG_RICHFACES_RESOURCE = "org.richfaces.resource";

    private ResourceHandler wrapped;

    public OutermostRichFacesPortletResourceHandler(ResourceHandler resourceHandler) {
        wrapped = resourceHandler;
    }

    @Override
    public Resource createResource(String resourceName) {
        Resource resource = getWrapped().createResource(resourceName);
        if (null != resource && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
            resource = new RichFacesPortletResource(resource);
        }
        return resource;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = getWrapped().createResource(resourceName, libraryName);
        if (null != resource && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
            resource = new RichFacesPortletResource(resource);
        }
        return resource;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource resource = getWrapped().createResource(resourceName, libraryName, contentType);

        if (null != resource && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
            resource = new RichFacesPortletResource(resource);
        }
        return resource;
    }

    /**
     * @see javax.faces.application.ResourceHandlerWrapper#getWrapped()
     */
    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

}
