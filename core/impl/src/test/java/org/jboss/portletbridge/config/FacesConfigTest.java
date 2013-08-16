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

import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author asmirnov
 *
 */
public class FacesConfigTest extends TestCase {

    /**
     * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
     *
     * @throws ParsingException
     */
    public void testParseInputStream() throws Exception {
        InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config.xml");
        FacesConfigProcessor config = new FacesConfigProcessor();
        config.parse(facesConfigResource, FacesConfigProcessor.ParsingLocation.DEFAULT);
        assertEquals(2, FacesConfigProcessor.getExcludedAttributes().size());
        assertTrue(FacesConfigProcessor.getExcludedAttributes().contains("foo.bar"));
        assertTrue(FacesConfigProcessor.getExcludedAttributes().contains("foo.baz.*"));
    }

    /**
     * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
     *
     * @throws ParsingException
     */
    public void testPublicParameters() throws Exception {
        InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config-params.xml");
        FacesConfigProcessor config = new FacesConfigProcessor();
        config.parse(facesConfigResource, FacesConfigProcessor.ParsingLocation.DEFAULT);
        Map<String, String> mapping = FacesConfigProcessor.getPublicParameterMappings();
        assertEquals(3, mapping.size());
        assertEquals("UserBean.lastName", mapping.get("name"));
        assertEquals("AnotherUser.name", mapping.get("AnotherPortlet:name"));
    }

}
