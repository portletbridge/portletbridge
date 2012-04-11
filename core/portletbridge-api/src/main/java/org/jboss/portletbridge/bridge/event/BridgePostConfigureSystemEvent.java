package org.jboss.portletbridge.bridge.event;

import javax.faces.event.SystemEvent;

import org.jboss.portletbridge.bridge.config.BridgeConfig;

/**
 * Faces <code>SystemEvent</code> sent by the Bridge in its <code>init()</code> method after initializing itself. Allows other
 * extensions to also do any necessary initializations.
 */
public class BridgePostConfigureSystemEvent extends SystemEvent {
    private static final long serialVersionUID = -1959650450402228370L;

    public BridgePostConfigureSystemEvent(BridgeConfig config) {
        super(config);
    }

    public BridgeConfig getBridgeConfig() {
        return (BridgeConfig) getSource();
    }
}