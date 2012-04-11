/**
 * 
 */
package javax.portlet.faces;

import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge.PortletPhase;

/**
 * Utility class designed to make it easy for Faces subsystems including the
 * bridge itself to determine whether this request is running in a portlet
 * container and/or which portlet request phase it is executing in.
 * @author asmirnov
 * 
 */
public class BridgeUtil {

	private BridgeUtil() {
		// There is only static methods in the class.
	}

	/**
	 * Indicates whether the current request is executing in the portlet
	 * container. If it returns <code>true</code> the request is a portlet
	 * request, otherwise it is not.
	 */
	public static boolean isPortletRequest() {
    	FacesContext ctx = FacesContext.getCurrentInstance();
    
    	// This method might be called during App startup (via a context listener) and hence no FacesContext
    	// For example a renderkit might createComponents during such time -- as the bridge overrides faces Application
    	// which implements createComponent and calls this method (to see if we need to wrap/replace with the NamingContainer
	    if (ctx == null)  return false;
    
		return null != getPortletRequestPhase();
	}

	/**
	 * Return describes the portlet request phase currently being executed. If
	 * <code>null</code> then this request is not being executed in a portlet
	 * container.
	 */
	public static Bridge.PortletPhase getPortletRequestPhase() {
		return (PortletPhase) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);
	}

}
