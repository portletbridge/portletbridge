/******************************************************************************
 * $Id$
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletContext;

/**
 * Stub {@link ExternalContext} implementation. The only context operations are implemented.
 * 
 * @author asmirnov
 * 
 */
public class InitExternalContext extends AbstractExternalContext {

    public InitExternalContext(PortletContext context) {
        super(context, null, null);
    }

    @Override
    public PortletContext getContext() {
        return (PortletContext) super.getContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.portletbridge.context.AbstractExternalContext#createResourceUrl(org.jboss.portletbridge.context.PortalActionURL
     * )
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#enumerateRequestParameterNames()
     */
    @Override
    protected Enumeration<String> enumerateRequestParameterNames() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getContextAttribute(java.lang.String)
     */
    @Override
    protected Object getContextAttribute(String name) {
        return getContext().getAttribute(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getContextAttributeNames()
     */
    @Override
    protected Enumeration<String> getContextAttributeNames() {
        return getContext().getAttributeNames();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getInitParametersNames()
     */
    @Override
    protected Enumeration<String> getInitParametersNames() {
        return getContext().getInitParameterNames();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getNamespace()
     */
    @Override
    protected String getNamespace() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestAttribute(java.lang.String)
     */
    @Override
    protected Object getRequestAttribute(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestAttributeNames()
     */
    @Override
    protected Enumeration<String> getRequestAttributeNames() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestHeader(java.lang.String)
     */
    @Override
    protected String getRequestHeader(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestHeaderNames()
     */
    @Override
    protected Enumeration<String> getRequestHeaderNames() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestHeaderValues(java.lang.String)
     */
    @Override
    protected String[] getRequestHeaderValues(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestParameter(java.lang.String)
     */
    @Override
    protected String getRequestParameter(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getRequestParameterValues(java.lang.String)
     */
    @Override
    protected String[] getRequestParameterValues(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getSessionAttribute(java.lang.String)
     */
    @Override
    protected Object getSessionAttribute(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#getSessionAttributeNames()
     */
    @Override
    protected Enumeration<String> getSessionAttributeNames() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#removeContextAttribute(java.lang.String)
     */
    @Override
    protected void removeContextAttribute(String name) {
        getContext().removeAttribute(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#removeRequestAttribute(java.lang.String)
     */
    @Override
    protected void removeRequestAttribute(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#removeSessionAttribute(java.lang.String)
     */
    @Override
    protected void removeSessionAttribute(String name) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#setContextAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    protected void setContextAttribute(String name, Object value) {
        // mock context does not support that
        getContext().setAttribute(name, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#setRequestAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    protected void setRequestAttribute(String name, Object value) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.portletbridge.context.AbstractExternalContext#setSessionAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    protected void setSessionAttribute(String name, Object value) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#dispatch(java.lang.String)
     */
    @Override
    public void dispatch(String path) throws IOException {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getAuthType()
     */
    @Override
    public String getAuthType() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        // mock context does not support that
        return getContext().getInitParameter(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRequestContextPath()
     */
    @Override
    public String getRequestContextPath() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRequestLocale()
     */
    @Override
    public Locale getRequestLocale() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRequestLocales()
     */
    @Override
    public Iterator<Locale> getRequestLocales() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRequestPathInfo()
     */
    @Override
    public String getRequestPathInfo() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getRequestServletPath()
     */
    @Override
    public String getRequestServletPath() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String path) throws MalformedURLException {
        return getContext().getResource(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String path) {
        return getContext().getResourceAsStream(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getResourcePaths(java.lang.String)
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return getContext().getResourcePaths(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getSession(boolean)
     */
    @Override
    public Object getSession(boolean create) {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        // mock context does not support that
        throw new UnsupportedOperationException("this method is not supported at initialization phase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role) {
        // mock context does not support that
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#log(java.lang.String)
     */
    @Override
    public void log(String message) {
        // mock context does not support that
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#log(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void log(String message, Throwable exception) {
        // mock context does not support that
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.context.ExternalContext#redirect(java.lang.String)
     */
    @Override
    public void redirect(String url) throws IOException {
        // mock context does not support that
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

}
