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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

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

    protected String getRequestHeader(String name) {
        if ("CONTENT-TYPE".equalsIgnoreCase(name)) {
            if (null == contentType) {
                constructContentType();
            }
            return contentType;
        }
        if ("CONTENT-LENGTH".equalsIgnoreCase(name)) {
            if (null == contentLength) {
                constructContentLength();
            }
            return contentLength;
        }

        return super.getRequestHeader(name);
    }

    protected Enumeration<String> getRequestHeaderNames() {
        List<String> names = new ArrayList<String>();
        Enumeration<String> propNames = super.getRequestHeaderNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            names.add(name);
        }
        names.add("CONTENT-TYPE");
        names.add("CONTENT-LENGTH");
        return Collections.enumeration(names);
    }

    protected String[] getRequestHeaderValues(String name) {
        if ("CONTENT-TYPE".equalsIgnoreCase(name)) {
            if (null == contentType) {
                constructContentType();
            }
            return new String[] { contentType };
        }
        if ("CONTENT-LENGTH".equalsIgnoreCase(name)) {
            if (null == contentLength) {
                constructContentLength();
            }
            return new String[] { contentLength };
        }

        return super.getRequestHeaderValues(name);
    }

    public void redirect(String url) throws IOException {
        if (null == url || url.length() < 0) {
            throw new NullPointerException("Path to redirect is null");
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext.getPartialViewContext().isPartialRequest()) {
            ResourceResponse resourceResponse = getResponse();
            resourceResponse.setContentType("text/xml");
            resourceResponse.setCharacterEncoding("UTF-8");

            PartialResponseWriter partialResponseWriter;
            ResponseWriter responseWriter = facesContext.getResponseWriter();

            if (responseWriter instanceof PartialResponseWriter) {
                partialResponseWriter = (PartialResponseWriter) responseWriter;
            } else {
                partialResponseWriter = facesContext.getPartialViewContext().getPartialResponseWriter();
            }

            partialResponseWriter.startDocument();
            partialResponseWriter.redirect(url);
            partialResponseWriter.endDocument();
            facesContext.responseComplete();
        } else {
            throw new UnsupportedEncodingException(
                    "Can only redirect during RESOURCE_PHASE if a JSF partial/Ajax request has been triggered");
        }
    }

    @Override
    public String encodeNamespace(String name) {
        // PBR-385 Don't add namespace to org.richfaces.extension otherwise JS is unable to process response
        if ("org.richfaces.extension".equalsIgnoreCase(name)) {
            return name;
        }
        return super.encodeNamespace(name);
    }

    public ResourceRequest getRequest() {
        return (ResourceRequest) super.getRequest();
    }

    public ResourceResponse getResponse() {
        return (ResourceResponse) super.getResponse();
    }

    @Override
    public void setResponseStatus(int statusCode) {
        ResourceResponse resourceResponse = (ResourceResponse) getResponse();
        resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(statusCode));
    }

}
