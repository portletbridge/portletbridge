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
package org.richfaces.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

/**
 * Extend RichFaces {@link BaseMultipartRequest} and implement {@link ResourceRequest}.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@SuppressWarnings("unchecked")
public abstract class BaseMultipartResourceRequest extends BaseMultipartRequest implements ResourceRequest, ActionRequest {

    protected ResourceRequest resourceRequest;

    public BaseMultipartResourceRequest(ResourceRequest resourceRequest, HttpServletRequest request, String uploadId,
            ProgressControl progressControl) {
        super(request, uploadId, progressControl);
        this.resourceRequest = resourceRequest;
    }

    @Override
    public InputStream getPortletInputStream() throws IOException {
        return resourceRequest.getPortletInputStream();
    }

    @Override
    public boolean isWindowStateAllowed(WindowState state) {
        return resourceRequest.isWindowStateAllowed(state);
    }

    @Override
    public boolean isPortletModeAllowed(PortletMode mode) {
        return resourceRequest.isPortletModeAllowed(mode);
    }

    @Override
    public PortletMode getPortletMode() {
        return resourceRequest.getPortletMode();
    }

    @Override
    public WindowState getWindowState() {
        return resourceRequest.getWindowState();
    }

    @Override
    public PortletPreferences getPreferences() {
        return resourceRequest.getPreferences();
    }

    @Override
    public PortletSession getPortletSession() {
        return resourceRequest.getPortletSession();
    }

    @Override
    public PortletSession getPortletSession(boolean create) {
        return resourceRequest.getPortletSession(create);
    }

    @Override
    public String getProperty(String name) {
        return resourceRequest.getProperty(name);
    }

    @Override
    public Enumeration<String> getProperties(String name) {
        return resourceRequest.getProperties(name);
    }

    @Override
    public Enumeration<String> getPropertyNames() {
        return resourceRequest.getPropertyNames();
    }

    @Override
    public PortalContext getPortalContext() {
        return resourceRequest.getPortalContext();
    }

    @Override
    public String getWindowID() {
        return resourceRequest.getWindowID();
    }

    @Override
    public Map<String, String[]> getPrivateParameterMap() {
        return resourceRequest.getPrivateParameterMap();
    }

    @Override
    public Map<String, String[]> getPublicParameterMap() {
        return resourceRequest.getPublicParameterMap();
    }

    @Override
    public String getETag() {
        return resourceRequest.getETag();
    }

    @Override
    public String getResourceID() {
        return resourceRequest.getResourceID();
    }

    @Override
    public Map<String, String[]> getPrivateRenderParameterMap() {
        return resourceRequest.getPrivateRenderParameterMap();
    }

    @Override
    public String getResponseContentType() {
        return resourceRequest.getResponseContentType();
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        return resourceRequest.getResponseContentTypes();
    }

    @Override
    public String getCacheability() {
        return resourceRequest.getCacheability();
    }

}
