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
/*

 * Created on 25.09.2004

 *

 * Copyright 1999-2004 The Apache Software Foundation.

 * 

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 * 

 *      http://www.apache.org/licenses/LICENSE-2.0

 * 

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */
package org.jboss.portletbridge.context;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.portlet.PortletResponse;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

/**
 * 
 * Implementation for <code>FacesContext</code> in Portlet Environment.
 * 
 * @author shura
 * 
 * 
 */
public class FacesContextImpl extends FacesContextWrapper {

	private final FacesContext parent;
	public FacesContextImpl(FacesContext parent) {
		this.parent = parent;
		setCurrentInstance(this);
    }

	@Override
    public FacesContext getWrapped() {
	    // TODO Auto-generated method stub
	    return parent;
    }

	  public void setViewRoot(UIViewRoot viewRoot) {
	      super.setViewRoot(viewRoot);
	      // JSR-301 PLT 6.1.2 FacesContext
	      if (null != viewRoot.getClass().getAnnotation(PortletNamingContainer.class) || viewRoot instanceof PortletNamingContainerUIViewRoot) {
	         Object response = getExternalContext().getResponse();
	         if (response instanceof PortletResponse) {
	            PortletResponse portletResponse = (PortletResponse) response;
	            portletResponse.setProperty("X-JAVAX-PORTLET-IS-NAMESPACED",
	                  "true");
	         }
	      }
	   }

 }
