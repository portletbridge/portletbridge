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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from rendering
 * the <head>...</head> section, which is what is done by the JSF implementation's version of this renderer.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletHeadRenderer extends Renderer {

    // Logger
    private static BridgeLogger logger = new JULLoggerImpl(PortletHeadRenderer.class.getName());

    static final String HEAD = "head";
    static final String BODY = "body";
    static final String ORIGINAL_TARGET = "originalTarget";

    private static final String ADDED = UIComponentBase.class.getName() + ".ADDED";
    private static final String START_OF_FACET = "start";
    private static final String MIDDLE_OF_FACET = "middle";
    private static final String END_OF_FACET = "end";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // do nothing
    }

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        logger.log(Level.INFO, "PortletHeadRenderer encodeBegin()");

        List<UIComponent> sortedHeadComponents = buildComponentAddSequence(uiComponent, facesContext);

        List<UIComponent> addToHead = new ArrayList<UIComponent>();
        List<UIComponent> moveToBody = new ArrayList<UIComponent>();

        ExternalContext externalContext = facesContext.getExternalContext();
        PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
        String markupHeadSupport = portletRequest.getPortalContext().getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT);
        boolean canMarkupHead = null != markupHeadSupport ? Boolean.valueOf(markupHeadSupport) : false;

        HeadResources headBean = HeadResources.instance();
        Set<String> resourceIds = null != headBean ? headBean.getIds() : new HashSet<String>();

        splitResourcesBetweenHeadAndBody(addToHead, moveToBody, sortedHeadComponents, facesContext, resourceIds, canMarkupHead);

        if (canMarkupHead) {
            ResponseWriter existingWriter = facesContext.getResponseWriter();

            ResponseWriter headWriter = (ResponseWriter) portletRequest.getAttribute("headWriter");
            if (null == headWriter) {
                headWriter = new PortletHeadResponseWriter(existingWriter, BridgeContext.getCurrentInstance().getPortletResponse());
            }
            portletRequest.setAttribute("headWriter", headWriter);
            facesContext.setResponseWriter(headWriter);

            for (UIComponent headResource : addToHead) {
                headResource.encodeAll(facesContext);
                resourceIds.add(generateComponentId(headResource));
            }

            facesContext.setResponseWriter(existingWriter);
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();
        for (UIComponent componentResource : moveToBody) {
            componentResource.getAttributes().put(ORIGINAL_TARGET, HEAD);
            // Prevents events from being fired when setParent() called on UIComponentBase
            componentResource.getAttributes().put(ADDED, Boolean.TRUE);
            viewRoot.addComponentResource(facesContext, componentResource, BODY);
        }
    }

    protected void splitResourcesBetweenHeadAndBody(List<UIComponent> head, List<UIComponent> body,
            List<UIComponent> components, FacesContext facesContext, Set<String> resourceIds, boolean canMarkupHead) {

        boolean isAjax = facesContext.getPartialViewContext().isAjaxRequest();

        for (UIComponent component : components) {
            if (!resourceIds.contains(generateComponentId(component))) {
                if (isAjax) {
                    body.add(component);
                } else {
                    if (canMarkupHead) {
                        head.add(component);
                    } else {
                        body.add(component);
                    }
                }
            }
        }
    }

    protected String generateComponentId(UIComponent component) {
        StringBuilder id = new StringBuilder();
        Map<String, Object> attributes = component.getAttributes();
        Object libraryObject = attributes.get("library");
        Object nameObject = attributes.get("name");

        if (null != libraryObject) {
            id.append(libraryObject);
            id.append(':');
        }

        if (null != nameObject) {
            id.append(nameObject);
        }
        return id.toString();
    }

    /**
     * Generate List of Resources from Head in following sequence:
     * - Contents of <f:facet name="start">
     * - Stylesheets
     * - Contents of <f:facet name="middle">
     * - Scripts (and everything else)
     * - Contents of <f:facet name="end">
     *
     * @param uiComponent
     * @param facesContext
     * @return
     */
    protected List<UIComponent> buildComponentAddSequence(UIComponent uiComponent, FacesContext facesContext) {
        List<UIComponent> headComponentResources = facesContext.getViewRoot().getComponentResources(facesContext, HEAD);
        List<UIComponent> resourcesForStart = getFacetResources(uiComponent, START_OF_FACET);
        List<UIComponent> resourcesForMiddle = getFacetResources(uiComponent, MIDDLE_OF_FACET);
        List<UIComponent> resourcesForEnd = getFacetResources(uiComponent, END_OF_FACET);

        List<UIComponent> headStylesheetComponentResources = null;
        List<UIComponent> headScriptComponentResources = null;

        for (UIComponent headComponentResource : headComponentResources) {
            String resourceName = (String) headComponentResource.getAttributes().get("name");
            if (null != resourceName && resourceName.endsWith("css")) {
                // Stylesheet
                if (null == headStylesheetComponentResources) {
                    headStylesheetComponentResources = new ArrayList<UIComponent>();
                }
                headStylesheetComponentResources.add(headComponentResource);
            } else {
                // Script, or anything else
                if (null == headScriptComponentResources) {
                    headScriptComponentResources = new ArrayList<UIComponent>();
                }
                headScriptComponentResources.add(headComponentResource);
            }
        }

        // Add Components based on defined sequence
        List<UIComponent> aggregatedComponentResources = new ArrayList<UIComponent>();
        if (null != resourcesForStart) {
            aggregatedComponentResources.addAll(resourcesForStart);
        }
        if (null != headStylesheetComponentResources) {
            aggregatedComponentResources.addAll(headStylesheetComponentResources);
        }
        if (null != resourcesForMiddle) {
            aggregatedComponentResources.addAll(resourcesForMiddle);
        }
        if (null != headScriptComponentResources) {
            aggregatedComponentResources.addAll(headScriptComponentResources);
        }
        if (null != resourcesForEnd) {
            aggregatedComponentResources.addAll(resourcesForEnd);
        }

        return aggregatedComponentResources;
    }

    protected List<UIComponent> getFacetResources(UIComponent uiComponent, String facetName) {
        List<UIComponent> resources = null;
        UIComponent facet = uiComponent.getFacet(facetName);

        if (null != facet) {
            resources = new ArrayList<UIComponent>();
            resources.addAll(facet.getChildren());
        }

        return resources;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // this renderer only encodes head resources
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    };
}
