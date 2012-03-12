package org.jboss.portletbridge.event;

import org.jboss.portletbridge.context.BridgeContext;


/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after
 * finishing request processing and just before it returns. Gives listeners
 * the ability to undo any work done in their own request initialization.
 */
public class BridgeDestroyRequestSystemEvent extends BridgeSystemEvent
{
  
  public BridgeDestroyRequestSystemEvent(BridgeContext ctx)
  {
    super(ctx);
  }
}