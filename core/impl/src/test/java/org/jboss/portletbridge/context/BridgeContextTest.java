/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.portlet.faces.Bridge;
import javax.servlet.http.Cookie;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.config.BridgeConfigImpl;
import org.jboss.portletbridge.bridge.context.BridgeContextImpl;

/**
 * @author Ken Finnigan
 */
public class BridgeContextTest extends TestCase {

    private TestBridgeContext context;

    @Override
    protected void setUp() throws Exception {
        BridgeConfig config = new BridgeConfigImpl();
        PortletConfig portletConfig = new PortletConfig() {
            @Override
            public String getPortletName() {
                return null;
            }
            @Override
            public PortletContext getPortletContext() {
                return null;
            }
            @Override
            public ResourceBundle getResourceBundle(Locale locale) {
                return null;
            }
            @Override
            public String getInitParameter(String name) {
                return null;
            }
            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
            @Override
            public Enumeration<String> getPublicRenderParameterNames() {
                return null;
            }
            @Override
            public String getDefaultNamespace() {
                return null;
            }
            @Override
            public Enumeration<QName> getPublishingEventQNames() {
                return null;
            }
            @Override
            public Enumeration<QName> getProcessingEventQNames() {
                return null;
            }
            @Override
            public Enumeration<Locale> getSupportedLocales() {
                return null;
            }
            @Override
            public Map<String, String[]> getContainerRuntimeOptions() {
                return null;
            }
        };

        config.setPortletConfig(portletConfig);

        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, TestAppFactory.class.getName());
        context = new TestBridgeContext(config);
    }

    public void testViewIdOnRequest() throws Exception {
        PortletRequest request = new TestPortletRequest("file://myFile.xhtml", null);

        String viewId = context.getViewId(request, false);

        assertEquals(null, viewId);
    }

    public void testViewIdOnRenderParameter() throws Exception {
        PortletRequest request = new TestPortletRequest(null, "file://myFile.xhtml");

        context.setPortletRequest(request);
        String viewId = context.getViewId(request, false);
        context.setPortletRequest(null);

        assertEquals(null, viewId);
    }

    static class TestBridgeContext extends BridgeContextImpl {
        public TestBridgeContext(BridgeConfig bridgeConfig) {
            super(bridgeConfig);
        }

        public String getViewId(PortletRequest request, boolean excludeQuery) {
            return super.getViewId(request, excludeQuery);
        }
    }

    static class TestPortletRequest implements PortletRequest {
        String viewId = null;
        String renderViewId = null;

        public TestPortletRequest(String viewId, String renderViewId) {
            this.viewId = viewId;
            this.renderViewId = renderViewId;
        }

        @Override
        public boolean isWindowStateAllowed(WindowState state) {
            return false;
        }

        @Override
        public boolean isPortletModeAllowed(PortletMode mode) {
            return false;
        }

        @Override
        public PortletMode getPortletMode() {
            return PortletMode.VIEW;
        }

        @Override
        public WindowState getWindowState() {
            return null;
        }

        @Override
        public PortletPreferences getPreferences() {
            return null;
        }

        @Override
        public PortletSession getPortletSession() {
            return null;
        }

        @Override
        public PortletSession getPortletSession(boolean create) {
            return null;
        }

        @Override
        public String getProperty(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getProperties(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getPropertyNames() {
            return null;
        }

        @Override
        public PortalContext getPortalContext() {
            return null;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Object getAttribute(String name) throws IllegalArgumentException {
            if (name.equals(Bridge.VIEW_ID)) {
                return viewId;
            } else if (name.equals(Bridge.PORTLET_LIFECYCLE_PHASE)) {
                return Bridge.PortletPhase.RENDER_PHASE;
            }

            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return renderViewId;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public void setAttribute(String name, Object o) {

        }

        @Override
        public void removeAttribute(String name) {

        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public String getResponseContentType() {
            return null;
        }

        @Override
        public Enumeration<String> getResponseContentTypes() {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public String getWindowID() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public Map<String, String[]> getPrivateParameterMap() {
            return null;
        }

        @Override
        public Map<String, String[]> getPublicParameterMap() {
            return null;
        }
    }

    public static class TestAppFactory extends ApplicationFactory {

        @Override
        public Application getApplication() {
            return new Application() {
                @Override
                public ActionListener getActionListener() {
                    return null;
                }

                @Override
                public void setActionListener(ActionListener listener) {

                }

                @Override
                public Locale getDefaultLocale() {
                    return null;
                }

                @Override
                public void setDefaultLocale(Locale locale) {

                }

                @Override
                public String getDefaultRenderKitId() {
                    return null;
                }

                @Override
                public void setDefaultRenderKitId(String renderKitId) {

                }

                @Override
                public String getMessageBundle() {
                    return null;
                }

                @Override
                public void setMessageBundle(String bundle) {

                }

                @Override
                public NavigationHandler getNavigationHandler() {
                    return null;
                }

                @Override
                public void setNavigationHandler(NavigationHandler handler) {

                }

                @Override
                public PropertyResolver getPropertyResolver() {
                    return null;
                }

                @Override
                public void setPropertyResolver(PropertyResolver resolver) {

                }

                @Override
                public VariableResolver getVariableResolver() {
                    return null;
                }

                @Override
                public void setVariableResolver(VariableResolver resolver) {

                }

                @Override
                public ViewHandler getViewHandler() {
                    return null;
                }

                @Override
                public void setViewHandler(ViewHandler handler) {

                }

                @Override
                public StateManager getStateManager() {
                    return null;
                }

                @Override
                public void setStateManager(StateManager manager) {

                }

                @Override
                public void addComponent(String componentType, String componentClass) {

                }

                @Override
                public UIComponent createComponent(String componentType) throws FacesException {
                    return null;
                }

                @Override
                public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType) throws FacesException {
                    return null;
                }

                @Override
                public Iterator<String> getComponentTypes() {
                    return null;
                }

                @Override
                public void addConverter(String converterId, String converterClass) {

                }

                @Override
                public void addConverter(Class<?> targetClass, String converterClass) {

                }

                @Override
                public Converter createConverter(String converterId) {
                    return null;
                }

                @Override
                public Converter createConverter(Class<?> targetClass) {
                    return null;
                }

                @Override
                public Iterator<String> getConverterIds() {
                    return null;
                }

                @Override
                public Iterator<Class<?>> getConverterTypes() {
                    return null;
                }

                @Override
                public MethodBinding createMethodBinding(String ref, Class<?>[] params) throws ReferenceSyntaxException {
                    return null;
                }

                @Override
                public Iterator<Locale> getSupportedLocales() {
                    return null;
                }

                @Override
                public void setSupportedLocales(Collection<Locale> locales) {

                }

                @Override
                public void addValidator(String validatorId, String validatorClass) {

                }

                @Override
                public Validator createValidator(String validatorId) throws FacesException {
                    return null;
                }

                @Override
                public Iterator<String> getValidatorIds() {
                    return null;
                }

                @Override
                public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
                    return null;
                }

                @Override
                public void addELContextListener(ELContextListener listener) {
                }
            };
        }

        @Override
        public void setApplication(Application application) {

        }
    }
}
