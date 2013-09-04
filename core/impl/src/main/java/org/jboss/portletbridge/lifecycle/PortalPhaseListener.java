/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.lifecycle;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

/**
 * @author asmirnov
 */
public class PortalPhaseListener implements PhaseListener {

    private static final long serialVersionUID = -4023885603543145666L;

    /**
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        // Only process the Event if it is for the FacesContext that we're currently working with.
        // Needed as Lifecycle is not thread safe and there is only one for a single web app.
        if (context != FacesContext.getCurrentInstance() || !BridgeUtil.isPortletRequest()) {
            return;
        }

        Object portletPhase = context.getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);

        if (Bridge.PortletPhase.EVENT_PHASE.equals(portletPhase)) {
            context.responseComplete();
        }

    }

    /**
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    public void beforePhase(PhaseEvent event) {
        // Do nothing.
    }

    /**
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    public PhaseId getPhaseId() {
        return PhaseId.PROCESS_VALIDATIONS;
    }

}
