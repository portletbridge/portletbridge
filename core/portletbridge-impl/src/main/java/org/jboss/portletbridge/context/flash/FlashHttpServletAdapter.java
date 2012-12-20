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
package org.jboss.portletbridge.context.flash;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class FlashHttpServletAdapter implements HttpServletResponse {

    PortletResponse wrapped;

    public FlashHttpServletAdapter(PortletResponse response) {
        wrapped = response;
    }

    @Override
    public String getCharacterEncoding() {
        if (wrapped instanceof MimeResponse) {
            return ((MimeResponse) wrapped).getCharacterEncoding();
        }
        return null;
    }

    @Override
    public String getContentType() {
        if (wrapped instanceof MimeResponse) {
            return ((MimeResponse) wrapped).getContentType();
        }
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public void write(int bytes) throws IOException {
                if (wrapped instanceof MimeResponse) {
                    ((MimeResponse)wrapped).getPortletOutputStream().write(bytes);
                }
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (wrapped instanceof MimeResponse) {
            return ((MimeResponse) wrapped).getWriter();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLength(int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentType(String type) {
        if (wrapped instanceof MimeResponse) {
            ((MimeResponse) wrapped).setContentType(type);
        }
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize() {
        if (wrapped instanceof MimeResponse) {
            return ((MimeResponse) wrapped).getBufferSize();
        }
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (wrapped instanceof MimeResponse) {
            ((MimeResponse) wrapped).flushBuffer();
        }
    }

    @Override
    public void resetBuffer() {
        if (wrapped instanceof MimeResponse) {
            ((MimeResponse) wrapped).resetBuffer();
        }
    }

    @Override
    public boolean isCommitted() {
        if (wrapped instanceof MimeResponse) {
            return ((MimeResponse) wrapped).isCommitted();
        }
        return false;
    }

    @Override
    public void reset() {
        if (wrapped instanceof MimeResponse) {
            ((MimeResponse) wrapped).reset();
        }
    }

    @Override
    public void setLocale(Locale loc) {
        if (wrapped instanceof ResourceResponse) {
            ((ResourceResponse) wrapped).setLocale(loc);
        }
    }

    @Override
    public Locale getLocale() {
        if (wrapped instanceof ResourceResponse) {
            return ((ResourceResponse) wrapped).getLocale();
        }
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
        wrapped.addProperty(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeURL(String url) {
        return wrapped.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl(String url) {
        return wrapped.encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader(String name, long date) {
    }

    @Override
    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void setStatus(int sc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

}
