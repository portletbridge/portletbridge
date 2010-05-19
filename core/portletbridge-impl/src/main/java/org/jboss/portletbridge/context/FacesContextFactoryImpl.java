/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
/*

 * Created on 25.09.2004

 *

 * Copyright 1999-2004 The Apache Software Foundation.

 * 

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 * 

 *      http://www.apache.org/licenses/LICENSE-2.0

 * 

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */
package org.jboss.portletbridge.context;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
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

import org.jboss.portletbridge.util.BridgeLogger;

/**
 * 
 * @author shura Implementation of <code>FacesContextFactory</code> for Portlet
 * 
 *         Environment Use default factory for create default Faces
 * 
 *         implementation context and setup wrappers for it.
 * 
 * 
 * 
 * 
 * 
 */
public class FacesContextFactoryImpl extends FacesContextFactory {
	/**
	 * 
	 * Hold <code>FacesContextFactory</code> from default implementation.
	 * 
	 */
    private ExceptionHandlerFactory exceptionHandlerFactory;
	private FacesContextFactory defaultFacesContextFactory;
	private static final Logger _log = BridgeLogger.FACES.getLogger();
	/**
	 * 
	 * Create instance of Faces context factory, based on implementation.
	 * 
	 * @param defaultFactory
	 *            -
	 * 
	 *            Factory from JSF implementation.
	 * 
	 */
	public FacesContextFactoryImpl(FacesContextFactory defaultFactory) {
		super();
		this.defaultFacesContextFactory = defaultFactory;
        exceptionHandlerFactory = (ExceptionHandlerFactory) FactoryFinder.getFactory(FactoryFinder.EXCEPTION_HANDLER_FACTORY);
		if (_log.isLoggable(Level.FINE)) {
			_log.fine("Portal - specific FacesContextFactory has initialised");
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see
	 * javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object,
	 * 
	 * java.lang.Object, java.lang.Object, javax.faces.lifecycle.Lifecycle)
	 */
	public FacesContext getFacesContext(Object context, Object request,
			Object response, Lifecycle lifecycle) throws FacesException {
		if ((null == context) || (null == request) || (null == response)
				|| (null == lifecycle)) {
			throw new NullPointerException(
					"One or more parameters for a faces context instantiation is null");
		}
		Object portletPhase = request instanceof PortletRequest?((PortletRequest)request).getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE):null;
		FacesContext facesContext = defaultFacesContextFactory.getFacesContext(context,
				request, response, lifecycle);
		if (null != portletPhase) {
			facesContext = new FacesContextImpl(facesContext);
		}
		return facesContext;
	}
}