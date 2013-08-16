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

import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import org.jboss.portletbridge.renderkit.portlet.HeadResources;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class HeadResourcesPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 5336202107411409441L;

    private static final String HEAD_RESOURCE_IDS = "headResourceIds";

    /**
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void afterPhase(PhaseEvent event) {
        // Do Nothing
    }

    /**
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        // Only process the Event if it is for the FacesContext that we're currently working with.
        // Needed as Lifecycle is not thread safe and there is only one for a single web app.
        if (context != FacesContext.getCurrentInstance() || !BridgeUtil.isPortletRequest()) {
            return;
        }

        Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase();
        if (Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase) || Bridge.PortletPhase.RESOURCE_PHASE.equals(portletPhase)) {
            if (PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
                beforeInvoke(context);
            } else if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
                beforeRender(context);
            }
        }
    }

    protected void beforeInvoke(FacesContext context) {
        Flash flash = context.getExternalContext().getFlash();

        @SuppressWarnings("unchecked")
        Set<String> resourceIds = (Set<String>) flash.get(HEAD_RESOURCE_IDS);

        if (null != resourceIds) {
            HeadResources bean = HeadResources.instance();
            if (null != bean) {
                flash.put(HEAD_RESOURCE_IDS, bean.getIds());
            }
        }
    }

    protected void beforeRender(FacesContext context) {
        Flash flash = context.getExternalContext().getFlash();

        @SuppressWarnings("unchecked")
        Set<String> resourceIds = (Set<String>) flash.get(HEAD_RESOURCE_IDS);

        if (null != resourceIds) {
            HeadResources bean = HeadResources.instance();
            if (null != bean) {
                Set<String> beanIds = bean.getIds();

                for (String id : resourceIds) {
                    if (!beanIds.contains(id)) {
                        beanIds.add(id);
                    }
                }
            }
        }
    }

    /**
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

}
