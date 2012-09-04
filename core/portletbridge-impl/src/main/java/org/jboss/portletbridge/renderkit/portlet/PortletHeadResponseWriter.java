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
package org.jboss.portletbridge.renderkit.portlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletHeadResponseWriter extends ResponseWriterWrapper {

    ResponseWriter wrapped;
    PortletResponse response;
    StringWriter writer;

    public PortletHeadResponseWriter(ResponseWriter parent, PortletResponse portletResponse) {
        this.writer = new StringWriter();
        this.wrapped = parent.cloneWithWriter(writer);
        this.response = portletResponse;
    }

    /**
     * @see javax.faces.context.ResponseWriterWrapper#getWrapped()
     */
    @Override
    public ResponseWriter getWrapped() {
        return wrapped;
    }

    @Override
    public void endElement(String name) throws IOException {
        super.endElement(name);

        StringBuffer temp = writer.getBuffer();

        try {
            Element elem = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder()
                                .parse(new ByteArrayInputStream(temp.toString().getBytes())).getDocumentElement();
            response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elem);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            temp.setLength(0);
        }
    }

}
