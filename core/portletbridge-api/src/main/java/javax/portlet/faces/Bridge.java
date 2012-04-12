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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

/**
 * The <CODE>Bridge</CODE> interface is used by a portlet to execute a JSF artifact. Its lifecycle follows the pattern used by
 * other web components such as portlets or servlets, namely:
 * <ul>
 * <li><code>init</code>: one time (per portlet) initialization. Usually invoked during portlet <code>init</code> but may also
 * occur lazily. Context is passed to the Bridge at initialization via <code>PortletContext</code> attributes. See method
 * description for details.</li>
 * <li><code>doFacesRequest</code>: called for each portlet request that is to be handled by Faces. Must only be called after
 * the bridge has been initialized.</li>
 * <li><code>destroy</code>: called to destroy this bridge instance. Usually invoked during portlet <code>destroy</code> but may
 * also occur earlier if the portlet decides to reclaim resources.</li>
 * </ul>
 * <P>
 * Portlet developers are encouraged to allow deployers an ability to configure the particular Bridge implementation it uses
 * within a given deployment. This ensures a best fit solution for a given application server, portlet container, and/or Faces
 * environment. The specifics for this configuation are undefined. Each portlet can define a preferred mechanism. Subclasses of
 * {@link GenericFacesPortlet} automatically inherit this behavior as it recognizes a defined portlet initialization parameter.
 * <p>
 * Implementations of this <code>Bridge</code> interface are required to have a <code>code</code> constructor.
 */
public interface Bridge {

    // Base Bridge attribute/context parameter prefix
    public static final String BRIDGE_PACKAGE_PREFIX = "javax.portlet.faces.";

    // Following are the names of context init parameters that control
    // Bridge behavior. These are specified in the web.xml

    /**
     * Context initialization parameter that specifies the maximum number of bridge request scopes to preserved across all uses
     * within this application.
     */
    public static final String MAX_MANAGED_REQUEST_SCOPES = BRIDGE_PACKAGE_PREFIX + "MAX_MANAGED_REQUEST_SCOPES";

    // allows a portletbridge to control render delgation. A value of "ALWAYS_DELEGATE" indicates
    // the bridge doesn't render itself, it merely delegates. A value of "NEVER_DELEGATE"
    // indicates the bridge never delegates, rather it always overrides and renders.
    // A value of "DEFAULT" indicates the bridge will delegate first and only render
    // if the delegatee throws an exception/throwable.
    public static final String RENDER_POLICY = BRIDGE_PACKAGE_PREFIX + "RENDER_POLICY";

    /**
     * Context initialization parameter that defines the lifecycle ID used to identify the Faces Lifecycle used for this
     * application.
     */
    public static final String LIFECYCLE_ID = "javax.faces.LIFECYCLE_ID";

    /**
     * Context initialization parameter that defines the SAVESTATE_FIELD_MARKER in use in the given deployment. If not set, the
     * Bridge will detect and run properly in either the Faces RI or MyFaces environments.
     */
    public static final String SAVESTATE_FIELD_MARKER = BRIDGE_PACKAGE_PREFIX + "SAVESTATE_FIELD_MARKER";

    // Following are the names of context init attributes set by the portlet to control
    // Bridge behavior. For the GenericFacesPortlet, the values for these come from
    // portlet initialization parameters in the portlet.xml

    /**
     * A PortletContext attribute that a portlet can set prior to calling the bridge's init() method to configure the bridge to
     * exclude specific attributes from its bridge request scope. Value is a comma delimited list containing either a fully
     * qualified attribute name or package name terminated with a ".*" wildcard indicator. In this later case, all attributes in
     * the package name which precedes the ".*" are excluded, non recursive.<br>
     * 
     * As this attribute is scoped to a specific portlet in an application-wide context the attribute name must be include the
     * portlet name as follows: BRIDGE_PACKAGE_PREFIX + context.getPortletName() + excludedRequestAttributes
     */
    public static final String EXCLUDED_REQUEST_ATTRIBUTES = "excludedRequestAttributes";

    /**
     * PortletContext attribute that a portlet must set prior to calling the bridge's init() method to convey to the bridge the
     * set of default viewIds that correspond to this portlet's supported <code>PortletMode</code>s. Its value is a Map with one
     * entry per mode. The mode name is the key. The entry's value is the corresponding default viewId the bridge should use for
     * this mode. <br>
     * <p/>
     * As this attribute is scoped to a specific portlet in an application-wide context the attribute name must be include the
     * portlet name as follows: BRIDGE_PACKAGE_PREFIX + context.getPortletName() + DEFAULT_VIEWID_MAP
     */
    public static final String DEFAULT_VIEWID_MAP = "defaultViewIdMap";

    /**
     * A PortletContext attribute that a portlet can set prior to calling the bridge's init() method to configure the bridge to
     * preserve action parameters received by this portlet along with bridge's request scope so that they may be restored and
     * acessed in subsequent renders. If "true", the action parameters are preserved. If "false", they are not preserved. The
     * bridge default is "false".<br>
     * 
     * As this attribute is scoped to a specific portlet in an application-wide context the attribute name must be include the
     * portlet name as follows: BRIDGE_PACKAGE_PREFIX + context.getPortletName() + preserveActionParams
     */
    public static final String PRESERVE_ACTION_PARAMS = "preserveActionParams";

    /**
     * A PortletContext attribute that a portlet can set prior to calling the bridge's init() method to configure the bridge to
     * use/call the associated eventHandler when processing an event. Value is an instance of <code>BridgeEventHandler</code>.
     * <p/>
     * As this attribute is scoped to a specific portlet in an application-wide context the attribute name must be include the
     * portlet name as follows: BRIDGE_PACKAGE_PREFIX + context.getPortletName() + bridgeEventHandler
     */
    public static final String BRIDGE_EVENT_HANDLER = "bridgeEventHandler";

    /**
     * A PortletContext attribute that a portlet can set prior to calling the bridge's init() method to configure the bridge to
     * use/call the associated publicRenderParameterHandler. This handler is used to process updates that result from public
     * render parameter changes passed in a request. The bridge first pushs all the public render parameter values into the
     * models and then calls this handler's processUpdates method. The handler can then compute further model changes based on
     * the changes. Value is an instance of <code>BridgePublicRenderParameterHandler</code>.
     * <p/>
     * As this attribute is scoped to a specific portlet in an application-wide context the attribute name must be include the
     * portlet name as follows: BRIDGE_PACKAGE_PREFIX + context.getPortletName() + bridgeEventHandler
     */
    public static final String BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER = "bridgePublicRenderParameterHandler";

    /**
     * A PortletContext attribute that a portlet can set prior to calling the bridge's init() method to configure the bridge to
     * default the renderKitId used for rendering this portlet to the named Id. In Faces, the default renderKitId is set in the
     * faces-config.xml and is application wide. In 1.2 this can be overidden by a specially named request parameter. To allow
     * differing portlets in the same app to use different default render kits, without having to add this parameter, the
     * portlet can set this attribute prior to the bridge init(). The bridge will recognize this configuration value and on each
     * request add the special faces request parameter to the request (if its not already present).
     */
    public static final String DEFAULT_RENDERKIT_ID = "defaultRenderKitId";

    // The following are request attributes a portlet can set to control the request
    // processing of the bridge.

    /**
     * PortletRequest attribute that a portlet may set prior to calling the bridge's doFacesRequest() method. The value of this
     * attribute is a <code>String</code> representing the Faces viewId the bridge is to target for this request. Used by a
     * portlet to specifically control a request's view target in situations such as navigating from a nonFaces view to a
     * specific Faces view (other than the default).
     * <p>
     * Generally, the use of this attribute is mutually exclusive with the use of VIEW_PATH. If both have been set in a given
     * request, the bridge gives precedence to VIEW_ID.
     * 
     * javax.portlet.faces.viewId: The value of this attribute identifies the Faces viewId the bridge must use for this request
     * (e.g. /myFacesPage.jsp). This is expected to be a valid Faces viewId though it may optionally contain a query string.
     */
    public static final String VIEW_ID = BRIDGE_PACKAGE_PREFIX + "viewId";

    /**
     * PortletRequest attribute that a portlet may set prior to calling the bridge's doFacesRequest() method. The value of this
     * attribute is a <code>String</code> containing a <code>ContextPath</code> relative path in which the Faces viewId is
     * encoded. Like VIEW_ID, this attribute provides a means for a portlet to explicitly control the Faces target for a
     * specific request. It is used in situations such as navigating from a nonFaces view to a specific Faces view (other than
     * the default).
     * <p>
     * Generally, the use of this attribute is mutually exclusive with the use of VIEW_PATH. If both have been set in a given
     * request, the bridge gives precedence to VIEW_ID.
     * 
     * javax.portlet.faces.viewPath: The value of this attribute contains the the Faces viewId the bridge must use for this
     * request in ContextPath relative path form (e.g. /faces/myFacesPage.jsp). This value may optionally contain a query
     * string.
     */
    public static final String VIEW_PATH = BRIDGE_PACKAGE_PREFIX + "viewPath";

    // Names for special QueryString parameters names the Bridge recognizes in
    // encodeActionURL as signifying to change the corresponding portlet values
    // in the resulting URL

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL() that it recognizes as an
     * indication that this action should encode a PortletMode change to the one indicated by the parameter's value.
     */
    public static final String PORTLET_MODE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "PortletMode";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL() that it recognizes as an
     * indication that this action should encode a WindowState change to the one indicated by the parameter's value.
     */
    public static final String PORTLET_WINDOWSTATE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "WindowState";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL() that it recognizes as an
     * indication that this action should encode a security level change to the one indicated by the parameter's value.
     */
    public static final String PORTLET_SECURE_PARAMETER = BRIDGE_PACKAGE_PREFIX + "Secure";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeActionURL() that it recognizes as an
     * indication that this action should be treated as a direct link and hence shouldn't be encoded as a Portlet action. Rather
     * encodeActionURL merely returns this url unchanged.
     */
    public static final String DIRECT_LINK = BRIDGE_PACKAGE_PREFIX + "DirectLink";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL() that it recognizes as an
     * indication that this resource should be handled in protocol.
     */
    public static final String IN_PROTOCOL_RESOURCE_LINK = BRIDGE_PACKAGE_PREFIX + "InProtocolResourceLink";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL() that it recognizes as an
     * indication that an URL refering back to the page which contains this portlet should be encoded in the resource url. This
     * reference is encoded as the value of a query string parameter whose name is the value of this back link token parameter.
     */
    public static final String BACK_LINK = BRIDGE_PACKAGE_PREFIX + "BackLink";

    /**
     * Special token parameter in the url passed to the bridge's ExternalContext.encodeResourceURL() that it recognizes as an
     * indication that this url refers to Faces view (navigation) and hence should be encoded as an portlet ActionURL rather
     * then a portlet resource url. This token is intended for use in urls signifying a view navigation using components such as
     * <code>h:outputLink</code>.
     */
    public static final String VIEW_LINK = BRIDGE_PACKAGE_PREFIX + "ViewLink";

    // Request attributes set by the bridge that can be used by Faces extensions
    // and/or applications to properly run in a portlet environment.

    /**
     * A PortletRequest attribute set by the bridge when processing a Faces request that signals this request is a Faces
     * postback. Its provided as an alternative signal to the common reliance on the view state parameter as an indicator that
     * this is a postback request. Implementations needing this information and not using the view state parameter indicator can
     * check this attribute when running in a portlet environment.
     */
    public static final String IS_POSTBACK_ATTRIBUTE = BRIDGE_PACKAGE_PREFIX + "isPostback";

    /**
     * A PortletRequest attribute set by the bridge in its <code>ViewHandler.renderView</code> prior to dispatching the request
     * to the view (jsp)to indicating a filter should put the AFTER_VIEW_CONTENT in a buffer on the request for it to process
     * after rendering the view components. In conjunction with the filter this enables preserving rendering order of native JSP
     * rendering and Faces rendering in a jsp.
     */
    public static final String RENDER_CONTENT_AFTER_VIEW = BRIDGE_PACKAGE_PREFIX + "RenderContentAfterView";

    /**
     * A PortletRequest attribute set by an include filter in recognition of the RenderContentAfterView bridge attribute. Its
     * value is either char[] or byte[] holding the AFTER_VIEW_CONTENT generated while rendering this jsp. In conjunction with
     * the bridge this enables preserving rendering order of native JSP rendering and Faces rendering in a jsp.
     */
    public static final String AFTER_VIEW_CONTENT = BRIDGE_PACKAGE_PREFIX + "AfterViewContent";

    /**
     * PortletRequest attribute set by the bridge prior to creating/acquiring a <code>FacesContext</code>. Its value indicates
     * which portlet phase this Faces is executing in. It can be used by Faces subsystems not only to determine the portlet
     * exectution phase but if present (not null) as an indication the request is being processed in a portlet container.
     */
    public static final String PORTLET_LIFECYCLE_PHASE = BRIDGE_PACKAGE_PREFIX + "phase";

    /**
     * PortletSession attribute set by the bridge to hold the last viewId accessed in a given mode. The attribute (key) is
     * composed of this name + the mode name. I.e. javax.portlet.faces.viewIdHistory.view. There is one attribute per supported
     * portlet mode. The attributes are always set even if the user session has never entered the mode. Its initial
     * setting/value is determined by the default viewId configured for the mode. Attribute is used by developers to
     * reference/return to the last view in a given Mode from another mode.
     */
    public static final String VIEWID_HISTORY = BRIDGE_PACKAGE_PREFIX + "viewIdHistory";

    /**
     * Name of PortletResponse property set by the bridge when it recognizes that the view has been rendered using a
     * <code>NamingContainer</code> that ensures all generated ids are namespaced using the consumer provided unique portlet id.
     */
    public static final String PORTLET_NAMESPACED_RESPONSE_PROPERTY = "X-JAVAX-PORTLET-FACES-NAMESPACED-RESPONSE";

    /**
     * Name of the render parameter set by the bridge when it encodes a navigation link to a nonFaces target. Though the bridge
     * recognizes nonFaces targets when it encodes a navigational link, it does not handle the subsequent request. It only
     * handles requests for Faces targets. It is the portlet's responsibility to detect and handle these requests. When the
     * nonFaces target is a path based resource (such as a jsp or servlet), the <code>ContextPath</code> relative path of the
     * resource is written as the value of this render parameter. For convenience, the GenericFacesPortlet recognizes this
     * render parameter in received requests and uses the <code>PortletRequestDispatcher</code> to dispatch to the encoded path
     * instead of calling the bridge to execute the request.
     */
    public static final String NONFACES_TARGET_PATH_PARAMETER = "_jsfBridgeNonFacesView";

    /**
     * Name of a request parameter (generally) encoded in a link from a nonFaces view response. It acts as a marker to the
     * portlet that the nonFaces view intends to navigate to the Faces view expressed in the value of this parameter. It differs
     * from the <code>FACES_VIEW_PATH_PARAMETER</code> in that its value is the actual Faces viewId of the target while the
     * formaer is a <code>ContextPath</code> relative path containing the viewId.
     * <p>
     * Portlets receiving such a parameter should set the the corresponding request attribute
     * <code>javax.portlet.faces.viewId</code> before calling the bridge to handle the request.
     */
    public static final String FACES_VIEW_ID_PARAMETER = "_jsfBridgeViewId";

    /**
     * Name of a request parameter (generally) encoded in a link from a nonFaces view response. It acts as a marker to the
     * portlet that the nonFaces view intends to navigate to the Faces view expressed in the value of this parameter. It differs
     * from the <code>FACES_VIEW_ID_PARAMETER</code> in that its value is a <code>ContextPath</code> relative path containing
     * the viewId while the former is the viewId itself.
     * <p>
     * Portlets receiving such a parameter should set the the corresponding request attribute
     * <code>javax.portlet.faces.viewPath</code> before calling the bridge to handle the request.
     */
    public static final String FACES_VIEW_PATH_PARAMETER = "_jsfBridgeViewPath";

    /**
     * Special value recognized during <code>encodeActionURL</code> of a portlet: url containing either the
     * <code>_jsfBridgeViewId</code> or <code>_jsfBridgeViewPath</code> parameter. <code>encodeActionURL</code> recognizes this
     * value as indicating it needs to generate and encode an URL to the current JSF including its current state. I.e. It not
     * only encodes the link reference but also the existing render parameters so they can be carried forward to reestablish the
     * state.
     */
    public static final String FACES_USE_CURRENT_VIEW_PARAMETER = "_jsfBridgeCurrentView";

    /**
     * Enumeration whose values describe the current portlet phase the bridge is executing Faces within.
     */
    public static enum PortletPhase {
        ACTION_PHASE, RENDER_PHASE, EVENT_PHASE, RESOURCE_PHASE;
    }

    /**
     * Enumeration whose values describe the render policy used by the bridge to render portlets in this application. A policy
     * of DEFAULT indicates the bridge will first delegate rendering and if this results in an exception being thrown will
     * render the itself. A policy of ALWAYS_DELEGATE indicates the bridge will always delegate rendering, never rendering
     * itself. A policy of NEVER_DELEGATE indicates the bridge will always render itself without delegating.
     */
    public static enum BridgeRenderPolicy {
        DEFAULT, ALWAYS_DELEGATE, NEVER_DELEGATE;
    }

    /**
     * Called by the portlet. It indicates that the bridge is being placed into service.
     * <p>
     * The portlet calls the <code>init</code> method exactly once before invoking other lifecycle methods. Usually, done
     * immediately after instantiating the bridge. The <code>init</code> method must complete successfully before the bridge can
     * receive any requests.
     * <p>
     * The portlet cannot place the bridge into service if the <code>init</code> method Throws a <code>BridgeException</code>.
     * <p>
     * Initialization context is passed to bridge via <code>PortletContext</code> attributes. The following attributes are
     * defined:
     * <ul>
     * <li><code>javax.portlet.faces.encodeRedirectURL</code>: instructs the bridge to call
     * <code>ExternalContext.encodeActionURL()</code> before processing the redirect request. This exists because some (newer)
     * versions of JSF 1.2 call <code>encodeActionURL</code> before calling <code>redirect</code> while others do not. This flag
     * adjusts the behavior of the bridge in accordance with the JSF 1.2 implementation it runs with.
     * <li><code>javax.portlet.faces.numManagedActionScopes</code>: defines the maximum number of actionScopes this bridge
     * preserves at any given time. Value is an integer. ActionScopes are managed on a per Bridge class portlet context wide
     * basis. As a typical portlet application uses the same bridge implementation for all its Faces based portlets, this means
     * that all actionScopes are managed in a single bucket.<br>
     * For convenience this interface defines the <code>NUM_MANAGED_ACTIONSCOPES</code> constant.
     * <li><code>javax.faces.lifecycleID</code>: defines the Faces <code>Lifecycle</code> id that bridge uses when acquiring the
     * <code>Faces.Lifecycle</code> via which it executes the request. As a context wide attribute, all bridge instances in this
     * portlet application will use this lifecyle.
     * <li><code>javax.portlet.faces.[portlet name].preserveActionParams</code>: instructs the bridge to preserve action
     * parameters in the action scope and represent them in subsequent renders. Should be used only when binding to a Faces
     * implementation that relies on accessing such parameters during its render phase. As this is a portlet/bridge instance
     * specific attribute, the <code>PortletContext</code>attribute name is qualified by the portlet instance name. This allows
     * different portlets within the same portlet application to have different settings.<br>
     * For convenience this interfaces defines a number of constants that simplifies constructing and/or recognizing this name.
     * </ul>
     * 
     * @param config a <code>PortletConfig</code> object containing the portlet's configuration and initialization parameters
     * @exception BridgeException if an exception has occurred that interferes with the bridge's normal operation. For example,
     *            if the bridge is already initialized.
     * @exception UnavailableException if the portlet cannot perform the initialization at this time.
     */
    public void init(PortletConfig config) throws BridgeException;

    /**
     * Called by the portlet when it wants the bridge to process an action request.
     * 
     * @param request the request object.
     * @param response the response object.
     * @throws BridgeDefaultViewNotSpecifiedException thrown if the request indicates to the Bridge that is should use the
     *         default ViewId and the portlet hasn't supplied one.
     * @throws BridgeUninitializedException thrown if the bridge is not initialized.
     * @throws BridgeException all other internal exceptions are converted to a BridgeException.
     */
    public void doFacesRequest(ActionRequest request, ActionResponse response) throws BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException, BridgeException;

    /**
     * Called by the portlet when it wants the bridge to process an event request.
     * 
     * @param request the request object.
     * @param response the response object.
     * @throws BridgeUninitializedException thrown if the bridge is not initialized.
     * @throws BridgeException all other internal exceptions are converted to a BridgeException.
     */
    public void doFacesRequest(EventRequest request, EventResponse response) throws BridgeUninitializedException,
            BridgeException;

    /**
     * Called by the portlet when it wants the bridge to process a render request.
     * 
     * @param request the request object.
     * @param response the response object.
     * @throws BridgeDefaultViewNotSpecifiedException thrown if the request indicates to the Bridge that is should use the
     *         default ViewId and the portlet hasn't supplied one.
     * @throws BridgeUninitializedException thrown if the bridge is not initialized.
     * @throws BridgeException all other internal exceptions are converted to a BridgeException.
     */
    public void doFacesRequest(RenderRequest request, RenderResponse response) throws BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException, BridgeException;

    /**
     * Called by the portlet when it wants the bridge to process an in-protocol resource request.
     * 
     * @param request the request object.
     * @param response the response object.
     * @throws BridgeUninitializedException thrown if the bridge is not initialized.
     * @throws BridgeException all other internal exceptions are converted to a BridgeException.
     */
    public void doFacesRequest(ResourceRequest request, ResourceResponse response) throws BridgeUninitializedException,
            BridgeException;

    /**
     * Called by the portlet to take the bridge out of service. Once out of service, the bridge must be reinitialized before
     * processing any further requests.
     */
    public void destroy();

    // Session attribute pushed by bridge into session scope to give one access
    // to Application scope
    public static final String SESSION_APPLICATION_SCOPE_MAP = BRIDGE_PACKAGE_PREFIX + "ApplicationScopeMap";
    /**
     * Name of the init parameter with maximum number of bridge request scopes maintained by the bridge. See JSR-301 PLT 3.2
     */
    public static final String MAX_MANAGED_REQUEST_SCOPE_ATTR = "javax.portlet.faces.MAX_MANAGED_REQUEST_SCOPES";
    /**
     * Per-portlet attributes prefix, for additional parameters, as described in the JSR 301 PLT 3.2
     */
    public static final String PORTLET_ATTR_PREFIX = "javax.portlet.faces.";
    /**
     * Per-portlet extensions attributes prefix ( this implementation-specific ), for additional parameters, as described in the
     * JSR 301 PLT 3.2
     */
    public static final String EXTENDED_PORTLET_ATTR_PREFIX = "javax.portlet.faces.extension.";
    public static final String PRESERVE_ACTION_PARAM_ATTR_SUFFIX = ".preserveActionParams";
    public static final String RENDER_POLICY_PARAM_ATTR_SUFFIX = ".renderPolicy";
    /**
     * Request-scope attribute name for the default request viewId . See JSR-301 PLT 3.4
     */
    public static final String DEFAULT_VIEW_ID_ATTR = "javax.portlet.faces.defaultViewId";

    /**
     * Allows portlets to reset the viewId when changing portlet modes. Thus the javax.portlet.faces.defaultViewId.{mode} value
     * is used to reset to the default view defined in portlet.xml
     */
    public static final String RESET_MODE_VIEWID = Bridge.EXTENDED_PORTLET_ATTR_PREFIX + "resetModeViewId";

}