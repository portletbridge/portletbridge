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
package org.jboss.portletbridge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import javax.faces.webapp.FacesServlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeUninitializedException;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.controller.BridgeController;
import org.jboss.portletbridge.bridge.event.BridgeDestroyRequestSystemEvent;
import org.jboss.portletbridge.bridge.event.BridgeInitializeRequestSystemEvent;
import org.jboss.portletbridge.bridge.factory.BridgeConfigFactory;
import org.jboss.portletbridge.bridge.factory.BridgeContextFactory;
import org.jboss.portletbridge.bridge.factory.BridgeControllerFactory;
import org.jboss.portletbridge.bridge.factory.BridgeFactoryFinder;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.config.FacesConfigProcessor;
import org.jboss.portletbridge.config.WebXmlProcessor;
import org.jboss.portletbridge.context.InitFacesContext;

/**
 * @author kenfinnigan
 */
public class PortletBridgeImpl implements Bridge {

    private static final Logger logger = Logger.getLogger(PortletBridgeImpl.class.getName(),
        BridgeLogger.LOGGING_BUNDLE);

    private BridgeConfig bridgeConfig;
    private BridgeController bridgeController;

    private boolean initialized = false;

    /**
     * @see javax.portlet.faces.Bridge#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig portletConfig) throws BridgeException {
        if (null == portletConfig) {
            throw new NullPointerException("PortletConfig null when initializing Portlet Bridge");
        }

        if (this.initialized) {
            throw new BridgeException("Portlet Bridge already initialized");
        }

        String portletName = portletConfig.getPortletName();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Commenced Portlet Bridge initialization for " + portletName);
        }

        this.bridgeConfig = getBridgeConfig(portletConfig);

        this.bridgeController = ((BridgeControllerFactory) BridgeFactoryFinder
            .getFactoryInstance(BridgeControllerFactory.class)).getBridgeController();
        this.bridgeController.init(bridgeConfig);

        this.initialized = true;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Completed Portlet Bridge initialization for " + portletName);
        }
    }

    @SuppressWarnings("unchecked")
    private BridgeConfig getBridgeConfig(PortletConfig portletConfig) {
        BridgeConfig bridgeConfig = ((BridgeConfigFactory) BridgeFactoryFinder
            .getFactoryInstance(BridgeConfigFactory.class)).getBridgeConfig();

        bridgeConfig.setPortletConfig(portletConfig);

        PortletContext portletContext = portletConfig.getPortletContext();

        String bridgeParametersPrefix = Bridge.BRIDGE_PACKAGE_PREFIX + portletConfig.getPortletName() + ".";

        // Check if Bridge should log messages
        Boolean enableLogging = (Boolean) portletContext.getAttribute(bridgeParametersPrefix
            + BridgeLogger.LOGGING_ENABLED_PORTLET_INIT_PARAM);
        bridgeConfig.getLogger().setEnabled(null != enableLogging ? enableLogging : Boolean.FALSE);

        // Bridge Event Handler
        bridgeConfig.setEventHandler((BridgeEventHandler) portletContext.getAttribute(bridgeParametersPrefix
            + Bridge.BRIDGE_EVENT_HANDLER));

        // Public Render Parameter Handler
        bridgeConfig.setPublicRenderParameterHandler((BridgePublicRenderParameterHandler) portletContext
            .getAttribute(bridgeParametersPrefix + Bridge.BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER));

        // Preserve Action Parameters
        bridgeConfig.setPreserveActionParameters((Boolean) portletContext.getAttribute(bridgeParametersPrefix
            + Bridge.PRESERVE_ACTION_PARAMS));

        // Excluded Request Attributes from Portlet definition
        bridgeConfig.setExcludedRequestAttributes((List<String>) portletContext.getAttribute(bridgeParametersPrefix
            + Bridge.EXCLUDED_REQUEST_ATTRIBUTES));

        // Lifecycle Id
        String lifecycleId = portletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
        if (null != lifecycleId && lifecycleId.trim().length() != 0) {
            bridgeConfig.setLifecyleId(lifecycleId);
        }

        // Faces Suffixes. JSF 2.0 allows multiple
        String suffixString = portletContext.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
        if (null == suffixString) {
            suffixString = ViewHandler.DEFAULT_SUFFIX;
        }
        bridgeConfig.setFacesSuffixes(Arrays.asList(suffixString.split(" ")));

        // Process web.xml content
        WebXmlProcessor webXmlProc = new WebXmlProcessor(portletConfig.getPortletContext());
        // Retrieve Faces Servlet Mapping
        bridgeConfig.setFacesServletMappings(webXmlProc.getFacesServlet().getMappings());
        // Retrieve Error Page Mapping
        bridgeConfig.setFacesErrorViewMappings(webXmlProc.getErrorViews());

        // Retrieve faces-config.xml settings
        // Excluded Request Attributes from Faces Config
        List<String> excludedAttrs = FacesConfigProcessor.getExcludedAttributes();
        if (null != excludedAttrs) {
            bridgeConfig.getExcludedRequestAttributes().addAll(excludedAttrs);
        }

        // Public Parameter Mapping
        Map<String, String> publicParams = FacesConfigProcessor.getPublicParameterMappings();
        if (null != publicParams) {
            bridgeConfig.getPublicRenderParameterMappings().putAll(publicParams);
        }

        // Write Behind Response Wrappers
        bridgeConfig.setWriteBehindRenderResponseWrapper(createWrapper(FacesConfigProcessor
            .getWriteBehindRenderResponseWrapperClassName()));
        bridgeConfig.setWriteBehindResourceResponseWrapper(createWrapper(FacesConfigProcessor
            .getWriteBehindResourceResponseWrapperClassName()));

        // Default View Id Mappings
        bridgeConfig.setDefaultViewMappings((Map<String, String>) portletContext.getAttribute(bridgeParametersPrefix
            + Bridge.DEFAULT_VIEWID_MAP));
        if (null == bridgeConfig.getDefaultViewMappings() || 0 == bridgeConfig.getDefaultViewMappings().size()) {
            throw new BridgeException("No JSF view id's defined in portlet.xml for " + portletConfig.getPortletName());
        }

        return bridgeConfig;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends BridgeWriteBehindResponse> createWrapper(String wrapperClassName) {
        if (null != wrapperClassName) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                return (Class<? extends BridgeWriteBehindResponse>) loader.loadClass(wrapperClassName);
            } catch (Exception e) {
                bridgeConfig.getLogger().log(
                    BridgeLogger.Level.WARNING,
                    "Unable to instantiate BridgeWriteBehindResponse class: " + wrapperClassName + " due to "
                        + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * @see javax.portlet.faces.Bridge#doFacesRequest(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void doFacesRequest(ActionRequest request, ActionResponse response) throws BridgeException {
        assertParameters(request, response);

        try {
            initRequest(request, response, PortletPhase.ACTION_PHASE);
            BridgeContext bridgeContext = getBridgeContext(request, response, PortletPhase.ACTION_PHASE);

            bridgeController.processPortletAction(bridgeContext);
        } finally {
            finalizeRequest(request, BridgeContext.getCurrentInstance());
        }
    }

    /**
     * @see javax.portlet.faces.Bridge#doFacesRequest(javax.portlet.EventRequest, javax.portlet.EventResponse)
     */
    public void doFacesRequest(EventRequest request, EventResponse response) throws BridgeException {
        assertParameters(request, response);

        try {
            initRequest(request, response, PortletPhase.EVENT_PHASE);
            BridgeContext bridgeContext = getBridgeContext(request, response, PortletPhase.ACTION_PHASE);

            bridgeController.handlePortletEvent(bridgeContext);
        } finally {
            finalizeRequest(request, BridgeContext.getCurrentInstance());
        }
    }

    /**
     * @see javax.portlet.faces.Bridge#doFacesRequest(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doFacesRequest(RenderRequest request, RenderResponse response) throws BridgeException {
        assertParameters(request, response);

        try {
            initRequest(request, response, PortletPhase.RENDER_PHASE);
            BridgeContext bridgeContext = getBridgeContext(request, response, PortletPhase.RENDER_PHASE);

            Object renderPartAttribute = request.getAttribute(RenderRequest.RENDER_PART);

            if (null != renderPartAttribute && renderPartAttribute.equals(RenderRequest.RENDER_HEADERS)) {
                bridgeController.renderPortletHead(bridgeContext);
            } else {
                bridgeController.renderPortletBody(bridgeContext);
            }
        } finally {
            finalizeRequest(request, BridgeContext.getCurrentInstance());
        }
    }

    /**
     * @see javax.portlet.faces.Bridge#doFacesRequest(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
     */
    public void doFacesRequest(ResourceRequest request, ResourceResponse response) throws BridgeException {
        assertParameters(request, response);

        try {
            initRequest(request, response, PortletPhase.RESOURCE_PHASE);
            BridgeContext bridgeContext = getBridgeContext(request, response, PortletPhase.RENDER_PHASE);

            bridgeController.renderResource(bridgeContext);
        } finally {
            finalizeRequest(request, BridgeContext.getCurrentInstance());
        }
    }

    /**
     * @see javax.portlet.faces.Bridge#destroy()
     */
    public void destroy() {
        if (this.initialized) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Destroy Portlet Bridge for " + this.bridgeConfig.getPortletConfig().getPortletName());
            }
            this.bridgeConfig = null;
            this.bridgeController = null;
            this.initialized = false;
        }
    }

    protected void assertParameters(PortletRequest request, PortletResponse response) {
        if (null == request) {
            throw new NullPointerException("PortletRequest parameter is null");
        }
        if (null == response) {
            throw new NullPointerException("PortletResponse parameter is null");
        }
        if (!initialized) {
            throw new BridgeUninitializedException("JSF Portlet Bridge is not initialized");
        }
    }

    protected void initRequest(PortletRequest request, PortletResponse response, PortletPhase phase) {
        request.setAttribute(Bridge.PORTLET_LIFECYCLE_PHASE, phase);
    }

    protected BridgeContext getBridgeContext(PortletRequest request, PortletResponse response, PortletPhase phase) {
        BridgeContext bridgeContext = ((BridgeContextFactory) BridgeFactoryFinder
            .getFactoryInstance(BridgeContextFactory.class)).getBridgeContext();

        bridgeContext.setBridgeConfig(bridgeConfig);
        bridgeContext.setPortletContext(bridgeConfig.getPortletConfig().getPortletContext());
        bridgeContext.setPortletRequest(request);
        bridgeContext.setPortletRequestPhase(phase);
        bridgeContext.setPortletResponse(response);
        bridgeContext.setPreFacesRequestAttrNames(Collections.list(request.getAttributeNames()));

        fireFacesSystemEvent(bridgeContext, BridgeInitializeRequestSystemEvent.class);

        return bridgeContext;

    }

    private void finalizeRequest(PortletRequest request, BridgeContext bridgeContext) {
        request.removeAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);

        if (null != bridgeContext) {
            releaseBridgeContext(bridgeContext);
        }
    }

    private void releaseBridgeContext(BridgeContext bridgeContext) {
        fireFacesSystemEvent(bridgeContext, BridgeDestroyRequestSystemEvent.class);

        bridgeContext.release();
    }

    private void fireFacesSystemEvent(BridgeContext bridgeContext, Class<? extends SystemEvent> eventClass) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        boolean createdInitContext = false;

        if (null == facesContext) {
            ApplicationFactory factory = (ApplicationFactory) FactoryFinder
                .getFactory(FactoryFinder.APPLICATION_FACTORY);

            facesContext = new InitFacesContext(factory.getApplication(), bridgeConfig.getPortletConfig()
                .getPortletContext());
            createdInitContext = true;
        }

        facesContext.getApplication().publishEvent(facesContext, eventClass, bridgeContext);

        if (createdInitContext) {
            facesContext.release();
        }
    }
}
