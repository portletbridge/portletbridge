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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.faces.BridgeWriteBehindResponse;
import javax.portlet.filter.PortletResponseWrapper;

import org.jboss.portletbridge.io.FastBufferOutputStream;
import org.jboss.portletbridge.io.FastPrintWriter;

/**
 * @author asmirnov, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class BufferedMimeResponseWrapper extends PortletResponseWrapper implements MimeResponse, BridgeWriteBehindResponse {

    protected FastBufferOutputStream fastBufferStream = null;

    protected FastPrintWriter fastPrintWriter = null;

    private boolean hasWriteBehindMarkup = false;

    public BufferedMimeResponseWrapper(MimeResponse response) {
        super(response);
    }

    @Override
    public MimeResponse getResponse() {
        return (MimeResponse) super.getResponse();
    }

    /**
     * @return
     * @throws IOException
     * @see javax.portlet.RenderResponse#getPortletOutputStream()
     */
    public FastBufferOutputStream getPortletOutputStream() throws IOException {
        if (null != fastPrintWriter) {
            throw new IllegalStateException();
        }

        if (null == fastBufferStream) {
            fastBufferStream = new FastBufferOutputStream();
        }

        return fastBufferStream;
    }

    /**
     * @return
     * @throws IOException
     * @see javax.portlet.RenderResponse#getWriter()
     */
    public FastPrintWriter getWriter() throws IOException {
        if (null != fastBufferStream) {
            throw new IllegalStateException();
        }

        if (null == fastPrintWriter) {
            fastPrintWriter = new FastPrintWriter();
        }

        return fastPrintWriter;
    }

    public void resetBuffers() {
        if (fastBufferStream != null) {
            fastBufferStream.reset();
        }

        if (fastPrintWriter != null) {
            fastPrintWriter.reset();
        }
    }

    /**
     * @see javax.portlet.RenderResponse#reset()
     */
    public void reset() {
        getResponse().reset();

        resetBuffers();
    }

    /**
     * @see javax.portlet.RenderResponse#resetBuffer()
     */
    public void resetBuffer() {
        if (!isCommitted()) {
            getResponse().resetBuffer();
        }
        resetBuffers();
    }

    /**
     * @throws IOException
     * @see javax.portlet.RenderResponse#flushBuffer()
     */
    public void flushBuffer() throws IOException {
        getResponse().flushBuffer();
    }

    public void writeBufferedData() throws IOException {
        if (fastBufferStream != null) {
            OutputStream outputStream = getResponse().getPortletOutputStream();
            fastBufferStream.writeTo(outputStream);
            outputStream.flush();
        } else if (fastPrintWriter != null) {
            PrintWriter writer = getResponse().getWriter();
            fastPrintWriter.writeTo(writer);
            writer.flush();
        }
    }

    public String toString() {
        if (fastBufferStream != null) {
            return "Stream content: " + fastBufferStream.toString();
        } else if (fastPrintWriter != null) {
            return "Writer content: " + fastPrintWriter.toString();
        } else {
            return super.toString() + ", no data written";
        }
    }

    public boolean isUseWriter() {
        return fastBufferStream == null;
    }

    public PortletURL createActionURL() {
        return getResponse().createActionURL();
    }

    public PortletURL createRenderURL() {
        return getResponse().createRenderURL();
    }

    public ResourceURL createResourceURL() {
        return getResponse().createResourceURL();
    }

    public int getBufferSize() {
        return getResponse().getBufferSize();
    }

    public CacheControl getCacheControl() {
        return getResponse().getCacheControl();
    }

    public String getCharacterEncoding() {
        return getResponse().getCharacterEncoding();
    }

    public String getContentType() {
        return getResponse().getCharacterEncoding();
    }

    public Locale getLocale() {
        return getResponse().getLocale();
    }

    public boolean isCommitted() {
        return getResponse().isCommitted();
    }

    public void setBufferSize(int size) {
        getResponse().setBufferSize(size);

    }

    public void setContentType(String type) {
        getResponse().setContentType(type);

    }

    /**
     * Hook method for ajax rendering.
     *
     * @param name
     * @param value
     */
    public void setHeader(String name, String value) {
        setProperty(name, value);
    }

    @Override
    public boolean isChars() {
        return null != fastPrintWriter;
    }

    @Override
    public char[] getChars() {
        if (isChars()) {
            fastPrintWriter.flush();
            return fastPrintWriter.toCharArray();
        }
        return null;
    }

    @Override
    public boolean isBytes() {
        return null != fastBufferStream;
    }

    @Override
    public byte[] getBytes() {
        if (isBytes()) {
            return fastBufferStream.toByteArray();
        }
        return null;
    }

    public void flushContentToWrappedResponse() throws IOException {
        hasWriteBehindMarkup = true;
        flushMarkupToWrappedResponse();
    }

    public void flushToWrappedResponse() throws IOException {
        hasWriteBehindMarkup = true;
        flushMarkupToWrappedResponse();
    }

    @Override
    public boolean hasFacesWriteBehindMarkup() {
        return hasWriteBehindMarkup;
    }
}