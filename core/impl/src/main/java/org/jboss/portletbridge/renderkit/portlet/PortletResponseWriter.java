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

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletResponseWriter extends ResponseWriterWrapper {

    private ResponseWriter wrappedResponseWriter;


    public PortletResponseWriter(ResponseWriter parent) {
        this.wrappedResponseWriter = parent;
    }

    @Override
    public ResponseWriter getWrapped() {
        return wrappedResponseWriter;
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return new PortletResponseWriter(wrappedResponseWriter.cloneWithWriter(writer));
    }

    @Override
    public void endElement(String name) throws IOException {
        super.endElement(name);
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.startElement(name, component);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        super.writeAttribute(name, value, property);
    }
}
