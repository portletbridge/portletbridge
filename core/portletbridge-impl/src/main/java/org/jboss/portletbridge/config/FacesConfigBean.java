package org.jboss.portletbridge.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This class contains parsed information about portlet-related faces-config extensions
 * @author asmirnov
 *
 */
public class FacesConfigBean {

	private Collection<String> excludedAttributes;
	
	private Map<String, String> publicParameters;
	
	private Iterator<String> bridgeFactory;
	
	private Iterator<String> bridgeContextFactory;
	
	private Iterator<String> bridgeRequestScopeFactory;

	private Iterator<String> bridgeRequestScopeManagerFactory;

	public Collection<String> getExcludedAttributes() {
		return excludedAttributes;
	}

	public Map<String, String> getPublicParameters() {
		return publicParameters;
	}

	public Iterator<String> getBridgeFactory() {
		return bridgeFactory;
	}

	public Iterator<String> getBridgeContextFactory() {
		return bridgeContextFactory;
	}

	public Iterator<String> getBridgeRequestScopeFactory() {
		return bridgeRequestScopeFactory;
	}

	public Iterator<String> getBridgeRequestScopeManagerFactory() {
		return bridgeRequestScopeManagerFactory;
	}
	
}
