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


import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.ServletException;
import java.util.Map;

import org.jboss.portletbridge.context.PortletBridgeContext;

/**
 * @author asmirnov
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

public class ExceptionHandlerImpl extends ExceptionHandlerBase {

	/**
	 * This method looks for the JSF page defined in the web.xml for exception or its
	 * clause class.
	 * @param context
	 * @param e
	 * @return viewId of the error page, or <code>null</code> if no page configured for this exception.
	 */
	protected String processException(FacesContext facesContext,
            BridgeRequestScope windowState, Exception e)  {
		PortletBridgeContext bridgeContext = PortletBridgeContext.getCurrentInstance(facesContext);
		Map<Class<? extends Throwable>, String> errorPages = bridgeContext.getBridgeConfig()
				.getErrorPages();
		return getErrorPage(e, errorPages);
	}


	/**
	 * Reccursive helper method for the {@link #getErrorViewId(FacesContext, Exception)} .
	 * This method calls itself for the exception clause ( if that exists ). If no error page was defined for
	 * the clause, look it up in the view id's map.
	 * @param e
	 * @param errorPages
	 * @return
	 */
	protected String getErrorPage(Throwable e, Map<Class<? extends Throwable>, String> errorPages) {
		Throwable cause = getCause(e);
		String errorPage = null;
		if (null != cause) {
			errorPage = getErrorPage(cause, errorPages);
			if (null != errorPage) {
				return errorPage;
			}
		}
		for (Class<? extends Throwable> errorClass : errorPages.keySet()) {
			if(errorClass.isInstance(e) && errorPages.containsKey(errorClass)){
				errorPage = errorPages.get(errorClass);
			}
		}
		return errorPage;
	}

	/**
	 * get exception clause or ServletException rootClause.
	 * @param exception
	 * @return
	 */
	protected Throwable getCause(Throwable exception) {
		Throwable cause = null;
		if (exception instanceof ServletException) {
			cause = ((ServletException) exception).getRootCause();
		} else {
			cause = exception.getCause();
		}
		return cause;
	}

}
