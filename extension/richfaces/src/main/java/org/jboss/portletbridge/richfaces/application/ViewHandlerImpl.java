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
package org.jboss.portletbridge.richfaces.application;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class ViewHandlerImpl extends ViewHandlerWrapper {

    ViewHandler parent;

    /**
     * @param parent
     */
    public ViewHandlerImpl(ViewHandler parent) {
        super();
        this.parent = parent;
    }

    @Override
    public String getResourceURL(FacesContext context, String path) {
        //Work around to prevent RF from pre-pending web context onto portal url.
        if (null != path) {
            Object resp = FacesContext.getCurrentInstance().getExternalContext().getResponse();
            if (resp instanceof MimeResponse) {
                String url = ((MimeResponse)resp).createResourceURL().toString();
                if (null != url) {
                    int pos = url.indexOf("?");
                    if (pos > 0) {
                        url = url.substring(0, pos);
                    }
                    if (path.startsWith(url)) {
                        return path;
                    }
                }
            }
        }
        return super.getResourceURL(context, path);
    }

    /**
     * @see javax.faces.application.ViewHandlerWrapper#getWrapped()
     */
    @Override
    public ViewHandler getWrapped() {
        return this.parent;
    }

}
