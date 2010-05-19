/**
 * 
 */
package org.jboss.portletbridge.util;

import java.util.logging.Logger;

/**
 * @author asmirnov
 *
 */
public enum BridgeLogger {
	
	BRIDGE("bridge"),
	FACES("faces"),
	CONTEXT("context"),
	AJAX("ajax"),
	RICHFACES("richfaces"),
	SEAM("seam");	
	
	private static final String PREFIX="org.jboss.portletbridge.";
	
	private static final String LOGGING_BUNDLE="org.jboss.portletbridge.LogMessages";
	private final String name;

	/**
	 * @param name
	 */
	private BridgeLogger(String name) {
		this.name = PREFIX+name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public Logger getLogger(){
		return Logger.getLogger(name, LOGGING_BUNDLE);
	}

}
