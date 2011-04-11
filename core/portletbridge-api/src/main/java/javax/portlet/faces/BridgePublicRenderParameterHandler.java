/**
 * 
 */
package javax.portlet.faces;

import javax.faces.context.FacesContext;

/**
 * @author asmirnov
 *
 */
public interface BridgePublicRenderParameterHandler {
	
	  /**
	 * @param context
	 */
	public void processUpdates(FacesContext context);


}
