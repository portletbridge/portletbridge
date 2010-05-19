/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.jboss.portletbridge.application;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.Behavior;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

public class PortletApplicationImpl extends ApplicationWrapper {

    private Application mWrapped;

    public PortletApplicationImpl(Application app) {
        mWrapped = app;
    }

 
    /**
     * Create a new UIComponent subclass, using the mappings defined by previous
     * calls to the addComponent method of this class.
     * <p>
     * @throws FacesException if there is no mapping defined for the specified
     * componentType, or if an instance of the specified type could not be
     * created for any reason.
     */
    public javax.faces.component.UIComponent createComponent(String componentType)
            throws FacesException {
        UIComponent component = mWrapped.createComponent(componentType);
        if (BridgeUtil.isPortletRequest()
                && component instanceof UIViewRoot
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
    public Application getWrapped() {
        return mWrapped;
    }
}
