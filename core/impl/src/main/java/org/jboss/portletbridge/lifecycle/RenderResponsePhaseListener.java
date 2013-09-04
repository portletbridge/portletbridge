package org.jboss.portletbridge.lifecycle;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

/**
 * Cause the Faces Lifecycle to end after RESTORE_VIEW. Used during RENDER_RESPONSE if we don't
 * want <code>f:viewParam</code> to work.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class RenderResponsePhaseListener implements PhaseListener {
    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        // Only process the Event if it is for the FacesContext that we're currently working with.
        // Needed as Lifecycle is not thread safe and there is only one for a single web app.
        if (context != FacesContext.getCurrentInstance() || !BridgeUtil.isPortletRequest()) {
            return;
        }

        Object portletPhase = context.getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);

        if (Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase)) {
            context.renderResponse();
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        // Do nothing.
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
