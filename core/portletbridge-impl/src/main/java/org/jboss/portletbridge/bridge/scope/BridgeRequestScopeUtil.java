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
package org.jboss.portletbridge.bridge.scope;

import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.faces.annotation.ExcludeFromManagedRequestScope;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.portletbridge.context.AbstractExternalContext;

/**
 * Contains static methods to determine whether an attribute belongs in the managed {@link BridgeRequestScope} or not,
 * based on the exclusion definition from JSR-329 5.1.2.1. Also provides method to generate {@link BridgeRequestScope}
 * Id prefix.
 *
 * @author kenfinnigan
 */
public class BridgeRequestScopeUtil {

    private BridgeRequestScopeUtil() {
        // Prevent instantiation
    }

    /**
     * Allows the caller to determine if a given attribute name/value pair will be excluded or not from the
     * {@link BridgeRequestScope}.
     *
     * @param key
     *            name of the attribute
     * @param value
     *            of the attribute
     * @param localExcludes
     *            any excludes that are not part of the JSR-329 5.1.2.1 spec
     * @return true if the attribute will be excluded, false otherwise.
     */
    public static boolean isExcluded(String key, Object value, List<String> localExcludes) {
        return ((null != value && isExcludedBean(value)) || (null != localExcludes && localExcludes.contains(key))
            || isExcludedByDefinition(key, value) || isExcludedNamespace(key, localExcludes));
    }

    public static boolean isExcludedByDefinition(String key, Object value) {
        if (null != value
            && (value instanceof PortletConfig || value instanceof PortletContext || value instanceof PortletRequest
                || value instanceof PortletResponse || value instanceof PortletSession
                || value instanceof PortletPreferences || value instanceof PortalContext
                || value instanceof FacesContext || value instanceof ExternalContext || value instanceof ServletConfig
                || value instanceof ServletContext || value instanceof ServletRequest
                || value instanceof ServletResponse || value instanceof HttpSession)) {
            return true;
        }

        return isNamespaceMatch(key, "javax.portlet.")
            || isNamespaceMatch(key, "javax.portlet.faces")
            || isNamespaceMatch(key, "javax.faces.")
            || isNamespaceMatch(key, "javax.servlet.")
            || isNamespaceMatch(key, "javax.servlet.include.")
            || isNamespaceMatch(key, AbstractExternalContext.INITIAL_REQUEST_ATTRIBUTES_NAMES);
    }

    public static boolean isExcludedBean(Object bean) {
        return bean.getClass().isAnnotationPresent(ExcludeFromManagedRequestScope.class);
    }

    public static boolean isExcludedNamespace(String key, List<String> localExcludes) {
        if (null == localExcludes) {
            return false;
        }
        if (localExcludes.contains(key)) {
            return true;
        }
        for (String exclude : localExcludes) {
            if (exclude.endsWith("*")) {
                if (isNamespaceMatch(key, exclude.substring(0, exclude.length() - 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNamespaceMatch(String key, String namespace) {
        if (null != key && null != namespace && key.startsWith(namespace)) {
            key = key.substring(0, key.lastIndexOf('.') + 1);
            return key.equals(namespace);
        }
        return false;
    }

    /**
     * Generate a {@link BridgeRequestScope} Id prefix from portletName, sessionId, viewId, and portletMode.
     *
     * @param portletName
     * @param sessionId
     * @param viewId
     * @param portletMode
     * @return
     */
    public static String generateBridgeRequestScopeIdPrefix(String portletName, String sessionId, String viewId,
        String portletMode) {
        return new StringBuffer(portletName).append(':').append(sessionId).append(':').append(viewId).append(':')
            .append(portletMode).toString();
    }
}
