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

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletHeadResponseWriter extends ResponseWriterWrapper {

    ResponseWriter wrapped;
    PortletResponse response;

    private Stack<Element> elements;
    private boolean preventSelfClosing = false;

    public PortletHeadResponseWriter(ResponseWriter parent, PortletResponse portletResponse) {
        this.wrapped = parent;
        this.response = portletResponse;
        this.elements = new Stack<Element>();
        this.preventSelfClosing = BridgeContext.getCurrentInstance().getBridgeConfig().doPreventSelfClosingScriptTag();
    }

    /**
     * @see javax.faces.context.ResponseWriterWrapper#getWrapped()
     */
    @Override
    public ResponseWriter getWrapped() {
        return wrapped;
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        elements.push(response.createElement(name));
    }

    @Override
    public void endElement(String name) throws IOException {
        if (elements.size() > 1) {
            Element child = elements.pop();
            elements.peek().appendChild(child);
        } else {
            Element elem = elements.pop();

            if (("script".equalsIgnoreCase(name) || "style".equalsIgnoreCase(name)) && !elem.hasAttribute("src")) {
                Text text1;
                Text text2 = null;
                CDATASection cdata;
                String content = elem.getTextContent();
                Document owner = elem.getOwnerDocument();

                if ("script".equalsIgnoreCase(name)) {
                    text1 = owner.createTextNode("\n//");
                    cdata = owner.createCDATASection("\n" + content + "\n//");
                } else {
                    text1 = owner.createTextNode("\n/*");
                    cdata = owner.createCDATASection("*/\n" + content + "\n/*");
                    text2 = owner.createTextNode("*/");
                }

                elem.setTextContent("");
                elem.appendChild(text1);
                elem.appendChild(cdata);
                if (null != text2) {
                    elem.appendChild(text2);
                }
            } else if (preventSelfClosing) {
                if (("script".equalsIgnoreCase(name)) && (null == elem.getTextContent() || elem.getTextContent().length() == 0)) {
                    elem.appendChild(elem.getOwnerDocument().createComment(" "));
                }
            }
            response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elem);
        }
    }

    private void append(String content) {
        Element elem = elements.peek();
        elem.setTextContent(elem.getTextContent() + content);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (null != value) {
            elements.peek().setAttribute(name, value.toString());
        } else {
            elements.peek().setAttribute(name, null);
        }
    }

    @Override
    public void writeComment(Object comment) throws IOException {
        if (null != comment) {
            append(comment.toString());
        }
    }

    @Override
    public void writeText(char[] text, int off, int len) throws IOException {
        write(text, off, len);
    }

    @Override
    public void writeText(Object text, UIComponent component, String property) throws IOException {
        if (null != text) {
            append(text.toString());
        }
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        if (null != text) {
            append(text.toString());
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        writeAttribute(name, value, property);
    }

    @Override
    public Writer append(char c) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(c);
        append(writer.getBuffer().toString());
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        StringWriter writer = new StringWriter();
        writer.append(csq, start, end);
        append(writer.getBuffer().toString());
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        StringWriter writer = new StringWriter();
        writer.append(csq);
        append(writer.getBuffer().toString());
        return this;
    }

    @Override
    public void write(int c) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(c);
        append(writer.getBuffer().toString());
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(cbuf);
        append(writer.getBuffer().toString());
    }

    @Override
    public void write(String str) throws IOException {
        if (null != str) {
            append(str);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(str, off, len);
        append(writer.getBuffer().toString());
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(cbuf, off, len);
        append(writer.getBuffer().toString());
    }

}
