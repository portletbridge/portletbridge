package org.jboss.portletbridge.factory;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;

import org.jboss.portletbridge.context.BridgeContext;


public abstract class BridgeContextFactory extends BridgeFactory<BridgeContextFactory>
{

	public abstract BridgeContext getBridgeContext() throws FacesException;

}
