/**
 * 
 */
package org.jboss.portletbridge.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.webapp.FacesServlet;
import javax.portlet.PortletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;

/**
 * This class reads content of the portlet application config file and collects
 * information about faces servlet mappings and error pages configuration.
 * @author asmirnov
 * 
 */
public class WebXML {

	public static final EntityResolver2 NULL_RESOLVER = new EntityResolver2(){
	
					public InputSource resolveEntity(String publicId,
							String systemId) throws SAXException, IOException {
						return new InputSource(new StringReader(""));
					}
	
					public InputSource getExternalSubset(String name, String baseURI)
							throws SAXException, IOException {
						return new InputSource(new StringReader(""));
					}
	
					public InputSource resolveEntity(String name, String publicId,
							String baseURI, String systemId) throws SAXException,
							IOException {
                     // Do nothing, to avoid network requests to external DTD/Schema
                     return new InputSource(new StringReader(""));
	            	
	            }
   };
	/**
	 * location of web application config.
	 */
	private static final String WEB_XML = "/WEB-INF/web.xml";
	/**
	 * list of FacesServlet mappings.
	 */
	List<String> facesServletMappings;
	/**
	 * map between error classes and JSF view ids.
	 */
	Map<Class<? extends Throwable>, String> errorViews;
	/**
	 * map between error class names and error page locations
	 */
	Map<String, String> errorLocations;
	private SAXParserFactory factory;

	public WebXML() {
		factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
    }
	/**
	 * Parse web.xml file from input stream.
	 * @param webXml
	 */
	public void parse(InputStream webXml) {
		try {
			SAXParser parser = factory.newSAXParser();
			//WL 10.3 parser returns new instance of XMLReader everytime
			XMLReader reader = parser.getXMLReader();
			
			// Parse web.xml with state-avare content handler.
			WebappHandler webappHandler = new WebappHandler(reader);
            
			reader.setContentHandler(webappHandler);
            reader.setEntityResolver(NULL_RESOLVER);
            reader.setErrorHandler(webappHandler);
            reader.setDTDHandler(webappHandler);

			reader.parse(new InputSource(webXml));
			
			ServletBean facesServlet = webappHandler.getFacesServlet();
			if (null == facesServlet) {
				throw new FacesException(
						"Faces Servlet did not found, is this a JSF application?");
			}
			facesServletMappings = facesServlet.getMappings();
			errorLocations = webappHandler.getErrorPages();
		} catch (Exception e) {
			throw new FacesException("XML parsing error", e);
		}
	}

	/**
	 * getter for the list contains {@link FacesServlet} mappings. 
	 * @return 
	 */
	public List<String> getFacesServletMappings() {
		return facesServletMappings;
	}

	/**
	 * Convert error page location into JSF viewId.
	 * @param location error page location
	 * @param viewSuffix JSF view suffix.
	 * @return view id if this location is mapped to the {@link FacesServlet} othervise null.
	 */
	protected String getViewIdFromLocation(String location, String viewSuffix) {
		String viewId = null;
		for (String mapping : facesServletMappings) {
			if (mapping.startsWith("*")) {
				// Suffix mapplig.
				String suffix = mapping.substring(1);
				if (location.endsWith(suffix)) {
					viewId = location.substring(0, location.length()
							- suffix.length())
							+ viewSuffix;
					break;
				}
			} else if (mapping.endsWith("*")) {
				// Preffix mapping.
				String prefix = mapping.substring(0, mapping.length() - 1);
				if (location.startsWith(prefix)) {
					int index = prefix.length();
					if(prefix.endsWith("/")){
						index--;
					}
					viewId = location.substring(index);
				}
			}
		}
		return viewId;
	}

	/**
	 * Parse application config file /WEB-INF/web.xml in the application context.
	 * @param portletContext
	 */
	public void parse(PortletContext portletContext) {
		InputStream inputStream = portletContext.getResourceAsStream(WEB_XML);
		parse(inputStream);
		String viewSuffix = portletContext
				.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
		if (null == viewSuffix) {
			viewSuffix = ViewHandler.DEFAULT_SUFFIX;
		}
		errorViews = createErrorViews(viewSuffix);
		try {
			inputStream.close();
		} catch (IOException e) {
			portletContext.log("Error parsing web.xml", e);
		}

	}

	/**
	 * Create map between error class and corresponding JSF view id.
	 * Map created from the {@link #errorLocations} string-based map.
	 * @param viewSuffix JSF view id suffix for mapping.
	 * @return map between exception class and view id.
	 */
	protected Map<Class<? extends Throwable>, String> createErrorViews(String viewSuffix) {
		LinkedHashMap<Class<? extends Throwable>, String> viewsMap = new LinkedHashMap<Class<? extends Throwable>, String>();
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (null == classLoader) {
			classLoader = this.getClass().getClassLoader();
		}
		for (Entry<String, String> entry : errorLocations.entrySet()) {
			try {
				Class<? extends Throwable> clazz = classLoader.loadClass(
						entry.getKey()).asSubclass(Throwable.class);
				String viewId = getViewIdFromLocation(entry.getValue(),
						viewSuffix);
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
	 * getter for the map between error class and corresponding JSF view id.
	 * @return the {@link #errorViews}
	 */
	public Map<Class<? extends Throwable>, String> getErrorViews() {
		return errorViews;
	}
}
