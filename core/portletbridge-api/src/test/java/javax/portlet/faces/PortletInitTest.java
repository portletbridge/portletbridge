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

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.portlet.PortletException;

import org.junit.Test;

public class PortletInitTest extends GenericPortletTestBase {

    @Test
    public void preseveActionParams() throws Exception {
        whenPortletInitParameter("javax.portlet.faces.preserveActionParams", "true");
        initPortlet();
        verifyContextAttribute("javax.portlet.faces.foo.preserveActionParams", Boolean.TRUE);
    }

    @Test
    public void excludedRequestAttreibutes() throws Exception {
        whenPortletInitParameter("javax.portlet.faces.excludedRequestAttributes", "bar,baz,boo");
        initPortlet();
        verifyContextAttribute("javax.portlet.faces.foo.excludedRequestAttributes", Arrays.asList("bar", "baz", "boo"));
    }

    @Test
    public void customAttribute() throws Exception {
        whenPortletInitParameter("javax.portlet.faces.extension.my_package.my_attribute", "xxx");
        initPortlet();
        verifyContextAttribute("javax.portlet.faces.extension.my_package.foo.my_attribute", "xxx");
    }

    private void initPortlet() throws PortletException {
        GenericFacesPortlet portlet = createGenericPortlet();
        portlet.init(portletConfig);
    }

    private void whenPortletInitParameter(String name, String value) {
        when(portletConfig.getInitParameter(name)).thenReturn(value);
        portletInitParameters.add(name);
    }

    private void verifyContextAttribute(String name, Object value) {
        verify(portletContext, atLeastOnce()).setAttribute(name, value);
    }
}
