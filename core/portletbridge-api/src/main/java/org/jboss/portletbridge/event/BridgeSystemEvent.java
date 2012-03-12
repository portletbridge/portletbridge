package org.jboss.portletbridge.event;

import javax.faces.event.SystemEvent;

import org.jboss.portletbridge.context.BridgeContext;

/**
 * Base bridge <code>SystemEvent</code> class the provides access to the <code>BridgeContext</code>.
 */
public class BridgeSystemEvent extends SystemEvent
{
    
  public BridgeSystemEvent(BridgeContext ctx)
  {
    super(ctx);
  }
  
  public BridgeContext getContext()
  {
    return (BridgeContext) getSource();
  }
  
}