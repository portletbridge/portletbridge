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
package org.jboss.portletbridge.richfaces.context;

import java.io.File;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portletbridge.richfaces.request.HttpServletRequestAdapter;
import org.jboss.portletbridge.richfaces.request.MultipartResourceRequest25;
import org.jboss.portletbridge.richfaces.request.MultipartResourceRequestSizeExceeded;
import org.richfaces.request.MultipartRequest;
import org.richfaces.request.MultipartRequestParser;
import org.richfaces.request.ProgressControl;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class FileUploadFacesContextFactory extends FacesContextFactory implements FacesWrapper<FacesContextFactory> {

    private FacesContextFactory wrappedFactory;

    public FileUploadFacesContextFactory(FacesContextFactory wrappedFactory) {
        super();
        this.wrappedFactory = wrappedFactory;
    }

    @Override
    public FacesContextFactory getWrapped() {
        return this.wrappedFactory;
    }

    /**
     * @see javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object, java.lang.Object, java.lang.Object,
     *      javax.faces.lifecycle.Lifecycle)
     */
    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException {

        if (request instanceof ClientDataRequest) {
            ClientDataRequest clientRequest = (ClientDataRequest) request;
            if (null != clientRequest.getContentType() && clientRequest.getContentType().startsWith("multipart/")) {
                String uid = clientRequest.getParameter(org.richfaces.context.FileUploadFacesContextFactory.UID_KEY);

                if (null != uid) {
                    long contentLength = clientRequest.getContentLength();

                    ProgressControl progressControl = new ProgressControl(uid, contentLength);

                    HttpServletRequest wrappedRequest = wrapMultipartRequestServlet25((PortletContext) context, clientRequest,
                            uid, contentLength, progressControl);

                    FacesContext facesContext = wrappedFactory.getFacesContext(context, wrappedRequest, response, lifecycle);
                    progressControl.setContextMap(facesContext.getExternalContext().getSessionMap());
                    return new FileUploadFacesContext(facesContext);
                }
            }
        }
        return wrappedFactory.getFacesContext(context, request, response, lifecycle);
    }

    private HttpServletRequest wrapMultipartRequestServlet25(PortletContext portletContext, ClientDataRequest request,
            String uploadId, long contentLength, ProgressControl progressControl) {

        HttpServletRequest multipartRequest;
        HttpServletRequestAdapter adapter = new HttpServletRequestAdapter(request);

        long maxRequestSize = getMaxRequestSize(portletContext);
        if (maxRequestSize == 0 || contentLength <= maxRequestSize) {
            boolean createTempFiles = isCreateTempFiles(portletContext);
            String tempFilesDirectory = getTempFilesDirectory(portletContext);

            MultipartRequestParser requestParser = new MultipartRequestParser(adapter, createTempFiles, tempFilesDirectory,
                    progressControl);

            multipartRequest = new MultipartResourceRequest25((ResourceRequest)request, adapter, uploadId, progressControl, requestParser);
        } else {
            multipartRequest = new MultipartResourceRequestSizeExceeded((ResourceRequest)request, adapter, uploadId, progressControl);
        }

        request.setAttribute(MultipartRequest.REQUEST_ATTRIBUTE_NAME, multipartRequest);

        return multipartRequest;
    }

    private long getMaxRequestSize(PortletContext portletContext) {
        String param = portletContext.getInitParameter("org.richfaces.fileUpload.maxRequestSize");
        if (param != null) {
            return Long.parseLong(param);
        }

        return 0;
    }

    private boolean isCreateTempFiles(PortletContext portletContext) {
        String param = portletContext.getInitParameter("org.richfaces.fileUpload.createTempFiles");
        if (param != null) {
            return Boolean.parseBoolean(param);
        }

        return true;
    }

    private String getTempFilesDirectory(PortletContext portletContext) {
        String result = portletContext.getInitParameter("org.richfaces.fileUpload.tempFilesDirectory");
        if (result == null) {
            File servletTempDir = (File) portletContext.getAttribute("javax.servlet.context.tempdir");
            if (servletTempDir != null) {
                result = servletTempDir.getAbsolutePath();
            }
        }
        if (result == null) {
            result = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        }

        return result;
    }

    private static final class FileUploadFacesContext extends FacesContextWrapper {
        private FacesContext wrappedContext;

        public FileUploadFacesContext(FacesContext facesContext) {
            super();
            this.wrappedContext = facesContext;
        }

        @Override
        public FacesContext getWrapped() {
            return wrappedContext;
        }

        @Override
        public void release() {
            MultipartRequest multipartRequest = (MultipartRequest) getExternalContext().getRequestMap().get(
                    MultipartRequest.REQUEST_ATTRIBUTE_NAME);

            if (multipartRequest != null) {
                multipartRequest.release();
            }

            super.release();
        }
    }

}
