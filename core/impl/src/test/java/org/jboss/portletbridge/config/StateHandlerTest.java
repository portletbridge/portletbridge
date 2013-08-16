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

import junit.framework.TestCase;

import org.jboss.portletbridge.config.StateHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov
 *
 */
public class StateHandlerTest extends TestCase {

    private static final String PREFIX = "foo:";
    private static final String BAR = "bar";
    private static final String NS = "http://foo.com/";

    private XMLReader reader;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        reader = new MockXmlReader();
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReturnBack() throws Exception {
        ContentHandler parentHandler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String name, Attributes attributes)
                throws SAXException {
                throw new SAXException();
            }

            @Override
            public void endElement(String uri, String localName, String name) throws SAXException {
                throw new SAXException();
            }
        };
        StateHandler handler = new StateHandler(reader, parentHandler) {

        };
        reader.setContentHandler(handler);
        handler.startElement(NS, BAR, PREFIX + BAR, null);
        handler.startElement(NS, BAR, PREFIX + BAR, null);
        handler.endElement(NS, BAR, PREFIX + BAR);
        assertSame(handler, reader.getContentHandler());
        handler.endElement(NS, BAR, PREFIX + BAR);
        assertSame(handler, reader.getContentHandler());
        handler.endElement(NS, BAR, PREFIX + BAR);
        assertSame(parentHandler, reader.getContentHandler());
    }

}
