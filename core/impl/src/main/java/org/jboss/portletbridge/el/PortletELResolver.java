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
package org.jboss.portletbridge.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.preference.Preference;

import org.jboss.portletbridge.context.map.PortletApplicationScopeSessionMap;
import org.jboss.portletbridge.preference.PreferenceImpl;

public class PortletELResolver extends ELResolver {

    public static final String PORTLET_CONFIG = "portletConfig";
    public static final String ACTION_REQUEST = "actionRequest";
    public static final String ACTION_RESPONSE = "actionResponse";
    public static final String EVENT_REQUEST = "eventRequest";
    public static final String EVENT_RESPONSE = "eventResponse";
    public static final String RENDER_REQUEST = "renderRequest";
    public static final String RENDER_RESPONSE = "renderResponse";
    public static final String RESOURCE_REQUEST = "resourceRequest";
    public static final String RESOURCE_RESPONSE = "resourceResponse";
    public static final String PORTLET_SESSION = "portletSession";
    public static final String PORTLET_SESSION_SCOPE = "portletSessionScope";
    public static final String HTTP_SESSION_SCOPE = "httpSessionScope";
    public static final String PORTLET_PREFERENCES = "portletPreferences";
    public static final String PORTLET_PREFERENCES_VALUES = "portletPreferencesValues";
    public static final String MUTABLE_PORTLET_PREFERENCES_VALUES = "mutablePortletPreferencesValues";

    // old portlet bridge 1.0 - leave for backwards compatibility in 2.0 GA
    public static final String SESSION_APPLICATION_SCOPE = "sessionApplicationScope";
    public static final String SESSION_PORTLET_SCOPE = "sessionPortletScope";
    public static final String PORTLET_PREFERENCE_VALUE = "portletPreferenceValue";
    public static final String PORTLET_PREFERENCE_VALUES = "portletPreferenceValues";

    // vendor specific
    public static final String AJAX_CONTEXT = "ajaxContext";

    private Map<String, Object> appScopeSessionMap = null;

    public PortletELResolver() {
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) throws ELException {
        // variable resolution is a special case of property resolution
        // where the base is null.
        if (!BridgeUtil.isPortletRequest() || null != base) {
            return null;
        }

        if (null == property) {
            throw new PropertyNotFoundException("Null property");
        }

        if (null == context) {
            throw new IllegalArgumentException("PortletELResolver.getValue was passed a null ELContext");
        }

        ELContextImpl portletELContext = (ELContextImpl) context.getContext(ELContextImpl.class);

        if (null == portletELContext) {
            return null;
        }

        if (portletELContext.isFacesResolved()) {
            return getValueWithFacesResolution(context, portletELContext, base, property);
        } else {
            return getValueWithJSPResolution(context, portletELContext, base, property);
        }
    }

    private Object getValueWithJSPResolution(ELContext context, ELContextImpl portletELContext, Object base, Object property) {
        if (property instanceof String) {
            FacesContext facesContext = (FacesContext) context.getContext(FacesContext.class);
            ExternalContext externalContext = facesContext.getExternalContext();

            try {
                if (HTTP_SESSION_SCOPE.equals(property)) {
                    context.setPropertyResolved(true);
                    return getHttpSessionMap(externalContext, portletELContext);
                } else if (MUTABLE_PORTLET_PREFERENCES_VALUES.equals(property)) {
                    context.setPropertyResolved(true);
                    return getMutablePortletPreferencesValues(externalContext, portletELContext);
                } else {
                    return null;
                }
            } catch (IllegalArgumentException e) {
                // Faces defers to the implicit object resolver when evaluating
                // in a JSP context. Alas, this means that Faces doesn't resolve
                // session scoped ManagedBeans in a JSP context if the managed bean
                // is already created rather it defers to the JSP Implicit resolver
                // which accesses the http session not the protlet session. I.e.
                // though the managed bean resolver sees that the session scoped bean
                // exists it can't be retrieved because its in the portlet session
                // not the http session.
                // So its up to us to see if the bean is in the session
                if (externalContext.getSessionMap().containsKey(property)) {
                    context.setPropertyResolved(true);
                    return externalContext.getSessionMap().get(property);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private Object getValueWithFacesResolution(ELContext context, ELContextImpl portletELContext, Object base, Object property) {
        FacesContext facesContext = (FacesContext) context.getContext(FacesContext.class);
        ExternalContext externalContext = facesContext.getExternalContext();

        if (PORTLET_CONFIG.equals(property)) {
            PortletConfig config = portletELContext.getPortletConfig();
            if (null != config) {
                context.setPropertyResolved(true);
                return config;
            } else {
                throw new ELException(
                        "EL Resolve failed: can't resolve portletConfig because its not set on this Faces EL Resolver.");
            }
        } else if (ACTION_REQUEST.equals(property) && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getRequest();
        } else if (ACTION_RESPONSE.equals(property)
                && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getResponse();
        } else if (RENDER_REQUEST.equals(property) && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RENDER_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getRequest();
        } else if (RENDER_RESPONSE.equals(property)
                && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RENDER_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getResponse();
        } else if (EVENT_REQUEST.equals(property) && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.EVENT_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getRequest();
        } else if (EVENT_RESPONSE.equals(property) && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.EVENT_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getResponse();
        } else if (RESOURCE_REQUEST.equals(property)
                && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getRequest();
        } else if (RESOURCE_RESPONSE.equals(property)
                && (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE)) {
            context.setPropertyResolved(true);
            return externalContext.getResponse();
        } else if (SESSION_APPLICATION_SCOPE.equals(property) || HTTP_SESSION_SCOPE.equals(property)) {
            context.setPropertyResolved(true);
            if (null == appScopeSessionMap) {
                Object request = externalContext.getRequest();
                if (BridgeUtil.isPortletRequest()) {
                    appScopeSessionMap = new PortletApplicationScopeSessionMap((PortletRequest) request);
                }
            }
            return appScopeSessionMap;
        } else if (SESSION_PORTLET_SCOPE.equals(property) || PORTLET_SESSION_SCOPE.equals(property)) {
            context.setPropertyResolved(true);
            return externalContext.getSessionMap();
        } else if (PORTLET_SESSION.equals(property)) {
            context.setPropertyResolved(true);
            return externalContext.getSession(false);
        } else if (PORTLET_PREFERENCE_VALUE.equals(property)) {
            context.setPropertyResolved(true);
            return getPreferencesValueMap(facesContext);
        } else if (PORTLET_PREFERENCES.equals(property)) {
            context.setPropertyResolved(true);
            return ((PortletRequest) externalContext.getRequest()).getPreferences();
        } else if (MUTABLE_PORTLET_PREFERENCES_VALUES.equals(property)) {
            context.setPropertyResolved(true);
            return getPreferenceMap(((PortletRequest) externalContext.getRequest()).getPreferences());
        } else if (PORTLET_PREFERENCES_VALUES.equals(property)) {
            context.setPropertyResolved(true);
            return ((PortletRequest) externalContext.getRequest()).getPreferences().getMap();
        } else {
            return null;
        }
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object val) throws ELException {
        if (null != base) {
            return;
        }
        if (null == property) {
            throw new PropertyNotFoundException("Null property");
        }

    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) throws ELException {
        if (null != base) {
            return false;
        }
        if (null == property) {
            throw new PropertyNotFoundException("Null property");
        }

        return false;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) throws ELException {
        if (null != base) {
            return null;
        }
        if (null == property) {
            throw new PropertyNotFoundException("Null property");
        }

        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (null != base) {
            return null;
        }
        ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(20);

        list.add(getFeatureDescriptor(ACTION_REQUEST, ACTION_REQUEST, ACTION_REQUEST, false, false, true,
                ActionRequest.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(ACTION_RESPONSE, ACTION_RESPONSE, ACTION_RESPONSE, false, false, true,
                ActionResponse.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(EVENT_REQUEST, EVENT_REQUEST, EVENT_REQUEST, false, false, true, EventRequest.class,
                Boolean.TRUE));
        list.add(getFeatureDescriptor(EVENT_RESPONSE, EVENT_RESPONSE, EVENT_RESPONSE, false, false, true,
                EventResponse.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(HTTP_SESSION_SCOPE, HTTP_SESSION_SCOPE, HTTP_SESSION_SCOPE, false, false, true,
                Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor("mutablePortletPreferences", "mutablePortletPreferences", "mutablePortletPreferences",
                false, false, true, Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_CONFIG, PORTLET_CONFIG, PORTLET_CONFIG, false, false, true,
                PortletConfig.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_PREFERENCES, PORTLET_PREFERENCES, PORTLET_PREFERENCES, false, false, true,
                PortletPreferences.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_PREFERENCES_VALUES, PORTLET_PREFERENCES_VALUES, PORTLET_PREFERENCES_VALUES,
                false, false, true, Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_SESSION, PORTLET_SESSION, PORTLET_SESSION, false, false, true,
                PortletSession.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_SESSION_SCOPE, PORTLET_SESSION_SCOPE, PORTLET_SESSION_SCOPE, false, false, true,
                Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(RENDER_REQUEST, RENDER_REQUEST, RENDER_REQUEST, false, false, true,
                RenderRequest.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(RENDER_RESPONSE, RENDER_RESPONSE, RENDER_RESPONSE, false, false, true,
                RenderResponse.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(RESOURCE_REQUEST, RESOURCE_REQUEST, RESOURCE_REQUEST, false, false, true,
                ResourceRequest.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(RESOURCE_RESPONSE, RESOURCE_RESPONSE, RESOURCE_RESPONSE, false, false, true,
                ResourceResponse.class, Boolean.TRUE));

        list.add(getFeatureDescriptor(SESSION_APPLICATION_SCOPE, SESSION_APPLICATION_SCOPE, SESSION_APPLICATION_SCOPE, false,
                false, true, Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(SESSION_PORTLET_SCOPE, SESSION_PORTLET_SCOPE, SESSION_PORTLET_SCOPE, false, false, true,
                Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_PREFERENCE_VALUE, PORTLET_PREFERENCE_VALUE, PORTLET_PREFERENCE_VALUE, false,
                false, true, Map.class, Boolean.TRUE));
        list.add(getFeatureDescriptor(PORTLET_PREFERENCE_VALUES, PORTLET_PREFERENCE_VALUES, PORTLET_PREFERENCE_VALUES, false,
                false, true, Map.class, Boolean.TRUE));

        list.add(getFeatureDescriptor(AJAX_CONTEXT, AJAX_CONTEXT, AJAX_CONTEXT, false, false, true, Map.class, Boolean.TRUE));
        return list.iterator();

    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (null != base) {
            return null;
        }
        return String.class;
    }

    private FeatureDescriptor getFeatureDescriptor(String name, String displayName, String desc, boolean expert,
            boolean hidden, boolean preferred, Object type, Boolean designTime) {

        FeatureDescriptor fd = new FeatureDescriptor();
        fd.setName(name);
        fd.setDisplayName(displayName);
        fd.setShortDescription(desc);
        fd.setExpert(expert);
        fd.setHidden(hidden);
        fd.setPreferred(preferred);
        fd.setValue(ELResolver.TYPE, type);
        fd.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, designTime);
        return fd;
    }

    private Map<String, String> getPreferencesValueMap(FacesContext context) {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, String[]> preferencesValuesMap = ((PortletRequest) context.getExternalContext().getRequest())
                .getPreferences().getMap();
        for (Iterator<Entry<String, String[]>> entryIterator = preferencesValuesMap.entrySet().iterator(); entryIterator
                .hasNext();) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entryIterator.next();
            String[] preferenceValues = (String[]) entry.getValue();
            if (null != preferenceValues && preferenceValues.length > 0) {
                m.put((String) entry.getKey(), preferenceValues[0]);
            }

        }

        return Collections.unmodifiableMap(m);
    }

    private Map<String, Preference> getPreferenceMap(PortletPreferences prefs) {
        Map<String, Preference> m;

        // construct a Map of PreferenceImpl objects for each preference
        Enumeration<String> e = prefs.getNames();

        if (e.hasMoreElements()) {
            m = new HashMap<String, Preference>();
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                m.put(name, new PreferenceImpl(prefs, name));
            }
        } else {
            m = Collections.emptyMap();
        }

        return m;
    }

    private Map<String, Object> getHttpSessionMap(ExternalContext externalContext, ELContextImpl portletELContext) {
        Map<String, Object> sessionMap = portletELContext.getHttpSessionMap();
        if (null == sessionMap) {
            sessionMap = new PortletApplicationScopeSessionMap((PortletRequest) externalContext.getRequest());
            portletELContext.setHttpSessionMap(sessionMap);
        }
        return sessionMap;
    }

    private Map getMutablePortletPreferencesValues(ExternalContext externalContext, ELContextImpl portletELContext) {
        Map<String, Preference> preferencesValuesMap = portletELContext.getMutablePortletPreferencesMap();
        if (null == preferencesValuesMap) {
            preferencesValuesMap = getPreferenceMap(((PortletRequest) externalContext.getRequest()).getPreferences());
            portletELContext.setMutablePortletPreferencesMap(preferencesValuesMap);
        }
        return preferencesValuesMap;
    }

}