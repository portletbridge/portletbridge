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
package org.jboss.portletbridge.context;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;
import org.jboss.portletbridge.context.jsf2_0.Jsf20PortletExternalContextWrapper;

public class PortletExternalContextFactoryImpl extends ExternalContextFactory {

    private ExternalContextFactory mWrapped;
    private static final BridgeLogger logger = new JULLoggerImpl(PortletExternalContextImpl.class.getName());

    public PortletExternalContextFactoryImpl(ExternalContextFactory factory) {
        mWrapped = factory;
        logger.log(Level.DEBUG, "Portlet Bridge - specific PortletExternalContextFactory has been initialised");
    }

    public ExternalContext getExternalContext(Object context, Object request, Object response) throws FacesException {
        if ((null == context) || (null == request) || (null == response)) {
            throw new NullPointerException("One or more parameters for a external context instantiation is null");
        }

        ExternalContext externalContext;

        if (context instanceof PortletContext) {
            Object portletPhase = request instanceof PortletRequest ? ((PortletRequest) request)
                    .getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE) : null;

            if (Bridge.PortletPhase.ACTION_PHASE.equals(portletPhase) && (request instanceof ActionRequest)
                    && (response instanceof ActionResponse)) {
                externalContext = new ActionRequestExternalContextImpl((PortletContext) context, (ActionRequest) request,
                        (ActionResponse) response);
                logger.log(Level.DEBUG, "Portal request - create portal version of the ExternalContext for action request");
            } else if (Bridge.PortletPhase.RENDER_PHASE.equals(portletPhase) && (request instanceof RenderRequest)
                    && (response instanceof RenderResponse)) {
                externalContext = new RenderPortletExternalContextImpl((PortletContext) context, (RenderRequest) request,
                        (RenderResponse) response);
                logger.log(Level.DEBUG, "Portal request - create portal version of the ExternalContext for render response");
            } else if (Bridge.PortletPhase.RESOURCE_PHASE.equals(portletPhase) && (request instanceof ResourceRequest)
                    && (response instanceof ResourceResponse)) {
                externalContext = new ResourceRequestExternalContextImpl((PortletContext) context, (ResourceRequest) request,
                        (ResourceResponse) response);
                logger.log(Level.DEBUG, "Portal request - create portal version of the ExternalContext for resource response");
            } else if (Bridge.PortletPhase.EVENT_PHASE.equals(portletPhase) && (request instanceof EventRequest)
                    && (response instanceof EventResponse)) {
                externalContext = new EventRequestExternalContextImpl((PortletContext) context, (EventRequest) request,
                        (EventResponse) response);
                logger.log(Level.DEBUG, "Portal request - create portal version of the ExternalContext for event request");
            } else {
                throw new FacesException("Invalid objects passed to getExternalContext() for portlet phase "
                        + portletPhase.toString());
            }

            // Create ExternalContext based on JSF version being used, pass in phase specific one to be wrapped.
            boolean isJsf22 = BridgeContext.getCurrentInstance().getBridgeConfig().isJsf22Runtime();
            if (isJsf22) {
                externalContext = new Jsf22PortletExternalContextWrapper(externalContext);
            } else {
                externalContext = new Jsf20PortletExternalContextWrapper(externalContext);
            }
        } else {
            externalContext = getWrapped().getExternalContext(context, request, response);
        }
        return externalContext;
    }

    @Override
    public ExternalContextFactory getWrapped() {
        return mWrapped;
    }

}
