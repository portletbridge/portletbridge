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
	PortletConfig getPortletConfig();

	String getInitParameter(String name);

	/**
	 * @return the facesServletMappings
	 */
	List<String> getFacesServletMappings();

	Map<String, String> getFilterInitParams(String className);
	/**
	 * @return the excludedAttributes
	 */
	Set<ExcludedRequestAttribute> getExcludedAttributes();

	/**
	 * @return the portletName
	 */
	String getPortletName();

	/**
	 * @return the preserveActionParams
	 */
	boolean isPreserveActionParams();

	/**
	 * @return the defaultViewIdMap
	 */
	Map<String, String> getDefaultViewIdMap();

	Map<Class<? extends Throwable>, String> getErrorPages();

	Lifecycle getFacesLifecycle();

	int getNumberOfRequestScopes();

	FacesContext createFacesContext(Object request, Object response);
	
	BridgeStrategy getStrategy();

}