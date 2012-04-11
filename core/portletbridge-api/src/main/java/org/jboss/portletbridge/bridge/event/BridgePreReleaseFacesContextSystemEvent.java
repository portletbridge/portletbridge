package org.jboss.portletbridge.bridge.event;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after executing the request and
 * before releasing the <code>FacesContext</code>.
 */
public class BridgePreReleaseFacesContextSystemEvent extends BridgeSystemEvent {
    private static final long serialVersionUID = 2971177710492374244L;

    public BridgePreReleaseFacesContextSystemEvent(BridgeContext ctx) {
        super(ctx);
    }
}