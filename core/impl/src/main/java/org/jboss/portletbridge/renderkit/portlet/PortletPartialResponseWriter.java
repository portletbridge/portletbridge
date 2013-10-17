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

import org.jboss.portletbridge.io.FastBufferWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

/**
 * Portlet specific {@link PartialResponseWriter} that will use a different underlying writer
 * when a <code>script</code> element is found. This is done to be able to convert <code>&amp;</code>
 * to <code>&</code> within the generated url, as <code>&amp;</code> is unable to be interpreted
 * in a partial response and the script fails to load.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletPartialResponseWriter extends PartialResponseWriter {

    private static final String SCRIPT_ELEMENT = "script";

    private boolean isScriptElement = false;
    private ResponseWriter scriptTempResponseWriter;
    private FastBufferWriter scriptWriter;

    public PortletPartialResponseWriter(ResponseWriter writer) {
        super(writer);
    }

    /**
     * If a <code>script</code> element is ending, call endElement() on the cloned writer,
     * then retrieve the written content from the underlying writer so that we can convert
     * <code>&amp;</code> to <code>&</code>. Lastly we write the modified content as a
     * String to the real {@link PartialResponseWriter}.
     *
     * @param name
     * @throws IOException
     */
    @Override
    public void endElement(String name) throws IOException {
        if (isScriptElement && SCRIPT_ELEMENT.equals(name)) {
            scriptTempResponseWriter.endElement(name);

            // Retrieve the captured content and replace characters as needed before writing result
            String result = scriptWriter.toString();
            result = result.replace("&amp;", "&");
            super.write(result);

            // Reset variables
            isScriptElement = false;
            scriptWriter = null;
            scriptTempResponseWriter = null;
        } else {
            super.endElement(name);
        }
    }

    /**
     * If a <code>script</code> element is beginning, create a new underlying writer and
     * clone the wrapped {@link ResponseWriter} with the new underlying writer. Call startElement()
     * on the cloned writer.
     *
     * @param name
     * @param component
     * @throws IOException
     */
    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if (!isScriptElement && SCRIPT_ELEMENT.equals(name)) {
            scriptWriter = new FastBufferWriter();
            scriptTempResponseWriter = super.cloneWithWriter(scriptWriter);
            scriptTempResponseWriter.startElement(name, component);
            isScriptElement = true;
        } else {
            super.startElement(name, component);
        }
    }

    /**
     * If we're currently processing a <code>script</code> element, then call writeAttribute() on
     * the cloned writer and not our parent.
     *
     * @param name
     * @param value
     * @param property
     * @throws IOException
     */
    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (isScriptElement) {
            scriptTempResponseWriter.writeAttribute(name, value, property);
        } else {
            super.writeAttribute(name, value, property);
        }
    }
}
