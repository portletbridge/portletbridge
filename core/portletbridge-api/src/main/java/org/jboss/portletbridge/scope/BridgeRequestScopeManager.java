package org.jboss.portletbridge.scope;

import java.util.List;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.context.BridgeContext;

/**
 * The RequestScopeManager manages a set of RequestScopes based on its desired policies.
 * Clients interact with this manager to create and remove request scopes and to lookup
 * (access) a request scope by its key.
 */

public interface BridgeRequestScopeManager
{
  /**
   * Create a new empty RequestScope.  
   * @param ctx
   * @param portletName name of the portlet to which this scope pertains
   * @param sessionId a unique identifier for the user session in which this scope pertains
   * @param viewId the faces viewId of this scope
   * @param portletMode the mode this scope pertains to
   * @return the new BridgeRequestScope
   */
  public BridgeRequestScope createRequestScope(BridgeContext ctx, String portletName, String sessionId,  String viewId, String portletMode);
  
  /**
   * Create a new empty RequestScope.  Scope qualifiers portletId, sessionId, and portletMode are taken from the current request object
   * in the <code>BridgeContext</code>.
   * @param ctx
   * @param viewId the faces viewId of this scope
   * @return the new BridgeRequestScope
   */
  public BridgeRequestScope createRequestScope(BridgeContext ctx, String viewId);
  
  /**
   * Returns the BridgeRequestScope corresponding to the supplied key (id)
   * @param ctx
   * @param id BridgeRequestScope's id
   * @return the BridgeRequestScope that corresponds to the supplied key or null if no match
   */
  public BridgeRequestScope getRequestScopeById(BridgeContext ctx, String id);
  
  /**
   * Returns the BridgeRequestScope corresponding to the portletId, sessionId, viewId and portletMode.
   * 
   * @param ctx
   * @param portletName uniquely identifies this portlet.
   * @param sessionId uniquely identifies a user context (session)
   * @param viewId Faces view to which this scope pertains
   * @param portletMode portlet mode that this scope represents
   * @return the BridgeRequestScope that corresponds to the supplied key or null if no match
   */
  public BridgeRequestScope getRequestScope(BridgeContext ctx, String portletName, String sessionId,  String viewId, String portletMode);
  
  
  /**
   * Returns the BridgeRequestScope corresponding to the portletId, sessionId, viewId and portletMode.
   * PortletName and sessionId are taken from the current request/portlet config
   * 
   * @param ctx
   * @param viewId Faces view to which this scope pertains
   * @param portletMode portlet mode that this scope represents
   * @return the BridgeRequestScope that corresponds to the supplied key or null if no match
   */
  public BridgeRequestScope getRequestScope(BridgeContext ctx, String viewId, String portletMode);
  
  /**
   * Returns the BridgeRequestScope corresponding to the portletId, sessionId, viewId and portletMode.
   * PortletName, sessionId, and portletMode are taken from the current request/portlet config
   * 
   * @param ctx
   * @param viewId Faces view to which this scope pertains
   * @return the BridgeRequestScope that corresponds to the supplied key or null if no match
   */
  public BridgeRequestScope getRequestScope(BridgeContext ctx, String viewId);
  
  /**
   * Returns the BridgeRequestScope corresponding to the portletId, sessionId, viewId and portletMode.
   * PortletName, sessionId, and portletMode and viewId are taken from the current bridge context
   * 
   * @param ctx
   * @return the BridgeRequestScope that corresponds to the supplied key or null if no match
   */
  public BridgeRequestScope getRequestScope(BridgeContext ctx);
  
  /**
   * Removes the specific BridgeRequestScope (from management) that
   * corresponds to the supplied key.
   * @param ctx
   * @param id
   * @return the BridgeRequestScope that corresponds to the supplied key.
   */
  public BridgeRequestScope removeRequestScopeById(BridgeContext ctx, String id);
  
  
  /**
   * Removes the supplied BridgeRequestScope from management.
   * @param ctx
   * @param scope
   * @return scope that was removed or null -- if scope not in the repository. (Note: scope
   * is cleared before being returned)
   */
  public BridgeRequestScope removeRequestScope(BridgeContext ctx, BridgeRequestScope scope);
  
  /**
   * Removes the request scope pertaining to the portletId, sessionId, viewId and portletMode.
   * This operation is a noop if a match is not found.
   * 
   * @param ctx
   * @param portletName uniquely identifies this portlet.
   * @param sessionId uniquely identifies a user context (session)
   * @param viewId Faces view to which this scope pertains
   * @param portletMode portlet mode that this scope represents
   * @return scope that was removed or null. (Note: scope
   * is cleared before being returned)
   */
  public BridgeRequestScope removeRequestScope(BridgeContext ctx, String portletName, String sessionId,  String viewId, String portletMode);
  
  
  /**
   * Removes the request scope pertaining to the portletId, sessionId, viewId and portletMode.
   * Portletname and sessionId are taken from the current request/portlet config
   * This operation is a noop if a match is not found.
   * 
   * @param ctx
   * @param viewId Faces view to which this scope pertains
   * @param portletMode portlet mode that this scope represents
   * @return scope that was removed or null. (Note: scope
   * is cleared before being returned)
   */
  public BridgeRequestScope removeRequestScope(BridgeContext ctx, String viewId, String portletMode);
  
  /**
   * Removes the request scope pertaining to the portletId, sessionId, viewId and portletMode.
   * PortletName, sessionId, and portletMode are taken from the current request/portlet config
   * This operation is a noop if a match is not found.
   * 
   * @param ctx
   * @param viewId Faces view to which this scope pertains
   * @return scope that was removed or null. (Note: scope
   * is cleared before being returned)
   */
  public BridgeRequestScope removeRequestScope(BridgeContext ctx, String viewId);
  
  
  /**
   * Removes all the BridgeRequestScope's currently by managed by this manager
   * for the specific portlet that is identified <code>portletName</code>.
   * @param ctx
   */
  public void removeRequestScopesByPortlet(BridgeContext ctx, String portletId);
  
  /**
   * Removes all the BridgeRequestScope's currently by managed by this manager
   * for the specific portlet that is identified in the PortletConfig object
   * obtainable from the BridgeContext.
   * @param ctx
   */
  public void removeRequestScopesByPortlet(BridgeContext ctx);
  
  /**
   * Removes all the  BridgeRequestScope's currently by managed by this manager
   * for the session identified by <code>sessionId</code>.
   * 
   * @param ctx
   */
  public void removeRequestScopesBySession(BridgeContext ctx, String sessionId);
  
  
  /**
   * Removes all the  BridgeRequestScope's currently by managed by this manager
   * for the current session.
   * 
   * @param ctx
   */
  public void removeRequestScopesBySession(BridgeContext ctx);
  

  
}