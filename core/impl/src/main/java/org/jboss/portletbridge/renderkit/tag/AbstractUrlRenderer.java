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

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import org.jboss.portletbridge.component.PortletParam;
import org.jboss.portletbridge.component.PortletProperty;

/**
 * Base Renderer for Portlet URL tags.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class AbstractUrlRenderer extends Renderer {

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ExternalContext externalContext = facesContext.getExternalContext();
        MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();

        Map<String, Object> attributes = component.getAttributes();
        Boolean escapeXML = (Boolean) attributes.get("escapeXml");
        Boolean secure = (Boolean) attributes.get("secure");
        String varName = (String) attributes.get("var");

        try {
            BaseURL url = createUrl(mimeResponse);

            if (null != secure) {
                url.setSecure(secure);
            }

            setParameters(url, component);

            List<UIComponent> children = component.getChildren();

            if (null != children) {
                for (UIComponent child : children) {
                    if (child instanceof PortletParam) {
                        PortletParam param = (PortletParam) child;
                        url.setParameter(param.getName(), (String) param.getValue());
                    } else if (child instanceof PortletProperty) {
                        PortletProperty prop = (PortletProperty) child;
                        url.addProperty(prop.getName(), (String) prop.getValue());
                    }
                }
            }

            String urlStr = encodePortletUrl(url, null != escapeXML ? escapeXML : true);

            if (null != varName) {
                externalContext.getRequestMap().put(varName, url);
            } else {
                ResponseWriter responseWriter = facesContext.getResponseWriter();
                responseWriter.write(urlStr);
            }

        } catch (Exception e) {
            throw new IOException(e.getMessage(), e.getCause());
        }
    }

    protected abstract BaseURL createUrl(MimeResponse mimeResponse);

    protected abstract void setParameters(BaseURL url, UIComponent component) throws Exception;

    protected String encodePortletUrl(BaseURL portletURL, boolean escape) throws IOException {
        StringWriter out = new StringWriter();
        portletURL.write(out, escape);
        return out.toString();
    }

    protected void setCommonParameters(PortletURL url, UIComponent component) throws Exception {
        Map<String, Object> attributes = component.getAttributes();
        String windowState = (String) attributes.get("windowState");
        String portletMode = (String) attributes.get("portletMode");
        Boolean copyCurrentRenderParameters = (Boolean) attributes.get("copyCurrentRenderParameters");

        if (null != windowState) {
            url.setWindowState(new WindowState(windowState));
        }

        if (null != portletMode) {
            url.setPortletMode(new PortletMode(portletMode));
        }

        if (null != copyCurrentRenderParameters && copyCurrentRenderParameters) {
            PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            Map<String, String[]> publicParameterMap = request.getPublicParameterMap();
            if (null != publicParameterMap) {
                url.setParameters(publicParameterMap);
            }
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
