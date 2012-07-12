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
package org.jboss.portletbridge.context;

import junit.framework.TestCase;

/**
 * @author asmirnov
 *
 */
public class PortalActionUrlTest extends TestCase {

    /**
     * @param name
     */
    public PortalActionUrlTest(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExternalUrl() throws Exception {
        PortalActionURL url = new PortalActionURL("http://www.apache.org?a=b");
        assertEquals("http:", url.getProtocol());
        assertEquals("//www.apache.org", url.getHost());
        assertEquals(-1, url.getPort());
        assertEquals("", url.getPath());
        assertEquals("a=b", url.getQueryString());
    }

    public void testActionUrl() throws Exception {
        PortalActionURL url = new PortalActionURL("ftp://foo.bar:90/some/path/index.jsf?a=b&cd=ef");
        assertEquals("ftp:", url.getProtocol());
        assertEquals("//foo.bar", url.getHost());
        assertEquals(90, url.getPort());
        assertEquals("/some/path/index.jsf", url.getPath());
        assertEquals("a=b&cd=ef", url.getQueryString());
    }

    public void testActionUrl1() throws Exception {
        PortalActionURL url = new PortalActionURL("//foo.bar:90/some/path/index.jsf?a=b&cd=ef");
        assertNull(url.getProtocol());
        assertEquals("//foo.bar", url.getHost());
        assertEquals(90, url.getPort());
        assertEquals("/some/path/index.jsf", url.getPath());
        assertEquals("a=b&cd=ef", url.getQueryString());
    }

    public void testActionUrl2() throws Exception {
        PortalActionURL url = new PortalActionURL("//foo.bar/some/path/index.jsf?a=b&cd=ef");
        assertNull(url.getProtocol());
        assertEquals("//foo.bar", url.getHost());
        assertEquals(-1, url.getPort());
        assertEquals("/some/path/index.jsf", url.getPath());
        assertEquals("a=b&cd=ef", url.getQueryString());
    }

    public void testActionUrl3() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf?a=b&cd=ef");
        assertNull(url.getProtocol());
        assertNull(url.getHost());
        assertEquals(-1, url.getPort());
        assertEquals("/some/path/index.jsf", url.getPath());
        assertEquals("a=b&cd=ef", url.getQueryString());
    }

    public void testActionUrl4() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf");
        assertNull(url.getProtocol());
        assertNull(url.getHost());
        assertEquals(-1, url.getPort());
        assertEquals("/some/path/index.jsf", url.getPath());
        assertNull(url.getQueryString());
    }

    public void testParseQueryString() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf?a=b&cd=ef");
        assertEquals(2, url.parametersSize());
        assertEquals("b", url.getParameter("a"));
        assertEquals("ef", url.getParameter("cd"));
        assertNull(url.getParameter("xxx"));
    }

    public void testParseQueryString1() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf?a=b");
        assertEquals(1, url.parametersSize());
        assertEquals("b", url.getParameter("a"));
        assertNull(url.getParameter("cd"));
    }

    public void testParseQueryString2() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf?+a=%40b");
        assertEquals(1, url.parametersSize());
        assertEquals("@b", url.getParameter(" a"));
        assertEquals("+a=%40b", url.getQueryString());
        assertNull(url.getParameter("cd"));
    }

    public void testParseQueryString3() throws Exception {
        PortalActionURL url = new PortalActionURL("/some/path/index.jsf?a=b&amp;cd=ef");
        assertEquals(2, url.parametersSize());
        assertEquals("b", url.getParameter("a"));
        assertEquals("ef", url.getParameter("cd"));
        assertNull(url.getParameter("xxx"));
    }
}
