/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.portletbridge.lifecycle;

import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

/**
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/01/09 18:57:12 $
 *
 */
public class PortletLifecycleFactory extends LifecycleFactory {

   private LifecycleFactory _defaultFactory;


//   private Map _lifecycles = new HashMap();


   /**
    * @param defaultFactory
    */
   public PortletLifecycleFactory(LifecycleFactory defaultFactory) {
      super();
      this._defaultFactory = defaultFactory;
      this._defaultFactory.addLifecycle(PortletLifecycle.FIX_PORTLET_LIFECYCLE,
              new PortletLifecycle());
   }


   /**
    * @param lifecycleId
    * @param lifecycle
    * @see javax.faces.lifecycle.LifecycleFactory#addLifecycle(java.lang.String, javax.faces.lifecycle.Lifecycle)
    */
   public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
      _defaultFactory.addLifecycle(lifecycleId, lifecycle);
   }


   /**
    * @param lifecycleId
    * @return
    * @see javax.faces.lifecycle.LifecycleFactory#getLifecycle(java.lang.String)
    */
   public Lifecycle getLifecycle(String lifecycleId) {
      return _defaultFactory.getLifecycle(lifecycleId);
   }


   /**
    * @return
    * @see javax.faces.lifecycle.LifecycleFactory#getLifecycleIds()
    */
   public Iterator<String> getLifecycleIds() {
      return _defaultFactory.getLifecycleIds();
   }


}
