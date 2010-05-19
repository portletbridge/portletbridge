/******************************************************************************
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
package org.jboss.portletbridge.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.context.PortletApplicationScopeSessionMap;
import org.jboss.portletbridge.context.PortletBridgeContext;


public class PortletELResolver extends ELResolver {

   public static final String PORTLET_CONFIG = "portletConfig";
   public static final String SESSION_APPLICATION_SCOPE = "sessionApplicationScope";
   public static final String SESSION_PORTLET_SCOPE = "sessionPortletScope";
   public static final String PORTLET_PREFERENCE_VALUE = "portletPreferenceValue";
   public static final String PORTLET_PREFERENCE_VALUES = "portletPreferenceValues";
   public static final String AJAX_CONTEXT = "ajaxContext";

   //go ahead and start adding extended el from JSR329 6.5.3
   //todo
   public static final String HTTP_SESSION_SCOPE = "httpSessionScope";

   private Map<String, Object> mAppScopeSessionMap = null;

   public PortletELResolver() {
   }

   @Override
   public Object getValue(ELContext context, Object base, Object property) throws ELException {
      // variable resolution is a special case of property resolution
      // where the base is null.
      if (base != null) {
         return null;
      }
      if (property == null) {
         throw new PropertyNotFoundException("Null property");
      }

      FacesContext facesContext = (FacesContext) context.getContext(FacesContext.class);
      ExternalContext extCtx = facesContext.getExternalContext();

      // only process if running in a portlet request
      Bridge.PortletPhase phase =
            (Bridge.PortletPhase) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);
      if (phase == null) {
         return null;
      }

      if (PORTLET_CONFIG.equals(property)) {
         context.setPropertyResolved(true);
         return context.getContext(PortletConfig.class);
      } else if (SESSION_APPLICATION_SCOPE.equals(property) || HTTP_SESSION_SCOPE.equals(property)) {
         context.setPropertyResolved(true);
         if (mAppScopeSessionMap == null) {
            Object request = extCtx.getRequest();
            Object portletLifecycleAttr = extCtx.getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);
            if (portletLifecycleAttr instanceof Bridge.PortletPhase) {
               mAppScopeSessionMap = new PortletApplicationScopeSessionMap((PortletRequest) request);
            }
         }
         return mAppScopeSessionMap;
      } else if (SESSION_PORTLET_SCOPE.equals(property)) {
         context.setPropertyResolved(true);
         return extCtx.getSessionMap();
      } else if (PORTLET_PREFERENCE_VALUE.equals(property)) {
         context.setPropertyResolved(true);
         return getPreferencesValueMap(facesContext);
      } else if (PORTLET_PREFERENCE_VALUES.equals(property)) {
         context.setPropertyResolved(true);
         return getPreferencesValuesMap(facesContext);
      } else {
         return null;
      }
//    }
   }

@Override
   public void setValue(ELContext context, Object base, Object property, Object val)
         throws ELException {
      if (base != null) {
         return;
      }
      if (property == null) {
         throw new PropertyNotFoundException("Null property");
      }

      /*int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
      if (index >= 0) {
         throw new PropertyNotWritableException((String) property);
      }*/
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property) throws ELException {
      if (base != null) {
         return false;
      }
      if (property == null) {
         throw new PropertyNotFoundException("Null property");
      }

      /*int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
      if (index >= 0) {
         context.setPropertyResolved(true);
         return true;
      }*/
      return false;
   }

   @Override
   public Class<?> getType(ELContext context, Object base, Object property) throws ELException {
      if (base != null) {
         return null;
      }
      if (property == null) {
         throw new PropertyNotFoundException("Null property");
      }

      /*int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
      if (index >= 0) {
         context.setPropertyResolved(true);
      }*/
      return null;
   }

   @Override
   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
      if (base != null) {
         return null;
      }
      ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(14);
      list.add(getFeatureDescriptor("portletConfig", "portletConfig", "portletConfig", false, false,
            true, Object.class, Boolean.TRUE));
      list.add(getFeatureDescriptor("sessionApplicationScope", "sessionApplicationScope",
            "sessionApplicationScope", false, false, true, Map.class,
            Boolean.TRUE));
      list.add(getFeatureDescriptor("httpSessionScope", "httpSessionScope",
            "httpSessionScope", false, false, true, Map.class,
            Boolean.TRUE));
      list.add(getFeatureDescriptor("sessionPortletScope", "sessionPortletScope",
            "sessionPortletScope", false, false, true, Map.class,
            Boolean.TRUE));
      list.add(getFeatureDescriptor("portletPreferenceValue", "portletPreferenceValue",
            "portletPreferenceValue", false, false, true, Map.class,
            Boolean.TRUE));
      list.add(getFeatureDescriptor("portletPreferenceValues", "portletPreferenceValues",
            "portletPreferenceValues", false, false, true, Map.class,
            Boolean.TRUE));
      return list.iterator();

   }

   @Override
   public Class<?> getCommonPropertyType(ELContext context, Object base) {
      if (base != null) {
         return null;
      }
      return String.class;
   }

   private FeatureDescriptor getFeatureDescriptor(String name, String displayName, String desc,
                                                  boolean expert, boolean hidden, boolean preferred,
                                                  Object type, Boolean designTime) {

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

   private Map getPreferencesValuesMap(FacesContext context) {
		if (isPortletRequest()) {
			return ((PortletRequest) context.getExternalContext().getRequest())
					.getPreferences().getMap();
		} else {
			PortletBridgeContext bridgeContext = (PortletBridgeContext) context
					.getExternalContext().getRequestMap().get(
							PortletBridgeContext.REQUEST_PARAMETER_NAME);
			Map portletPreferencesMap = Collections.emptyMap();
			if (null != bridgeContext) {
				// TODO - implement proper portlet preferences access.
//				BridgeRequestScope windowState = bridgeContext.getRequestScope();
//				portletPreferencesMap = windowState
//						.getPortletPreferencesMap();
			}
			return portletPreferencesMap;
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getPreferencesValueMap(FacesContext context) {
		Map<String, String> m = new HashMap<String, String>();
		Map preferencesValuesMap = getPreferencesValuesMap(context);
		for (Iterator entryIterator = preferencesValuesMap.entrySet()
				.iterator(); entryIterator.hasNext();) {
			Map.Entry entry = (Map.Entry) entryIterator.next();
			String[] preferenceValues = (String[]) entry.getValue();
			if (null != preferenceValues && preferenceValues.length > 0) {
				m.put((String) entry.getKey(), preferenceValues[0]);
			}

		}

		return Collections.unmodifiableMap(m);
	}

   public static boolean isPortletRequest() {
      Map<String, Object> m = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
      Bridge.PortletPhase phase = (Bridge.PortletPhase) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(Bridge.PORTLET_LIFECYCLE_PHASE);
      if (phase != null) {
         return true;
      } else {
         return false;
      }
   }

}