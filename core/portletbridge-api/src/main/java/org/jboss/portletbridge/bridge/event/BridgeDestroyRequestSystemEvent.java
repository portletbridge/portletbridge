package org.jboss.portletbridge.bridge.event;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after finishing request
 * processing and just before it returns. Gives listeners the ability to undo any work done in their own request initialization.
 */
public class BridgeDestroyRequestSystemEvent extends BridgeSystemEvent {

    private static final long serialVersionUID = 1481200076962439477L;

    public BridgeDestroyRequestSystemEvent(BridgeContext ctx) {
        super(ctx);
    }
}