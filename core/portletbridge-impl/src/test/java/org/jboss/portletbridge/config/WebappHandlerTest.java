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

import javax.portlet.PortletContext;

import junit.framework.TestCase;

import org.jboss.portletbridge.config.WebXmlProcessor.WebXmlHandler;
import org.xml.sax.ContentHandler;

/**
 * @author asmirnov, kenfinnigan
 */
public class WebappHandlerTest extends TestCase {

    private static final String LOCATION = "location";
    private static final String EXCEPTION_TYPE = "exception-type";
    private static final String ERROR_PAGE = "error-page";
    private static final char[] FACES_SERVLET = "Faces Servlet".toCharArray();
    private static final char[] FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet".toCharArray();
    private static final String SERVLET = "servlet";
    private static final String WEBAPP = "web-app";
    private static final String SERVLET_NAME = "servlet-name";
    private static final String SERVLET_CLASS = "servlet-class";
    private static final String SERVLET_MAPPING = "servlet-mapping";
    private MockXmlReader reader;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reader = new MockXmlReader();
    }

    public void testServletElement() throws Exception {
        WebXmlProcessor processor = new WebXmlProcessor((PortletContext) null);
        WebXmlHandler handler = processor.new WebXmlHandler(reader);
        reader.setContentHandler(handler);
        handler.startElement(null, WEBAPP, WEBAPP, null);
        assertEquals(handler, reader.getContentHandler());
        handler.startElement(null, SERVLET, SERVLET, null);
        ContentHandler servletHandler = reader.getContentHandler();
        assertSame(WebXmlHandler.ServletHandler.class, reader.getContentHandler().getClass());
        servletHandler.startElement(null, SERVLET_NAME, SERVLET_NAME, null);
        assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
        ContentHandler servletNameHandler = reader.getContentHandler();
        servletNameHandler.characters(FACES_SERVLET, 0, FACES_SERVLET.length);
        servletNameHandler.endElement(null, SERVLET_NAME, SERVLET_NAME);
        assertSame(servletHandler, reader.getContentHandler());
        servletHandler.startElement(null, SERVLET_CLASS, SERVLET_CLASS, null);
        assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
        ContentHandler servletClassHandler = reader.getContentHandler();
        servletClassHandler.characters(FACES_SERVLET_CLASS, 0, FACES_SERVLET_CLASS.length);
        servletClassHandler.endElement(null, SERVLET_CLASS, SERVLET_CLASS);
        assertSame(servletHandler, reader.getContentHandler());
        servletHandler.endElement(null, SERVLET, SERVLET);
        assertEquals(handler, reader.getContentHandler());
        handler.endElement(null, WEBAPP, WEBAPP);
        handler.endDocument();
        assertEquals("Faces Servlet", WebXmlProcessor.facesServlet.getName());
    }

    public void testMappingElement() throws Exception {
        WebXmlProcessor processor = new WebXmlProcessor((PortletContext) null);
        WebXmlHandler handler = processor.new WebXmlHandler(reader);
        reader.setContentHandler(handler);
        handler.startElement(null, WEBAPP, WEBAPP, null);
        assertEquals(handler, reader.getContentHandler());
        handler.startElement(null, SERVLET_MAPPING, SERVLET_MAPPING, null);
        ContentHandler servletHandler = reader.getContentHandler();
        assertSame(WebXmlHandler.MappingHandler.class, reader.getContentHandler().getClass());
        servletHandler.startElement(null, SERVLET_NAME, SERVLET_NAME, null);
        assertSame(StringContentHandler.class, reader.getContentHandler().getClass());

    }

    public void testErrorPagesElement() throws Exception {
        WebXmlProcessor processor = new WebXmlProcessor((PortletContext) null);
        WebXmlHandler handler = processor.new WebXmlHandler(reader);
        reader.setContentHandler(handler);
        handler.startElement(null, WEBAPP, WEBAPP, null);
        assertEquals(handler, reader.getContentHandler());
        handler.startElement(null, ERROR_PAGE, ERROR_PAGE, null);
        ContentHandler errorPageHandler = reader.getContentHandler();
        assertSame(WebXmlHandler.ErrorPageHandler.class, reader.getContentHandler().getClass());
        errorPageHandler.startElement(null, EXCEPTION_TYPE, EXCEPTION_TYPE, null);
        assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
        reader.getContentHandler().endElement(null, EXCEPTION_TYPE, EXCEPTION_TYPE);
        errorPageHandler.startElement(null, LOCATION, LOCATION, null);
        assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
        reader.getContentHandler().endElement(null, LOCATION, LOCATION);
        assertSame(WebXmlHandler.ErrorPageHandler.class, reader.getContentHandler().getClass());
        errorPageHandler.endElement(null, ERROR_PAGE, ERROR_PAGE);
        assertEquals(handler, reader.getContentHandler());

    }
}
