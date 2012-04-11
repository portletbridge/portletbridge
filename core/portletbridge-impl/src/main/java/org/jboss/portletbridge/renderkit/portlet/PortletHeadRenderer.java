/**
 * Copyright (C) 2010 portletfaces.org
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *               http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is PortletFaces open source software code.
 *
 * The Initial Developer of the Original Code is mimacom ag, Switzerland.
 * Portions created by the Initial Developer are Copyright (C) 2010
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License.
 */
package org.jboss.portletbridge.renderkit.portlet;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from rendering
 * the <head>...</head> section, which is what is done by the JSF implementation's version of this renderer. This renderer
 * avoids rendering the <head>...</head> section and instead delegates that responsibility to the portal.
 * 
 * @author Neil Griffin
 */
public class PortletHeadRenderer extends Renderer {

    // Logger
    private static BridgeLogger logger = new JULLoggerImpl(PortletHeadRenderer.class.getName());

    private static final String TARGET_HEAD = "head";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // do nothing
    }

    /**
     * Rather than render the <head>...</head> section to the response, this method attempts to delegate this responsibility to
     * the portlet container.
     * 
     * @see Renderer#encodeBegin(FacesContext, UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        logger.log(Level.INFO, "Header encodeBegin()");
        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        List<UIComponent> uiComponentResources = uiViewRoot.getComponentResources(facesContext, TARGET_HEAD);
        for (UIComponent uiComponentResource : uiComponentResources) {
            uiComponentResource.encodeAll(facesContext);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // this renderer only encodes head resources
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    };
}
