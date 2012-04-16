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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RenderRequestTest extends GenericPortletTestBase {

    @Mock
    private RenderRequest request;

    @Mock
    private RenderResponse response;

    @Before
    public void setupRequest() {

    }

    /**
     * Test method for
     * {@link javax.portlet.faces.GenericFacesPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
     * .
     *
     * @throws PortletException
     * @throws IOException
     */
    @Test
    public void testDoDispatchRenderRequestRenderResponse() throws PortletException, IOException {
        GenericFacesPortlet portlet = createGenericPortlet();
        doReturn(portletContext).when(portlet).getPortletContext();
        doReturn(portletConfig).when(portlet).getPortletConfig();
        when(request.getPortletMode()).thenReturn(PortletMode.VIEW);
        when(request.getWindowState()).thenReturn(WindowState.NORMAL);
        portlet.doDispatch(request, response);
        verify(portlet).doFacesDispatch(request, response);
    }

}
