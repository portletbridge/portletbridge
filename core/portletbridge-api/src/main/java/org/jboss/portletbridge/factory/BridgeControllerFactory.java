package org.jboss.portletbridge.factory;

import javax.faces.FacesException;

import org.jboss.portletbridge.controller.BridgeController;


public abstract class BridgeControllerFactory extends BridgeFactory<BridgeControllerFactory>
{

	public abstract BridgeController getBridgeController() throws FacesException;

	public BridgeControllerFactory getWrapped()
  {
		return null;
	}
}
