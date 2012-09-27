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
package org.jboss.portletbridge.richfaces.request;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.richfaces.model.UploadedFile;
import org.richfaces.request.MultipartRequest25;
import org.richfaces.request.MultipartRequestParser;
import org.richfaces.request.ProgressControl;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class MultipartResourceRequest25 extends MultipartResourceRequestSizeExceeded {

    private MultipartRequest25 multipartRequest;

    public MultipartResourceRequest25(ResourceRequest resourceRequest, HttpServletRequest request, String uploadId,
            ProgressControl progressControl, MultipartRequestParser requestParser) {
        super(resourceRequest, request, uploadId, progressControl);

        this.multipartRequest = new MultipartRequest25(request, uploadId, progressControl, requestParser);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Enumeration getParameterNames() {
        return multipartRequest.getParameterNames();
    }

    @Override
    public String getParameter(String name) {
        return multipartRequest.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return multipartRequest.getParameterValues(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map getParameterMap() {
        return multipartRequest.getParameterMap();
    }

    public Iterable<UploadedFile> getUploadedFiles() {
        return multipartRequest.getUploadedFiles();
    }

    public void release() {
        super.release();
        multipartRequest.release();
    }

    public ResponseState getResponseState() {
        return multipartRequest.getResponseState();
    }

}
