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
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.servlet.http.HttpServletRequest;

/**
 * @author asmirnov
 *
 */
public class ResourceRequestExternalContextImpl extends MimeExternalContextImpl {

    /**
     * @param context
     * @param request
     * @param response
     */
    public ResourceRequestExternalContextImpl(PortletContext context, ResourceRequest request, ResourceResponse response) {
        super(context, request, response);
    }

    @Override
    public String getRequestCharacterEncoding() {
        // TODO - save character encoding from action request.
        return getRequest().getCharacterEncoding();
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        try {
            getRequest().setCharacterEncoding(encoding);
        } catch (IllegalStateException e) {
            // TODO: handle exception
        }
    }

    @Override
    public int getRequestContentLength() {
        return getRequest().getContentLength();
    }

    public void redirect(String url) throws IOException {
        if (null == url || url.length() < 0) {
            throw new NullPointerException("Path to redirect is null");
        }
        PortalActionURL actionURL = new PortalActionURL(url);
        if ((!actionURL.isInContext(getRequestContextPath()) && null == actionURL.getParameter(Bridge.FACES_VIEW_ID_PARAMETER))
                || "true".equalsIgnoreCase(actionURL.getParameter(Bridge.DIRECT_LINK))) {
            dispatch(actionURL.getPath());
        } else {
            internalRedirect(actionURL);
        }
    }

    public ResourceRequest getRequest() {
        return (ResourceRequest) super.getRequest();
    }

    public ResourceResponse getResponse() {
        return (ResourceResponse) super.getResponse();
    }

    private HttpServletRequest getMultipartRequest() {
        return (HttpServletRequest) getRequest().getAttribute("org.ajax4jsf.request.MultipartRequest");
    }

    @Override
    protected String getRequestParameter(String name) {
        HttpServletRequest multipartRequest = getMultipartRequest();
        if (multipartRequest != null) {
            return multipartRequest.getParameter(name);
        } else {
            return super.getRequestParameter(name);
        }
    }

    @Override
    protected String[] getRequestParameterValues(String name) {
        HttpServletRequest multipartRequest = getMultipartRequest();
        if (multipartRequest != null) {
            return multipartRequest.getParameterValues(name);
        } else {
            return super.getRequestParameterValues(name);
        }

    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
        HttpServletRequest multipartRequest = getMultipartRequest();
        if (multipartRequest != null) {
            return createParameterMap(multipartRequest);
        } else {
            return super.getRequestParameterValuesMap();
        }
    }

    @Override
    public void setResponseStatus(int statusCode) {
        ResourceResponse resourceResponse = (ResourceResponse) getResponse();
        resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(statusCode));
    }

    /**
     * ceate a parameter map out of the multi part request. Fix related to PBR-170
     *
     * @param multipartRequest the multipart request
     * @return value map of the parameters
     */
    @SuppressWarnings("unchecked")
    private Map<String, String[]> createParameterMap(HttpServletRequest multipartRequest) {
        Map<String, String[]> result = new HashMap<String, String[]>();
        Map<String, Object> multipartMap = multipartRequest.getParameterMap();
        for (String name : multipartMap.keySet()) {
            Object value = multipartMap.get(name);
            // This can happen because of an error in RF MultipartRequest
            // Line 666: params.put(name, vp.getValue()); as getValue can return a String
            if (value instanceof String) {
                result.put(name, new String[] { (String) value });
            } else if (value instanceof String[]) {
                result.put(name, (String[]) value);
            }
        }
        return result;
    }

}
