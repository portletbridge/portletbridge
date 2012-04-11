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
package org.jboss.portletbridge.config;

import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov
 */
public abstract class StateHandler extends DefaultHandler {

    private final ContentHandler parentHandler;

    private final XMLReader reader;

    private int depth = 0;

    public StateHandler(XMLReader reader, ContentHandler parentHandler) {
        super();
        this.reader = reader;
        this.parentHandler = parentHandler;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        ContentHandler nextHandler = getNextHandler(uri, localName, attributes);
        if (null == nextHandler) {
            depth++;
        } else {
            reader.setContentHandler(nextHandler);
        }
    }

    protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) throws SAXException {
        return null;
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (depth-- == 0) {
            if (null != parentHandler) {
                reader.setContentHandler(parentHandler);
            }
            endLastElement();
        }
    }

    protected void endLastElement() throws SAXException {
        // Do nothing
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        // Do nothing, to avoid network requests to external DTD/Schema
        return new InputSource(new StringReader(""));
    }

    protected XMLReader getReader() {
        return reader;
    }
}
