/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov
 * 
 */
public class FacesConfigHandler extends DefaultHandler {

	private static final String JEE_NS = "http://java.sun.com/xml/ns/javaee";
	private static final String BRIDGE_NS = "http://myfaces.apache.org/portletbridge";
	private static final String APPLICATION_ELEMENT = "application";
	private static final String APP_EXTENSION_ELEMENT = "application-extension";
	private static final String EXCLUDED_ATTRIBUTES_ELEMENT = "excluded-attributes";
	private static final String EXCLUDED_ATTRIBUTE_ELEMENT = "excluded-attribute";
	private static final String PUBLIC_PARAMETER_MAPPINGS = "public-parameter-mappings";
	private static final String PUBLIC_PARAMETER_MAPPING = "public-parameter-mapping";
	private static final String PARAMETER_ELEMENT = "parameter";
	private static final String MODEL_EL_ELEMENT = "model-el";

	private final List<String> excludedAttributes;

	private final Map<String, String> parameterMapping;

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

	@Override
	public void startElement(String uri, String localName, String name,
	        Attributes attributes) throws SAXException {
		if (APPLICATION_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
			reader.setContentHandler(new ApplicationHandler());
		}
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
	        throws SAXException {
		// Do nothing, to avoid network requests to external DTD/Schema
		return new InputSource(new StringReader(""));
	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ApplicationHandler extends StateHandler {
		public ApplicationHandler() {
			super(reader, FacesConfigHandler.this);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
		        Attributes attributes) {
			if (APP_EXTENSION_ELEMENT.equals(localName) && JEE_NS.equals(uri)) {
				return new ApplicationExtensionHandler(this);
			} else {
				return null;
			}
		}

	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ApplicationExtensionHandler extends StateHandler {
		public ApplicationExtensionHandler(ContentHandler parent) {
			super(reader, parent);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
		        Attributes attributes) {
			if (EXCLUDED_ATTRIBUTES_ELEMENT.equals(localName)
			        && !JEE_NS.equals(uri)) {
				return new ExcludedAttributesHandler(this);
			} else if (PUBLIC_PARAMETER_MAPPINGS.equals(localName)
			        && !JEE_NS.equals(uri)) {
				return new ParameterMappingsHandler(this);
			} else {
				return null;
			}
		}

	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ExcludedAttributesHandler extends StateHandler {

		public ExcludedAttributesHandler(ContentHandler parent) {
			super(reader, parent);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
		        Attributes attributes) {
			if (EXCLUDED_ATTRIBUTE_ELEMENT.equals(localName)
			        && !JEE_NS.equals(uri)) {
				return new ExcludedAttributeHandler(this);
			} else {
				return null;
			}
		}

	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ExcludedAttributeHandler extends StringContentHandler {

		public ExcludedAttributeHandler(ContentHandler parent) {
			super(reader, parent, new StringBuilder());
		}

		@Override
		protected void endLastElement() {
			excludedAttributes.add(getResult().toString());
		}
	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ParameterMappingsHandler extends StateHandler {

		public ParameterMappingsHandler(ContentHandler parent) {
			super(reader, parent);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
		        Attributes attributes) {
			if (PUBLIC_PARAMETER_MAPPING.equals(localName)
			        && !JEE_NS.equals(uri)) {
				return new ParameterMappingHandler(this);
			} else {
				// TODO - throw exception for unexpected element.
				return null;
			}
		}

	}

	/**
	 * @author asmirnov
	 * 
	 */
	private class ParameterMappingHandler extends StateHandler {

		private StringBuilder parameterName = new StringBuilder();
		private StringBuilder modelEl = new StringBuilder();

		public ParameterMappingHandler(ContentHandler parent) {
			super(reader, parent);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
		        Attributes attributes) throws SAXException {
			ContentHandler nextHandler = null;
			if (PARAMETER_ELEMENT.equals(localName) ) {
				nextHandler = new StringContentHandler(getReader(), this,
				        parameterName);
			} else if (MODEL_EL_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
				        modelEl);
			} else {
				// TODO - throw exception for unexpected element.
			}
			return nextHandler;
		}

		@Override
		protected void endLastElement() throws SAXException {
			if (parameterName.length() > 0 && modelEl.length() > 0) {
				parameterMapping.put(parameterName.toString(), modelEl
				        .toString());
			} else {
				// TODO - throw exception for incomplete content.
			}
		}

	}
}
