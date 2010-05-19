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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import org.jboss.portletbridge.util.BridgeLogger;

/**
 * @author asmirnov
 *
 */
public abstract class LifecyclePhase {


   private static final Logger _log = BridgeLogger.FACES.getLogger();


   private final Lifecycle _lifecycle;

   public LifecyclePhase(Lifecycle lifecycle) {
      this._lifecycle = lifecycle;
   }


   /**
    * Execute phase methods with
    * @param context
    */
   public void execute(FacesContext context){
      int listenerToCall = 0;
      PhaseEvent event = new PhaseEvent(context,getPhaseId(),getLifecycle());
      PhaseListener[] listeners = getLifecycle().getPhaseListeners();
      // Execute listeners before phase.
      try {
         for(;listenerToCall<listeners.length;listenerToCall++){
            PhaseListener listener = listeners[listenerToCall];
            if(isExecutableListener(listener)){
               listener.beforePhase(event);
            }
         }
      } catch (Exception e) {
         _log.log(Level.SEVERE,"Error to execute beforePhase "+getPhaseId().toString()+ " method for listener", e);
      }
      try {
         executePhase(context);
      } catch ( FacesException e){
    	  _log.log(Level.SEVERE,"Error executing " + getPhaseId().toString() + " phase.",e);
    	  throw e;
      } catch (Exception e) {
//         if(PhaseId.RENDER_RESPONSE.compareTo(getPhaseId())==0 || PhaseId.INVOKE_APPLICATION.compareTo(getPhaseId())==0
//                 || PhaseId.RESTORE_VIEW.compareTo(getPhaseId())==0){
            _log.log(Level.SEVERE,"Error executing " + getPhaseId().toString() + " phase.",e);
            throw new FacesException(e);
//         } else{
//            _log.error("Error execute phase "+getPhaseId().toString(),e);
//         }
      } finally {
         // Execute listeners after phase, in the reverse order of the before phase.
         try {
            for(listenerToCall--;listenerToCall>=0;listenerToCall--){
               PhaseListener listener = listeners[listenerToCall];
               if(isExecutableListener(listener)){
                  listener.afterPhase(event);
               }
            }
         } catch (Exception e) {
            _log.log(Level.SEVERE,"Error to execute afterPhase "+getPhaseId().toString()+ " method for listener", e);
         }

      }
      if(!context.getRenderResponse() && !context.getResponseComplete()){
         executeNextPhase(context);
      }
   }

   /**
    * @param context
    */
   protected abstract void executeNextPhase(FacesContext context);


   /**
    * @param context
    */
   public abstract void executePhase(FacesContext context);


   /**
    * @return
    */
   protected  abstract PhaseId getPhaseId();

   private boolean isExecutableListener(PhaseListener listener) {
      PhaseId phaseId = listener.getPhaseId();
      return 0 == getPhaseId().compareTo(phaseId) || 0 == PhaseId.ANY_PHASE.compareTo(phaseId);
   }


   /**
    * @return the lifecycle
    */
   public Lifecycle getLifecycle() {
      return _lifecycle;
   }

}
