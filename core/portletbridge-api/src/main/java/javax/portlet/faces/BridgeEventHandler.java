/**
 * 
 */
package javax.portlet.faces;

import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.event.EventNavigationResult;

/**
 * @author asmirnov
 *
 */
public interface BridgeEventHandler {
	/**
	 * @param context
	 * @param event
	 * @return
	 */
	public EventNavigationResult handleEvent(FacesContext context, Event event);
}
