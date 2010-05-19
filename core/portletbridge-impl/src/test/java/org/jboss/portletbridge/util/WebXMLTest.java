/**
 * 
 */
package org.jboss.portletbridge.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.servlet.ServletException;

import junit.framework.TestCase;

/**
 * @author asmirnov
 *
 */
public class WebXMLTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.util.WebXML#parse(java.io.InputStream)}.
	 */
	public void testParse() throws Exception {
		WebXML webXml = new WebXML();
		InputStream inputStream = this.getClass().getResourceAsStream("/test-web.xml");
		webXml.parse(inputStream);
		inputStream.close();
		assertEquals(2, webXml.getFacesServletMappings().size());
		assertEquals("*.jsf", webXml.getFacesServletMappings().get(1));
		assertEquals("/faces/*", webXml.getFacesServletMappings().get(0));
		webXml.createErrorViews(".jspx");
		assertEquals(2, webXml.errorLocations.size());
		assertEquals("/faces/error.xhtml", webXml.errorLocations.get(ServletException.class.getName()));
		assertEquals("/error.jsf", webXml.errorLocations.get(ViewExpiredException.class.getName()));
	}
	
	public void testGetViewIdFromLocation() throws Exception {
		WebXML webXml = new WebXML();
		webXml.facesServletMappings = new ArrayList<String>();
		webXml.facesServletMappings.add("*.jsf");
		webXml.facesServletMappings.add("/faces/*");
		webXml.facesServletMappings.add("/seam*");
		assertNull(webXml.getViewIdFromLocation("/foo/bar.jsp", ".jspx"));
		assertEquals("/foo/bar.jspx",webXml.getViewIdFromLocation("/foo/bar.jsf", ".jspx"));
		assertEquals("/foo/bar.jsp",webXml.getViewIdFromLocation("/faces/foo/bar.jsp", ".jspx"));
		assertEquals("/foo/bar.jsp",webXml.getViewIdFromLocation("/seam/foo/bar.jsp", ".jspx"));
	}
	
	public void testCreateErrorViews() throws Exception {
		WebXML webXml = new WebXML();
		webXml.facesServletMappings = new ArrayList<String>();
		webXml.facesServletMappings.add("*.jsf");
		webXml.errorLocations = new LinkedHashMap<String, String>();
		webXml.errorLocations.put(IOException.class.getName(), "/foo/bar.jsf");
		webXml.errorLocations.put(FacesException.class.getName(), "/error/faces.jsf");
		webXml.errorLocations.put(ServletException.class.getName(), "/foo/bar.jsp");
		webXml.errorLocations.put("no.such.Exception", "/foo/baz.jsp");
		Map<Class<? extends Throwable>, String> errorViews = webXml.createErrorViews(".jspx");
		assertEquals(2, errorViews.size());
		assertEquals("/foo/bar.jspx", errorViews.get(IOException.class));
		assertEquals("/error/faces.jspx", errorViews.get(FacesException.class));
	}

}
