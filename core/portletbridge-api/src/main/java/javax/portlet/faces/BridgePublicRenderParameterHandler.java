/**
 * 
 */
package javax.portlet.faces;

import javax.faces.context.FacesContext;
import javax.portlet.Event;

 /**
  * The <code>BridgePublicRenderParameterHandler</code> interface defines the class the bridge relies
  * on to post process portlet public render parameters.  The handler provides the
  * portlet a means for resynching application state following any model updates
  * that resulted from the bridge pushing changed public render parameter values
  * based on declarative mappings.  After the bridge pushes such values the bridge
  * calls the handler if it has been configured during bridge <code>init()</code>.
  * Though the FacesContext has been acquired before the portlet is called to 
  * process these updates, the Lifecycle has not been acquired or run.  Because
  * of this is is no current active view.  Unlike events, one can't navigate 
  * based on a public render parameter change.
  */

public interface BridgePublicRenderParameterHandler
{
  /**
   * Called by the bridge after pushing incoming public render parameter
   * values into mapped managed beans.  Only called if there is at least
   * one public render parameter in the incoming request whose value is 
   * different (updates) the underlying bean.  This give the portlet an
   * opportunity to perform further computations based on these changes to 
   * resynchronize its application state.  
   * 
   * @param context
   *          current FacesContext. A Lifecycle has been acquired and the current view restored.
   */
  public void processUpdates(FacesContext context);
}
