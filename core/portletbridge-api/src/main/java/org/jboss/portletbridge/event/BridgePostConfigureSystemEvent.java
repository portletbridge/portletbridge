package org.jboss.portletbridge.event;

import javax.faces.event.SystemEvent;

import org.jboss.portletbridge.config.BridgeConfig;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>init()</code> method after
 * initializing itself. Allows other extensions to also do any necessary
 * initializations.
 */
public class BridgePostConfigureSystemEvent extends SystemEvent
{  

  public BridgePostConfigureSystemEvent(BridgeConfig config)
  {
    super(config);
  }
  
  public BridgeConfig getBridgeConfig()
  {
    return (BridgeConfig) getSource();
  }
}