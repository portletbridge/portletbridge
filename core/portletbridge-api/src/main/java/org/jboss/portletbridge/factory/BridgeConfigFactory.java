package org.jboss.portletbridge.factory;

import javax.faces.FacesException;

import org.jboss.portletbridge.config.BridgeConfig;


public abstract class BridgeConfigFactory extends BridgeFactory<BridgeConfigFactory>
{

	public abstract BridgeConfig getBridgeConfig() throws FacesException;
  

}
