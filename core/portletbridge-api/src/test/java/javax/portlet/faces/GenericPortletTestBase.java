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
package javax.portlet.faces;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class GenericPortletTestBase {

    static final String FOO = "foo";

    @Mock
    protected ServletContext servletContext;
    @Mock
    protected PortletConfig portletConfig;
    @Mock
    protected PortletContext portletContext;

    protected GenericFacesPortlet facesPortlet;

    protected Collection<String> portletInitParameters = new ArrayList<String>();

    protected Collection<String> contextInitParameters = new ArrayList<String>();

    public GenericPortletTestBase() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        when(portletConfig.getInitParameterNames()).thenAnswer(enumerationAnswer(portletInitParameters));
        when(portletContext.getInitParameterNames()).thenAnswer(enumerationAnswer(contextInitParameters));
        when(portletConfig.getPortletContext()).thenReturn(portletContext);
        when(portletConfig.getPortletName()).thenReturn(FOO);
        facesPortlet = spy(new GenericFacesPortlet());
        when(portletContext.getInitParameter("javax.portlet.faces.BridgeClassName")).thenReturn(Bridge.class.getName());
    }

    private Answer<Enumeration<String>> enumerationAnswer(final Collection<String> collection) {
        return new Answer<Enumeration<String>>() {

            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                return Collections.enumeration(collection);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    protected GenericFacesPortlet createGenericPortlet() {
        return facesPortlet;
    }

}
