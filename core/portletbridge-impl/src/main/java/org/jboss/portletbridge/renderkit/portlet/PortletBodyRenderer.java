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

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;

/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from rendering
 * the <body>...</body> section, which is what is done by the JSF implementation's version of this renderer.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletBodyRenderer extends Renderer {

    // Logger
    private static BridgeLogger logger = new JULLoggerImpl(PortletBodyRenderer.class.getName());

    private static final String STYLE_CLASS = "styleClass";
    private static final String DIV = "div";
    private static final String PBR_STYLE_CLASS = "pbr-body";

    private static final Attribute[] BODY_ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.OUTPUTBODY);

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
        responseWriter.startElement(DIV, uiComponent);

        String portletClientId = facesContext.getViewRoot().getContainerClientId(facesContext);
        responseWriter.writeAttribute("id", portletClientId, "id");

        String styleClass = (String) uiComponent.getAttributes().get(STYLE_CLASS);
        if (null != styleClass && styleClass.length() != 0) {
            responseWriter.writeAttribute("class", styleClass + " " + PBR_STYLE_CLASS, STYLE_CLASS);
        } else {
            responseWriter.writeAttribute("class", PBR_STYLE_CLASS, STYLE_CLASS);
        }

        RenderKitUtils.renderPassThruAttributes(facesContext, responseWriter, uiComponent, BODY_ATTRIBUTES);

        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        List<UIComponent> uiComponentResources = uiViewRoot.getComponentResources(facesContext, PortletHeadRenderer.BODY);

        if (null != uiComponentResources) {
            for (UIComponent uiComponentResource : uiComponentResources) {
                String originalTarget = (String) uiComponentResource.getAttributes().get(PortletHeadRenderer.ORIGINAL_TARGET);

                if (PortletHeadRenderer.HEAD.equals(originalTarget)) {
                    uiComponentResource.encodeAll(facesContext);
                }
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        List<UIComponent> uiComponentResources = uiViewRoot.getComponentResources(facesContext, PortletHeadRenderer.BODY);

        if (null != uiComponentResources) {
            for (UIComponent uiComponentResource : uiComponentResources) {
                String originalTarget = (String) uiComponentResource.getAttributes().get(PortletHeadRenderer.ORIGINAL_TARGET);

                if (!PortletHeadRenderer.HEAD.equals(originalTarget)) {
                    uiComponentResource.encodeAll(facesContext);
                }
            }
        }

        RenderKitUtils.renderUnhandledMessages(facesContext);
        responseWriter.endElement(DIV);
    }
}
