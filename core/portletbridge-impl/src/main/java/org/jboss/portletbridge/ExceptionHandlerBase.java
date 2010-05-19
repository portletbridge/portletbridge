/******************************************************************************
 * $Id$
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portletbridge.context.PortletBridgeContext;

/**
 * @author asmirnov
 *
 */
public abstract class ExceptionHandlerBase implements ExceptionHandler {
	private static final String HANDLE_VIEW_EXPIRED_ON_CLIENT = "org.ajax4jsf.handleViewExpiredOnClient";
	private static final String AJAX_FLAG_HEADER = "Ajax-Response";
	public static final String AJAX_EXPIRED = "Ajax-Expired";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.portletbridge.ExceptionHandler#processRenderException(javax
	 * .faces.context.FacesContext,
	 * org.jboss.portletbridge.application.PortletViewState,
	 * java.lang.Exception)
	 */
	public void processRenderException(FacesContext context,
	        BridgeRequestScope windowState, Exception e) throws BridgeException {
		String redirectViewId = processException(context, windowState, e);
		if (null != redirectViewId) {
			PortletBridgeContext bridgeContext = PortletBridgeContext
			        .getCurrentInstance(context);
			PortletRequest request = (PortletRequest) context
			        .getExternalContext().getRequest();
			bridgeContext.resetRequestAttributes(request);
			windowState.restoreBeans(context);
			renderErrorPage(context, windowState, e, redirectViewId);
		} else {
			throw new BridgeException("Error processing render lifecycle", e);
		}
	}

	public void processResourceException(FacesContext facesContext,
	        BridgeRequestScope windowState, Exception e) throws BridgeException {
		windowState.saveRequestParameters(facesContext);
		String redirectViewId = processException(facesContext, windowState, e);
		if (null != redirectViewId) {
			renderErrorPage(facesContext, windowState, e, redirectViewId);
		} else {
			// PBR-127, richFaces viewExpired processing.
			boolean handleViewExpiredOnClient = Boolean
			        .parseBoolean(facesContext.getExternalContext()
			                .getInitParameter(
			                        HANDLE_VIEW_EXPIRED_ON_CLIENT));
			if (handleViewExpiredOnClient && isViewExpired(e)) {
				sendAjaxViewExpired(facesContext, e);
			} else {
				throw new BridgeException("Error processing resource lifecycle",
				        e);
			}
		}
	}

	private void sendAjaxViewExpired(FacesContext facesContext, Exception e) {
	    ResourceResponse response = (ResourceResponse) facesContext
	            .getExternalContext().getResponse();
	    try {
	    	response.reset();
	    	String message;
	    	try {
	    		ResourceBundle resourceBundle = ResourceBundle
	    		        .getBundle("org.ajax4jsf.messages",
	    		                facesContext.getApplication()
	    		                        .getViewHandler()
	    		                        .calculateLocale(facesContext),
	    		                Thread.currentThread()
	    		                        .getContextClassLoader());
	    		message = resourceBundle.getString("AJAX_VIEW_EXPIRED");

	    	} catch (MissingResourceException e2) {
	    		message = "%AJAX_VIEW_EXPIRED%";
	    	}
	    	response.setProperty(AJAX_EXPIRED, message);
	    	response.setContentType("text/xml");
	    	PrintWriter output = response.getWriter();
	    	output
	    	        .write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    	                + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>"
	    	                + "<meta name=\""
	    	                + AJAX_FLAG_HEADER
	    	                + "\" content=\"true\" />"
	    	                + "<meta name=\""
	    	                + AJAX_EXPIRED
	    	                + "\" content=\""
	    	                + message
	    	                + "\" />"
	    	                + "</head></html>");
	    	output.flush();
	    } catch (IOException e1) {
	    	throw new BridgeException("Error render Ajax-expired content",
	    	        e);
	    }
    }

	public void processEventException(FacesContext facesContext, BridgeRequestScope windowState, Exception e)
    throws BridgeException {
    	throw new BridgeException("Error processing faces lifecycle from event request", e);
        
    }

	public void processActionException(FacesContext context, BridgeRequestScope windowState, Exception e)
    throws BridgeException {
    	String errorPage = processException(context, windowState, e);
    	if (null != errorPage) {
    		windowState.reset();
    		setupErrorParameters(context,e);
    		windowState.setViewId(errorPage);
    		windowState.saveRequestParameters(context);
    		windowState.saveBeans(context);
    	} else {
    		throw new BridgeException("Error processing render lifecycle", e);
    	}
    
    }

	protected void renderErrorPage(FacesContext context,
	        BridgeRequestScope windowState, Exception e, String redirectViewId) {
		PortletBridgeContext bridgeContext = PortletBridgeContext
		        .getCurrentInstance(context);
		PortletRequest request = (PortletRequest) context.getExternalContext()
		        .getRequest();
		Object response = context.getExternalContext().getResponse();
		context.release();
		request.setAttribute(Bridge.VIEW_ID, redirectViewId);
		context = bridgeContext.getBridgeConfig().createFacesContext(request,
		        response);
		windowState.restoreMessages(context);
		setupErrorParameters(context, e);
		bridgeContext.render(context);
	}

	protected abstract String processException(FacesContext facesContext,
            BridgeRequestScope windowState, Exception e) ;

	/**
	 * Check for a {@link ViewExpiredException} in the exception chain.
	 * 
	 * @param e
	 *            exception from filter chain
	 * @return true if any exception in the chain instance of the
	 *         {@link ViewExpiredException}
	 */
	private boolean isViewExpired(Throwable e) {
		while (null != e) {
			if (e instanceof ViewExpiredException) {
				return true;
			} else {
				e = e.getCause();
			}
		}
		return false;
	}

	/**
     * Simulate Servlet error page attributes.
     * @param context
     * @param throwable
     */
    protected void setupErrorParameters(FacesContext context, Throwable throwable) {
    	Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
    	requestMap.put("javax.servlet.error.exception", throwable);
    	requestMap.put("javax.servlet.error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	requestMap.put("javax.servlet.error.message", throwable.getLocalizedMessage());
    	requestMap.put("javax.servlet.error.servlet_name", "Faces Servlet");
    }

}
