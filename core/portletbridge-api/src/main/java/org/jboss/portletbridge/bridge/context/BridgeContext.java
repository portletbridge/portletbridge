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
package org.jboss.portletbridge.bridge.context;

import java.util.List;

import java.util.Map;

import javax.faces.context.FacesContext;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeInvalidViewPathException;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager;

/**
 * The <CODE>BridgeContext</CODE> contains the runtime specific state the bridge makes public so its various independent
 * pieces can work together to properly operate. Simplistically, the bridge is a controller and a set of Faces
 * extensions that work together execute Faces requests in a portlet container. By encapsulating state that is shared
 * between these pieces, an extensible, pluggable bridge is supported as bridge implementors can now extend a base
 * implementation by extending or overriding any of the Faces extensions (and other hook points) required to add their
 * specific behaviors.
 * <p>
 * A new <code>BridgeContext</code> is created in each bridge request <code>doFacesRequest()</code>. After the bridge
 * controller has initialized the context the <code>SystemEvent BridgeContextInitializedEvent</code> is dispatched to
 * any registered listeners for further transformation. Once the bridge has acquired the <code>FacesContext</code>, the
 * <code>BridgeContext</code> is added to the <code>FacesContext</code> scope making it freely available from anywhere
 * within Faces during that request.
 */
public abstract class BridgeContext {

    /**
     * Called at the end of bridge request processing. It releases associated state.
     */
    public abstract void release();

    /**
     * Sets the <code>PortletContext</code> of the portlet invoking this bridge.
     *
     * @param context
     */
    public abstract void setPortletContext(PortletContext context);

    /**
     *
     * @return the <code>PortletContext</code> of the portlet invoking this bridge.
     */
    public abstract PortletContext getPortletContext();

    /**
     * Sets the <code>PortletRequest</code> that invoked the bridge.
     *
     * @param request
     */
    public abstract void setPortletRequest(PortletRequest request);

    /**
     *
     * @return the <code>PortletRequest</code> that invoked the bridge.
     */
    public abstract PortletRequest getPortletRequest();

    /**
     * Sets the <code>PortletResponse</code> that invoked the bridge.
     *
     * @param response
     */
    public abstract void setPortletResponse(PortletResponse response);

    /**
     *
     * @return the <code>PortletResponse</code> that invoked the bridge.
     */
    public abstract PortletResponse getPortletResponse();

    /**
     * Sets the request phase govering this context. Used to both detect whether one is executing in a portlet request
     * without referencing potential unbound classes as well as to determine the type of request and response one is
     * dealing with as there are distinct portlet request and response objects per portlet lifecycle phase.
     * <p>
     * Note: as per specification, its more typical for the Faces developer to rely on the request attribute containing
     * this same information. Its included in the BridgeContext primarily for bridge (extension) implementors.
     *
     * @param phase
     */
    public abstract void setPortletRequestPhase(Bridge.PortletPhase phase);

    /**
     * Gets the request phase govering this context. Used to both detect whether one is executing in a portlet request
     * without referencing potential unbound classes as well as to determine the type of request and response one is
     * dealing with as there are distinct portlet request and response objects per portlet lifecycle phase.
     * <p>
     * Note: as per specification, its more typical for the Faces developer to rely on the request attribute containing
     * this same information. Its included in the BridgeContext primarily for bridge (extension) implementors.
     *
     * @return the current portlet request lifcycle phase we are executing in.
     */
    public abstract Bridge.PortletPhase getPortletRequestPhase();

    /**
     * Sets the <code>BridgeConfig</code> that corresponds to this instance.
     *
     * @param config
     */
    public abstract void setBridgeConfig(BridgeConfig config);

    /**
     *
     * @return the <code>BridgeConfig</code> that corresponds to this instance.
     */
    public abstract BridgeConfig getBridgeConfig();

    /**
     * This <code>Map</code> is a place to put extra (implementation specific) bridge state or anything else whose
     * lifetime matches this scope.
     *
     * @return a mutable <code>Map<String, Object></code> of bridge context scoped attributes
     */
    public abstract Map<String, Object> getAttributes();

    // Default = true; always parts of the bridge read/write whether bridge request scope is preserved at the end of
    // this
    // request
    /**
     * Indicates whether the controller should preserve the bridge request scope. By specification there are a variety
     * of reasons why the bridge shouldn't preserve a request scope. Sometimes this is determined in one of the bridge's
     * Faces extensions that is separate from the controller. The dectector used this method to convery to the
     * controller its behavior when its done with the request and considers whether to save the bridge request scope or
     * not. <code>true</code> means the request scope should be preserved. <code>false</code> means the request scope
     * should not be preserved. The default is <code>true</code>
     *
     * @param preserve
     */
    public abstract void setBridgeRequestScopePreserved(boolean preserve);

    /**
     * @return the <code>boolean</code> indicating whether the controller should preserve the bridge request scope or
     *         not. <code>true</code> means the request scope should be preserved. <code>false</code> means the request
     *         scope should not be preserved. The default is <code>true</code>
     */
    public abstract boolean isBridgeRequestScopePreserved();

    /**
     * Faces stores the current view state in a hidden field in the response. When this is resupplied as part of the
     * POSTBACK, Faces is able to restore the view and process the action. In the portlet model, however, not every
     * (following) request is a POSTBACK. The portal can ask the portlet to redisplay itself at any time merely because
     * something has else has occurred on the page (outside the portlet) that causes the portal to rerender the entire
     * page. As this isn't a POSTBACK the view state hidden field isn't supplied, yet Faces still needs to restore the
     * view from this state if its to properly re-render. To support this bridge must capture the value of this hidden
     * field and manage it in its state (often the bridge request scope). This capture involves using a Faces extension
     * (<code>StateManager</code>) to extract the parameter value and the controller to manage the state. The value is
     * conveyed between these two subsystems through here. This method sets the captured view state parameter value for
     * later retrieval.
     *
     * @param savedViewStateParam
     */
    // Allows a StateManager to communicate the saved ViewState to the controller -- so controller
    // can retain for reference on portlet redisplay
    public abstract void setSavedViewStateParam(String savedViewStateParam);

    /**
     * Faces stores the current view state in a hidden field in the response. When this is resupplied as part of the
     * POSTBACK, Faces is able to restore the view and process the action. In the portlet model, however, not every
     * (following) request is a POSTBACK. The portal can ask the portlet to redisplay itself at any time merely because
     * something has else has occurred on the page (outside the portlet) that causes the portal to rerender the entire
     * page. As this isn't a POSTBACK the view state hidden field isn't supplied, yet Faces still needs to restore the
     * view from this state if its to properly re-render. To support this bridge must capture the value of this hidden
     * field and manage it in its state (often the bridge request scope). This capture involves using a Faces extension
     * (<code>StateManager</code>) to extract the parameter value and the controller to manage the state. The value is
     * conveyed between these two subsystems through here.
     *
     * @return the captured view state parameter.
     */
    public abstract String getSavedViewStateParam();

    /**
     * The bridge allows viewIds in the faces-config.xml to contain queryStrings. As the <code>NavigationHandler</code>
     * resolves such navigation and create the asscoiated view and Faces itself doesn't expect these query strings, the
     * bridge needs to extract this query string and stash its value for later retrieval/use by the controller which
     * encodes these extra parameters as part of its action response.
     *
     * @param queryString
     *            query string portion of a viewId
     */
    public abstract void setNavigationQueryString(String queryString);

    /**
     * The bridge allows viewIds in the faces-config.xml to contain queryStrings. As the <code>NavigationHandler</code>
     * resolves such navigation and create the asscoiated view and Faces itself doesn't expect these query strings, the
     * bridge needs to extract this query string and stash its value for later retrieval/use by the controller which
     * encodes these extra parameters as part of its action response.
     *
     * @return the queryString portion associated with the viewId that created the current <code>UIViewRoot</code>
     */
    public abstract String getNavigationalQueryString();

    /**
     * The bridge is required to support redirects to other Faces views during render. As the portlet model doesn't
     * support redirects, the bridge controller must simulate the redirect by unwinding the current lifecycle execution
     * and restarting a new lifecycle execution based on the new target. In addition any additional query string
     * parameters must be represented in the request when this lifecycle is rerun.
     * <p>
     * Such redirects are detected in <code>ExternalContext.redirect()</code> but implemented by the controller. This
     * sets the query string portion of the redirect url (exclusive of the ?) so its available to the controller in
     * handling the render redirect. This method automatically calls <code>setRenderRedirect(true);</code> to flag that
     * a renderRedirect has been detected.
     *
     * @param queryString
     */
    public abstract void setRenderRedirectQueryString(String queryString);

    /**
     * The bridge is required to support redirects to other Faces views during render. As the portlet model doesn't
     * support redirects, the bridge controller must simulate the redirect by unwinding the current lifecycle execution
     * and restarting a new lifecycle execution based on the new target. In addition any additional query string
     * parameters must be represented in the request when this lifecycle is rerun.
     * <p>
     * Such redirects are detected in <code>ExternalContext.redirect()</code> but implemented by the controller.
     *
     * @return prevously set query string (exclusive of the ?) portion of the render redirect url
     */
    public abstract String getRenderRedirectQueryString();

    /**
     * Sets whether a render redirect has occurred in this request. Generally this is set automatically by
     * <code>setRenderRedirectQueryString</code>.
     *
     * @param redirect
     */
    public abstract void setRenderRedirect(boolean redirect);

    /**
     *
     * @return indication of whether a render redirect has occured. <code>true</code> if there has been one in this
     *         request. Default is <code>false</code>.
     */
    public abstract boolean hasRenderRedirect();

    /**
     * Set if the renderRedirect occurred after the (JSP) dispatch. Needed because <code>dispatch.forward()</code> can't
     * be called twice in the same request. Setting this will allow the bridge to know to use
     * <code>dispatch.include()</code> for rendering the "redirected" view.
     *
     * @param afterDispatch
     */
    public abstract void setRenderRedirectAfterDispatch(boolean afterDispatch);

    /**
     *
     * @return indication of whether the renderRedirect occurred before or after the dispatch. <code>true</code>
     *         indicates the redirect ocurred after the dispatch. <code>false</code> by default.
     */
    public abstract boolean hasRenderRedirectAfterDispatch();

    // Holds a List of attr names that exist prior to acquriing Faces artifacts -- used
    // to properly clean things up post Lifecycle and in support of renderredirects

    /**
     * Sets the names of the attributes which existed before the <code>FacesContext</code> is acquired. Used for
     * attribute exclusion, request resetting before a render redirect, as well as allowing the FacesContext to release
     * request attrs during its release.
     *
     * @param names
     *            list of attribute names that exist in the request scope before acquiring the <code>FacesContext</code>
     */
    public abstract void setPreFacesRequestAttrNames(List<String> names);

    /**
     *
     * @return the names of the request attributes that existed before the <code>FacesContext</code> was acquired.
     */
    public abstract List<String> getPreFacesRequestAttrNames();

    // Set by the controller to communicate which parameters are part of the preserved ActionParameters
    // so the encodeActionURL can excldue them when encoding a self-referencing portlet: url -- i.e
    // one that is supposed to be a redisplay preserving the current state.

    /**
     * <code>Map</code> of the action parameters that have been preserved for this render request. Set by the controller
     * when processing a render so the <code>ExternalContext.encodeActionURL()</code> can exclude them from its encoding
     * of a self-referencing portlet: url. I.e. one that is supposed to redisplay preserving the existing state.
     *
     * @param actionParamMap
     *            <code>Map</code> of the action parameters in this render request.
     */
    public abstract void setPreservedActionParams(Map<String, String[]> actionParamMap);

    /**
     * @return previously set <code>Map</code> of the action parameters in this render request.
     */
    public abstract Map<String, String[]> getPreservedActionParams();

    /**
     * Bridge maintains in the session the last viewId accessed in each mode. This allows developers to use EL access to
     * get the viewId (with appropriate QS) to navigate back to the view that was left when a new mode was entered. Key
     * in the session is: javax.portlet.faces.viewIdHistory.<i>modeName</i>. value is the corresponding viewId with an
     * encoded QS containing the pertinent state (scope id, etc) to return you to the state you left. Prior to the first
     * request the bridge sets up a default history by adding one session attribute per supported mode. The value is the
     * default viewId configured for this mode for this portlet. This is done so a developers EL will always resolve to
     * non-null.
     *
     * @param mode
     *            name of the portlet mode
     * @param viewId
     *            JSF view that we are currently in/adding to the history. Includes a QS of the various state needed to
     *            restore this view
     * @param preserveRenderParams
     *            indicates whether the render params are carried into the history. Generally <code>true</code> is the
     *            view's mode hasn't changed from what the bridge finds encoded in its render params and
     *            <code>false</code> otherwise.
     */
    public abstract void setViewHistory(String mode, String viewId, boolean preserveRenderParams);

    /**
     * Returns the last viewId rendered for a given mode. Allows one to return to the view you left when you entered a
     * new mode. Generally this isn't called directly other than potentially to verify that the attributes have
     * initially been set up as the primary use case is to allow developers to access this value directly from the
     * session object using EL.
     *
     * @param mode
     * @return
     */
    public abstract String getViewHistory(String mode);

    /**
     * Returns the Faces (viewid) target as described by the request. Note: This value must always be calculated (never
     * cached). The value must reflect the target described by the request object contained in the
     * <code>ExternalContext</code> if available. If not, then the target is computed from the <code>BridgeContext's
     *  PortletRequest</code>
     *
     * @param excludeQueryString
     *            if <code>true</code> then only the target Faces viewId is returned regardless of whether the viewId
     *            contains a query string. I.e. unlike core Faces, the bridge allows the default viewIds and viewIds in
     *            the Faces navigation rules to contain query strings that augment the view. For example one can include
     *            a new mode in a Faces navigation rule. This boolean controls whether this extra query string portion
     *            of a viewid is returned or not.
     *
     * @return Returns the Faces (viewid) target as described by the request. Returns <code>null</code> if the request
     *         doesn't explicitly specify a target or if the target specified in the request doesn't match the request's
     *         <code>PortletMode</code>. When a target viewId is returned it may contain contain (an optional) query
     *         string if not specifically excluded.
     */
    public abstract String getFacesViewIdFromRequest(boolean excludeQueryString) throws BridgeInvalidViewPathException;

    /**
     * Returns the Faces (viewid) currently targeted. Note: This value must always be calculated (never cached). If the
     * <code>FacesContext</code> has a current <code>UIViewRoot</code>, it returns its id. Otherwise it computes the
     * target <code>viewId</code> as follows: returns the result of calling <code>getFacesViewIdFromRequest()</code> if
     * not null otherwise return the result of <code>getDefaultViewIdForRequest()</code>.
     *
     * @param excludeQueryString
     *            if <code>true</code> then only the target Faces viewId is returned regardless of whether the viewId
     *            contains a query string. I.e. unlike core Faces, the bridge allows the default viewIds and viewIds in
     *            the Faces navigation rules to contain query strings that augment the view. For example one can include
     *            a new mode in a Faces navigation rule. This boolean controls whether this extra query string portion
     *            of a viewid is returned or not.
     *
     * @return Returns the Faces (viewid) target as described by the request.
     */
    public abstract String getFacesViewId(boolean excludeQueryString) throws BridgeInvalidViewPathException;

    /**
     * Returns the Bridge request scope corresponding to current view and mode. I.e. the result of calling
     * <code>getFacesViewId</code> and <code>PortletRequest.getPortletMode().toString()</code>.
     *
     * @return the current Bridge request scope or null if no scope corresponding to the current view and mode
     */
    public abstract BridgeRequestScope getBridgeScope();

    /**
     * Returns the Bridge request scope corresponding to passed viewId and mode.
     *
     * @param viewId
     *            scope's viewId
     * @param mode
     *            scope's portlet mode
     * @return the current Bridge request scope or null if no scope corresponding to the current view and mode
     */
    public abstract BridgeRequestScope getBridgeScope(String viewId, String mode);

    /**
     *
     * @return the Bridge's RequestScopeManager used by this portlet
     */
    public abstract BridgeRequestScopeManager getBridgeRequestScopeManager();

    /**
     * Returns the Faces (viewid) target as represented in the path. Basically, this does a path to viewId mapping based
     * on the Faces servlet mappings in the web.xml. I.e. if Faces is suffix mapped (*.jsf) then a path of /foo.jsf will
     * return foo.jsp (or whatever is the configured mapping from .jsf to .xxx). If Faces is prefix mapped (/faces/*)
     * then a path of /faces/foo.jsp will return foo.jsp. Note:
     *
     * @param path
     *            contains encoded viewId to be extracted. <code>path</code> can either contain the context path or be
     *            relative to it. If <code>path</code> contains a query string it is ignored.
     *
     * @return the target viewId extracted from the path. This viewId will never contain a query string.
     */
    public abstract String getFacesViewIdFromPath(String path) throws BridgeInvalidViewPathException;

    /**
     * Returns the default Faces (viewid) target configured for the current request's <code>PortletMode</code>. The
     * value must use the request contained in the <code>ExternalContext</code> if available. If not, then the target is
     * computed from the <code>BridgeContext's
     *  PortletRequest</code>
     *
     * @param excludeQueryString
     *            if <code>true</code> then only the target Faces viewId is returned regardless of whether the viewId
     *            contains a query string. I.e. unlike core Faces, the bridge allows the default viewIds and viewIds in
     *            the Faces navigation rules to contain query strings that augment the view. For example one can include
     *            a new mode in a Faces navigation rule. This boolean controls whether this extra query string portion
     *            of a viewid is returned or not.
     *
     * @return Returns the default Faces (viewid) target configured for the current request's <code>PortletMode</code>.
     *         The return value may contain contain (an optional) query string if not specifically excluded.
     */
    public abstract String getDefaultFacesViewIdForRequest(boolean excludeQueryString)
        throws BridgeDefaultViewNotSpecifiedException;

    // ---------------------------------------------------------- Static Methods

    /**
     * <p>
     * The <code>ThreadLocal</code> variable used to record the {@link FacesContext} instance for each processing
     * thread.
     * </p>
     */
    private static ThreadLocal<BridgeContext> sInstance = new ThreadLocal<BridgeContext>() {
        protected BridgeContext initialValue() {
            return (null);
        }
    };

    /**
     * Return the {@link BridgeContext} instance for the request that is being processed by the current thread.
     */
    public static BridgeContext getCurrentInstance() {

        return (sInstance.get());

    }

    /**
     * <p>
     * Set the {@link FacesContext} instance for the request that is being processed by the current thread.
     * </p>
     *
     * @param context
     *            The {@link FacesContext} instance for the current thread, or <code>null</code> if this thread no
     *            longer has a <code>FacesContext</code> instance.
     *
     */
    protected static void setCurrentInstance(BridgeContext context) {

        if (context == null) {
            sInstance.remove();
        } else {
            sInstance.set(context);
        }

    }

    /**
     * Convenience methods for classes that need to log without having or knowing whether they are currently executing
     * in a BridgeContext
     *
     * @param s
     * @param t
     */
    public static void log(String s, Throwable t) {
        BridgeContext bCtx = getCurrentInstance();
        if (bCtx != null) {
            bCtx.getBridgeConfig().getLogger().log(s, t);
        }
    }

    /**
     * Convenience methods for classes that need to log without having or knowing whether they are currently executing
     * in a BridgeContext
     *
     * @param s
     */
    public static void log(String s) {
        BridgeContext bCtx = getCurrentInstance();
        if (bCtx != null) {
            bCtx.getBridgeConfig().getLogger().log(s);
        }
    }

}