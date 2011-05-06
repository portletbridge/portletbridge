/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class WebappHandler extends DefaultHandler {

	private static final String SERVLET_MAPPING_ELEMENT = "servlet-mapping";

	private static final String ERROR_PAGE_ELEMENT = "error-page";

	private static final String SERVLET_ELEMENT = "servlet";

	private static final String LOCATION_ELEMENT = "location";

	private static final String EXCEPTION_CLASS_ELEMENT = "exception-type";

	static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

	private List<ServletBean> servlets = new ArrayList<ServletBean>();

	private List<ServletMapping> mappings = new ArrayList<ServletMapping>();
	
	private Map<String,String> errorPages = new LinkedHashMap<String, String>();

	private ServletBean facesServlet;

	private XMLReader xmlReader;

	static final String URL_PATTERN_ELEMENT = "url-pattern";

	static final String SERVLET_NAME_ELEMENT = "servlet-name";

	static final String SERVLET_CLASS_ELEMENT = "servlet-class";

	public WebappHandler(XMLReader reader) {
		this.xmlReader = reader;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
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
				for (ServletMapping mapping : mappings) {
					if (servlet.getName().equals(mapping.getServletName())) {
						facesServlet.getMappings().add(mapping.getUrlPattern());
					}
				}
			}
		}
	}
	
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException
    {
      // Do nothing, to avoid network requests to external DTD/Schema
      return new InputSource(new StringReader(""));
    }


	final class ServletHandler extends StateHandler {

		private StringBuilder servletName = new StringBuilder();
		private StringBuilder servletClass = new StringBuilder();

		public ServletHandler() {
			super(xmlReader, WebappHandler.this);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
				Attributes attributes) {
			ContentHandler nextHandler = null;
			if (SERVLET_NAME_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						servletName);
			} else if (SERVLET_CLASS_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						servletClass);
			}
			return nextHandler;
		}

		@Override
		protected void endLastElement() {
			servlets.add(new ServletBean(servletName.toString().trim(),
					servletClass.toString().trim()));
		}
	}

	final class MappingHandler extends StateHandler {

		private StringBuilder servletName = new StringBuilder();
		private StringBuilder urlPattern = new StringBuilder();

		public MappingHandler() {
			super(xmlReader, WebappHandler.this);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
				Attributes attributes) {
			ContentHandler nextHandler = null;
			if (SERVLET_NAME_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						servletName);
			} else if (URL_PATTERN_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						urlPattern);
			}
			return nextHandler;
		}

		@Override
		protected void endLastElement() {
			mappings.add(new ServletMapping(servletName.toString().trim(),
					urlPattern.toString().trim()));
		}
	}

	final class ErrorPageHandler extends StateHandler {

		private StringBuilder exceptionType = new StringBuilder();
		private StringBuilder location = new StringBuilder();

		public ErrorPageHandler() {
			super(xmlReader, WebappHandler.this);
		}

		@Override
		protected ContentHandler getNextHandler(String uri, String localName,
				Attributes attributes) {
			ContentHandler nextHandler = null;
			if (EXCEPTION_CLASS_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						exceptionType);
			} else if (LOCATION_ELEMENT.equals(localName)) {
				nextHandler = new StringContentHandler(getReader(), this,
						location);
			}
			return nextHandler;
		}

		@Override
		protected void endLastElement() {
			errorPages.put(exceptionType.toString().trim(),
					location.toString().trim());
		}
	}

	public XMLReader getXmlReader() {
		return xmlReader;
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

}
