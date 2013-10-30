/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.renderkit.portlet;

import com.sun.faces.util.HtmlUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Portlet specific writer to be able to convert <code>&amp;</code>
 * to <code>&</code> within the generated url, as <code>&amp;</code> is unable to be interpreted
 * in a partial response and the script fails to load.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletPartialResponseWriter extends ResponseWriterWrapper {

    private ResponseWriter wrappedResponseWriter;

    public PortletPartialResponseWriter(ResponseWriter writer) {
        this.wrappedResponseWriter = writer;
    }

    @Override
    public ResponseWriter getWrapped() {
        return wrappedResponseWriter;
    }

    @Override
    public void endElement(String name) throws IOException {
        super.write("></" + name + ">");
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.write("<" + name);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        super.write(" " + name + "=\"" + value + "\"");
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        if (value != null && (value instanceof String)) {
            StringWriter writer = new StringWriter();
            String url = (String) value;
            char[] buff = new char[url.length() * 2];

            HtmlUtils.writeURL(writer, url, buff , wrappedResponseWriter.getCharacterEncoding());

            writer.flush();

            String encodedUrl = writer.toString();
            if (encodedUrl != null) {
                encodedUrl = encodedUrl.replaceAll("[&]amp;", "&");
                writeAttribute(name, encodedUrl, property);
            }
        } else {
            super.writeURIAttribute(name, value, property);
        }
    }
}
