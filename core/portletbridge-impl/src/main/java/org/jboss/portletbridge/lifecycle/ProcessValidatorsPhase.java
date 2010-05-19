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

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

/**
 * @author asmirnov
 *
 */
public class ProcessValidatorsPhase extends LifecyclePhase {

   private final LifecyclePhase nextPhase;

   /**
    * @param lifecycle
    */
   public ProcessValidatorsPhase(Lifecycle lifecycle) {
      super(lifecycle);
      nextPhase = new UpdateModelPhase(lifecycle);
   }

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executeNextPhase(javax.faces.context.FacesContext, javax.faces.event.PhaseListener[])
    */
   protected void executeNextPhase(FacesContext context) {
      nextPhase.execute(context);

   }

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executePhase(javax.faces.context.FacesContext)
    */
   public void executePhase(FacesContext context) {
		UIViewRoot viewRoot = context.getViewRoot();
		if (null != viewRoot) {
			viewRoot.processValidators(context);

		}
	}

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#getPhaseId()
    */
   protected PhaseId getPhaseId() {
      return PhaseId.PROCESS_VALIDATIONS;
   }

}
