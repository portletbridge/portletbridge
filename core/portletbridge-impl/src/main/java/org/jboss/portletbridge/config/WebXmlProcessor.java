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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.faces.FacesException;
import javax.faces.webapp.FacesServlet;
import javax.portlet.PortletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author kenfinnigan
 */
public final class WebXmlProcessor {

    private static final String WEB_XML_PATH = "/WEB-INF/web.xml";

    private static SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    private static AtomicBoolean scan = new AtomicBoolean(true);
    private static boolean scanned = false;

    static {
        saxFactory.setValidating(false);
        saxFactory.setNamespaceAware(true);
    }

    static List<ServletBean> servlets = new ArrayList<ServletBean>();
    static Map<String, ArrayList<String>> urlMappings = new HashMap<String, ArrayList<String>>();
    static Map<String, String> errorPages = new LinkedHashMap<String, String>();

    static ServletBean facesServlet;
    static Map<Class<? extends Throwable>, String> errorViews;

    public WebXmlProcessor(PortletContext portletContext) {
        if (scan.compareAndSet(true, false)) {
            if (null != portletContext) {
                InputStream inputStream = portletContext.getResourceAsStream(WEB_XML_PATH);
                this.parse(inputStream);

                errorViews = createErrorViews();

                try {
                    inputStream.close();
                } catch (IOException e) {
                    portletContext.log("Portlet Bridge error parsing web.xml", e);
                }
                scanned = true;
            }
        } else {
            while (!scanned) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public WebXmlProcessor(InputStream webXml) {
        if (scan.compareAndSet(true, false)) {
            if (null != webXml) {
                this.parse(webXml);
                scanned = true;
            }
        } else {
            while (!scanned) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * @return the errorPages
     */
    public Map<String, String> getErrorPages() {
        return errorPages;
    }

    /**
     * @return the facesServlet
     */
    public ServletBean getFacesServlet() {
        return facesServlet;
    }

    /**
     * getter for the map between error class and corresponding JSF view id.
     *
     * @return the {@link #errorViews}
     */
    public Map<Class<? extends Throwable>, String> getErrorViews() {
        return errorViews;
    }

    public void parse(InputStream webXml) {
        try {
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            WebXmlHandler webXmlHandler = new WebXmlHandler(reader);

            reader.setContentHandler(webXmlHandler);
            reader.setEntityResolver(NULL_RESOLVER);
            reader.setErrorHandler(webXmlHandler);
            reader.setDTDHandler(webXmlHandler);

            reader.parse(new InputSource(webXml));
        } catch (Exception e) {
            throw new FacesException("XML parsing error", e);
        }
    }

    /**
     * Create map between error class and corresponding JSF view id. Map created from the {@link #errorLocations}
     * string-based map.
     *
     * @return map between exception class and view id.
     */
    protected Map<Class<? extends Throwable>, String> createErrorViews() {
        LinkedHashMap<Class<? extends Throwable>, String> viewsMap = new LinkedHashMap<Class<? extends Throwable>, String>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (null == classLoader) {
            classLoader = this.getClass().getClassLoader();
        }

        for (Entry<String, String> entry : errorPages.entrySet()) {
            try {
                Class<? extends Throwable> clazz = classLoader.loadClass(entry.getKey()).asSubclass(Throwable.class);
                String viewId = getViewIdFromLocation(entry.getValue());
                if (null != viewId) {
                    viewsMap.put(clazz, viewId);
                }
            } catch (ClassNotFoundException e) {
                // Configuration error, just ignore.
            }
        }
        return viewsMap;
    }

    /**
     * Convert error page location into JSF viewId.
     *
     * @param location
     *            error page location
     * @return view id if this location is mapped to the {@link FacesServlet} othervise null.
     */
    protected String getViewIdFromLocation(String location) {
        String viewId = null;
        for (String mapping : facesServlet.getMappings()) {
            if (mapping.startsWith("*")) {
                // Suffix mapping.
                String suffix = mapping.substring(1);
                if (location.endsWith(suffix)) {
                    viewId = location.substring(0, location.length() - suffix.length());
                    break;
                }
            } else if (mapping.endsWith("*")) {
                // Prefix mapping.
                String prefix = mapping.substring(0, mapping.length() - 1);
                if (location.startsWith(prefix)) {
                    int index = prefix.length();
                    if (prefix.endsWith("/")) {
                        index--;
                    }
                    viewId = location.substring(index);
                }
            }
        }
        return viewId;
    }

    public static final EntityResolver2 NULL_RESOLVER = new EntityResolver2() {

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }

        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }

        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
            throws SAXException, IOException {
            // Do nothing, to avoid network requests to external DTD/Schema
            return new InputSource(new StringReader(""));

        }
    };

    class WebXmlHandler extends DefaultHandler {

        private static final String SERVLET_ELEMENT = "servlet";
        private static final String SERVLET_NAME_ELEMENT = "servlet-name";
        private static final String SERVLET_MAPPING_ELEMENT = "servlet-mapping";
        private static final String SERVLET_CLASS_ELEMENT = "servlet-class";
        private static final String ERROR_PAGE_ELEMENT = "error-page";
        private static final String LOCATION_ELEMENT = "location";
        private static final String EXCEPTION_CLASS_ELEMENT = "exception-type";
        private static final String URL_PATTERN_ELEMENT = "url-pattern";
        private static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

        private XMLReader xmlReader;

        public WebXmlHandler(XMLReader xmlReader) {
            this.xmlReader = xmlReader;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if (SERVLET_ELEMENT.equals(localName)) {
                xmlReader.setContentHandler(new ServletHandler());
            } else if (SERVLET_MAPPING_ELEMENT.equals(localName)) {
                xmlReader.setContentHandler(new MappingHandler());
            } else if (ERROR_PAGE_ELEMENT.equals(localName)) {
                xmlReader.setContentHandler(new ErrorPageHandler());
            }
        }

        @Override
        public void endDocument() throws SAXException {
            for (ServletBean servlet : servlets) {
                if (FACES_SERVLET_CLASS.equals(servlet.getClassName())) {
                    facesServlet = servlet;
                    List<String> servletMappings = urlMappings.get(servlet.getName());
                    if (null != servletMappings) {
                        facesServlet.getMappings().addAll(servletMappings);
                    }
                }
            }
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
            // Do nothing, to avoid network requests to external DTD/Schema
            return new InputSource(new StringReader(""));
        }

        final class ServletHandler extends StateHandler {

            private StringBuilder servletName = new StringBuilder();
            private StringBuilder servletClass = new StringBuilder();

            public ServletHandler() {
                super(xmlReader, WebXmlHandler.this);
            }

            @Override
            protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
                ContentHandler nextHandler = null;
                if (SERVLET_NAME_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, servletName);
                } else if (SERVLET_CLASS_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, servletClass);
                }
                return nextHandler;
            }

            @Override
            protected void endLastElement() {
                servlets.add(new ServletBean(servletName.toString().trim(), servletClass.toString().trim()));
            }
        }

        final class MappingHandler extends StateHandler {

            private StringBuilder servletName = new StringBuilder();
            private StringBuilder urlPattern = new StringBuilder();

            public MappingHandler() {
                super(xmlReader, WebXmlHandler.this);
            }

            @Override
            protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
                ContentHandler nextHandler = null;
                if (SERVLET_NAME_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, servletName);
                } else if (URL_PATTERN_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, urlPattern);
                }
                return nextHandler;
            }

            @Override
            protected void endLastElement() {
                if (urlMappings.containsKey(servletName.toString().trim())) {
                    urlMappings.get(servletName.toString().trim()).add(urlPattern.toString().trim());
                } else {
                    urlMappings.put(servletName.toString().trim(), new ArrayList<String>());
                    urlMappings.get(servletName.toString().trim()).add(urlPattern.toString().trim());
                }
            }
        }

        final class ErrorPageHandler extends StateHandler {

            private StringBuilder exceptionType = new StringBuilder();
            private StringBuilder location = new StringBuilder();

            public ErrorPageHandler() {
                super(xmlReader, WebXmlHandler.this);
            }

            @Override
            protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
                ContentHandler nextHandler = null;
                if (EXCEPTION_CLASS_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, exceptionType);
                } else if (LOCATION_ELEMENT.equals(localName)) {
                    nextHandler = new StringContentHandler(getReader(), this, location);
                }
                return nextHandler;
            }

            @Override
            protected void endLastElement() {
                errorPages.put(exceptionType.toString().trim(), location.toString().trim());
            }
        }
    }
}
