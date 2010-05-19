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
package org.jboss.portletbridge.context;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author shura
 * 
 * 
 * 
 *         Class for create extended context maps - concatend real parameters
 *         map with
 * 
 *         additional from cocoon sitemap ...
 * 
 */
abstract class ContextConcatentedMap<K, V> extends ContextMap<K, V> {
	private Map<K, V> extendMap;

	private Set<Entry<K, V>> baseSet;

	/**
	 * 
	 * @param extendedMap
	 * 
	 */
	public ContextConcatentedMap(Map<K, V> extendedMap) {
		this.extendMap = extendedMap;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see java.util.Map#entrySet() use lasy creation , since used only in
	 * 
	 * static collection.
	 */
	public Set<Entry<K, V>> entrySet() {
		if (this.baseSet == null) {
			this.baseSet = super.entrySet();
			this.baseSet.addAll(this.extendMap.entrySet());
		}
		return this.baseSet;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key) {
		V result = this.extendMap.get(key);
		if (null != result) {
			return result;
		} else {
			return getBase(key);
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @return
	 * 
	 */
	protected abstract V getBase(Object key);
}
