/**
 * 
 */
package org.jboss.portletbridge.context;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletRequest;

import org.jboss.portletbridge.BridgeConfig;
import org.jboss.portletbridge.BridgeRequestScope;
import org.jboss.portletbridge.StateId;

/**
 * This is a special request-scope bean to hold all Bridge-related information,
 * instead of using a lot of separate parameters.
 * @author asmirnov
 *
 */
public class PortletBridgeContext {
	
	public static final String REQUEST_PARAMETER_NAME = PortletBridgeContext.class.getName();
	
	
	/*============================================================================
	 * Context methods
	 */
	
	private Set<String> initialRequestAttributeNames;
	
	private String redirectViewId;
	
	private Map<String, String[]> redirectRequestParameters;
	
	private StateId stateId;
	
	private final BridgeConfig bridgeConfig;
	
	private BridgeRequestScope requestScope;
	
	private Map<String, String[]> viewIdParameters;
	
	/**
	 * @param bridgeConfig
	 */
	public PortletBridgeContext(BridgeConfig bridgeConfig) {
		this.bridgeConfig = bridgeConfig;
	}
	
	
	public static PortletBridgeContext getCurrentInstance(FacesContext context){
		return (PortletBridgeContext) context.getExternalContext().getRequestMap().get(REQUEST_PARAMETER_NAME);
	}


	public void reset(){
		this.requestScope = null;
		this.redirectViewId = null;
		this.stateId = null;
		this.redirectRequestParameters = null;
	}
	
	/**
	 * @return the initialRequestAttributeNames
	 */
	public Set<String> getInitialRequestAttributeNames() {
		return initialRequestAttributeNames;
	}

	/**
	 * @param names the initialRequestAttributeNames to set
	 */
	@SuppressWarnings("unchecked")
	public void setInitialRequestAttributeNames(
			Enumeration names) {		
		this.initialRequestAttributeNames = new HashSet<String>();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			this.initialRequestAttributeNames.add(name);
		}
	}
	
	/**
	 * Reset request attributes to its initial state stored as {@link #initialRequestAttributeNames}
	 * @param request
	 */
	public void resetRequestAttributes(PortletRequest request) {
		Set<String> initialAttributes = getInitialRequestAttributeNames();
		List<String> currentAttributes = Collections.list(request
		.getAttributeNames());
		currentAttributes.removeAll(initialAttributes);
		for (Object newAttribute : currentAttributes) {
			request.removeAttribute((String) newAttribute);
}

    }
	/**
	 * @return the redirectViewId
	 */
	public String getRedirectViewId() {
		return redirectViewId;
	}

	/**
	 * @param redirectViewId the redirectViewId to set
	 */
	public void setRedirectViewId(String redirectViewId) {
		this.redirectViewId = redirectViewId;
	}

	/**
	 * @return the redirectRequestParameters
	 */
	public Map<String, String[]> getRedirectRequestParameters() {
		return redirectRequestParameters;
	}

	/**
	 * @param redirectRequestParameters the redirectRequestParameters to set
	 */
	public void setRedirectRequestParameters(
			Map<String, String[]> redirectRequestParameters) {
		this.redirectRequestParameters = redirectRequestParameters;
	}

	/**
	 * @return the stateId
	 */
	public StateId getStateId() {
		return stateId;
	}

	/**
	 * @param stateId the stateId to set
	 */
	public void setStateId(StateId stateId) {
		this.stateId = stateId;
	}

	/**
	 * @return the bridgeConfig
	 */
	public BridgeConfig getBridgeConfig() {
		return bridgeConfig;
	}

	/**
	 * @return the windowState
	 */
	public BridgeRequestScope getRequestScope() {
		return requestScope;
	}

	/**
	 * @param windowState the windowState to set
	 */
	public void setRequestScope(BridgeRequestScope windowState) {
		this.requestScope = windowState;
	}


	/**
	 * @return the viewIdParameters
	 */
	public Map<String, String[]> getViewIdParameters() {
		return viewIdParameters;
	}


	/**
	 * @param viewIdParameters the viewIdParameters to set
	 */
	public void setViewIdParameters(Map<String, String[]> viewIdParameters) {
		this.viewIdParameters = viewIdParameters;
	}


	public void render(FacesContext context) {
		Lifecycle lifecycle = getBridgeConfig().getFacesLifecycle();
		lifecycle.execute(context);
		if(!context.getResponseComplete()){
			lifecycle.render(context);
		}
	}
}
