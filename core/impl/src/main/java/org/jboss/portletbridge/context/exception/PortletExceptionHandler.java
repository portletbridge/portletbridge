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
package org.jboss.portletbridge.context.exception;

import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletException;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactoryImpl;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletExceptionHandler extends ExceptionHandlerWrapper {
    private static final BridgeLogger logger = BridgeLoggerFactoryImpl.getLogger(PortletExceptionHandler.class.getName());

    private ExceptionHandler wrapped;

    public PortletExceptionHandler(ExceptionHandler exceptionHandler) {
        this.wrapped = exceptionHandler;
    }

    /**
     * @see javax.faces.context.ExceptionHandlerWrapper#getWrapped()
     */
    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
        Map<Class<? extends Throwable>, String> errorViews = bridgeContext.getBridgeConfig().getFacesErrorViewMappings();
        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            if (null != context) {
                Throwable t = context.getException();
                if (null != t) {
                    String errorView = getErrorView(t, errorViews);
                    if (null != errorView) {
                        FacesContext fc = FacesContext.getCurrentInstance();
                        try {
                            if (fc.getCurrentPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
                                // if we are already redirecting from this error
                                // to this error, just skip
                                // this would happen if there's an error on the
                                // errorView and would cause an infinite loop
                                if (!errorView.equals(bridgeContext.getRedirectViewId())) {
                                    // browser has seen already some content, so, we can't
                                    // reset the buffer/response
                                    if (!fc.getExternalContext().isResponseCommitted()) {
                                        fc.getExternalContext().responseReset();
                                        fc.getExternalContext().setResponseBufferSize(-1);
                                    }
                                    bridgeContext.setRedirectViewId(errorView);
                                    bridgeContext.setRenderRedirect(true);
                                }
                            } else {
                                NavigationHandler nav = fc.getApplication().getNavigationHandler();
                                nav.handleNavigation(fc, null, errorView);
                                fc.renderResponse();
                            }
                        } finally {
                            i.remove();
                        }
                    } else {
                        logger.log(Level.ERROR, "No error mapping found in web.xml for Throwable: " + t.getClass().getName());
                    }
                } else {
                    logger.log(Level.ERROR,
                            "Null exception found on ExceptionQueuedEventContext in Phase: " + context.getPhaseId()
                                    + " and Component:" + context.getComponent());
                }
            } else {
                logger.log(Level.ERROR, "ExceptionQueuedEventContext null for ExceptionQueuedEvent: " + event.toString());
            }
        }

        // Delegate exceptions without error view mappings back to default JSF2 Exception Handler
        getWrapped().handle();
    }

    /**
     * This method calls itself for the exception clause ( if that exists ). If no error page was defined for the clause, look
     * it up in the view id's map.
     *
     * @param t Throwable to retrieve Faces view for
     * @param errorViews Mapping of Throwable to Faces views built from web.xml
     * @return String representing Faces View to display for Throwable
     */
    protected String getErrorView(Throwable t, Map<Class<? extends Throwable>, String> errorViews) {
        Throwable cause = getCause(t);
        String errorView = null;
        if (null != cause) {
            errorView = getErrorView(cause, errorViews);
            if (null != errorView) {
                return errorView;
            }
        }
        for (Class<? extends Throwable> errorClass : errorViews.keySet()) {
            if (errorClass.isInstance(t) && errorViews.containsKey(errorClass)) {
                errorView = errorViews.get(errorClass);
            }
        }
        return errorView;
    }

    /**
     * Get exception clause or ServletException rootClause.
     *
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
