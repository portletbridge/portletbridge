/******************************************************************************
 * $Id$
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
package org.jboss.portletbridge.context.flash;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

/**
 * @author asmirnov
 *
 */
public class PortletFlash extends Flash {
	
	public static final String ATTRIBUTE_NAME = "pcsfcff";

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#doPostPhaseActions(javax.faces.context.FacesContext)
	 */
	@Override
	public void doPostPhaseActions(FacesContext ctx) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#doPrePhaseActions(javax.faces.context.FacesContext)
	 */
	@Override
	public void doPrePhaseActions(FacesContext ctx) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#isKeepMessages()
	 */
	@Override
	public boolean isKeepMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#isRedirect()
	 */
	@Override
	public boolean isRedirect() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#keep(java.lang.String)
	 */
	@Override
	public void keep(String key) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#putNow(java.lang.String, java.lang.Object)
	 */
	@Override
	public void putNow(String key, Object value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#setKeepMessages(boolean)
	 */
	@Override
	public void setKeepMessages(boolean newValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.faces.context.Flash#setRedirect(boolean)
	 */
	@Override
	public void setRedirect(boolean newValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Object> t) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}
	//============================================================
	// public constants

	//============================================================
	// private constants

	//============================================================
	// static variables

	//============================================================
	// instance variables

	//============================================================
	// constructors

	//============================================================
	// public methods

	//============================================================
	// non-public methods

	//============================================================
	// inner classes

}
