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
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from
 * rendering the <head>...</head> section, which is what is done by the JSF implementation's version of this renderer.
 * This renderer avoids rendering the <head>...</head> section and instead delegates that responsibility to the portal.
 *
 * @author Neil Griffin
 */
public class PortletBodyRenderer extends Renderer {

    // Logger
    private static BridgeLogger logger = new JULLoggerImpl(PortletBodyRenderer.class.getName());
    // Private Constants
    private static final String ATTR_STYLE_CLASS = "styleClass";
    private static final String ELEMENT_DIV = "div";
    private static final String[] BODY_PASS_THRU_ATTRIBUTES = new String[] { "onclick", "ondblclick", "onkeydown",
        "onkeypress", "onkeyup", "onload", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup",
        "onunload", "styleClass", "title" };
    private static final String STYLE_CLASS_PORTLET_BODY = "portletfaces-bridge-body";
    private static final String TARGET_BODY = "body";

    /**
     * It is forbidden for a portlet to render the &amp;&lt;body&amp;&gt; element, so instead, render a
     * &amp;&lt;div&amp;&gt;element.
     *
     * @see Renderer#encodeBegin(FacesContext, UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        logger.log(Level.INFO, "encodeBegin()");
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement(ELEMENT_DIV, uiComponent);
        String portletClientId = facesContext.getViewRoot().getContainerClientId(facesContext);
        responseWriter.writeAttribute("id", portletClientId, "id");
        for (int i = 0; i < BODY_PASS_THRU_ATTRIBUTES.length; i++) {
            String attributeName = BODY_PASS_THRU_ATTRIBUTES[i];
            String renderedName = attributeName;
            Object attributeValue = uiComponent.getAttributes().get(attributeName);

            if (attributeName.equals(ATTR_STYLE_CLASS)) {
                renderedName = "class";

                // Add a special CSS class name in order to clue-in the
                // developer who might be examining the rendered
                // markup that a <div> was rendered instead of <body>.
                if (attributeValue == null) {
                    attributeValue = STYLE_CLASS_PORTLET_BODY;
                } else {
                    attributeValue = attributeValue.toString() + " " + STYLE_CLASS_PORTLET_BODY;
                }
            }

            if (attributeValue != null) {
                responseWriter.writeAttribute(renderedName, attributeValue, attributeName);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        List<UIComponent> uiComponentResources = uiViewRoot.getComponentResources(facesContext, TARGET_BODY);
        for (UIComponent uiComponentResource : uiComponentResources) {
            uiComponentResource.encodeAll(facesContext);
        }

        responseWriter.endElement(ELEMENT_DIV);
    }
}
