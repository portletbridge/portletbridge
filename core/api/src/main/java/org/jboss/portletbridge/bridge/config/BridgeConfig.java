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

import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;

/**
 * The <CODE>BridgeConfig</CODE> represents the set of values a portlet can configure for a given bridge instance. Currently, by
 * spec, the portlet passes most of this configuration to the bridge using <CODE>PortletContext</CODE> attributes. The bridge,
 * in its <code>init()</code> method, is responsible for packaging all this configuration information into a BridgeConfig. It
 * must then only reference such configuration information from this object. I.e. it no longer makes reference to the
 * <CODE>PortletContext</CODE> attributes. This allows replaceable pieces of the bridge implementation to always have a
 * consistent object to retrieve configuration information from.
 */
public interface BridgeConfig {

    /**
     * Sets the <code>PortletConfig</code> object of the portlet that is utilizing this bridge.
     *
     * @param config the config object.
     */
    void setPortletConfig(PortletConfig config);

    /**
     * Gets the <code>PortletConfig</code> object of the portlet that is utilizing this bridge.
     *
     * @return the config object.
     */
    PortletConfig getPortletConfig();

    /**
     * Sets the <code>Map</code> describing the mapping between each supported <code>PortletMode</code> and its default Faces
     * View (id). When the bridge receives a request that isn't directly encoded with the target Faces view (such as the initial
     * render request), the bridge relies on these default mappings to determine the target. There is one mapping per
     * <code>PortletMode</code> supported by the portlet (and handled by Faces). The key to the each entry in the map is the
     * <code>String</code> name of the associated <code>PortletMode</code>;
     *
     * @param defaultMappings between each supported <code>PortletMode</code> and the default Faces target.
     */
    void setDefaultViewMappings(Map<String, String> defaultMappings);

    /**
     * Gets the <code>Map</code> describing the mapping between each supported <code>PortletMode</code> and its default Faces
     * View (id). When the bridge receives a request that isn't directly encoded with the target Faces view (such as the initial
     * render request), the bridge relies on these default mappings to determine the target. There is one mapping per
     * <code>PortletMode</code> supported by the portlet (and handled by Faces). The key to the each entry in the map is the
     * <code>String</code> name of the associated <code>PortletMode</code>;
     *
     * @return defaultMappings between each supported <code>PortletMode</code> and the default Faces target.
     */
    Map<String, String> getDefaultViewMappings();

    /**
     * Sets the <code>List</code> of <code>Servlet</code> mappings to the Faces servlet (information taken from web.xml). The
     * bridge uses these mappings to both detect whether a given URL is handled by Faces or not and to deal with mapping between
     * viewIds and their underlying resources.
     *
     * @param mappings the various servlet mappings for the <code>FacesServlet</code>.
     */
    void setFacesServletMappings(List<String> mappings);

    /**
     * Gets the <code>List</code> of <code>Servlet</code> mappings to the Faces servlet (information taken from web.xml). The
     * bridge uses these mappings to both detect whether a given URL is handled by Faces or not and to deal with mapping between
     * viewIds and their underlying resources.
     *
     * @return the various servlet mappings for the <code>FacesServlet</code>.
     */
    List<String> getFacesServletMappings();

    /**
     * Sets the <code>Map</code> of <code>Exception</code> classes to Faces views (information taken from web.xml).
     *
     * @param errorViewMappings the various exception to jsf view mappings for errors
     */
    void setFacesErrorViewMappings(Map<Class<? extends Throwable>, String> errorViewMappings);

    /**
     * Gets the <code>Map</code> of <code>Exception</code> classes to Faces views (information taken from web.xml).
     *
     * @return the various exception to jsf view mappings for errors
     */
    Map<Class<? extends Throwable>, String> getFacesErrorViewMappings();

    /**
     * Sets the name of the parameter used by the bridge to encode the target viewId.
     *
     * @param name parameter name that holds the bridge encoded target viewId.
     */
    void setViewIdRenderParameterName(String name);

    /**
     * Gets the name of the parameter used by the bridge to encode the target viewId.
     *
     * @return parameter name that holds the bridge encoded target viewId.
     */
    String getViewIdRenderParameterName();

    /**
     * Sets the name of the parameter used by the bridge to encode the target viewId when encoding a Resource URL. As
     * resourceURLs can't impact render parameters and the resource request always receives the current render parameters, its
     * we need a different parameter to hold this information. This allows us to use its existence in the request as an
     * indication of whether the target is a Faces resource or a regular portlet one.
     *
     * @param name parameter name that holds the bridge encoded target viewId.
     */
    void setViewIdResourceParameterName(String name);

    /**
     * Gets the name of the parameter used by the bridge to encode the target viewIdwhen encoding a Resource URL. As
     * resourceURLs can't impact render parameters and the resource request always receives the current render parameters, its
     * we need a different parameter to hold this information. This allows us to use its existence in the request as an
     * indication of whether the target is a Faces resource or a regular portlet one.
     *
     * @return parameter name that holds the bridge encoded target viewId.
     */
    String getViewIdResourceParameterName();

    /**
     * Sets the <code>BridgeLogger</code> that the bridge uses to log diagnostic and warning messages.
     *
     * @param logger <code>BridgeLogger</code>.
     */
    void setLogger(BridgeLogger logger);

    /**
     * Gets the <code>BridgeLogger</code> that the bridge uses to log diagnostic and warning messages.
     *
     * @return <code>BridgeLogger</code>.
     */
    BridgeLogger getLogger();

    /**
     * Sets the <code>BridgeEventHandler</code> that the bridge calls to handle any portlet event it processes.
     *
     * @param handler
     */
    void setEventHandler(BridgeEventHandler handler);

    /**
     * Gets the <code>BridgeEventHandler</code> that the bridge calls to handle any portlet event it processes.
     *
     * @return the <code>BridgeEventHandler</code>
     */
    BridgeEventHandler getEventHandler();

    /**
     * Sets the <code>BridgePublicRenderParameterHandler</code> that the bridge calls to handle post processing recalculations
     * following the bridge pushing incoming portlet public render parameters to their models.
     *
     * @param handler
     */
    void setPublicRenderParameterHandler(BridgePublicRenderParameterHandler handler);

    /**
     * Gets the <code>BridgePublicRenderParameterHandler</code> that the bridge calls to handle post processing recalculations
     * following the bridge pushing incoming portlet public render parameters to their models.
     *
     * @return <code>BridgePublicRenderParameterHandler</code>
     */
    BridgePublicRenderParameterHandler getPublicRenderParameterHandler();

    /**
     * Sets whether or not the bridge should carry action parameters forward into subsequent renders.
     *
     * @param preserve <code>true</code> indicates the action parameters are preserved. <code>false</code> indicates they are
     *        not.
     */
    void setPreserveActionParameters(boolean preserve);

    /**
     * Sets whether or not the bridge should carry action parameters forward into subsequent renders.
     *
     * @param preserve <code>Boolean.TRUE</code> indicates the action parameters are preserved. <code>Boolean.FALSE</code>
     *        indicates they are not.
     */
    void setPreserveActionParameters(Boolean preserve);

    /**
     * Gets whether or not the bridge should carry action parameters forward into subsequent renders. If not previously set, it
     * returns <code>false</code>.
     *
     * @return <code>true</code> indicates the action parameters are preserved. <code>false</code> indicates they are not.
     */
    boolean hasPreserveActionParameters();

    /**
     * Sets the <code>List</code> of attributes to be excluded from the bridge's request scope. This list includes both the
     * attributes configured in the portlet.xml (portlet init parameter) as well as any configured in any of this web
     * application's faces-config.xml(s). It doesn't include any of the predefined attributes as defined by the specification. A
     * list entry is either the fully qualified name of the attribute that should be excluded or a wildcard terminated (package)
     * path. In the latter case, all attributes whose names reside in this package (non-recursive) are excluded.
     *
     * @param excludedAttributes <code>List</code> of request attribute names that are to be excluded from the bridge's managed
     *        request scope.
     */
    void setExcludedRequestAttributes(List<String> excludedAttributes);

    /**
     * Gets the <code>List</code> of attributes to be excluded from the bridge's request scope. This list includes both the
     * attributes configured in the portlet.xml (portlet init parameter) as well as any configured in any of this web
     * application's faces-config.xml(s). It doesn't include any of the predefined attributes as defined by the specification. A
     * list entry is either the fully qualified name of the attribute that should be excluded or a wildcard terminated (package)
     * path. In the latter case, all attributes whose names reside in this package (non-recusive) are excluded.
     *
     * @return <code>List</code> of request attribute names that are to be excluded from the bridge's managed request scope. If
     *         no entries an empty List is returned.
     */
    List<String> getExcludedRequestAttributes();

    /**
     * Sets the <code>Map</code> containing the mappings between portlet public render parameter names and a corresponding Faces
     * EL statement. The Faces EL is expected to resolve to a managed bean property allowing the bridge to push/pull public
     * render parameter values directly from managed bean properties. This configuration information is extracted from the
     * faces-config.xml(s).
     *
     *
     * @param prpMappings <code>Map<String, String></code>. The key is the name of the portlet public render parameter for this
     *        mapping. If prefixed with portletName: the mapping only pertains to the specifically named portlet, otherwise the
     *        mapping pertains to all portlets in the web application. The value is a Faces EL that resolves to a managed bean
     *        property.
     */

    void setPublicRenderParameterMappings(Map<String, String> prpMappings);

    /**
     * Gets the <code>Map</code> containing the mappings between portlet public render parameter names and a corresponding Faces
     * EL statement. The Faces EL is expected to resolve to a managed bean property allowing the bridge to push/pull public
     * render parameter values directly from managed bean properties. This configuration information is extracted from the
     * faces-config.xml(s).
     *
     *
     * @return <code>Map<String, String></code>. The key is the name of the portlet public render parameter for this mapping. If
     *         prefixed with portletName: the mapping only pertains to the specifically named portlet, otherwise the mapping
     *         pertains to all portlets in the web application. The value is a Faces EL that resolves to a managed bean
     *         property.
     */
    Map<String, String> getPublicRenderParameterMappings();

    /**
     *
     * @return <code>true</code> if the config has public render parameter mappings.
     */
    boolean hasPublicRenderParameterMappings();

    /**
     * Sets the <code>Class</code> that the bridge uses to wrap the response when rendering a <code>JSP</code> to implement the
     * Faces implementation specific support for handling interleaved response writing.
     *
     * @param renderResponseWrapper <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface and is a proper
     *        portlet render response wrapper.
     */
    void setWriteBehindRenderResponseWrapper(Class<? extends BridgeWriteBehindResponse> renderResponseWrapper);

    /**
     * Gets the <code>Class</code> that the bridge uses to wrap the response when rendering a <code>JSP</code> to implement the
     * Faces implementation specific support for handling interleaved response writing.
     *
     * @return <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface and is a proper portlet
     *         render response wrapper.
     */
    Class<? extends BridgeWriteBehindResponse> getWriteBehindRenderResponseWrapper();

    /**
     * Sets the <code>Class</code> that the bridge uses to wrap the response when rendering a <code>JSP</code> resource to
     * implement the Faces implementation specific support for handling interleaved response writing.
     *
     * @param resourceResponseWrapper <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface and is a proper
     *        portlet resource response wrapper.
     */
    void setWriteBehindResourceResponseWrapper(Class<? extends BridgeWriteBehindResponse> resourceResponseWrapper);

    /**
     * Gets the <code>Class</code> that the bridge uses to wrap the response when rendering a <code>JSP</code> resource to
     * implement the Faces implementation specific support for handling interleaved response writing.
     *
     * @return <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface and is a proper portlet
     *         resource response wrapper.
     */
    Class<? extends BridgeWriteBehindResponse> getWriteBehindResourceResponseWrapper();

    /**
     * Sets the <code>List<String></code> of the possible suffixes that Faces recognizes as Faces processed targets. Since JSF
     * 2.0 the default suffix mapping Faces recognizes is a list rather than a single value. This information comes from the
     * web.xml and is used to help the bridge map between viewIds and their underlying resources.
     *
     * @param suffixes <code>List</code> of the suffixes Faces recognizes as Faces targets.
     */
    void setFacesSuffixes(List<String> suffixes);

    /**
     * gets the <code>List<String></code> of the possible suffixes that Faces recognizes as Faces processed targets. Since JSF
     * 2.0 the default suffix mapping Faces recognizes is a list rather than a single value. This information comes from the
     * web.xml and is used to help the bridge map between viewIds and their underlying resources.
     *
     * @return <code>List</code> of the suffixes Faces recognizes as Faces targets.
     */
    List<String> getFacesSuffixes();

    /**
     * Sets the id of the lifecycle the portlet should use for executing Faces requests.
     *
     * @param id
     */
    void setLifecyleId(String id);

    /**
     *
     * @return the lifecycle id the portlet should use for executing Faces requests. If not previously set the value
     *         <code>LifecycleFactory.DEFAULT_LIFECYCLE</code> is returned.
     */
    String getLifecycleId();

    /**
     * This <code>Map</code> is a place to put extra (implementation specific) bridge state or anything else whose lifetime
     * matches this scope.
     *
     * @return a mutable <code>Map<String, Object></code> of bridge context scoped attributes
     */
    Map<String, Object> getAttributes();

    /**
     * By spec, the portlet can configure the specific renderkit it uses vs others in the app as a Portlet init parameter. This
     * allows differing portlets in the app to use different render kits.
     *
     * @return configured renderkit id for this portlet or null if none is configured.
     */
    String getDefaultRenderKitId();

    /**
     * Sets whether or not the bridge should prevent script tags from being rendered as self-closing in the page HEAD.
     *
     * @param preventSelfClosingScriptTag <code>Boolean.TRUE</code> indicates the script tag will not be self-closing.
     *                                    <code>Boolean.FALSE</code> indicates they are self-closing.
     */
    void setPreventSelfClosingScriptTag(boolean preventSelfClosingScriptTag);

    /**
     * Gets whether or not the bridge should prevent script tags from being rendered as self-closing in the page HEAD.
     * If not previously set, it returns <code>false</code>.
     *
     * @return <code>true</code> the script tag will not be self-closing. <code>false</code> indicates they are self-closing.
     */
    boolean doPreventSelfClosingScriptTag();

    /**
     * Sets whether or not the bridge is running with a JSF 2.2 runtime.
     *
     * @param jsf22Runtime <code>Boolean.TRUE</code> indicates we are running with a JSF 2.2 runtime.
     *                     <code>Boolean.FALSE</code> indicates we are NOT running with a JSF 2.2 runtime.
     */
    void setJsf22Runtime(boolean jsf22Runtime);

    /**
     * Gets whether or not the bridge is running with a JSF 2.2 runtime.
     * If not previously set, it returns <code>false</code>.
     *
     * @return <code>true</code> if its a JSF 2.2 runtime. <code>false</code> indicates it is not.
     */
    boolean isJsf22Runtime();

    /**
     * Sets whether or not the bridge should disable support for <code>f:viewParam</code> execution.
     *
     * @param viewParamHandlingDisabled <code>Boolean.TRUE</code> indicates <code>f:viewParam</code> will be disabled.
     *                                  <code>Boolean.FALSE</code> indicates <code>f:viewParam</code> is enabled.
     */
    void setViewParamHandlingDisabled(boolean viewParamHandlingDisabled);

    /**
     * Gets whether or not the bridge should disable support for <code>f:viewParam</code> execution.
     *
     * @return <code>true</code> if <code>f:viewParam</code> is disabled. <code>false</code> indicates it is enabled.
     */
    boolean isViewParamHandlingDisabled();

    /**
     * Sets whether or not the bridge scope should store the result of an ajax request.
     *
     * @param bridgeScopeEnabledOnAjaxRequest <code>Boolean.TRUE</code> indicates the data should be stored in the bridge scope.
     *                                        <code>Boolean.FALSE</code> indicates the data will not be stored in the bridge scope.
     */
    void setBridgeScopeEnabledOnAjaxRequest(boolean bridgeScopeEnabledOnAjaxRequest);

    /**
     * Gets whether or not the bridge scope should store the result of an ajax request. If not previously set, it
     * returns <code>false</code>.
     *
     * @return <code>true</code> if ajax request data should be retained in bridge scope. <code>false</code> indicates
     * it should not.
     */
    boolean isBridgeScopeEnabledOnAjaxRequest();

    /**
     * Sets whether or not Faces Messages should be stored in the bridge scope at the end of an ajax request. Only applicable
     * if {@see isBridgeScopeEnabledOnAjaxRequest} is set to true.
     *
     * @param facesMessagesStoredOnAjaxRequest <code>Boolean.TRUE</code> indicates the messages should be stored.
     *                                         <code>Boolean.FALSE</code> indicates the messages should not be stored.
     */
    void setFacesMessagesStoredOnAjaxRequest(boolean facesMessagesStoredOnAjaxRequest);

    /**
     * Gets whether or not Faces Messages should be stored in the bridge scope at the end of an ajax request. If not
     * previously set, it returns <code>true</code>.
     *
     * @return <code>true</code> if faces messages should be stored in bridge scope, <code>false</code> indicates they
     * should not.
     */
    boolean isFacesMessagesStoredOnAjaxRequest();

    /**
     * Set the name of the parameter for the session id. Only relevant when cookies are disabled. Default value is
     * <code>jsessionid</code>.
     *
     * @param sessionIdParameterName
     */
    void setSessionIdParameterName(String sessionIdParameterName);

    /**
     * Get the name of the parameter for the session id. If not overwritten, the default value is <code>jsessionid</code>.
     *
     * @return String Represents the name session id parameter
     */
    String getSessionIdParameterName();

    /**
     * Sets whether or not the bridge scope should be retained at the end of a Render Request for future use.
     *
     * @param bridgeScopePreservedPostRender <code>Boolean.TRUE</code> indicates scope should be retained.
     *                                        <code>Boolean.FALSE</code> indicates scope should be removed.
     */
    void setBridgeScopePreservedPostRender(boolean bridgeScopePreservedPostRender);

    /**
     * Gets whether or not the bridge scope should be retained at the completion of a Render Request. If not previously set,
     * it returns <code>false</code>.
     *
     * @return <code>true</code> if bridge scope should be retained. <code>false</code> indicates it should not.
     */
    boolean isBridgeScopePreservedPostRender();

    /**
     * Sets whether or not the namespace added to components should be shortened to make them easier to read and reduce
     * the bandwidth requirements for transport to the browser.
     *
     * @param componentNamespaceShortened <code>Boolean.TRUE</code> indicates the namespace should be shortened.
     *                                    <code>Boolean.FALSE</code> indicates the namespace should not be shortened.
     */
    void setComponentNamespaceShortened(boolean componentNamespaceShortened);

    /**
     * Gets whether or not the namespace added to components should be shortened. If not previously set, it returns
     * <code>true</code>.
     *
     * @return <code>true</code> if the namespace should be shortened. <code>false</code> indicates it should not.
     */
    boolean isComponentNamespaceShortened();
}