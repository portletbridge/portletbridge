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
package org.jboss.portletbridge.application.view;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.ResourceResponse;

/**
 * @author asmirnov
 */
public class BufferedResourceResponseWrapper extends BufferedMimeResponseWrapper implements ResourceResponse {

    /**
     * @param response
     */
    public BufferedResourceResponseWrapper(ResourceResponse response) {
        super(response);
    }

    @Override
    public ResourceResponse getResponse() {
        return (ResourceResponse) super.getResponse();
    }

    /**
     * @see javax.portlet.ResourceResponse#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String charset) {
        getResponse().setCharacterEncoding(charset);
    }

    /**
     * @see javax.portlet.ResourceResponse#setContentLength(int)
     */
    public void setContentLength(int len) {
        getResponse().setContentLength(len);
    }

    /**
     * @see javax.portlet.ResourceResponse#setLocale(java.util.Locale)
     */
    public void setLocale(Locale loc) {
        getResponse().setLocale(loc);
    }

    @Override
    public void flushMarkupToWrappedResponse() throws IOException {
        ResourceResponse response = getResponse();

        flushBuffer();

        if (isBytes()) {
            response.getPortletOutputStream().write(getBytes());
            fastBufferStream.reset();
        } else if (isChars()) {
            response.getWriter().write(getChars());
            fastPrintWriter.reset();
        }
    }
}
