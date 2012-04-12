package org.jboss.portletbridge.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.portlet.PortletContext;
import javax.servlet.ServletException;

import junit.framework.TestCase;

/**
 * @author asmirnov
 * 
 */
public class WebXMLTest extends TestCase {

    /**
     * Test method for {@link org.jboss.portletbridge.config.WebXML#parse(java.io.InputStream)}.
     */
    public void testParse() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/test-web.xml");
        WebXmlProcessor webXml = new WebXmlProcessor(inputStream);
        inputStream.close();
        assertEquals(2, webXml.getFacesServlet().getMappings().size());
        assertEquals("*.jsf", webXml.getFacesServlet().getMappings().get(1));
        assertEquals("/faces/*", webXml.getFacesServlet().getMappings().get(0));
        Map<Class<? extends Throwable>, String> errorViews = webXml.createErrorViews(".jspx");
        assertEquals(2, errorViews.size());
        assertEquals("/error.xhtml", errorViews.get(ServletException.class));
        assertEquals("/error.jspx", errorViews.get(ViewExpiredException.class));
    }

    public void testGetViewIdFromLocation() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        WebXmlProcessor.facesServlet.getMappings().add("/faces/*");
        WebXmlProcessor.facesServlet.getMappings().add("/seam*");
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);
        assertNull(webXml.getViewIdFromLocation("/foo/bar.jsp", ".jspx"));
        assertEquals("/foo/bar.jspx", webXml.getViewIdFromLocation("/foo/bar.jsf", ".jspx"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/faces/foo/bar.jsp", ".jspx"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/seam/foo/bar.jsp", ".jspx"));
    }

    public void testCreateErrorViews() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        WebXmlProcessor.errorPages.put(IOException.class.getName(), "/foo/bar.jsf");
        WebXmlProcessor.errorPages.put(FacesException.class.getName(), "/error/faces.jsf");
        WebXmlProcessor.errorPages.put(ServletException.class.getName(), "/foo/bar.jsp");
        WebXmlProcessor.errorPages.put("no.such.Exception", "/foo/baz.jsp");
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);
        Map<Class<? extends Throwable>, String> errorViews = webXml.createErrorViews(".jspx");
        assertEquals(3, errorViews.size());
        assertEquals("/foo/bar.jspx", errorViews.get(IOException.class));
        assertEquals("/error/faces.jspx", errorViews.get(FacesException.class));
        assertEquals("/error.jspx", errorViews.get(ViewExpiredException.class));
    }

}
