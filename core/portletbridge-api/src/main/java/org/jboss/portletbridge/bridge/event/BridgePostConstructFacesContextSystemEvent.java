package org.jboss.portletbridge.bridge.event;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>doFacesRequest()</code> method after the bridge has acquired
 * the <code>FacesContext</code> but before it runs the <code>Lifecyle</code>. Gives listeners the ability to do work at a point
 * in which the entire request processing environment has been established but before actually executing the request. One use
 * allows <code>ExternalContext</code> implementations to be notified prior to use to do any initializations that couldn't be
 * done in its constructor because it lacked access to the <code>BridgeContext</code>.
 */
public class BridgePostConstructFacesContextSystemEvent extends BridgeSystemEvent {
    private static final long serialVersionUID = -4648253075591280844L;

    public BridgePostConstructFacesContextSystemEvent(BridgeContext ctx) {
        super(ctx);
    }
}