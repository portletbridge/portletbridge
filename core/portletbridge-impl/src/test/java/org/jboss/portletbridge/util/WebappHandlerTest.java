/**
 * 
 */
package org.jboss.portletbridge.util;

import junit.framework.TestCase;

import org.xml.sax.ContentHandler;

/**
 * @author asmirnov
 *
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

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		reader = new MockXmlReader();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testServletElement() throws Exception {
		WebappHandler handler = new WebappHandler(reader);
		reader.setContentHandler(handler);
		handler.startElement(null, WEBAPP, WEBAPP, null);
		assertEquals(handler, reader.getContentHandler());
		handler.startElement(null, SERVLET, SERVLET, null);
		ContentHandler servletHandler = reader.getContentHandler();
		assertSame(WebappHandler.ServletHandler.class, reader.getContentHandler().getClass());
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
		assertEquals("Faces Servlet", handler.getFacesServlet().getName());
	}
	
	public void testMappingElement() throws Exception {
		WebappHandler handler = new WebappHandler(reader);
		reader.setContentHandler(handler);
		handler.startElement(null, WEBAPP, WEBAPP, null);
		assertEquals(handler, reader.getContentHandler());
		handler.startElement(null, SERVLET_MAPPING, SERVLET_MAPPING, null);
		ContentHandler servletHandler = reader.getContentHandler();
		assertSame(WebappHandler.MappingHandler.class, reader.getContentHandler().getClass());
		servletHandler.startElement(null, SERVLET_NAME, SERVLET_NAME, null);
		assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
		
	}

	public void testErrorPagesElement() throws Exception {
		WebappHandler handler = new WebappHandler(reader);
		reader.setContentHandler(handler);
		handler.startElement(null, WEBAPP, WEBAPP, null);
		assertEquals(handler, reader.getContentHandler());
		handler.startElement(null, ERROR_PAGE, ERROR_PAGE, null);
		ContentHandler errorPageHandler = reader.getContentHandler();
		assertSame(WebappHandler.ErrorPageHandler.class, reader.getContentHandler().getClass());
		errorPageHandler.startElement(null, EXCEPTION_TYPE,EXCEPTION_TYPE, null);
		assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
		reader.getContentHandler().endElement(null, EXCEPTION_TYPE,EXCEPTION_TYPE);
		errorPageHandler.startElement(null, LOCATION,LOCATION, null);
		assertSame(StringContentHandler.class, reader.getContentHandler().getClass());
		reader.getContentHandler().endElement(null, LOCATION,LOCATION);
		assertSame(WebappHandler.ErrorPageHandler.class, reader.getContentHandler().getClass());
		errorPageHandler.endElement(null, ERROR_PAGE, ERROR_PAGE);
		assertEquals(handler, reader.getContentHandler());
		
	}
}
