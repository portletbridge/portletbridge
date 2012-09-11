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
        Map<Class<? extends Throwable>, String> errorViews = webXml.createErrorViews();
        assertEquals(2, errorViews.size());
        assertEquals("/error.xhtml", errorViews.get(ServletException.class));
        assertEquals("/error", errorViews.get(ViewExpiredException.class));
    }

    public void testGetViewIdFromLocation() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        WebXmlProcessor.facesServlet.getMappings().add("/faces/*");
        WebXmlProcessor.facesServlet.getMappings().add("/seam*");
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);
        assertEquals("/foo/bar", webXml.getViewIdFromLocation("/foo/bar.jsf"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/faces/foo/bar.jsp"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/seam/foo/bar.jsp"));
    }

    public void testCreateErrorViews() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        WebXmlProcessor.errorPages.put(IOException.class.getName(), "/foo/bar.jsf");
        WebXmlProcessor.errorPages.put(FacesException.class.getName(), "/error/faces.jsf");
        WebXmlProcessor.errorPages.put(ServletException.class.getName(), "/foo/bar.jsp");
        WebXmlProcessor.errorPages.put("no.such.Exception", "/foo/baz.jsp");
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);
        Map<Class<? extends Throwable>, String> errorViews = webXml.createErrorViews();
        assertEquals(3, errorViews.size());
        assertEquals("/foo/bar", errorViews.get(IOException.class));
        assertEquals("/error/faces", errorViews.get(FacesException.class));
        assertEquals("/error", errorViews.get(ViewExpiredException.class));
    }

}
