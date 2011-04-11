/**
 * 
 */
package javax.portlet.faces;

import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge.PortletPhase;

/**
 * @author asmirnov
 * 
 */
public class BridgeUtil {

	/**
	 * 
	 */
	private BridgeUtil() {
		// There is only static methods in the class.
	}

	/**
	 * Indicates whether the current request is executing in the portlet
	 * container. If it returns <code>true</code> the request is a portlet
	 * request, otherwise it is not.
	 */
	public static boolean isPortletRequest() {
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
