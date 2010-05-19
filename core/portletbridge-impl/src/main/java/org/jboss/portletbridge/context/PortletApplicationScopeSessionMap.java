package org.jboss.portletbridge.context;

import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

public class PortletApplicationScopeSessionMap extends ContextAttributesMap<Object> {

   private PortletSession session;

   public PortletApplicationScopeSessionMap(PortletRequest req) {
      session = req.getPortletSession(true);
   }

   @Override
   protected Object getAttribute(String name) {
      Object retVal = null;
      try {
         retVal =  session.getAttribute(name, PortletSession.APPLICATION_SCOPE);
      }
      catch (IllegalStateException e) {
         // todo - Handle invalidated session state
      }
         return retVal;
   }

   @Override
   protected void setAttribute(String name, Object value) {
      session.setAttribute(name, value, PortletSession.APPLICATION_SCOPE);

   }

   @Override
   protected void removeAttribute(String name) {
      session.removeAttribute(name, PortletSession.APPLICATION_SCOPE);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Enumeration<String> getEnumeration() {
      return session.getAttributeNames(PortletSession.APPLICATION_SCOPE);
   }


}
