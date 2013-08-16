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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.ServletException;

import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author asmirnov
 *
 */
@RunWith(JUnit4.class)
public class WebXMLTest {

    @Before
    public void resetProcessor() {
        WebXmlProcessor.scan = new AtomicBoolean(true);
        WebXmlProcessor.setServlets(new ArrayList<ServletBean>());
        WebXmlProcessor.setUrlMappings(new HashMap<String, ArrayList<String>>());
        WebXmlProcessor.setErrorPages(new LinkedHashMap<String, String>());
        WebXmlProcessor.facesServlet = null;
        WebXmlProcessor.errorViews = new LinkedHashMap<Class<? extends Throwable>, String>();
    }

    /**
     * Test method for {@link org.jboss.portletbridge.config.WebXmlProcessor#parse(java.io.InputStream)}.
     */
    @Test
    public void parse() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/test-web.xml");
        WebXmlProcessor webXml = new WebXmlProcessor(
                getPortletContext(Descriptors.importAs(WebAppDescriptor.class)
                                             .fromStream(inputStream))
                );
        inputStream.close();

        assertEquals(2, webXml.getFacesServlet().getMappings().size());
        assertEquals("*.jsf", webXml.getFacesServlet().getMappings().get(1));
        assertEquals("/faces/*", webXml.getFacesServlet().getMappings().get(0));
        assertEquals(2, WebXmlProcessor.errorViews.size());
        assertEquals("/error.xhtml", WebXmlProcessor.errorViews.get(ServletException.class));
        assertEquals("/error", WebXmlProcessor.errorViews.get(ViewExpiredException.class));
    }

    @Test
    public void getViewIdFromLocation() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        WebXmlProcessor.facesServlet.getMappings().add("/faces/*");
        WebXmlProcessor.facesServlet.getMappings().add("/seam*");
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);

        assertEquals("/foo/bar", webXml.getViewIdFromLocation("/foo/bar.jsf"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/faces/foo/bar.jsp"));
        assertEquals("/foo/bar.jsp", webXml.getViewIdFromLocation("/seam/foo/bar.jsp"));
    }

    @Test
    public void createErrorViews() throws Exception {
        WebXmlProcessor.facesServlet = new ServletBean();
        WebXmlProcessor.facesServlet.getMappings().add("*.jsf");
        LinkedHashMap<String, String> pages = new LinkedHashMap<String, String>();
        pages.put(IOException.class.getName(), "/foo/bar.jsf");
        pages.put(FacesException.class.getName(), "/error/faces.jsf");
        pages.put(ServletException.class.getName(), "/foo/bar.jsp");
        pages.put("no.such.Exception", "/foo/baz.jsp");
        WebXmlProcessor.setErrorPages(pages);
        WebXmlProcessor webXml = new WebXmlProcessor((PortletContext) null);
        webXml.createErrorViews();

        assertEquals(2, WebXmlProcessor.errorViews.size());
        assertEquals("/foo/bar", WebXmlProcessor.errorViews.get(IOException.class));
        assertEquals("/error/faces", WebXmlProcessor.errorViews.get(FacesException.class));
    }

    @Test
    public void emptyErrorPages() throws Exception {
        WebXmlProcessor webXml = new WebXmlProcessor(getPortletContext(buildWebXml()));

        assertEquals(0, webXml.getErrorViews().size());
        assertEquals(0, webXml.getErrorPages().size());
    }

    @Test
    public void invalidExceptionClass() throws Exception {
        WebAppDescriptor webApp = buildWebXml();
        webApp.createErrorPage()
                    .exceptionType("javax.servlet.ServletSillyException")
                    .location("/faces/error.xhtml")
                    .up()
              .createErrorPage()
                .exceptionType("javax.servlet.ServletException")
                .location("/faces/error.xhtml")
                .up();

        WebXmlProcessor webXml = new WebXmlProcessor(getPortletContext(webApp));

        assertEquals(1, webXml.getErrorViews().size());
    }

    @Test
    public void invalidErrorPage() throws Exception {
        WebAppDescriptor webApp = buildWebXml();
        webApp.createErrorPage()
                .exceptionType("java.lang.Exception")
                .location("/face/error.xhtml")
                .up()
                .createErrorPage()
                .exceptionType("javax.servlet.ServletException")
                .location("/faces/error.xhtml")
                .up();

        WebXmlProcessor webXml = new WebXmlProcessor(getPortletContext(webApp));

        assertEquals(1, webXml.getErrorViews().size());
    }

    private WebAppDescriptor buildWebXml() {
        WebAppDescriptor webApp = Descriptors.create(WebAppDescriptor.class);
        webApp.addDefaultNamespaces()
                .version("3.0")
                .displayName("WebAppXmlTest")
                .createContextParam()
                    .paramName("javax.faces.DEFAULT_SUFFIX")
                    .paramValue(".xhtml")
                    .up()
                .createServlet()
                .servletName("FacesServlet")
                .servletClass("javax.faces.webapp.FacesServlet")
                .loadOnStartup(1)
                .up()
                .createServletMapping()
                .servletName("FacesServlet")
                .urlPattern("/faces/*")
                .up()
                .createServletMapping()
                .servletName("FacesServlet")
                .urlPattern("*.jsf")
                .up();
        return webApp;
    }

    private PortletContext getPortletContext(final WebAppDescriptor webApp) {
        return new PortletContext() {
            @Override
            public String getServerInfo() {
                return null;
            }

            @Override
            public PortletRequestDispatcher getRequestDispatcher(String path) {
                return null;
            }

            @Override
            public PortletRequestDispatcher getNamedDispatcher(String name) {
                return null;
            }

            @Override
            public InputStream getResourceAsStream(String path) {
                return new ByteArrayInputStream(webApp.exportAsString().getBytes());
            }

            @Override
            public int getMajorVersion() {
                return 0;
            }

            @Override
            public int getMinorVersion() {
                return 0;
            }

            @Override
            public String getMimeType(String file) {
                return null;
            }

            @Override
            public String getRealPath(String path) {
                return null;
            }

            @Override
            public Set<String> getResourcePaths(String path) {
                return null;
            }

            @Override
            public URL getResource(String path) throws MalformedURLException {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }

            @Override
            public void log(String msg) {
            }

            @Override
            public void log(String message, Throwable throwable) {
            }

            @Override
            public void removeAttribute(String name) {
            }

            @Override
            public void setAttribute(String name, Object object) {
            }

            @Override
            public String getPortletContextName() {
                return null;
            }

            @Override
            public Enumeration<String> getContainerRuntimeOptions() {
                return null;
            }
        };
    }
}
