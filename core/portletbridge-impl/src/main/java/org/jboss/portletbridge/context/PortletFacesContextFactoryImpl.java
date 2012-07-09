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

import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletContext;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.el.ELContextImpl;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletFacesContextFactoryImpl extends FacesContextFactory {

    private FacesContextFactory facesContextFactory;
    private ELContext elContext;

    public PortletFacesContextFactoryImpl(FacesContextFactory wrappedFactory) {
        facesContextFactory = wrappedFactory;
    }

    @Override
    public FacesContextFactory getWrapped() {
        return facesContextFactory;
    }

    /**
     * @see javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object, java.lang.Object, java.lang.Object,
     *      javax.faces.lifecycle.Lifecycle)
     */
    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException {
        FacesContext facesContext = getWrapped().getFacesContext(context, request, response, lifecycle);
        if (facesContext.getExternalContext().getContext() instanceof PortletContext) {
            facesContext = new PortletFacesContextImpl(facesContext);
        }
        return facesContext;
    }

    private class PortletFacesContextImpl extends FacesContextWrapper {
        private FacesContext wrapped;

        public PortletFacesContextImpl(FacesContext facesContext) {
            wrapped = facesContext;
            setCurrentInstance(this);
        }

        @Override
        public FacesContext getWrapped() {
            return wrapped;
        }

        @Override
        public ELContext getELContext() {
            if (null == elContext) {
                Application app = getApplication();
                elContext = new ELContextImpl(app.getELResolver());
                elContext.putContext(FacesContext.class, FacesContext.getCurrentInstance());

                UIViewRoot viewRoot = getViewRoot();
                if (null != viewRoot) {
                    elContext.setLocale(viewRoot.getLocale());
                }

                ELContextListener[] listeners = app.getELContextListeners();
                ELContextEvent elEvent = new ELContextEvent(elContext);
                for (ELContextListener elContextListener : listeners) {
                    elContextListener.contextCreated(elEvent);
                }
            }
            return elContext;
        }

        @Override
        public void release() {
            elContext = null;

            ExternalContext extContext = getWrapped().getExternalContext();
            Map<String, Object> requestAttrs = extContext.getRequestMap();

            List<String> preExistingAttrs = BridgeContext.getCurrentInstance().getPreFacesRequestAttrNames();
            if (null != preExistingAttrs) {
                for (String key : requestAttrs.keySet()) {
                    if (preExistingAttrs.contains(key)) {
                        requestAttrs.remove(key);
                    }
                }
            }

            super.release();
        }

    }
}
