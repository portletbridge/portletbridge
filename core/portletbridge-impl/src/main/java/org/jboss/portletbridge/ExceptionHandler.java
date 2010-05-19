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
package org.jboss.portletbridge;

import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeException;

/**
 * @author asmirnov
 * 
 */
public interface ExceptionHandler {

	/**
	 * Process JSF exceptions at the portlet action phase
	 * 
	 * @param context
	 * @param windowState
	 * @param e
	 * @throws BridgeException
	 */
	public void processActionException(FacesContext context,
	        BridgeRequestScope windowState, Exception e) throws BridgeException;

	/**
	 * Process JSF exceptions at the portlet render phase.
	 * 
	 * @param context
	 * @param windowState
	 * @param e
	 * @throws BridgeException
	 */
	public void processRenderException(FacesContext context,
	        BridgeRequestScope windowState, Exception e) throws BridgeException;

	/**
	 * Process JSF exceptions from portlet serveresource phase.
	 * @param facesContext
	 * @param windowState
	 * @param e
	 * @throws BridgeException
	 */
	public void processResourceException(FacesContext facesContext,
	        BridgeRequestScope windowState, Exception e) throws BridgeException;

	/**
	 * Process JSF exceptions from portlet event request phase.
	 * @param facesContext
	 * @param windowState
	 * @param e
	 */
	public void processEventException(FacesContext facesContext,
            BridgeRequestScope windowState, Exception e) throws BridgeException;

}
