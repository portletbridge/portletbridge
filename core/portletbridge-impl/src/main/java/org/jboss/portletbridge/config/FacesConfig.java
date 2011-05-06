/**
 * 
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.webapp.FacesServlet;
import javax.portlet.PortletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author asmirnov
 * 
 */
public class FacesConfig {

	private static final Logger log = Logger.getLogger(FacesConfig.class
			.getName());

	private static final String FACES_CONFIG_CLASS_PATH = "META-INF/faces-config.xml";

	private static final String FACES_CONFIG_WEB_PATH = "/WEB-INF/faces-config.xml";

	private final List<String> excludedAttributes;

    private final Map<String, String> parameterMapping;

	private SAXParserFactory factory;

	public FacesConfig() {
		factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		parameterMapping = new HashMap<String, String>();
		excludedAttributes = new ArrayList<String>();
	}

	public void parse(PortletContext context) {
		try {
			parseClasspath(context);
			parseOptional(context);
			parseDefault(context);
		} catch (ParsingException e) {
			throw new FacesException("Error parsing faces-config",e);
		}
	}

	protected void parseDefault(PortletContext context) throws ParsingException {
		// Parse default faces config.
		InputStream inputStream = context
				.getResourceAsStream(FACES_CONFIG_WEB_PATH);
		if (null != inputStream) {
			try {
				parse(inputStream);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.log(Level.WARNING,
							"Can't close input stream for web resource "
									+ FACES_CONFIG_WEB_PATH, e);
				}
			}
		}
	}

	protected void parseOptional(PortletContext context)
			throws ParsingException {
		// Parse additional faces-config files, if present.
		String facesConfigs = context
				.getInitParameter(FacesServlet.CONFIG_FILES_ATTR);
		if (null != facesConfigs) {
			String[] configNamesArray = facesConfigs.trim().split(
					"(\\s)*,(\\s)*");
			for (int i = 0; i < configNamesArray.length; i++) {
				String facesConfigPath = configNamesArray[i];
				InputStream inputStream = context
						.getResourceAsStream(facesConfigPath);
				if (null != inputStream) {
					try {
						parse(inputStream);
					} finally {
						try {
							inputStream.close();
						} catch (IOException e) {
							log.log(Level.WARNING,
									"Can't close input stream for web resource "
											+ facesConfigPath, e);
						}
					}
				}
			}
		}
	}

	protected void parseClasspath(PortletContext context)
			throws ParsingException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (null == classLoader) {
			classLoader = context.getClass().getClassLoader();
		}
		try {
			// Parse all faces-config.xml files in the classpath.
			Enumeration<URL> resources = classLoader
					.getResources(FACES_CONFIG_CLASS_PATH);
			while (resources.hasMoreElements()) {
				URL resourceURL = (URL) resources.nextElement();
				try {
					URLConnection connection = resourceURL.openConnection();
					// To avoid file locking in the Windows environmemt.
					connection.setUseCaches(false);
					InputStream inputStream = connection.getInputStream();
					try {
						parse(inputStream);
					} finally {
						inputStream.close();
					}
				} catch (IOException e) {
					log.log(Level.WARNING, "Can't parse "
							+ resourceURL.toExternalForm(), e);
				}
			}
		} catch (IOException e) {
			log.log(Level.WARNING,
					"Can't get META-INF/faces-config.xml resources", e);
		}
	}

	protected void parse(InputStream facesConfig) throws ParsingException {
		try {
			SAXParser parser = getParser();
			//WL 10.3 parser returns new instance of XMLReader everytime
			XMLReader reader = parser.getXMLReader();
			FacesConfigHandler facesConfigHandler = new FacesConfigHandler(reader);
			reader.setContentHandler(facesConfigHandler);
            reader.setEntityResolver(WebXML.NULL_RESOLVER);
            reader.setErrorHandler(facesConfigHandler);
            reader.setDTDHandler(facesConfigHandler);
			reader.parse(new InputSource(facesConfig));
			excludedAttributes.addAll(facesConfigHandler.getExcludedAttributes());
			parameterMapping.putAll(facesConfigHandler.getParameterMapping());
		} catch (SAXException e) {
			log.log(Level.WARNING, "Exception at faces-config.xml parsing", e);
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception at faces-config.xml parsing", e);
		}
	}

	protected SAXParser getParser() throws ParsingException {
		try {
			SAXParser parser = factory.newSAXParser();
			return parser;
		} catch (ParserConfigurationException e) {
			throw new ParsingException("SAX Parser configuration error", e);
		} catch (SAXException e) {
			throw new ParsingException("SAX Parser instantiation error", e);
		}
	}

	public List<String> getExcludedAttributes() {
		return excludedAttributes;
	}

	/**
     * @return the parameterMapping
     */
    public Map<String, String> getParameterMapping() {
    	return parameterMapping;
    }
}
