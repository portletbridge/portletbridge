package org.jboss.portletbridge.scope;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

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


/**
 * BridgeRequestScopeUtil contains static class methods to test whether an atrtibute belongs in the
 * BridgeManagedScope or not.  This is provided not only for the BridgeScope implementation
 * but also so impls supporting the specified Bridge PreDestory can test attributes being
 * added to the request scope when a bridge scope hasn't yet been established.
 */
 
public class BridgeRequestScopeUtil 
{
  

  /**
    * Allows the caller to (pre-determine) if a given attribute name/value pair
    * will be excluded ot not.
    * 
    * @param key name of the attribute 
    * @param value of the attribute
    * @param configuredExcludes
    * @return true if the attribute will be excluded, false otherwise.
    */

  static public boolean isExcluded(String key, Object value, List<String> configuredExcludes)
  {
    return ((value != null && value.getClass().getAnnotation(ExcludeFromManagedRequestScope.class) != null) || 
          (configuredExcludes != null && configuredExcludes.contains(key)) ||
          isPreDefinedExcludedObject(key, value) ||
          isExcludedNamespace(key, configuredExcludes));
  }
  
  static public boolean isPreDefinedExcludedObject(String s, Object o)
  {
    if (o != null &&
        (o instanceof PortletConfig || o instanceof PortletContext ||
         o instanceof PortletRequest || o instanceof PortletResponse ||
         o instanceof PortletSession || o instanceof PortletPreferences ||
         o instanceof PortalContext || o instanceof FacesContext ||
         o instanceof ExternalContext || o instanceof ServletConfig ||
         o instanceof ServletContext || o instanceof ServletRequest ||
         o instanceof ServletResponse || o instanceof HttpSession))
      return true;
    else
    {
      return isInNamespace(s, "javax.portlet.") ||
        isInNamespace(s, "javax.portlet.faces.") ||
        isInNamespace(s, "javax.faces.") ||
        isInNamespace(s, "javax.servlet.") ||
        isInNamespace(s, "javax.servlet.include.") ||
        isInNamespace(s, "org.apache.myfaces.portlet.faces.") ||
        // our ExternalContext uses this prefix internally to append to url which might
        // contain another '.' -- so exclude all that are prefixed with this
        s.startsWith("org.apache.myfaces.portlet.faces.context.");
    }
  }

  static public boolean isExcludedNamespace(String s, List<String> configuredExcludes)
  {
    if (configuredExcludes == null)
    {
      return false;
    }

    if (configuredExcludes.contains(s))
    {
      return true;
    }

    // No direct match -- walk through this list and process namespace checks
    Iterator<String> i = configuredExcludes.iterator();
    while (i.hasNext())
    {
      String exclude = i.next();
      if (exclude.endsWith("*"))
      {
        if (isInNamespace(s, exclude.substring(0, exclude.length() - 1)))
        {
          return true;
        }
      }
    }
    return false;
  }

  static private boolean isInNamespace(String s, String namespace)
  {
    // This is a non-recursive check so s must be the result of removing the namespace.
    if (s.startsWith(namespace))
    {
      // extract entire namespace and compare
      s = s.substring(0, s.lastIndexOf('.') + 1);
      return s.equals(namespace);
    }
    return false;
  }
  
 
 

 
  
}