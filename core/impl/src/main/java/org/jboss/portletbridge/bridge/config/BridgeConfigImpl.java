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
package org.jboss.portletbridge.bridge.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.bridge.factory.BridgeFactoryFinder;
import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactory;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;

/**
 * Represents values that a Portlet can configure for a given Bridge instance. Per the spec, this information is passed to the
 * Bridge via {@link PortletContext} attributes. As part of the init() method the Bridge will store all these values into a
 * BridgeConfig for use by all parts of the Bridge.
 *
 * @author kenfinnigan
 */
public class BridgeConfigImpl implements BridgeConfig {

    public static final String VIEWID_RENDER_PARAMETER_NAME = "__pbrVIEWID";
    public static final String VIEWID_RESOURCE_PARAMETER_NAME = "__pbrVIEWRESID";

    private PortletConfig portletConfig;
    private Map<String, String> defaultViewMappings;
    private Map<Class<? extends Throwable>, String> errorViewMappings;
    private List<String> facesServletMappings;
    private BridgeLogger logger;
    private BridgeEventHandler eventHandler;
    private BridgePublicRenderParameterHandler publicRenderParameterHandler;
    private boolean preserveActionParams = false;
    private List<String> excludedRequestAttributes;
    private Map<String, String> publicRenderParameterMappings;
    private Class<? extends BridgeWriteBehindResponse> writeBehindRenderResponseWrapperClass;
    private Class<? extends BridgeWriteBehindResponse> writeBehindResourceResponseWrapperClass;
    private List<String> facesSuffixes;
    private String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
    private String viewIdParameterName = Bridge.FACES_VIEW_ID_PARAMETER;
    private String viewIdResourceParameterName = VIEWID_RESOURCE_PARAMETER_NAME;
    private Map<String, Object> attributes;
    private boolean preventSelfClosingScriptTag = false;
    private boolean insideJsf22Runtime = false;
    private boolean viewParamHandlingDisabled = false;
    private boolean bridgeScopeEnabledOnAjaxRequest = false;
    private boolean facesMessagesStoredOnAjaxRequest = true;
    private String sessionIdParameterName = "jsessionid";
    private boolean bridgeScopePreservedPostRender = false;
    private boolean componentNamespaceShortened = true;

    public BridgeConfigImpl() {
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPortletConfig(javax.portlet.PortletConfig)
     */
    public void setPortletConfig(PortletConfig config) {
        portletConfig = config;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getPortletConfig()
     */
    public PortletConfig getPortletConfig() {
        return portletConfig;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setDefaultViewMappings(java.util.Map)
     */
    public void setDefaultViewMappings(Map<String, String> defaultMappings) {
        defaultViewMappings = null;
        if (null != defaultMappings) {
            defaultViewMappings = new ConcurrentHashMap<String, String>(defaultMappings);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getDefaultViewMappings()
     */
    public Map<String, String> getDefaultViewMappings() {
        if (null == defaultViewMappings) {
            defaultViewMappings = new ConcurrentHashMap<String, String>();
        }
        return defaultViewMappings;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setFacesServletMappings(java.util.List)
     */
    public void setFacesServletMappings(List<String> mappings) {
        facesServletMappings = null;
        if (null != mappings) {
            facesServletMappings = new ArrayList<String>(mappings);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getFacesServletMappings()
     */
    public List<String> getFacesServletMappings() {
        if (null == facesServletMappings) {
            facesServletMappings = new ArrayList<String>();
        }
        return facesServletMappings;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setFacesErrorViewMappings(java.util.Map)
     */
    public void setFacesErrorViewMappings(Map<Class<? extends Throwable>, String> errorViewMappings) {
        this.errorViewMappings = null;
        if (null != errorViewMappings) {
            this.errorViewMappings = new ConcurrentHashMap<Class<? extends Throwable>, String>(errorViewMappings);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getFacesErrorViewMappings()
     */
    public Map<Class<? extends Throwable>, String> getFacesErrorViewMappings() {
        if (null == errorViewMappings) {
            errorViewMappings = new ConcurrentHashMap<Class<? extends Throwable>, String>();
        }
        return errorViewMappings;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setViewIdRenderParameterName(java.lang.String)
     */
    public void setViewIdRenderParameterName(String name) {
        viewIdParameterName = name;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getViewIdRenderParameterName()
     */
    public String getViewIdRenderParameterName() {
        return viewIdParameterName;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setViewIdResourceParameterName(java.lang.String)
     */
    public void setViewIdResourceParameterName(String name) {
        viewIdResourceParameterName = name;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getViewIdResourceParameterName()
     */
    public String getViewIdResourceParameterName() {
        return viewIdResourceParameterName;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setLogger(org.jboss.portletbridge.bridge.logger.BridgeLogger)
     */
    public void setLogger(BridgeLogger logger) {
        this.logger = logger;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getLogger()
     */
    public BridgeLogger getLogger() {
        if (null == logger) {
            setLogger(((BridgeLoggerFactory) BridgeFactoryFinder.getFactoryInstance(BridgeLoggerFactory.class))
                    .getBridgeLogger(this));
        }
        return logger;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setEventHandler(javax.portlet.faces.BridgeEventHandler)
     */
    public void setEventHandler(BridgeEventHandler handler) {
        eventHandler = handler;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getEventHandler()
     */
    public BridgeEventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPublicRenderParameterHandler(javax.portlet.faces.BridgePublicRenderParameterHandler)
     */
    public void setPublicRenderParameterHandler(BridgePublicRenderParameterHandler handler) {
        publicRenderParameterHandler = handler;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getPublicRenderParameterHandler()
     */
    public BridgePublicRenderParameterHandler getPublicRenderParameterHandler() {
        return publicRenderParameterHandler;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPreserveActionParameters(boolean)
     */
    public void setPreserveActionParameters(boolean preserve) {
        preserveActionParams = preserve;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPreserveActionParameters(java.lang.Boolean)
     */
    public void setPreserveActionParameters(Boolean preserve) {
        preserveActionParams = false;
        if (null != preserve) {
            preserveActionParams = preserve.booleanValue();
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#hasPreserveActionParameters()
     */
    public boolean hasPreserveActionParameters() {
        return preserveActionParams;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setExcludedRequestAttributes(java.util.List)
     */
    public void setExcludedRequestAttributes(List<String> excludedAttributes) {
        excludedRequestAttributes = null;
        if (null != excludedAttributes) {
            excludedRequestAttributes = new ArrayList<String>(excludedAttributes);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getExcludedRequestAttributes()
     */
    public List<String> getExcludedRequestAttributes() {
        if (null == excludedRequestAttributes) {
            excludedRequestAttributes = new ArrayList<String>();
        }
        return excludedRequestAttributes;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPublicRenderParameterMappings(java.util.Map)
     */
    public void setPublicRenderParameterMappings(Map<String, String> prpMappings) {
        publicRenderParameterMappings = null;
        if (null != prpMappings) {
            publicRenderParameterMappings = new ConcurrentHashMap<String, String>(prpMappings);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getPublicRenderParameterMappings()
     */
    public Map<String, String> getPublicRenderParameterMappings() {
        if (null == publicRenderParameterMappings) {
            publicRenderParameterMappings = new ConcurrentHashMap<String, String>();
        }
        return publicRenderParameterMappings;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#hasPublicRenderParameterMappings()
     */
    public boolean hasPublicRenderParameterMappings() {
        return publicRenderParameterMappings != null && !publicRenderParameterMappings.isEmpty();
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setWriteBehindRenderResponseWrapper(java.lang.Class)
     */
    public void setWriteBehindRenderResponseWrapper(Class<? extends BridgeWriteBehindResponse> renderResponseWrapper) {
        writeBehindRenderResponseWrapperClass = renderResponseWrapper;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getWriteBehindRenderResponseWrapper()
     */
    public Class<? extends BridgeWriteBehindResponse> getWriteBehindRenderResponseWrapper() {
        return writeBehindRenderResponseWrapperClass;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setWriteBehindResourceResponseWrapper(java.lang.Class)
     */
    public void setWriteBehindResourceResponseWrapper(Class<? extends BridgeWriteBehindResponse> resourceResponseWrapper) {
        writeBehindResourceResponseWrapperClass = resourceResponseWrapper;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getWriteBehindResourceResponseWrapper()
     */
    public Class<? extends BridgeWriteBehindResponse> getWriteBehindResourceResponseWrapper() {
        return writeBehindResourceResponseWrapperClass;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setFacesSuffixes(java.util.List)
     */
    public void setFacesSuffixes(List<String> suffixes) {
        facesSuffixes = null;
        if (null != suffixes) {
            facesSuffixes = new ArrayList<String>(suffixes);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getFacesSuffixes()
     */
    public List<String> getFacesSuffixes() {
        if (null == facesSuffixes) {
            facesSuffixes = new ArrayList<String>();
        }
        return facesSuffixes;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setLifecyleId(java.lang.String)
     */
    public void setLifecyleId(String id) {
        lifecycleId = id;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getLifecycleId()
     */
    public String getLifecycleId() {
        return lifecycleId;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getAttributes()
     */
    public Map<String, Object> getAttributes() {
        if (null == attributes) {
            attributes = new ConcurrentHashMap<String, Object>(10);
        }
        return attributes;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getDefaultRenderKitId()
     */
    public String getDefaultRenderKitId() {
        return (String) portletConfig.getPortletContext().getAttribute(
                Bridge.BRIDGE_PACKAGE_PREFIX + portletConfig.getPortletName() + "." + Bridge.DEFAULT_RENDERKIT_ID);
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#doPreventSelfClosingScriptTag()
     */
    @Override
    public boolean doPreventSelfClosingScriptTag() {
        return preventSelfClosingScriptTag;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setPreventSelfClosingScriptTag(boolean)
     */
    @Override
    public void setPreventSelfClosingScriptTag(boolean preventSelfClosingScriptTag) {
        this.preventSelfClosingScriptTag = preventSelfClosingScriptTag;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isJsf22Runtime()
     */
    @Override
    public boolean isJsf22Runtime() {
        return insideJsf22Runtime;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setJsf22Runtime(boolean)
     */
    @Override
    public void setJsf22Runtime(boolean jsf22Runtime) {
        this.insideJsf22Runtime = jsf22Runtime;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isViewParamHandlingDisabled()
     */
    @Override
    public boolean isViewParamHandlingDisabled() {
        return viewParamHandlingDisabled;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setViewParamHandlingDisabled(boolean)
     */
    @Override
    public void setViewParamHandlingDisabled(boolean viewParamHandlingDisabled) {
        this.viewParamHandlingDisabled = viewParamHandlingDisabled;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isBridgeScopeEnabledOnAjaxRequest()
     */
    @Override
    public boolean isBridgeScopeEnabledOnAjaxRequest() {
        return bridgeScopeEnabledOnAjaxRequest;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setBridgeScopeEnabledOnAjaxRequest(boolean)
     */
    @Override
    public void setBridgeScopeEnabledOnAjaxRequest(boolean bridgeScopeEnabledOnAjaxRequest) {
        this.bridgeScopeEnabledOnAjaxRequest = bridgeScopeEnabledOnAjaxRequest;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isFacesMessagesStoredOnAjaxRequest()
     */
    @Override
    public boolean isFacesMessagesStoredOnAjaxRequest() {
        return facesMessagesStoredOnAjaxRequest;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setFacesMessagesStoredOnAjaxRequest(boolean)
     */
    @Override
    public void setFacesMessagesStoredOnAjaxRequest(boolean facesMessagesStoredOnAjaxRequest) {
        this.facesMessagesStoredOnAjaxRequest = facesMessagesStoredOnAjaxRequest;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#getSessionIdParameterName()
     */
    @Override
    public String getSessionIdParameterName() {
        return sessionIdParameterName;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setSessionIdParameterName(String)
     */
    @Override
    public void setSessionIdParameterName(String sessionIdParameterName) {
        this.sessionIdParameterName = sessionIdParameterName;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isBridgeScopePreservedPostRender()
     */
    @Override
    public boolean isBridgeScopePreservedPostRender() {
        return bridgeScopePreservedPostRender;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setBridgeScopePreservedPostRender(boolean)
     */
    @Override
    public void setBridgeScopePreservedPostRender(boolean bridgeScopePreservedPostRender) {
        this.bridgeScopePreservedPostRender = bridgeScopePreservedPostRender;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#isComponentNamespaceShortened()
     */
    @Override
    public boolean isComponentNamespaceShortened() {
        return componentNamespaceShortened;
    }

    /**
     * @see org.jboss.portletbridge.bridge.config.BridgeConfig#setComponentNamespaceShortened(boolean)
     */
    @Override
    public void setComponentNamespaceShortened(boolean componentNamespaceShortened) {
        this.componentNamespaceShortened = componentNamespaceShortened;
    }
}
