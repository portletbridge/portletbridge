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
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;

/**
 * @author asmirnov
 *
 */
public class RenderPortletExternalContextImpl extends MimeExternalContextImpl {

    private Map<String, String[]> _requestParameters;

    public RenderPortletExternalContextImpl(PortletContext context, RenderRequest request, RenderResponse response) {
        super(context, request, response);
        _requestParameters = request.getParameterMap();
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        // Do nothing.
    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void redirect(String url) throws IOException {
        if (null == url) {
            throw new NullPointerException("Path to redirect is null");
        }
        PortalActionURL actionURL = new PortalActionURL(url);
        if ((!actionURL.isInContext(getRequestContextPath()) && null == actionURL
            .getParameter(Bridge.FACES_VIEW_ID_PARAMETER))
            || "true".equalsIgnoreCase(actionURL.getParameter(Bridge.DIRECT_LINK))) {
            // dispatch(url);

            // throw new IllegalStateException(
            // "Redirect to new url not at action phase");
        } else {
            // HACK - if page is in the context, just treat it as navigation
            // case
            internalRedirect(actionURL);
        }
    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
        return _requestParameters;
    }

    @Override
    public RenderRequest getRequest() {
        return (RenderRequest) super.getRequest();
    }

    @Override
    public RenderResponse getResponse() {
        return (RenderResponse) super.getResponse();
    }

    @Override
    protected String getRequestParameter(String name) {
        String[] retObj = getRequestParameterValues(name);
        if (retObj == null) {
            return super.getRequestParameter(name);
        }
        return retObj[0];
    }

    @Override
    protected Enumeration<String> enumerateRequestParameterNames() {
        Map<String, String[]> requestParameters = _requestParameters;
        if (null != requestParameters) {
            return Collections.enumeration(requestParameters.keySet());
        } else {
            return super.enumerateRequestParameterNames();
        }
    }

    @Override
    protected String[] getRequestParameterValues(String name) {
        Map<String, String[]> requestParameters = _requestParameters;
        if (null != requestParameters) {
            return requestParameters.get(name);
        } else {
            return super.getRequestParameterValues(name);
        }
    }

    @Override
    public void addResponseHeader(String name, String value) {
        if ("X-Portlet-Title".equals(name)) {
            this.getResponse().setTitle(value);
        } else {
            super.addResponseHeader(name, value);
        }
    }

    @Override
    public void setResponseHeader(String name, String value) {
        if ("X-Portlet-Title".equals(name)) {
            this.getResponse().setTitle(value);
        } else {
            super.setResponseHeader(name, value);
        }
    }
}
