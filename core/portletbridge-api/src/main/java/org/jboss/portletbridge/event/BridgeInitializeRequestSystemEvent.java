package org.jboss.portletbridge.event;

import org.jboss.portletbridge.context.BridgeContext;


/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after
 * acquiring the <code>BridgeContext</code> (and before doing anything else). Gives listeners
 * the ability to do their own request initialization and/or to augment the <code>BridgeContext</code>.
 * For example a listener could get the request or response from the <code>BridgeContext</code>,
 * wrap it to add their own overrides, and then put back into the <code>BridgeContext</code>.
 * Such use would ensure the wrapped request or response is what is used throughout the bridge's request processing.
 */
public class BridgeInitializeRequestSystemEvent extends BridgeSystemEvent
{
  public BridgeInitializeRequestSystemEvent(BridgeContext ctx)
  {
    super(ctx);
  }
}