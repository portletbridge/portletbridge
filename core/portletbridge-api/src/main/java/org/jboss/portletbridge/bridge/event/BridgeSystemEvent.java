package org.jboss.portletbridge.bridge.event;

import javax.faces.event.SystemEvent;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * Base bridge <code>SystemEvent</code> class the provides access to the <code>BridgeContext</code>.
 */
public class BridgeSystemEvent extends SystemEvent {
    private static final long serialVersionUID = -1014521626723222093L;

    public BridgeSystemEvent(BridgeContext ctx) {
        super(ctx);
    }

    public BridgeContext getContext() {
        return (BridgeContext) getSource();
    }

}