/******************************************************************************
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
package org.jboss.portletbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Wrap portlet request to simulate servlet environment.
 * @author asmirnov
 *
 */
public class PortletRequestWrapper implements HttpServletRequest {
    
    private PortletRequest wrapped;

    public PortletRequestWrapper(PortletRequest wrapped) {
    this.wrapped = wrapped;
    }

    public String getAuthType() {
   return wrapped.getAuthType();
    }

    public String getContextPath() {
   return null;
    }

    private static final Cookie[] noCookies = {};
    public Cookie[] getCookies() {
   return noCookies;
    }

    public long getDateHeader(String arg0) {
   return 0;
    }

    public String getHeader(String arg0) {
   return null;
    }

    public Enumeration getHeaderNames() {
   return Collections.enumeration(Collections.EMPTY_LIST);
    }

    public Enumeration getHeaders(String arg0) {
   return Collections.enumeration(Collections.EMPTY_LIST);
    }

    public int getIntHeader(String arg0) {
   return 0;
    }

    public String getMethod() {
   return "GET";
    }

    public String getPathInfo() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getPathTranslated() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getQueryString() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRemoteUser() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRequestURI() {
   // TODO Auto-generated method stub
   return null;
    }

    public StringBuffer getRequestURL() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRequestedSessionId() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getServletPath() {
   // TODO Auto-generated method stub
   return null;
    }

    public HttpSession getSession() {
   // TODO Auto-generated method stub
   return null;
    }

    public HttpSession getSession(boolean arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public Principal getUserPrincipal() {
   // TODO Auto-generated method stub
   return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
   // TODO Auto-generated method stub
   return false;
    }

    public boolean isRequestedSessionIdFromURL() {
   // TODO Auto-generated method stub
   return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
   // TODO Auto-generated method stub
   return false;
    }

    public boolean isRequestedSessionIdValid() {
   // TODO Auto-generated method stub
   return false;
    }

    public boolean isUserInRole(String arg0) {
   // TODO Auto-generated method stub
   return false;
    }

    public Object getAttribute(String arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public Enumeration getAttributeNames() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getCharacterEncoding() {
   // TODO Auto-generated method stub
   return null;
    }

    public int getContentLength() {
   // TODO Auto-generated method stub
   return 0;
    }

    public String getContentType() {
   // TODO Auto-generated method stub
   return null;
    }

    public ServletInputStream getInputStream() throws IOException {
   // TODO Auto-generated method stub
   return null;
    }

    public String getLocalAddr() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getLocalName() {
   // TODO Auto-generated method stub
   return null;
    }

    public int getLocalPort() {
   // TODO Auto-generated method stub
   return 0;
    }

    public Locale getLocale() {
   // TODO Auto-generated method stub
   return null;
    }

    public Enumeration getLocales() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getParameter(String arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public Map getParameterMap() {
   // TODO Auto-generated method stub
   return null;
    }

    public Enumeration getParameterNames() {
   // TODO Auto-generated method stub
   return null;
    }

    public String[] getParameterValues(String arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public String getProtocol() {
   // TODO Auto-generated method stub
   return null;
    }

    public BufferedReader getReader() throws IOException {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRealPath(String arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRemoteAddr() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getRemoteHost() {
   // TODO Auto-generated method stub
   return null;
    }

    public int getRemotePort() {
   // TODO Auto-generated method stub
   return 0;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
   // TODO Auto-generated method stub
   return null;
    }

    public String getScheme() {
   // TODO Auto-generated method stub
   return null;
    }

    public String getServerName() {
   // TODO Auto-generated method stub
   return null;
    }

    public int getServerPort() {
   // TODO Auto-generated method stub
   return 0;
    }

    public boolean isSecure() {
   // TODO Auto-generated method stub
   return false;
    }

    public void removeAttribute(String arg0) {
   // TODO Auto-generated method stub

    }

    public void setAttribute(String arg0, Object arg1) {
   // TODO Auto-generated method stub

    }

    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
   // TODO Auto-generated method stub

    }
   
   public PortletPreferences getPreferences() {
      return wrapped.getPreferences();
   }

    
    
}
