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
package org.jboss.portletbridge.application;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;
import org.jboss.portletbridge.context.PortalActionURL;

/**
 * @author asmirnov, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletViewHandler extends ViewHandlerWrapper {

    private static final BridgeLogger logger = new JULLoggerImpl(PortletViewHandler.class.getName());

    ViewHandler parent;

    /**
     * @param parent
     */
    public PortletViewHandler(ViewHandler parent) {
        super();
        this.parent = parent;
    }

    @Override
    public Locale calculateLocale(FacesContext context) {
        Locale locale;
        if (BridgeUtil.isPortletRequest()) {
            locale = context.getExternalContext().getRequestLocale();
            if (null == locale) {
                locale = super.calculateLocale(context);
            } else {
                // check if given locale is supported by JSF Application
                for (Iterator<Locale> i = context.getApplication().getSupportedLocales(); i.hasNext();) {
                    Locale loc = i.next();
                    if (loc.equals(locale)) {
                        break;
                    }
                }
                // locale is not supported
                locale = super.calculateLocale(context);
            }
        } else {
            locale = super.calculateLocale(context);
        }
        return locale;
    }

    @Override
    public UIViewRoot createView(FacesContext facesContext, String viewId) {
        if (!BridgeUtil.isPortletRequest()) {
            return super.createView(facesContext, viewId);
        }

        viewId = evaluateUrl(facesContext, viewId);
        String queryString = null;
        try {
            PortalActionURL viewIdUrl = new PortalActionURL(viewId);
            viewId = viewIdUrl.getPath();
            BridgeContext.getCurrentInstance().setNavigationQueryString(viewIdUrl.getQueryString());
            queryString = viewIdUrl.getQueryString();
        } catch (MalformedURLException e) {
            // Do nothing, it is ordinary view Id
            logger.log(Level.INFO, "Mailformed ViewId url", e);
        }

        UIViewRoot root = super.createView(facesContext, viewId);

        Class<? extends UIViewRoot> rootClass = root.getClass();

        if (rootClass.getAnnotation(PortletNamingContainer.class) == null) {
            // Creates correct UIViewRoot with our NamingContainer if for some reason createComponent of
            // PortletApplicationImpl
            // was not called
            UIViewRoot portletRoot = new PortletNamingContainerUIViewRoot();
            if (null != queryString && queryString.length() > 0) {
                portletRoot.setViewId(root.getViewId() + "?" + queryString);
            } else {
                portletRoot.setViewId(root.getViewId());
            }
            portletRoot.setLocale(root.getLocale());
            portletRoot.setRenderKitId(root.getRenderKitId());
            portletRoot.setId(root.getId());
            root = portletRoot;
        } else {
            if (null != queryString && queryString.length() > 0) {
                root.setViewId(root.getViewId() + "?" + queryString);
            }
        }

        Object response = facesContext.getExternalContext().getResponse();
        if (response instanceof PortletResponse) {
            PortletResponse portletResponse = (PortletResponse) response;
            portletResponse.setProperty("X-JAVAX-PORTLET-IS-NAMESPACED", "true");
        }
        return root;
    }

    public String getActionURL(FacesContext context, String url) {
        if (!BridgeUtil.isPortletRequest()) {
            return super.getActionURL(context, url);
        }

        // action URLs are processed by the bridge in encodeActionURL
        // however the bridge extends Faces navigation rule support in that it
        // allows a to-view-id element to contain an EL expression.
        // We recognize this EL expresion here and evaluate to a viewid
        // before delegating. Only executed during portlet request or AJAX
        // request
        // from portlet page.

        url = evaluateUrl(context, url);

        // Faces can't do suffix mapping (extension mapping) properly if there is a query string
        PortalActionURL viewIdUrl = null;
        try {
            viewIdUrl = new PortalActionURL(url);
            url = viewIdUrl.getPath();
        } catch (MalformedURLException e) {
            // Do nothing, it is ordinary view Id
            logger.log(Level.INFO, "Mailformed ViewId url", e);
        }

        String actionURL = super.getActionURL(context, url);

        // Now add the parameters back on
        if (viewIdUrl.parametersSize() > 0) {
            int qMark = actionURL.indexOf('?');
            if (qMark < 0) {
                actionURL += "?" + viewIdUrl.getQueryString();
            } else {
                actionURL += "&" + viewIdUrl.getQueryString();
            }
        }

        return actionURL;
    }

    protected String evaluateUrl(FacesContext context, String url) {
        if (url.startsWith("/#")) {
            url = url.substring(1);
        }

        if (url.startsWith("#")) {
            // evaluate this as an EL expression
            url = (String) context.getApplication().evaluateExpressionGet(context, url, String.class);
            if (url == null) {
                throw new FacesException("Evaluated view ID is null " + url);
            }
        }
        return url;
    }

    @Override
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        if (!BridgeUtil.isPortletRequest()) {
            super.renderView(context, viewToRender);
            return;
        }

        // Get the renderPolicy from the init parameters
        ExternalContext externalContext = context.getExternalContext();
        String renderPolicyParam = externalContext.getInitParameter(Bridge.RENDER_POLICY);

        Bridge.BridgeRenderPolicy renderPolicy;
        if (null == renderPolicyParam) {
            renderPolicy = Bridge.BridgeRenderPolicy.DEFAULT;
        } else {
            renderPolicy = Bridge.BridgeRenderPolicy.valueOf(renderPolicyParam);
        }

        if (renderPolicy == Bridge.BridgeRenderPolicy.ALWAYS_DELEGATE) {
            super.renderView(context, viewToRender);
        } else if (renderPolicy == Bridge.BridgeRenderPolicy.DEFAULT) {
            // https://jira.jboss.org/jira/browse/PBR-121 - save original
            // request/response objects.
            Object portletRequest = externalContext.getRequest();
            Object portletResponse = externalContext.getResponse();
            try {
                // IDEA - set ServletRequest/ServletResponse wrappers to
                // ExternalContext
                // to use original view handler functionality.
                super.renderView(context, viewToRender);
            } catch (Exception e) {
                logger.log(Level.DEBUG,
                    "Error rendering view by parent ViewHandler, try to render as portletbridge JSP page", e);
                // Restore request/response objects if parent renderer change
                // them.
                if (portletRequest != externalContext.getRequest()) {
                    externalContext.setRequest(portletRequest);
                }
                if (portletResponse != externalContext.getResponse()) {
                    externalContext.setResponse(portletResponse);
                }
                // catch all throws and swallow -- falling through to our own
                // render
                // suppress rendering if "rendered" property on the component is
                // false
                if (viewToRender.isRendered()) {
                    doRenderView(context, viewToRender);
                }

            }
        } else if (viewToRender.isRendered()) {
            // NEVER_DELEGATE
            doRenderView(context, viewToRender);
        }

    }

    private void doRenderView(FacesContext context, UIViewRoot viewToRender) throws IOException {
        getViewDeclarationLanguage(context, viewToRender.getViewId()).renderView(context, viewToRender);
    }

    @Override
    public ViewHandler getWrapped() {
        return parent;
    }

}
