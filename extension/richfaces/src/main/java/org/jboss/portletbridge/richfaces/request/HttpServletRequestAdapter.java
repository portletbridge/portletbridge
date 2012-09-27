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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.portlet.filter.PortletRequestWrapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class HttpServletRequestAdapter extends PortletRequestWrapper implements HttpServletRequest {

    private PortletRequest request;

    public HttpServletRequestAdapter(PortletRequest request) {
        super(request);
        this.request = request;
    }

    /**
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {
        if (request instanceof ClientDataRequest) {
            return ((ClientDataRequest) request).getCharacterEncoding();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        if (request instanceof ClientDataRequest) {
            ((ClientDataRequest) request).setCharacterEncoding(enc);
        }
    }

    /**
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    @Override
    public int getContentLength() {
        if (request instanceof ClientDataRequest) {
            return ((ClientDataRequest) request).getContentLength();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getContentType()
     */
    @Override
    public String getContentType() {
        if (request instanceof ClientDataRequest) {
            return ((ClientDataRequest) request).getContentType();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (request instanceof ClientDataRequest) {
            return new ServletInputStreamAdapter((ClientDataRequest) request);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        if (request instanceof ClientDataRequest) {
            return ((ClientDataRequest) request).getReader();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
     */
    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    @Override
    public int getLocalPort() {
        return request.getServerPort();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
     */
    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
     */
    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    @Override
    public String getMethod() {
        if (request instanceof ClientDataRequest) {
            return ((ClientDataRequest) request).getMethod();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     */
    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

}
