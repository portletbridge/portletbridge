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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.webapp.FacesServlet;
import javax.portlet.PortletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author kenfinnigan
 */
public final class FacesConfigProcessor implements SystemEventListener {

    private static final Logger logger = Logger.getLogger(FacesConfigProcessor.class.getName(), BridgeLogger.LOGGING_BUNDLE);

    private static SAXParserFactory saxFactory = SAXParserFactory.newInstance();

    static {
        saxFactory.setValidating(false);
        saxFactory.setNamespaceAware(true);
    }

    private static final String FACES_CONFIG_CLASS_PATH = "META-INF/faces-config.xml";
    private static final String FACES_CONFIG_WEB_PATH = "/WEB-INF/faces-config.xml";

    private static boolean scanned;
    private static List<String> excludedAttributes;
    private static Map<String, String> publicParameterMapping;
    private static String writeBehindRenderResponseWrapper = null;
    private static String writeBehindResourceResponseWrapper = null;

    public FacesConfigProcessor() {
        scanned = false;
        excludedAttributes = new ArrayList<String>();
        publicParameterMapping = new HashMap<String, String>();
    }

    public static List<String> getExcludedAttributes() {
        return excludedAttributes;
    }

    public static Map<String, String> getPublicParameterMappings() {
        return publicParameterMapping;
    }

    public static String getWriteBehindRenderResponseWrapperClassName() {
        return writeBehindRenderResponseWrapper;
    }

    public static String getWriteBehindResourceResponseWrapperClassName() {
        return writeBehindResourceResponseWrapper;
    }

    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (!scanned) {
            try {
                parseClasspath();
                parseDefault();
            } catch (ParsingException e) {
                throw new FacesException("Portlet Bridge error parsing faces-config(s)", e);
            }

            scanned = true;
        }
    }

    public boolean isListenerForSource(Object source) {
        return true;
    }

    protected void parseClasspath() throws ParsingException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (null == classLoader) {
            classLoader = FacesContext.getCurrentInstance().getExternalContext().getClass().getClassLoader();
        }
        try {
            // Parse all faces-config.xml files in the classpath.
            Enumeration<URL> resources = classLoader.getResources(FACES_CONFIG_CLASS_PATH);
            while (resources.hasMoreElements()) {
                URL resourceURL = (URL) resources.nextElement();
                try {
                    URLConnection connection = resourceURL.openConnection();
                    // To avoid file locking in the Windows environmemt.
                    connection.setUseCaches(false);
                    InputStream inputStream = connection.getInputStream();
                    try {
                        parse(inputStream, ParsingLocation.CLASSPATH);
                    } finally {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    logger.log(java.util.logging.Level.WARNING, "Can't parse " + resourceURL.toExternalForm(), e);
                }
            }
        } catch (IOException e) {
            logger.log(java.util.logging.Level.WARNING, "Can't get META-INF/faces-config.xml resources", e);
        }
    }

    protected void parseDefault() throws ParsingException {
        // Parse default faces config.
        InputStream inputStream = FacesContext.getCurrentInstance().getExternalContext()
                .getResourceAsStream(FACES_CONFIG_WEB_PATH);
        if (null != inputStream) {
            try {
                parse(inputStream, ParsingLocation.DEFAULT);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.log(java.util.logging.Level.WARNING, "Can't close input stream for web resource "
                            + FACES_CONFIG_WEB_PATH, e);
                }
            }
        }
    }

    public void parseOptional(PortletContext context) throws ParsingException {
        // Parse additional faces-config files, if present.
        String facesConfigs = context.getInitParameter(FacesServlet.CONFIG_FILES_ATTR);
        if (null != facesConfigs) {
            String[] configNamesArray = facesConfigs.trim().split("(\\s)*,(\\s)*");
            for (int i = 0; i < configNamesArray.length; i++) {
                String facesConfigPath = configNamesArray[i];
                InputStream inputStream = context.getResourceAsStream(facesConfigPath);
                if (null != inputStream) {
                    try {
                        parse(inputStream, ParsingLocation.OPTIONAL);
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            logger.log(java.util.logging.Level.WARNING, "Can't close input stream for web resource "
                                    + facesConfigPath, e);
                        }
                    }
                }
            }
        }
    }

    protected void parse(InputStream facesConfig, ParsingLocation location) throws ParsingException {
        try {
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            FacesConfigHandler facesConfigHandler = new FacesConfigHandler(reader);
            reader.setContentHandler(facesConfigHandler);
            reader.setEntityResolver(WebXmlProcessor.NULL_RESOLVER);
            reader.setErrorHandler(facesConfigHandler);
            reader.setDTDHandler(facesConfigHandler);

            reader.parse(new InputSource(facesConfig));

            excludedAttributes.addAll(facesConfigHandler.getExcludedAttributes());
            publicParameterMapping.putAll(facesConfigHandler.getParameterMapping());

            if (location.equals(ParsingLocation.CLASSPATH) || location.equals(ParsingLocation.DEFAULT)) {
                writeBehindRenderResponseWrapper = facesConfigHandler.getRenderResponseWrapperClass();
                writeBehindResourceResponseWrapper = facesConfigHandler.getResourceResponseWrapperClass();
            } else if (location.equals(ParsingLocation.OPTIONAL)) {
                if (null == writeBehindRenderResponseWrapper) {
                    writeBehindRenderResponseWrapper = facesConfigHandler.getRenderResponseWrapperClass();
                }
                if (null == writeBehindResourceResponseWrapper) {
                    writeBehindResourceResponseWrapper = facesConfigHandler.getResourceResponseWrapperClass();
                }
            }
        } catch (ParserConfigurationException e) {
            throw new ParsingException("SAX Parser configuration error", e);
        } catch (SAXException e) {
            logger.log(java.util.logging.Level.WARNING, "Exception at faces-config.xml parsing", e);
        } catch (IOException e) {
            logger.log(java.util.logging.Level.WARNING, "Exception at faces-config.xml parsing", e);
        }
    }

    enum ParsingLocation {
        CLASSPATH, OPTIONAL, DEFAULT;
    }
}
