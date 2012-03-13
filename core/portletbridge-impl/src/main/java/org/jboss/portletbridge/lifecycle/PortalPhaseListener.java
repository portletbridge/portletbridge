/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge.lifecycle;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.faces.Bridge;

/**
 * @author asmirnov
 */
public class PortalPhaseListener implements PhaseListener {

   
   /**
    *
    */
   private static final long serialVersionUID = -4023885603543145666L;


   /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
   public void afterPhase(PhaseEvent event) {
      PhaseId phaseId = event.getPhaseId();
      FacesContext context = event.getFacesContext();
      Object portletPhase = context.getExternalContext().getRequestMap().get(
              Bridge.PORTLET_LIFECYCLE_PHASE);

      if (phaseId.equals(PhaseId.RESTORE_VIEW)) {
         if (Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase)) {
            context.renderResponse();
         }

      } else if (phaseId.equals(PhaseId.PROCESS_VALIDATIONS)) {
          if (Bridge.PortletPhase.EVENT_PHASE.equals(portletPhase)) {
              context.responseComplete();
           }
      }

   }

   /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
   public void beforePhase(PhaseEvent event) {
	   // DO nothing.
   }

   /*
     * (non-Javadoc)
     *
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
   public PhaseId getPhaseId() {
      // This listener process all phases.
      return PhaseId.ANY_PHASE;
   }

}
