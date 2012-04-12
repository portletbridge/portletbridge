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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portletbridge.bridge.factory.BridgeConfigFactory;
import org.jboss.portletbridge.bridge.factory.BridgeContextFactory;
import org.jboss.portletbridge.bridge.factory.BridgeControllerFactory;
import org.jboss.portletbridge.bridge.factory.BridgeFactory;
import org.jboss.portletbridge.bridge.factory.BridgeFactoryFinder;
import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactory;
import org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory;
import org.jboss.portletbridge.bridge.factory.BridgeRequestScopeManagerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov, kenfinnigan
 */
public class FacesConfigHandler extends DefaultHandler {

    private static final String JEE_NS = "http://java.sun.com/xml/ns/javaee";
    private static final String BRIDGE_NS = "http://jboss.org/xml/ns/bridge/bridge-extension";
    private static final String APPLICATION_ELEMENT = "application";
    private static final String APP_EXTENSION_ELEMENT = "application-extension";
    private static final String FACTORY_ELEMENT = "factory";
    private static final String FACTORY_EXTENSION_ELEMENT = "factory-extension";
    private static final String EXCLUDED_ATTRIBUTES_ELEMENT = "excluded-attributes";
    private static final String EXCLUDED_ATTRIBUTE_ELEMENT = "excluded-attribute";
    private static final String PUBLIC_PARAMETER_MAPPINGS = "public-parameter-mappings";
    private static final String PUBLIC_PARAMETER_MAPPING = "public-parameter-mapping";
    private static final String WRITE_BEHIND_RESPONSE_WRAPPERS = "write-behind-response-wrappers";
    private static final String RENDER_RESPONSE_WRAPPER_CLASS = "render-response-wrapper-class";
    private static final String RESOURCE_RESPONSE_WRAPPER_CLASS = "resource-response-wrapper-class";
    private static final String BRIDGE_CONFIG_FACTORY = "bridge-config-factory";
    private static final String BRIDGE_CONTEXT_FACTORY = "bridge-context-factory";
    private static final String BRIDGE_CONTROLLER_FACTORY = "bridge-controller-factory";
    private static final String BRIDGE_LOGGER_FACTORY = "bridge-logger-factory";
    private static final String BRIDGE_REQUEST_SCOPE_MANAGER_FACTORY = "bridge-request-scope-manager-factory";
    private static final String BRIDGE_REQUEST_SCOPE_FACTORY = "bridge-request-scope-factory";
    private static final String PARAMETER_ELEMENT = "parameter";
    private static final String MODEL_EL_ELEMENT = "model-el";

    private final List<String> excludedAttributes;

    private final Map<String, String> parameterMapping;

    private String renderResponseWrapperClass;
    private String resourceResponseWrapperClass;

    private XMLReader reader;

    public FacesConfigHandler(XMLReader reader) {
        this.reader = reader;
        this.excludedAttributes = new ArrayList<String>();
        this.parameterMapping = new HashMap<String, String>();
    }

    /**
     * @return the excludedAttributes
     */
    public List<String> getExcludedAttributes() {
        return excludedAttributes;
    }

    /**
     * @return the parameterMapping
     */
    public Map<String, String> getParameterMapping() {
        return parameterMapping;
    }

    /**
     * @return RenderResponse Wrapper Class
     */
    public String getRenderResponseWrapperClass() {
        return renderResponseWrapperClass;
    }

    /**
     * @return ResourceResponse Wrapper Class
     */
    public String getResourceResponseWrapperClass() {
        return resourceResponseWrapperClass;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (APPLICATION_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
            reader.setContentHandler(new ApplicationHandler());
        } else if (FACTORY_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
            reader.setContentHandler(new FactoryHandler());
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        // Do nothing, to avoid network requests to external DTD/Schema
        return new InputSource(new StringReader(""));
    }

    private class ApplicationHandler extends StateHandler {
        public ApplicationHandler() {
            super(reader, FacesConfigHandler.this);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
            if (APP_EXTENSION_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
                return new ApplicationExtensionHandler(this);
            } else {
                return null;
            }
        }

    }

    private class ApplicationExtensionHandler extends StateHandler {
        public ApplicationExtensionHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
            if (EXCLUDED_ATTRIBUTES_ELEMENT.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new ExcludedAttributesHandler(this);
            } else if (PUBLIC_PARAMETER_MAPPINGS.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new ParameterMappingsHandler(this);
            } else if (WRITE_BEHIND_RESPONSE_WRAPPERS.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new WriteBehindResponseWrappersHandler(this);
            } else {
                return null;
            }
        }

    }

    private class FactoryHandler extends StateHandler {
        public FactoryHandler() {
            super(reader, FacesConfigHandler.this);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
            if (FACTORY_EXTENSION_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
                return new FactoryExtensionHandler(this);
            } else {
                return null;
            }
        }

    }

    private class FactoryExtensionHandler extends StateHandler {
        public FactoryExtensionHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) throws ParsingException {
            if (BRIDGE_CONFIG_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeConfigFactory.class);
            } else if (BRIDGE_CONTEXT_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeContextFactory.class);
            } else if (BRIDGE_CONTROLLER_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeControllerFactory.class);
            } else if (BRIDGE_LOGGER_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeLoggerFactory.class);
            } else if (BRIDGE_REQUEST_SCOPE_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeRequestScopeFactory.class);
            } else if (BRIDGE_REQUEST_SCOPE_MANAGER_FACTORY.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new BridgeFactoryHandler(this, BridgeRequestScopeManagerFactory.class);
            } else {
                throw new ParsingException("Unexpected element: " + localName + " within " + FACTORY_EXTENSION_ELEMENT
                        + " element");
            }
        }

    }

    private class ExcludedAttributesHandler extends StateHandler {

        public ExcludedAttributesHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) {
            if (EXCLUDED_ATTRIBUTE_ELEMENT.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new ExcludedAttributeHandler(this);
            } else {
                return null;
            }
        }

    }

    private class ExcludedAttributeHandler extends StringContentHandler {

        public ExcludedAttributeHandler(ContentHandler parent) {
            super(reader, parent, new StringBuilder());
        }

        @Override
        protected void endLastElement() {
            excludedAttributes.add(getResult().toString());
        }
    }

    private class ParameterMappingsHandler extends StateHandler {

        public ParameterMappingsHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) throws ParsingException {
            if (PUBLIC_PARAMETER_MAPPING.equals(localName) && BRIDGE_NS.equals(uri)) {
                return new ParameterMappingHandler(this);
            } else {
                throw new ParsingException("Unexpected element: " + localName + " within " + PUBLIC_PARAMETER_MAPPINGS
                        + " element");
            }
        }

    }

    private class ParameterMappingHandler extends StateHandler {

        private StringBuilder parameterName = new StringBuilder();
        private StringBuilder modelEl = new StringBuilder();

        public ParameterMappingHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) throws SAXException {
            ContentHandler nextHandler = null;
            if (PARAMETER_ELEMENT.equals(localName)) {
                nextHandler = new StringContentHandler(getReader(), this, parameterName);
            } else if (MODEL_EL_ELEMENT.equals(localName)) {
                nextHandler = new StringContentHandler(getReader(), this, modelEl);
            } else {
                throw new ParsingException("Unexpected element: " + localName + " within " + PUBLIC_PARAMETER_MAPPING
                        + " element");
            }
            return nextHandler;
        }

        @Override
        protected void endLastElement() throws SAXException {
            if (parameterName.length() > 0 && modelEl.length() > 0) {
                parameterMapping.put(parameterName.toString(), modelEl.toString());
            } else {
                throw new ParsingException(PUBLIC_PARAMETER_MAPPING + " did not contain both " + PARAMETER_ELEMENT + " and "
                        + MODEL_EL_ELEMENT);
            }
        }
    }

    private class WriteBehindResponseWrappersHandler extends StateHandler {

        private StringBuilder renderResponseWrapperClass = new StringBuilder();
        private StringBuilder resourceResponseWrapperClass = new StringBuilder();

        public WriteBehindResponseWrappersHandler(ContentHandler parent) {
            super(reader, parent);
        }

        @Override
        protected ContentHandler getNextHandler(String uri, String localName, Attributes attributes) throws SAXException {
            ContentHandler nextHandler = null;
            if (RENDER_RESPONSE_WRAPPER_CLASS.equals(localName)) {
                nextHandler = new StringContentHandler(getReader(), this, renderResponseWrapperClass);
            } else if (RESOURCE_RESPONSE_WRAPPER_CLASS.equals(localName)) {
                nextHandler = new StringContentHandler(getReader(), this, resourceResponseWrapperClass);
            } else {
                throw new ParsingException("Unexpected element: " + localName + " within " + WRITE_BEHIND_RESPONSE_WRAPPERS
                        + " element");
            }
            return nextHandler;
        }

        @Override
        protected void endLastElement() throws SAXException {
            if (renderResponseWrapperClass.length() > 0) {
                FacesConfigHandler.this.renderResponseWrapperClass = renderResponseWrapperClass.toString();
            }
            if (resourceResponseWrapperClass.length() > 0) {
                FacesConfigHandler.this.resourceResponseWrapperClass = resourceResponseWrapperClass.toString();
            }
        }
    }

    private class BridgeFactoryHandler extends StringContentHandler {
        private Class<? extends BridgeFactory<?>> bridgeType;

        public BridgeFactoryHandler(ContentHandler parent, Class<? extends BridgeFactory<?>> type) {
            super(reader, parent, new StringBuilder());
            bridgeType = type;
        }

        @Override
        protected void endLastElement() {
            BridgeFactoryFinder.addFactoryDefinition(bridgeType, getResult().toString().trim());
        }
    }
}
