package org.jboss.portletbridge;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletConfig;

public interface BridgeConfig {

	/**
	 * @return the portletConfig
	 */
	public abstract PortletConfig getPortletConfig();

	public abstract String getInitParameter(String name);

	/**
	 * @return the facesServletMappings
	 */
	public abstract List<String> getFacesServletMappings();

	/**
	 * @return the excludedAttributes
	 */
	public abstract Set<ExcludedRequestAttribute> getExcludedAttributes();

	/**
	 * @return the portletName
	 */
	public abstract String getPortletName();

	/**
	 * @return the preserveActionParams
	 */
	public abstract boolean isPreserveActionParams();

	/**
	 * @return the defaultViewIdMap
	 */
	public abstract Map<String, String> getDefaultViewIdMap();

	public abstract Map<Class<? extends Throwable>, String> getErrorPages();

	public abstract Lifecycle getFacesLifecycle();

	public abstract int getNumberOfRequestScopes();

	public abstract FacesContext createFacesContext(Object request, Object response);
	
	public abstract BridgeStrategy getStrategy();

}