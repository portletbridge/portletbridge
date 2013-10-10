/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.bridge.controller;

import org.jboss.portletbridge.bridge.config.BridgeConfig;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

/**
 * Controller to handle JSF 2.2 specific actions that would cause JSF 2.0 runtimes to break due to missing classes,
 * methods, etc.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class Jsf22ControllerImpl extends Jsf20ControllerImpl {
    public Jsf22ControllerImpl(BridgeConfig bridgeConfig) {
        super(bridgeConfig);
    }

    /**
     * Call <code>Lifecycle.attachWindow()</code> to retrieve/create a Client Window Id for this request.
     *
     * @param facesContext      Faces Context for the current portlet request.
     * @param facesLifecycle    Lifecycle for the current portlet request.
     */
    @Override
    protected void performPreExecuteTasks(FacesContext facesContext, Lifecycle facesLifecycle) {
        facesLifecycle.attachWindow(facesContext);
    }
}
