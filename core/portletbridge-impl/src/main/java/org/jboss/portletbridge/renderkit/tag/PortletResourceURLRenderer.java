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
package org.jboss.portletbridge.renderkit.tag;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletResourceURLRenderer extends AbstractUrlRenderer {

    /**
     * @see org.jboss.portletbridge.renderkit.tag.AbstractUrlRenderer#createUrl(javax.portlet.MimeResponse)
     */
    @Override
    protected BaseURL createUrl(MimeResponse mimeResponse) {
        return mimeResponse.createResourceURL();
    }

    /**
     * @see org.jboss.portletbridge.renderkit.tag.AbstractUrlRenderer#setParameters(javax.portlet.BaseURL,
     *      javax.faces.component.UIComponent)
     */
    @Override
    protected void setParameters(BaseURL url, UIComponent component) throws Exception {
        Map<String, Object> attributes = component.getAttributes();
        ResourceURL resourceURL = (ResourceURL) url;
        String id = (String) attributes.get("id");
        String cacheability = (String) attributes.get("cacheability");

        if (null != id) {
            resourceURL.setResourceID(id);
        }

        if (null != cacheability) {
            resourceURL.setCacheability(cacheability);
        } else {
            resourceURL.setCacheability(ResourceURL.PAGE);
        }
    }

}
