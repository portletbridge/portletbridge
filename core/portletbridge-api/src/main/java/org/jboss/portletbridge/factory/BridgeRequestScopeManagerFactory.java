package org.jboss.portletbridge.factory;

import javax.faces.FacesException;

import org.jboss.portletbridge.config.BridgeConfig;
import org.jboss.portletbridge.scope.BridgeRequestScopeManager;


public abstract class BridgeRequestScopeManagerFactory extends BridgeFactory<BridgeRequestScopeManagerFactory>
{

	public abstract BridgeRequestScopeManager getBridgeRequestScopeManager(BridgeConfig bConfig) throws FacesException;

	public BridgeRequestScopeManagerFactory getWrapped()
  {
		return null;
	}
}
