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
package org.jboss.portletbridge.application;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

public class PortletApplicationImpl extends ApplicationWrapper {

    private Application wrappedApplication;

    public PortletApplicationImpl(Application app) {
        wrappedApplication = app;
    }

    /**
     * Create a new UIComponent subclass, using the mappings defined by previous calls to the addComponent method of
     * this class.
     * <p>
     *
     * @throws FacesException
     *             if there is no mapping defined for the specified componentType, or if an instance of the specified
     *             type could not be created for any reason.
     */
    @Override
    public javax.faces.component.UIComponent createComponent(String componentType) throws FacesException {
        UIComponent component = wrappedApplication.createComponent(componentType);
        if (BridgeUtil.isPortletRequest() && component instanceof UIViewRoot
            && UIViewRoot.class.getAnnotation(PortletNamingContainer.class) == null) {
            // replace with our own UIViewRoot
            UIViewRoot root = (UIViewRoot) component;
            UIViewRoot portletRoot = new PortletNamingContainerUIViewRoot();
            portletRoot.setViewId(root.getViewId());
            portletRoot.setLocale(root.getLocale());
            portletRoot.setRenderKitId(root.getRenderKitId());
            portletRoot.setId(root.getId());
            component = portletRoot;
        }
        return component;
    }

    @Override
    public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass, Class<?> sourceClass, SystemEventListener listener) {
        if (null != sourceClass && UIViewRoot.class.getName().equals(sourceClass.getName())) {
            sourceClass = PortletNamingContainerUIViewRoot.class;
        }
        super.subscribeToEvent(systemEventClass, sourceClass, listener);
    }

    @Override
    public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass, Class<?> sourceClass, SystemEventListener listener) {
        if (null != sourceClass && UIViewRoot.class.getName().equals(sourceClass.getName())) {
            sourceClass = PortletNamingContainerUIViewRoot.class;
        }
        super.unsubscribeFromEvent(systemEventClass, sourceClass, listener);
    }

    @Override
    public Application getWrapped() {
        return wrappedApplication;
    }
}
