package org.jboss.portletbridge.factory;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;

import org.jboss.portletbridge.config.BridgeConfig;


public abstract class BridgeFactory<T> implements FacesWrapper<BridgeFactory>
{
  public BridgeFactory()
  {

  }

	public BridgeFactory<T> getWrapped()
  {
		return null;
	}
  
}
