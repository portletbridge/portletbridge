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
package org.jboss.portletbridge.util;

import java.util.List;

import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.portlet.PortletContext;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @version $Revision: 630 $
 */
public class FacesUtil {

   private static List<String> facesMappings = null;
   private static PortletContext portletContext;
   private static ExternalContext externalContext;

   public static String getViewIdFromPath(String url)
  {
    // Get a string that holds the path after the Context-Path through the
    // target

    // First remove the query string
    int i = url.indexOf("?");
    if (i != -1)
    {
      url = url.substring(0, i);
    }

    // Now remove up through the ContextPath
    String ctxPath = externalContext.getRequestContextPath();
    i = url.indexOf(ctxPath);
    if (i != -1)
    {
      url = url.substring(i + ctxPath.length());
    }

    String viewId = null;
    // Okay now figure out whether this is prefix or suffixed mapped
    if (isSuffixedMapped(url, facesMappings))
    {
      viewId =
          viewIdFromSuffixMapping(url, facesMappings, portletContext.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME));
    }
    else if (isPrefixedMapped(url, facesMappings))
    {
      viewId = viewIdFromPrefixMapping(url, facesMappings);
    }
    else
    {
      // Set to what follows the URL
      viewId = url;
    }
    return viewId;
  }

   private static boolean isSuffixedMapped(String url, List<String> mappings)
  {
    // see if the viewId terminates with an extension
    // if non-null value contains *.XXX where XXX is the extension
    String ext = extensionMappingFromViewId(url);
    return ext != null && mappings.contains(ext);
  }

   private static String extensionMappingFromViewId(String viewId)
  {
    // first remove/ignore any querystring
    int i = viewId.indexOf('?');
    if (i != -1)
    {
      viewId = viewId.substring(0, i);
    }

    int extLoc = viewId.lastIndexOf('.');

    if (extLoc != -1 && extLoc > viewId.lastIndexOf('/'))
    {
      StringBuilder sb = new StringBuilder("*");
      sb.append(viewId.substring(extLoc));
      return sb.toString();
    }
    return null;
  }

    private static String viewIdFromSuffixMapping(String url, List<String> mappings, String ctxDefault)
  {
    // replace extension with the DEFAULT_SUFFIX
    if (ctxDefault == null)
    {
      ctxDefault = ViewHandler.DEFAULT_SUFFIX;
    }

    int i = url.lastIndexOf(".");
    if (ctxDefault != null && i != -1)
    {
      if (ctxDefault.startsWith("."))
      {
        url = url.substring(0, i) + ctxDefault;
      }
      else
      {
        // shouldn't happen
        url = url.substring(0, i) + "." + ctxDefault;
      }
    }
    return url;
  }

   private static String viewIdFromPrefixMapping(String url, List<String> mappings)
  {
    for (int i = 0; i < mappings.size(); i++)
    {
      String prefix = null;
      String mapping = mappings.get(i);
      if (mapping.startsWith("/"))
      {
        int j = mapping.lastIndexOf("/*");
        if (j != -1)
        {
          prefix = mapping.substring(0, j);
        }
      }
      if (prefix != null && url.startsWith(prefix))
      {
        return url.substring(prefix.length());
      }
    }
    return null;
  }

   private static boolean isPrefixedMapped(String url, List<String> mappings)
  {
    for (int i = 0; i < mappings.size(); i++)
    {
      String prefix = null;
      String mapping = mappings.get(i);
      if (mapping.startsWith("/"))
      {
        int j = mapping.lastIndexOf("/*");
        if (j != -1)
        {
          prefix = mapping.substring(0, j);
        }
      }
      if (prefix != null && url.startsWith(prefix))
      {
        return true;
      }
    }
    return false;
  }
}
