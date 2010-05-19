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

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import com.sun.faces.util.Util;

/**
 * @author asmirnov
 *
 *
 * This lifecykle phases must by use only to fix error https://javaserverfaces.dev.java.net/issues/show_bug.cgi?id=1509
 * in version JSF 2.0.3 must be removed
*/
public class PortletLifecycle extends Lifecycle {

    public static final String FIX_PORTLET_LIFECYCLE = "FIX_PORTLET_LIFECYCLE";

   //List<PhaseListener>
   private final List<PhaseListener> _phaseListeners = new ArrayList<PhaseListener>();

   private final LifecyclePhase restorePhase = new RestoreViewPhase(this);

   private final LifecyclePhase renderPhase = new RenderResponsePhase(this);

   private LifecyclePhase[] phases = {
        null, // ANY_PHASE placeholder, not a real LifecyclePhase
        restorePhase,
        new ApplyValuesPhase(this),
        new ProcessValidatorsPhase(this),
        new UpdateModelPhase(this),
        new InvokeApplicationPhase(this),
        renderPhase
    };

   private PhaseListener[] _phaseListenersArray = null;
    
    public PortletLifecycle() {
    }

    /**
     * @param listener
     * @see javax.faces.lifecycle.Lifecycle#addPhaseListener(javax.faces.event.PhaseListener)
     */
    public void addPhaseListener(PhaseListener listener) {
      if(null == listener){
         throw new NullPointerException("PhaseListener for add to Lifecycle can't be null");
      }
      synchronized (_phaseListeners) {
         _phaseListeners.add(listener);
         _phaseListenersArray = null;
      }
    }

    /**
     * @param listener
     * @see javax.faces.lifecycle.Lifecycle#addPhaseListener(javax.faces.event.PhaseListener)
     */
    protected void addPhaseListener(int index, PhaseListener listener) {
      if(null == listener){
         throw new NullPointerException("PhaseListener for add to Lifecycle can't be null");
      }
      synchronized (_phaseListeners) {
         _phaseListeners.add(index, listener);
         _phaseListenersArray = null;
      }
    }
 
    /**
    * @param listener
    * @see javax.faces.lifecycle.Lifecycle#removePhaseListener(javax.faces.event.PhaseListener)
    */
   public void removePhaseListener(PhaseListener listener) {
      if(null == listener){
         throw new NullPointerException("PhaseListener for remove from Lifecycle can't be null");
      }
      synchronized (_phaseListeners) {
         _phaseListeners.remove(listener);
         _phaseListenersArray = null;
      }
   }

   /**
    * @return
    * @see javax.faces.lifecycle.Lifecycle#getPhaseListeners()
    */
   public PhaseListener[] getPhaseListeners() {
      if (_phaseListenersArray == null) {
         // Lazy creation of a working copy listeners Array.
         synchronized (_phaseListeners) {
            _phaseListenersArray = (PhaseListener[]) _phaseListeners.toArray(new PhaseListener[_phaseListeners.size()]);
         }
      }

      return _phaseListenersArray;
   }

   /**
     * @param context
     * @throws FacesException
     * @see javax.faces.lifecycle.Lifecycle#execute(javax.faces.context.FacesContext)
     */
    public void execute(FacesContext context) throws FacesException {

        Util.getViewHandler(context).initView(context);

        if(isRestoreOnly()){
            restorePhase.execute(context);
        }else{
            for (int i = 1, len = phases.length - 1; i < len; i++) { // Skip ANY_PHASE placeholder

                if (context.getRenderResponse() || context.getResponseComplete()) {
                    break;
                }

                phases[i].execute(context);

            }
        }

       // TODO - in the portletbridge mode, save state
//       Object request = context.getExternalContext().getRequest();
//       if(request instanceof PortletRequest){
//          context.getApplication().getStateManager().saveSerializedView(context);
//       }
//       // TODO - save request scope variables and Faces Messages.
//       PortletViewState windowState = PortletStateHolder.getInstance(context).getWindowState(context);
//       windowState.saveMessages(context);
//       HashMap requestScopeBeans = new HashMap();
//       for (Iterator iterator = context.getExternalContext().getRequestMap().entrySet().iterator(); iterator.hasNext();) {
//         Entry entry = (Entry) iterator.next();
//         String paramName = entry.getKey().toString();
//         Object bean = entry.getValue();
//         if(!paramName.contains(".") && bean instanceof Serializable){
//            requestScopeBeans.put(paramName, bean);
//         }
//      }
//       if(requestScopeBeans.size()>0){
//          windowState.setRequestScopeBeans(requestScopeBeans);
//       }
    }

    /**
     * @param context
     * @throws FacesException
     * @see javax.faces.lifecycle.Lifecycle#render(javax.faces.context.FacesContext)
     */
    public void render(FacesContext context) throws FacesException {
       // TODO - in the portletbridge mode, restore state, request scope variables and Faces Messages.
//       Object request = context.getExternalContext().getRequest();
//       if(request instanceof PortletRequest){
//          executePhase.executePhase(context);
//          PortletViewState windowState = PortletStateHolder.getInstance(context).getWindowState(context);
//          windowState.restoreMessages(context);
//          Map requestScopeBeans = windowState.getRequestScopeBeans();
//          if(null != requestScopeBeans){
//             context.getExternalContext().getRequestMap().putAll(requestScopeBeans);
//          }
//       }
      if (!context.getResponseComplete()) {
         renderPhase.execute(context);
      }
    }

    protected boolean isRestoreOnly() {
        return false;
    }
    
}
