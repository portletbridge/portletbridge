package org.jboss.portletbridge.factory;

import javax.faces.FacesException;

import org.jboss.portletbridge.config.BridgeConfig;
import org.jboss.portletbridge.logger.BridgeLogger;


public abstract class BridgeLoggerFactory extends BridgeFactory<BridgeLoggerFactory>
{

	public abstract BridgeLogger getBridgeLogger(BridgeConfig config) throws FacesException;

	public BridgeLoggerFactory getWrapped()
  {
		return null;
	}
}
