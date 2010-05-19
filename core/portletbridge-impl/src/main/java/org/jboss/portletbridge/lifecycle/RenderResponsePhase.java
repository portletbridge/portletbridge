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

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.view.ViewDeclarationLanguage;

/**
 * @author asmirnov
 * New changes include VDL
 *
 *
 * This lifecykle must by use only to fix error https://javaserverfaces.dev.java.net/issues/show_bug.cgi?id=1509
 * in version JSF 2.0.3 must be removed
*/
public class RenderResponsePhase extends LifecyclePhase {

   /**
    * @param lifecycle
    */
   public RenderResponsePhase(Lifecycle lifecycle) {
      super(lifecycle);
   }

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executeNextPhase(javax.faces.context.FacesContext, javax.faces.event.PhaseListener[])
    */
   protected void executeNextPhase(FacesContext context) {
      // do nothing.

   }

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executePhase(javax.faces.context.FacesContext)
    */
   public void executePhase(FacesContext context) {


        // For requests intended to produce a partial response, we need prohibit
        // writing any content outside of the view itself (f:view).
        PartialViewContext partialViewContext = context.getPartialViewContext();
//        if (partialViewContext.isPartialRequest()) {
//            OnOffResponseWrapper onOffResponse = new OnOffResponseWrapper(facesContext);
//            onOffResponse.setEnabled(false);
//        }

        try {

            ViewHandler vh = context.getApplication().getViewHandler();

            ViewDeclarationLanguage vdl =
                    vh.getViewDeclarationLanguage(context,
                    context.getViewRoot().getViewId());
            if (vdl != null) {
                vdl.buildView(context, context.getViewRoot());
            }

            boolean viewIdsUnchanged;
            do {
                String beforePublishViewId = context.getViewRoot().getViewId();
                // the before render event on the view root is a special case to keep door open for navigation
                // this must be called *after* PDL.buildView() and before VH.renderView()
                context.getApplication().publishEvent(context,
                        PreRenderViewEvent.class,
                        context.getViewRoot());
                String afterPublishViewId = context.getViewRoot().getViewId();
                viewIdsUnchanged = beforePublishViewId == null && afterPublishViewId == null
                        || (beforePublishViewId != null && afterPublishViewId != null)
                        && beforePublishViewId.equals(afterPublishViewId);
                if (context.getResponseComplete()) {
                    return;
                }
            } while (!viewIdsUnchanged);

            //render the view
            vh.renderView(context, context.getViewRoot());

        } catch (IOException e) {
            throw new FacesException(e.getMessage(), e);
        }

   }

   /* (non-Javadoc)
    * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#getPhaseId()
    */
   protected PhaseId getPhaseId() {
      // TODO Auto-generated method stub
      return PhaseId.RENDER_RESPONSE;
   }

}
