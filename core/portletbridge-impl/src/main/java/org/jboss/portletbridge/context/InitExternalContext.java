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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletContext;

import org.jboss.portletbridge.context.map.PortletApplicationMap;
import org.jboss.portletbridge.context.map.PortletInitParameterMap;

/**
 * Stub {@link ExternalContext} implementation. The only context operations are implemented.
 *
 * @author asmirnov
 */
public class InitExternalContext extends ExternalContext {

    private PortletContext portletContext;
    private Map<String, Object> applicationMap = null;
    private Map<String, String> initParameterMap = null;

    public InitExternalContext(PortletContext context) {
        super();
        this.portletContext = context;
    }

    @Override
    public PortletContext getContext() {
        return portletContext;
    }

    /**
     * @see javax.faces.context.ExternalContext#dispatch(java.lang.String)
     */
    @Override
    public void dispatch(String path) throws IOException {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getAuthType()
     */
    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        return getContext().getInitParameter(name);
    }

    /**
     * @see javax.faces.context.ExternalContext#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestContextPath()
     */
    @Override
    public String getRequestContextPath() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocale()
     */
    @Override
    public Locale getRequestLocale() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestLocales()
     */
    @Override
    public Iterator<Locale> getRequestLocales() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestPathInfo()
     */
    @Override
    public String getRequestPathInfo() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getRequestServletPath()
     */
    @Override
    public String getRequestServletPath() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String path) throws MalformedURLException {
        return getContext().getResource(path);
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String path) {
        return getContext().getResourceAsStream(path);
    }

    /**
     * @see javax.faces.context.ExternalContext#getResourcePaths(java.lang.String)
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return getContext().getResourcePaths(path);
    }

    /**
     * @see javax.faces.context.ExternalContext#getSession(boolean)
     */
    @Override
    public Object getSession(boolean create) {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /**
     * @see javax.faces.context.ExternalContext#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String)
     */
    @Override
    public void log(String message) {
    }

    /**
     * @see javax.faces.context.ExternalContext#log(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void log(String message, Throwable exception) {
    }

    /**
     * @see javax.faces.context.ExternalContext#redirect(java.lang.String)
     */
    @Override
    public void redirect(String url) throws IOException {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    @Override
    public String encodeActionURL(String url) {
        return url;
    }

    @Override
    public String encodeResourceURL(String url) {
        return url;
    }

    @Override
    public String encodeNamespace(String name) {
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    @Override
    public Map<String, Object> getApplicationMap() {
        if (null == applicationMap) {
            applicationMap = new PortletApplicationMap(portletContext);
        }
        return applicationMap;
    }

    @Override
    public Map<String, String> getInitParameterMap() {
        if (null == initParameterMap) {
            initParameterMap = new PortletInitParameterMap(portletContext);
        }
        return initParameterMap;
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Map<String, Object> getRequestCookieMap() {
        return Collections.unmodifiableMap(Collections.<String, Object> emptyMap());
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        return Collections.unmodifiableMap(Collections.<String, String> emptyMap());
    }

    @Override
    public Map<String, String[]> getRequestHeaderValuesMap() {
        return Collections.unmodifiableMap(Collections.<String, String[]> emptyMap());
    }

    @Override
    public Map<String, Object> getRequestMap() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getRequestParameterMap() {
        return Collections.unmodifiableMap(Collections.<String, String> emptyMap());
    }

    @Override
    public Iterator<String> getRequestParameterNames() {
        return Collections.<String> emptyList().iterator();
    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
        return Collections.unmodifiableMap(Collections.<String, String[]> emptyMap());
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public Map<String, Object> getSessionMap() {
        return Collections.emptyMap();
    }
}
