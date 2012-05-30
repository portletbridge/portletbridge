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

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactoryImpl;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class AjaxPortletExceptionHandler extends ExceptionHandlerWrapper {
    private static final BridgeLogger logger = BridgeLoggerFactoryImpl.getLogger(AjaxPortletExceptionHandler.class.getName());

    private ExceptionHandler wrapped;

    public AjaxPortletExceptionHandler(ExceptionHandler exceptionHandler) {
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
        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = event.getContext();
            if (null != context) {
                Throwable throwable = context.getException();
                if (null != throwable) {
                    logger.log(Level.ERROR, "Exception while processing Ajax Request in Portlet Bridge", throwable);
                } else {
                    logger.log(Level.ERROR,
                            "Null exception found on ExceptionQueuedEventContext in Phase: " + context.getPhaseId()
                                    + " and Component:" + context.getComponent());
                }
            } else {
                logger.log(Level.ERROR, "ExceptionQueuedEventContext null for ExceptionQueuedEvent: " + event.toString());
            }
        }
        // Delegate to wrapped exception handler to process exceptions
        super.handle();
    }

}
