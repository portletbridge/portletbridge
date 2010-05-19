/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge.renderkit.portlet;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.faces.BridgeUtil;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @version $Revision: 630 $
 */
public class PortletViewRootRenderer extends Renderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
	        throws IOException {
		if (BridgeUtil.isPortletRequest()) {
			ResponseWriter writer = context.getResponseWriter();
			Object namespace = component.getClientId(context);
			// encode portletbridge window marker
			writer.startElement("div", component);
			writer.writeAttribute("id", namespace, "id");

		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
	        throws IOException {
		if (BridgeUtil.isPortletRequest()) {
			// Encode portletbridge window marker
			ResponseWriter writer = context.getResponseWriter();
			writer.endElement("div");
		}
	}

}
