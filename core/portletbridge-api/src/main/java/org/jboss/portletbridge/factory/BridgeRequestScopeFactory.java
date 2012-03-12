package org.jboss.portletbridge.factory;

import java.util.Map;

import javax.faces.FacesException;

import org.jboss.portletbridge.scope.BridgeRequestScope;


public abstract class BridgeRequestScopeFactory extends BridgeFactory<BridgeRequestScopeFactory>
{

	public abstract BridgeRequestScope getBridgeRequestScope(String portletId, String sessionId, String viewId, String portletMode) throws FacesException;
  
  public abstract BridgeRequestScope getBridgeRequestScope(String portletId, String sessionId, String viewId, String portletMode, int initialCapacity) throws FacesException;
  
  public abstract BridgeRequestScope getBridgeRequestScope(String portletId, String sessionId, String viewId, String portletMode, int initialCapacity, float loadFactor, int concurrencyLevel) throws FacesException;
  
  public abstract BridgeRequestScope getBridgeRequestScope(String portletId, String sessionId, String viewId, String portletMode, Map<String,Object> t) throws FacesException;

	public BridgeRequestScopeFactory getWrapped()
  {
		return null;
	}
}
