package org.jboss.portletbridge.event;

import org.jboss.portletbridge.context.BridgeContext;


/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after
 * executing the request and before releaing the <code>FacesContext</code>.
 */
public class BridgePreReleaseFacesContextSystemEvent extends BridgeSystemEvent
{
  public BridgePreReleaseFacesContextSystemEvent(BridgeContext ctx)
  {
    super(ctx);
  }
}