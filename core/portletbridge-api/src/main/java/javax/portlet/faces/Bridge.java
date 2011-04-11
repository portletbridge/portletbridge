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
package javax.portlet.faces;

import javax.portlet.*;

public interface Bridge {

   // Base Bridge attribute/context parameter prefix
   public static final String BRIDGE_PACKAGE_PREFIX = "javax.portlet.faces.";

   // Following are the names of context init parameters that control
   // Bridge behavior.  These are specified in the web.xml

   public static final String MAX_MANAGED_REQUEST_SCOPES =
           BRIDGE_PACKAGE_PREFIX + "MAX_MANAGED_REQUEST_SCOPES";

   /**
    * Context initialization parameter that defines the SAVESTATE_FIELD_MARKER
    * in use in the given deployment.  If not set, the Bridge will detect and run
    * properly in either the Faces RI or MyFaces environments.
    */
   public static final String SAVESTATE_FIELD_MARKER = BRIDGE_PACKAGE_PREFIX
           + "SAVESTATE_FIELD_MARKER";


   public static final String LIFECYCLE_ID =
           "javax.faces.LIFECYCLE_ID";

   // Attribute signifying whether this render is a postback or not.
   public static final String IS_POSTBACK_ATTRIBUTE =
           BRIDGE_PACKAGE_PREFIX + "isPostback";

   /**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL()
    * that it recognizes as an indication that this action should encode a PortletMode
    * change to the one indicated by the parameter's value.
    */
   public static final String PORTLET_MODE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "PortletMode";

   /**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL()
    * that it recognizes as an indication that this action should encode a WindowState change to
    * the one indicated by the parameter's value.
    */
   public static final String PORTLET_WINDOWSTATE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "WindowState";

   /**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL()
    * that it recognizes as an indication that this action should encode a security level change to
    * the one indicated by the parameter's value.
    */
   public static final String PORTLET_SECURE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "Secure";

   // Special session attribute name to hold the application_scope in the
   // portlet_scope of the session so these are accessible as well.
   public static final String APPLICATION_SCOPE_MAP = "javax.portlet.faces.ApplicationScopeMap";

   // Following are the names of context attributes that a portletbridge can set prior
   // to calling the bridge's init() method to control Bridge behavior.

   // These attributes are scoped to a specific portletbridge in the context
   // hence to acquire one must include the portletbridge name within attribute name:
   // BRIDGE_PACKAGE_PREFIX + context.getPortletName() + attributeName

   // if "true" indicates the bridge will preserve all the action params in its
   // request scope and restore them as parameters in the subsequent renders
   public static final String PRESERVE_ACTION_PARAMS = "preserveActionParams";

   // allows a portletbridge to control render delgation.  A value of "ALWAYS_DELEGATE" indicates
   // the bridge doesn't render itself, it merely delegates.  A value of "NEVER_DELEGATE"
   // indicates the bridge never delegates, rather it always overrides and renders.
   // A value of "DEFAULT" indicates the bridge will delegate first and only render
   // if the delegatee throws an exception/throwable.
   public static final String RENDER_POLICY = BRIDGE_PACKAGE_PREFIX+"RENDER_POLICY";


   // Parameter that can be added to an ActionURL to signify it is a direct link
   // and hence shouldn't be encoded by encodeActionURL as an actionURL
   public static final String DIRECT_LINK = BRIDGE_PACKAGE_PREFIX + "DirectLink";

   // Session attribute pushed by bridge into session scope to give one access
   // to Application scope
   public static final String SESSION_APPLICATION_SCOPE_MAP = BRIDGE_PACKAGE_PREFIX
           + "ApplicationScopeMap";

   /**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL() 
    * that it recognizes as an indication that this resource should be handled in protocol. 
    */
   public static final String 	IN_PROTOCOL_RESOURCE_LINK = BRIDGE_PACKAGE_PREFIX + "InProtocolResourceLink"; 

   // Request attribute pushed by bridge in renderView to indicate it can
   // handle a filter putting the AFTER_VIEW_CONTENT in a buffer on the request.
   // Allows rendering order to be preserved in jsps
   public static final String RENDER_CONTENT_AFTER_VIEW = BRIDGE_PACKAGE_PREFIX
           + "RenderContentAfterView";

   // Request attribute set by servlet filter in request/responseWrapper to
   // place the AFTER_VIEW_CONTENT in a buffer on the request.
   // Allows filter to transfer such content back to the bridge/renderView so
   // if can output in correct order.  Should only be done if
   // RENDER_CONTENT_AFTER_VIEW request attribute is true.
   public static final String AFTER_VIEW_CONTENT = BRIDGE_PACKAGE_PREFIX
           + "AfterViewContent";

   /**
	 * javax.portlet.faces.viewId: The value of this attribute identifies the
	 * Faces viewId the bridge must use for this request (e.g.
	 * /myFacesPage.jsp). This is expected to be a valid Faces viewId though it
	 * may optionally contain a query string.
	 */
   public static final String VIEW_ID = BRIDGE_PACKAGE_PREFIX + "viewId";

/**
	 * javax.portlet.faces.viewPath: The value of this attribute contains the
	 * the Faces viewId the bridge must use for this request in ContextPath
	 * relative path form (e.g. /faces/myFacesPage.jsp). This value may
	 * optionally contain a query string.
	 */
   public static final String VIEW_PATH = BRIDGE_PACKAGE_PREFIX + "viewPath";

/**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL()
    * that it recognizes as an indication that this url refers to Faces view (navigation) and
    * hence should be encoded as an portlet ActionURL rather then a portlet resource url.  This token
    * is intended for use in urls signifying a view navigation using components such as
    * <code>h:outputLink</code>.
    */

   public static final String VIEW_LINK = BRIDGE_PACKAGE_PREFIX + "ViewLink";

/**
    * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL()
    * that it recognizes as an indication that an URL refering back to the page which
    * contains this portlet should be encoded in the resource url. This reference is
    * encoded as the value of a query string parameter whose name is the value of this back
    * link token parameter.
    */

   public static final String BACK_LINK = BRIDGE_PACKAGE_PREFIX + "BackLink";

/**
    * PortletContext attribute that a portlet must set prior
    * to calling the bridge's init() method to convey to the bridge the set of default
    * viewIds that correspond to this portlet's supported <code>PortletMode</code>s.
    * Its value is a Map with one entry per mode.  The mode name is the key.  The entry's
    * value is the corresponding default viewId the bridge should use for this mode.
    * <br>
    * <p/>
    * As this attribute is scoped to a specific portlet in an application-wide context
    * the attribute name must be include the portlet name as follows:
    * BRIDGE_PACKAGE_PREFIX + context.getPortletName() + DEFAULT_VIEWID_MAP
    */
   public static final String DEFAULT_VIEWID_MAP = "defaultViewIdMap";

   /**
    * PortletSession attribute set by the bridge to hold the last viewId accessed in a given mode.
    * The attribute (key) is composed of this name + the mode name.  I.e.
    * javax.portlet.faces.viewIdHistory.view.  There is one attribute per supported portlet
    * mode.  The attributes are always set even if the user session has never entered the
    * mode.  Its initial setting/value is determined by the default viewId configured
    * for the mode.  Attribute is used by developers to reference/return to the last view in
    * a given Mode from another mode.
    */
   public static final String VIEWID_HISTORY = BRIDGE_PACKAGE_PREFIX + "viewIdHistory";
   

   // Following are the names of request attributes the Bridge must set before
   // acquiring its first FacesContext/FacesContextFactory in each request
   public static final String PORTLET_LIFECYCLE_PHASE = BRIDGE_PACKAGE_PREFIX + "phase";

   public static final String PORTLET_NAMESPACED_RESPONSE_PROPERTY = "X-JAVAX-PORTLET-FACES-NAMESPACED-RESPONSE";
   /**
    * Name of the init parameter with maximum number of bridge request scopes
    * maintained by the bridge. See JSR-301 PLT 3.2
    */
   public static final String MAX_MANAGED_REQUEST_SCOPE_ATTR = "javax.portlet.faces.MAX_MANAGED_REQUEST_SCOPES";
   /**
    * Per-portlet attributes prefix, for additional parameters, as described in
    * the JSR 301 PLT 3.2
    */
   public static final String PORTLET_ATTR_PREFIX = "javax.portlet.faces.";
   /**
    * Per-portlet extensions attributes prefix ( this implementation-specific ),
    * for additional parameters, as described in the JSR 301 PLT 3.2
    */
   public static final String EXTENDED_PORTLET_ATTR_PREFIX = "javax.portlet.faces.extension.";
   // allows a portletbridge to which request attributes the bridge excludes from its
   // managed request scope.
   public static final String EXCLUDED_REQUEST_ATTRIBUTES = "excludedRequestAttributes";
   public static final String PRESERVE_ACTION_PARAM_ATTR_SUFFIX = ".preserveActionParams";
   public static final String RENDER_POLICY_PARAM_ATTR_SUFFIX = ".renderPolicy";
   /**
    * Request-scope attribute name for the default request viewId . See JSR-301
    * PLT 3.4
    */
   public static final String DEFAULT_VIEW_ID_ATTR = "javax.portlet.faces.defaultViewId";
   
   
   /** 
    * 
    */
   public static final String NONFACES_TARGET_PATH_PARAMETER = "_jsfBridgeNonFacesView";

   /** 
    * 
    */
   public static final String FACES_VIEW_ID_PARAMETER = "_jsfBridgeViewId";

   /** 
    * 
    */
   public static final String FACES_VIEW_PATH_PARAMETER = "_jsfBridgeViewPath";


   /**
    * Allows portlets to reset the viewId when changing portlet modes. Thus the
    * javax.portlet.faces.defaultViewId.{mode} value is used to reset to the default 
    * view defined in portlet.xml
    */
   public static final String RESET_MODE_VIEWID = Bridge.EXTENDED_PORTLET_ATTR_PREFIX + "resetModeViewId";


   /**
    * A PortletContext attribute that a portlet can set prior
    * to calling the bridge's init() method to configure the bridge to use/call
    * the associated eventHandler when processing an event. Value is an instance of
    * <code>BridgeEventHandler</code>.
    * <p/>
    * As this attribute is scoped to a specific portlet in an application-wide context
    * the attribute name must be include the portlet name as follows:
    * BRIDGE_PACKAGE_PREFIX + context.getPortletName() + bridgeEventHandler
    */
   public static final String BRIDGE_EVENT_HANDLER = "bridgeEventHandler";

   /**
    * A PortletContext attribute that a portlet can set prior
    * to calling the bridge's init() method to configure the bridge to use/call
    * the associated publicRenderParameterHandler.  This handler is used to
    * process updates that result from public render parameter changes passed in
    * a request.  The bridge first pushs all the public render parameter values into the models and
    * then calls this handler's processUpdates method.  The handler can then compute
    * further model changes based on the changes. Value is an instance of
    * <code>BridgePublicRenderParameterHandler</code>.
    * <p/>
    * As this attribute is scoped to a specific portlet in an application-wide context
    * the attribute name must be include the portlet name as follows:
    * BRIDGE_PACKAGE_PREFIX + context.getPortletName() + bridgeEventHandler
    */
   public static final String BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER = "bridgePublicRenderParameterHandler";

   public static enum PortletPhase {
	    ACTION_PHASE,
	    RENDER_PHASE,
	    EVENT_PHASE,
	    RESOURCE_PHASE;
   }

   public static enum BridgeRenderPolicy {
      DEFAULT,
      ALWAYS_DELEGATE,
      NEVER_DELEGATE;
   }

   /*
    * (non-Javadoc)
    *
    * @see javax.portlet.GenericPortlet#init(javax.portlet.PortletConfig)
    */
   public void init(PortletConfig config) throws BridgeException;

   /*
    * (non-Javadoc)
    *
    * @see javax.portlet.GenericPortlet#destroy()
    */
   public void destroy();

   /**
	 * @param request
	 * @param response
	 * @throws PortletException
	 */
	public void doFacesRequest(ActionRequest request, ActionResponse response)
			throws BridgeUninitializedException,
			BridgeDefaultViewNotSpecifiedException, BridgeException;

	/**
	 * @param request
	 * @param response
	 * @throws PortletException
	 */
	public void doFacesRequest(RenderRequest request, RenderResponse response)
			throws BridgeUninitializedException,
			BridgeDefaultViewNotSpecifiedException, BridgeException;

	/**
	 * @param request
	 * @param response
	 * @throws BridgeUninitializedException
	 * @throws BridgeException
	 */
	public void doFacesRequest(ResourceRequest request,
			ResourceResponse response) throws BridgeUninitializedException,
			BridgeException;

	/**
	 * @param request
	 * @param response
	 * @throws BridgeUninitializedException
	 * @throws BridgeException
	 */
	public void doFacesRequest(EventRequest request, EventResponse response)
			throws BridgeUninitializedException, BridgeException;

}