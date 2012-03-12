package org.jboss.portletbridge.config;

import java.util.List;

import java.util.Map;

import javax.faces.lifecycle.LifecycleFactory;

import javax.portlet.PortletConfig;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.logger.BridgeLogger;


/**
 * The <CODE>BridgeConfig</CODE> represents the set of values a portlet can configure for
 * a given bridge instance. Currently, by spec, the portlet passes most of this
 * configuration to the bridge using <CODE>PortletContext</CODE> attributes.  The bridge,
 * in its <code>init()</code> method, is responsible for packaging all this configuration
 * information into a BridgeConfig.  It must then only reference such configuation information
 * from this object.  I.e. it no longer makes reference to the <CODE>PortletContext</CODE> attributes.
 * This allows replacable pieces of the bridge implementation to always have a consistent
 * object to retrieve configuration information from.
 */

public interface BridgeConfig
{
  
  /**
   * Sets the <code>PortletConfig</code> object of the portlet that is utilizing this bridge.
   * 
   * @param config
   *          the config object.
   */
  public void setPortletConfig(PortletConfig config);
  
  /**
   * Gets the <code>PortletConfig</code> object of the portlet that is utilizing this bridge.
   * 
   * @return 
   *          the config object.
   */
  public PortletConfig getPortletConfig();
  

  /**
   * Sets the <code>Map</code> describing the mapping between each supported <code>PortletMode</code> 
   * and its default Faces View (id).  When the bridge receives a request that isn't directly encoded
   * with the target Faces view (such as the initial render request), the bridge relies on these
   * default mappings to determine the target. There is one mapping per <code>PortletMode</code>
   * supported by the portlet (and handledby Faces).  The key to the each entry in the map is the
   * <code>String</code> name of the associated <code>PortletMode</code>;
   * 
   * @param defaultMappings between each supported <code>PortletMode</code> and the default
   *          Faces target.
   */  
  public void setDefaultViewMappings(Map<String, String> defaultMappings);


  /**
   * Gets the <code>Map</code> describing the mapping between each supported <code>PortletMode</code> 
   * and its default Faces View (id).  When the bridge receives a request that isn't directly encoded
   * with the target Faces view (such as the initial render request), the bridge relies on these
   * default mappings to determine the target. There is one mapping per <code>PortletMode</code>
   * supported by the portlet (and handledby Faces).  The key to the each entry in the map is the
   * <code>String</code> name of the associated <code>PortletMode</code>;
   * 
   * @return defaultMappings between each supported <code>PortletMode</code> and the default
   *          Faces target.
   */  
  public Map<String, String> getDefaultViewMappings();
  
  
  
  /**
   * Sets the <code>List</code> of <code>Servlet</code> mappings to the Faces servlet (information taken
   * from web.xml).  The bridge uses these mappings to both detect whether a given URL is handled
   * by Faces or not and to deal with mapping between viewIds and their underlying resources.
   * 
   * @param mappings
   *          the various servlet mappings for the <code>FacesServlet</code>.
   */
  public void setFacesServletMappings(List<String> mappings);


  /**
   * Gets the <code>List</code> of <code>Servlet</code> mappings to the Faces servlet (information taken
   * from web.xml).  The bridge uses these mappings to both detect whether a given URL is handled
   * by Faces or not and to deal with mapping between viewIds and their underlying resources.
   * 
   * @return 
   *          the various servlet mappings for the <code>FacesServlet</code>.
   */  
  public List<String> getFacesServletMappings();
  
  
  /**
   * Sets the name of the parameter used by the bridge to encode the target viewId.
   * 
   * @param name
   *          parameter name that holds the bridge encoded target viewId.
   */
  public void setViewIdRenderParameterName(String name);
  
  
  /**
   * Gets the name of the parameter used by the bridge to encode the target viewId.
   * 
   * @return
   *          parameter name that holds the bridge encoded target viewId.
   */  
  public String getViewIdRenderParameterName();
  
  /**
   * Sets the name of the parameter used by the bridge to encode the target viewId when encoding 
   * a Resource URL.  As resourceURLs can't impact render parameters and the resource request
   * always receives the current render parameters, its we need a different parameter to hold
   * this information.  This allows us to use its existence in the request as an indication 
   * of whether the target is a Faces resource or a regular portlet one.
   * 
   * @param name
   *          parameter name that holds the bridge encoded target viewId.
   */
  public void setViewIdResourceParameterName(String name);
  
  
  /**
   * Gets the name of the parameter used by the bridge to encode the target viewIdwhen encoding 
   * a Resource URL.  As resourceURLs can't impact render parameters and the resource request
   * always receives the current render parameters, its we need a different parameter to hold
   * this information.  This allows us to use its existence in the request as an indication 
   * of whether the target is a Faces resource or a regular portlet one.
   * 
   * @return
   *          parameter name that holds the bridge encoded target viewId.
   */  
  public String getViewIdResourceParameterName();
   
  
  /**
   * Sets the <code>BridgeLogger</code> that the bridge uses to log diagnostic and warning messages.
   * 
   * @param logger
   *          <code>BridgeLogger</code>.
   */  
  public void setLogger(BridgeLogger logger);
  
  
  /**
   * Gets the <code>BridgeLogger</code> that the bridge uses to log diagnostic and warning messages.
   * 
   * @return
   *          <code>BridgeLogger</code>.
   */
  public BridgeLogger getLogger();
  

  /**
   * Sets the <code>BridgeEventHandler</code> that the bridge calls to handle any portlet event it processes.
   * 
   * @param handler
   */    
  public void setEventHandler(BridgeEventHandler handler);
 
 
  /**
   * Gets the <code>BridgeEventHandler</code> that the bridge calls to handle any portlet event it processes.
   * 
   * @return the <code>BridgeEventHandler</code>
   */     
  public BridgeEventHandler getEventHandler();
  
 
  /**
   * Sets the <code>BridgePublicRenderParameterHandler</code> that the bridge calls to handle post processing recalculations
   * following the bridge pushing incoming portlet public render parameters to their models.
   * 
   * @param handler
   */  
  public void setPublicRenderParameterHandler(BridgePublicRenderParameterHandler handler);
  
  
  /**
   * Gets the <code>BridgePublicRenderParameterHandler</code> that the bridge calls to handle post processing recalculations
   * following the bridge pushing incoming portlet public render parameters to their models.
   * 
   * @return <code>BridgePublicRenderParameterHandler</code>
   */ 
  public BridgePublicRenderParameterHandler getPublicRenderParameterHandler();
  
  
  
  /**
   * Sets whether or not the bridge should carry action parameters forward into subsequent renders.
   * 
   * @param preserve <code>true</code> indicates the action parameters are preserved.  
   *                 <code>false</code> indicates they are not.
   */
  public void setPreserveActionParameters(boolean preserve);
  
  /**
   * Sets whether or not the bridge should carry action parameters forward into subsequent renders.
   * 
   * @param preserve <code>Boolean.TRUE</code> indicates the action parameters are preserved.  
   *                 <code>Boolean.FALSE</code> indicates they are not.
   */
  public void setPreserveActionParameters(Boolean preserve);
  
  /**
   * Gets whether or not the bridge should carry action parameters forward into subsequent renders.
   * If not previously set, it returns <code>false</code>.
   * 
   * @return <code>true</code> indicates the action parameters are preserved.  
   *                 <code>false</code> indicates they are not.
   */
  public boolean hasPreserveActionParameters();
  
  
  /**
   * Sets the <code>List</code> of attributes to be excluded from the bridge's request scope.
   * This list includes both the attributes configured in the portlet.xml (portlet init parameter)
   * as well as any configured in any of this web application's faces-config.xml(s).  It doesn't
   * include any of the predefined attributes as defined by the specification.  A list entry is
   * either the fully qualified name of the attribute that should be excluded or a wildcard terminated
   * (package) path.  In the latter case, all attributes whose names reside in this package
   * (non-recusive) are excluded.
   * 
   * @param excludedAttributes <code>List</code> of request attribute names that are to be
   * excluded from the bridge's managed request scope.
   */
  public void setExcludedRequestAttributes(List<String> excludedAttributes);
  
  
  /**
   * Gets the <code>List</code> of attributes to be excluded from the bridge's request scope.
   * This list includes both the attributes configured in the portlet.xml (portlet init parameter)
   * as well as any configured in any of this web application's faces-config.xml(s).  It doesn't
   * include any of the predefined attributes as defined by the specification.  A list entry is
   * either the fully qualified name of the attribute that should be excluded or a wildcard terminated
   * (package) path.  In the latter case, all attributes whose names reside in this package
   * (non-recusive) are excluded.
   * 
   * @return <code>List</code> of request attribute names that are to be
   * excluded from the bridge's managed request scope.  If no entries an empty List is returned.
   */
  public List<String> getExcludedRequestAttributes();
  
 
  /**
   * Sets the <code>Map</code> containing the mappings between portlet public render parameter names
   * and a corresponding Faces EL statement.  The Faces EL is expected to resolve to
   * a managed bean property allowing the bridge to push/pull public render parameter 
   * values directly from managed bean properties.
   * This configuration information is extracted from the faces-config.xml(s).
   * 
   * 
   * @param prpMappings <code>Map<String, String></code>.  The key is the name of the 
   * portlet public render parameter for this mapping.  If prefixed with
   * portletName: the mapping only pertains to the specifically named portlet, otherwise
   * the mapping pertains to all portlets in the web application.  The value is a
   * Faces EL that resolves to a managed bean property.
   */
  
  public void setPublicRenderParameterMappings(Map<String, String> prpMappings);
  
  
  /**
   * Gets the <code>Map</code> containing the mappings between portlet public render parameter names
   * and a corresponding Faces EL statement.  The Faces EL is expected to resolve to
   * a managed bean property allowing the bridge to push/pull public render parameter 
   * values directly from managed bean properties.
   * This configuration information is extracted from the faces-config.xml(s).
   * 
   * 
   * @return <code>Map<String, String></code>.  The key is the name of the 
   * portlet public render parameter for this mapping.  If prefixed with
   * portletName: the mapping only pertains to the specifically named portlet, otherwise
   * the mapping pertains to all portlets in the web application.  The value is a
   * Faces EL that resolves to a managed bean property.
   */
  public Map<String, String> getPublicRenderParameterMappings();
  
  /**
   *
   * @return <code>true</code> if the config has public render parameter mappings.
   */
  public boolean hasPublicRenderParameterMappings();
  
  
  /**
   * Sets the <code>Class</code> that the bridge uses to wrap the response when 
   * rendering a <code>JSP</code> to implement the Faces implementation specific
   * support for handling interleaved response writing.
   * 
   * @param wbrClass
   *          <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface
   *          and is a proper portlet render response wrapper.
   */
  public void setWriteBehindRenderResponseWrapper (Class<? extends BridgeWriteBehindResponse> renderResponseWrapper);
  
  
  /**
   * Gets the <code>Class</code> that the bridge uses to wrap the response when 
   * rendering a <code>JSP</code> to implement the Faces implementation specific
   * support for handling interleaved response writing.
   * 
   * @return 
   *          <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface
   *          and is a proper portlet render response wrapper.
   */
  public Class<? extends BridgeWriteBehindResponse> getWriteBehindRenderResponseWrapper();
  
  
  /**
   * Sets the <code>Class</code> that the bridge uses to wrap the response when 
   * rendering a <code>JSP</code> resource to implement the Faces implementation specific
   * support for handling interleaved response writing.
   * 
   * @param wbrClass
   *          <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface
   *          and is a proper portlet resource response wrapper.
   */
  public void setWriteBehindResourceResponseWrapper (Class<? extends BridgeWriteBehindResponse> resourceResponseWrapper);
  
  
  /**
   * Gets the <code>Class</code> that the bridge uses to wrap the response when 
   * rendering a <code>JSP</code> resource to implement the Faces implementation specific
   * support for handling interleaved response writing.
   * 
   * @return 
   *          <code>Class</code> that implements the <code>BridgeWritebehindResponse</code> interface
   *          and is a proper portlet resource response wrapper.
   */
  public Class<? extends BridgeWriteBehindResponse> getWriteBehindResourceResponseWrapper();
  
  
  /**
   * Sets the <code>List<String></code> of the possible suffixes that
   * Faces recognizes as Faces processed targets.  Since JSF 2.0 the default suffix
   * mapping Faces recognizes is a list rather than a single value.  This information
   * comes from the web.xml and is used to help the bridge map between viewIds and
   * their underlying resources.
   * 
   * @param suffixes
   *          <code>List</code> of the suffixes Faces recognizes as Faces targets.
   */
  public void setFacesSuffixes(List<String> suffixes);
  
  
  /**
   * gets the <code>List<String></code> of the possible suffixes that
   * Faces recognizes as Faces processed targets.  Since JSF 2.0 the default suffix
   * mapping Faces recognizes is a list rather than a single value.  This information
   * comes from the web.xml and is used to help the bridge map between viewIds and
   * their underlying resources.
   * 
   * @return  <code>List</code> of the suffixes Faces recognizes as Faces targets.
   */  
  public List<String> getFacesSuffixes();
  
  /**
   * Sets the id of the lifecycle the portlet should use for executing Faces requests.
   * @param id
   */
  public void setLifecyleId(String id);
  
  /**
   * 
   * @return the lifecycle id the portlet should use for executing Faces requests.  If not previously set the value 
   * <code>LifecycleFactory.DEFAULT_LIFECYCLE</code> is returned.
   */
  public String getLifecycleId();
  
  /**
   * This <code>Map</code> is a place to put extra (implementation specific) bridge state
   * or anything else whose lifetime matches this scope.
   * @return a mutable <code>Map<String, Object></code> of bridge context scoped attributes
   */
  public Map<String, Object> getAttributes();
  
  /**
   * By spec, the portlet can configure the specific renderkit it uses vs others in the app as a Portlet
   * init parameter.  This allows differeing portlets in the app to use different render kits.
   * 
   * @return configured renderkit id for this portlet or null if none is configured.
   */
  public String getDefaultRenderKitId();
  
}