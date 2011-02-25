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
package org.jboss.portletbridge.preference;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.faces.preference.Preference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wesley Hales
 */
public class PreferenceImpl implements Preference
{
   private PortletPreferences portletPreferences;
   private String preferenceKey = null;
  
   public PreferenceImpl(PortletPreferences preferences)
   {
    super();
    portletPreferences = preferences;
   }

   public PreferenceImpl(PortletPreferences preferences, String name)
   {
    this(preferences);
    preferenceKey = name;
   }

   public void setName(String name)
   {
      preferenceKey = name;
   }

   public String getName()
   {
      return preferenceKey;
   }

   public void setValue(String value) throws ReadOnlyException
   {
      portletPreferences.setValue(preferenceKey, value);
   }

   public String getValue()
   {
      return portletPreferences.getValue(preferenceKey, null);
   }

   public void setValues(String[] values) throws ReadOnlyException
   {
      portletPreferences.setValues(preferenceKey, values);
   }

   public List<String> getValues()
   {
      return Arrays.asList(portletPreferences.getValues(preferenceKey, null));
   }

   public boolean isReadOnly()
   {
      return portletPreferences.isReadOnly(preferenceKey);
   }

   public void reset() throws ReadOnlyException
   {
      portletPreferences.reset(preferenceKey);
   }
}
