/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.listener;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.config.ConfigManager;
import com.sun.faces.mgbean.BeanManager;
import com.sun.faces.spi.InjectionProvider;
import org.jboss.portletbridge.bridge.context.BridgeContext;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletBridgeListener implements ServletContextListener, HttpSessionListener {

    private static final String MOJARRA_VIEW_SCOPE_MANAGER = "com.sun.faces.application.view.viewScopeManager";
    private static final String MOJARRA_ACTIVE_VIEW_MAPS = "com.sun.faces.application.view.activeViewMaps";

    private static PortletBridgeListener INSTANCE;

    private ServletContext servletContext;
    private ApplicationAssociate applicationAssociate;
    private InjectionProvider injectionProvider;

    public PortletBridgeListener() {
        INSTANCE = this;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (null == this.servletContext) {
            this.servletContext = sce.getServletContext();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (Enumeration e = servletContext.getAttributeNames(); e.hasMoreElements(); ) {
            String beanName = (String)e.nextElement();
            handleAttributeEvent(beanName,
                    servletContext.getAttribute(beanName));
        }

        this.servletContext = null;
        this.applicationAssociate = null;
        INSTANCE = null;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // We can only retrieve this once JSF has initialized, which is after the ServletContext contextInitialized() call.
        if (null == this.injectionProvider) {
            getInjectionProvider();
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        for (Enumeration<String> attrs = session.getAttributeNames(); attrs.hasMoreElements(); ) {
            String attribute = attrs.nextElement();

            if (null != attribute && attribute.startsWith("javax.portlet.p.")) {
                // Attribute is namespaced to a portlet and was added with PortletSession.setAttribute()

                int pos = attribute.indexOf('?');
                if (pos > 0) {
                    String jsfAttribute = attribute.substring(pos + 1);

                    Object value = session.getAttribute(attribute);
                    session.removeAttribute(attribute);

                    if (null != value) {
                        boolean destroyCalled = handleAttributeEvent(jsfAttribute, value);

                        if (!destroyCalled) {
                            String valueClass = value.getClass().getName();

                            if (null != valueClass && valueClass.contains("com.sun.faces")) {
                                session.setAttribute(jsfAttribute, value);

                                if (MOJARRA_ACTIVE_VIEW_MAPS.equals(jsfAttribute)) {
                                    // Clean up View Scoped beans
                                    HttpSessionListener viewScopeManager =
                                            (HttpSessionListener) servletContext.getAttribute(MOJARRA_VIEW_SCOPE_MANAGER);

                                    if (null != viewScopeManager) {
                                        viewScopeManager.sessionDestroyed(se);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean handleAttributeEvent(String beanName,
                                      Object bean) {

        boolean destroyCalled = false;
        ApplicationAssociate associate = getAssociate();

        try {
            if (null != associate) {
                BeanManager beanManager = associate.getBeanManager();
                if (null != beanManager && beanManager.isManaged(beanName)) {
                    // Check whether class has annotations present to determine whether JSF BeanBuilder
                    // will call invokePreDestroy() on InjectionProvider. If it won't, we do directly.
                    if (scanForAnnotations(bean.getClass())) {
                        beanManager.destroy(beanName, bean);
                    } else {
                        getInjectionProvider().invokePreDestroy(bean);
                    }
                    destroyCalled = true;
                }
            }
        } catch (Exception e) {
            String className = e.getClass().getName();
            BridgeContext.log("Error calling predestroy on instance of: " + className, e);
        }

        return destroyCalled;
    }

    private ApplicationAssociate getAssociate() {
        if (null == applicationAssociate) {
            applicationAssociate = ApplicationAssociate.getInstance(servletContext);
        }

        return applicationAssociate;
    }

    private InjectionProvider getInjectionProvider() {
        if (null == injectionProvider) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (null != context) {
                injectionProvider = (InjectionProvider) context.getAttributes().get(ConfigManager.INJECTION_PROVIDER_KEY);
            }
        }

        return injectionProvider;
    }

    private boolean scanForAnnotations(Class<?> clazz) {
        if (clazz != null) {
            while (clazz != Object.class) {
                Field[] fields = clazz.getDeclaredFields();
                if (fields != null) {
                    for (Field field : fields) {
                        if (field.getAnnotations().length > 0) {
                            return true;
                        }
                    }
                }

                Method[] methods = clazz.getDeclaredMethods();
                if (methods != null) {
                    for (Method method : methods) {
                        if (method.getDeclaredAnnotations().length > 0) {
                            return true;
                        }
                    }
                }

                clazz = clazz.getSuperclass();
            }
        }

        return false;
    }

    public static PortletBridgeListener getCurrentInstance() {
        return INSTANCE;
    }
}
