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

import java.util.Enumeration;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.util.ParameterFunction;
import org.jboss.portletbridge.util.PublicParameterUtil;

/**
 * 
 * 
 * @author kenfinnigan
 */
public class PublicParameterPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 6815081977853452874L;

    private BridgeConfig bridgeConfig;
    private PortletRequest portletRequest;
    private boolean mappingsProcessed;

    public PublicParameterPhaseListener(BridgeConfig bridgeConfig, PortletRequest portletRequest) {
        this.bridgeConfig = bridgeConfig;
        this.portletRequest = portletRequest;
        this.mappingsProcessed = false;
    }

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

        if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {
            // Process Incoming Public Parameter Mappings for all Portlet Phases, as per Spec 5.2.4, 5.2.5, 5.2.6, and 5.2.7.
            processIncomingParameters(context, portletRequest);

            if (BridgeUtil.getPortletRequestPhase().equals(Bridge.PortletPhase.EVENT_PHASE)) {
                if (!mappingsProcessed) {
                    // No PRPs mapped so skip action processing
                    context.renderResponse();
                }
            }
        }
    }

    /**
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    public void beforePhase(PhaseEvent event) {
        // Do nothing
    }

    /**
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    protected void processIncomingParameters(FacesContext facesContext, PortletRequest portletRequest) {
        Map<String, String> publicParameterMapping = bridgeConfig.getPublicRenderParameterMappings();
        Enumeration<String> parameterNames = bridgeConfig.getPortletConfig().getPublicRenderParameterNames();

        if (null != publicParameterMapping && publicParameterMapping.size() > 0 && parameterNames.hasMoreElements()) {

            ParameterFunction incomingFunction = new ParameterFunction() {
                public boolean processParameter(ELContext elContext, Map<String, String[]> publicParameters, String name,
                        ValueExpression valueExpression) {
                    boolean valueChanged = false;
                    Object oldValue = valueExpression.getValue(elContext);
                    if (publicParameters.containsKey(name)) {
                        String[] values = publicParameters.get(name);
                        String newValue = (null != values && values.length > 0) ? values[0] : null;
                        if (null == oldValue || !oldValue.equals(newValue)) {
                            valueExpression.setValue(elContext, newValue);
                            valueChanged = true;
                        }
                    } else if (null != oldValue) {
                        valueExpression.setValue(elContext, null);
                        valueChanged = true;
                    }
                    return valueChanged;
                }

            };

            boolean valueChanged = PublicParameterUtil.processPublicParameters(facesContext, portletRequest,
                    publicParameterMapping, parameterNames, incomingFunction, bridgeConfig.getPortletConfig().getPortletName());

            if (valueChanged && null != bridgeConfig.getPublicRenderParameterHandler()) {
                bridgeConfig.getPublicRenderParameterHandler().processUpdates(facesContext);
                mappingsProcessed = true;
            }
        }
    }

}
