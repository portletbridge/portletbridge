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
package org.jboss.portletbridge.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

public class MultipartResourceRequest implements ResourceRequest {

    private ResourceRequest parent;
    private Map<String, String[]> parameters;

    public MultipartResourceRequest(ResourceRequest parent, Map<String, String[]> parameters) {
        this.parent = parent;
        this.parameters = new HashMap<String, String[]>();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        if (parent != null && parent.getParameterMap() != null && !parent.getParameterMap().isEmpty()) {
            this.parameters.putAll(parent.getParameterMap());
        }
    }

    public String getParameter(String arg0) {
        return parameters.containsKey(arg0) ? parameters.get(arg0)[0] : null;
    }

    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public String[] getParameterValues(String arg0) {
        return parameters.containsKey(arg0) ? parameters.get(arg0) : new String[] {};
    }

    public Object getAttribute(String arg0) {
        return parent.getAttribute(arg0);
    }

    public Enumeration<String> getAttributeNames() {
        return parent.getAttributeNames();
    }

    public String getAuthType() {
        return parent.getAuthType();
    }

    public String getCharacterEncoding() {
        return parent.getCharacterEncoding();
    }

    public int getContentLength() {
        return parent.getContentLength();
    }

    public String getContentType() {
        return parent.getContentType();
    }

    public String getContextPath() {
        return parent.getContextPath();
    }

    public Cookie[] getCookies() {
        return parent.getCookies();
    }

    public Locale getLocale() {
        return parent.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return parent.getLocales();
    }

    public String getMethod() {
        return parent.getMethod();
    }

    public PortalContext getPortalContext() {
        return parent.getPortalContext();
    }

    public InputStream getPortletInputStream() throws IOException {
        return parent.getPortletInputStream();
    }

    public PortletMode getPortletMode() {
        return parent.getPortletMode();
    }

    public PortletSession getPortletSession() {
        return parent.getPortletSession();
    }

    public PortletSession getPortletSession(boolean arg0) {
        return parent.getPortletSession(arg0);
    }

    public PortletPreferences getPreferences() {
        return parent.getPreferences();
    }

    public Map<String, String[]> getPrivateParameterMap() {
        return parent.getPrivateParameterMap();
    }

    public Enumeration<String> getProperties(String arg0) {
        return parent.getProperties(arg0);
    }

    public String getProperty(String arg0) {
        return parent.getProperty(arg0);
    }

    public Enumeration<String> getPropertyNames() {
        return parent.getPropertyNames();
    }

    public Map<String, String[]> getPublicParameterMap() {
        return parent.getPublicParameterMap();
    }

    public BufferedReader getReader() throws IOException {
        return parent.getReader();
    }

    public String getRemoteUser() {
        return parent.getRemoteUser();
    }

    public String getRequestedSessionId() {
        return parent.getRequestedSessionId();
    }

    public String getResponseContentType() {
        return parent.getResponseContentType();
    }

    public Enumeration<String> getResponseContentTypes() {
        return parent.getResponseContentTypes();
    }

    public String getScheme() {
        return parent.getScheme();
    }

    public String getServerName() {
        return parent.getServerName();
    }

    public int getServerPort() {
        return parent.getServerPort();
    }

    public Principal getUserPrincipal() {
        return parent.getUserPrincipal();
    }

    public String getWindowID() {
        return parent.getWindowID();
    }

    public WindowState getWindowState() {
        return parent.getWindowState();
    }

    public boolean isPortletModeAllowed(PortletMode arg0) {
        return parent.isPortletModeAllowed(arg0);
    }

    public boolean isRequestedSessionIdValid() {
        return parent.isRequestedSessionIdValid();
    }

    public boolean isSecure() {
        return parent.isSecure();
    }

    public boolean isUserInRole(String arg0) {
        return parent.isUserInRole(arg0);
    }

    public boolean isWindowStateAllowed(WindowState arg0) {
        return parent.isWindowStateAllowed(arg0);
    }

    public void removeAttribute(String arg0) {
        parent.removeAttribute(arg0);
    }

    public void setAttribute(String arg0, Object arg1) {
        parent.setAttribute(arg0, arg1);
    }

    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
        parent.setCharacterEncoding(arg0);
    }

    @Override
    public String getETag() {
        return parent.getETag();
    }

    @Override
    public String getResourceID() {
        return parent.getResourceID();
    }

    @Override
    public Map<String, String[]> getPrivateRenderParameterMap() {
        return parent.getPrivateParameterMap();
    }

    @Override
    public String getCacheability() {
        return parent.getCacheability();
    }

    // the hack.

    public ResourceActionRequest getAsActionRequest() {
        if (resourceActionRequest == null) {
            resourceActionRequest = new ResourceActionRequest(this);
        }
        return resourceActionRequest;
    }

    private ResourceActionRequest resourceActionRequest;

    protected class ResourceActionRequest implements ActionRequest {

        private ResourceRequest resourceRequest;

        public ResourceActionRequest(ResourceRequest resourceRequest) {
            this.resourceRequest = resourceRequest;
        }

        public InputStream getPortletInputStream() throws IOException {
            return resourceRequest.getPortletInputStream();
        }

        @Override
        public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
            resourceRequest.setCharacterEncoding(enc);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return resourceRequest.getReader();
        }

        @Override
        public String getCharacterEncoding() {
            return resourceRequest.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return resourceRequest.getContentType();
        }

        @Override
        public int getContentLength() {
            return resourceRequest.getContentLength();
        }

        @Override
        public String getMethod() {
            return resourceRequest.getMethod();
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
        public String getAuthType() {
            return resourceRequest.getAuthType();
        }

        @Override
        public String getContextPath() {
            return resourceRequest.getContextPath();
        }

        @Override
        public String getRemoteUser() {
            return resourceRequest.getRemoteUser();
        }

        @Override
        public Principal getUserPrincipal() {
            return resourceRequest.getUserPrincipal();
        }

        @Override
        public boolean isUserInRole(String role) {
            return resourceRequest.isUserInRole(role);
        }

        @Override
        public Object getAttribute(String name) {
            return resourceRequest.getAttribute(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return resourceRequest.getAttributeNames();
        }

        @Override
        public String getParameter(String name) {
            return resourceRequest.getParameter(name);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return resourceRequest.getParameterNames();
        }

        @Override
        public String[] getParameterValues(String name) {
            return resourceRequest.getParameterValues(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return resourceRequest.getParameterMap();
        }

        @Override
        public boolean isSecure() {
            return resourceRequest.isSecure();
        }

        @Override
        public void setAttribute(String name, Object o) {
            resourceRequest.setAttribute(name, o);
        }

        @Override
        public void removeAttribute(String name) {
            resourceRequest.removeAttribute(name);
        }

        @Override
        public String getRequestedSessionId() {
            return resourceRequest.getRequestedSessionId();
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return resourceRequest.isRequestedSessionIdValid();
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
        public Locale getLocale() {
            return resourceRequest.getLocale();
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return resourceRequest.getLocales();
        }

        @Override
        public String getScheme() {
            return resourceRequest.getScheme();
        }

        @Override
        public String getServerName() {
            return resourceRequest.getServerName();
        }

        @Override
        public int getServerPort() {
            return resourceRequest.getServerPort();
        }

        @Override
        public String getWindowID() {
            return resourceRequest.getWindowID();
        }

        @Override
        public Cookie[] getCookies() {
            return resourceRequest.getCookies();
        }

        @Override
        public Map<String, String[]> getPrivateParameterMap() {
            return resourceRequest.getPrivateParameterMap();
        }

        @Override
        public Map<String, String[]> getPublicParameterMap() {
            return resourceRequest.getPublicParameterMap();
        }

    }

}
