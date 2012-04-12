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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author shura
 * 
 * 
 * 
 *         Base class for All maps in <code>ExternalContextCocoonImpl</code> get from
 * 
 *         Sun Jsf-ri
 * 
 * 
 * 
 */
abstract class ContextMap<K, V> extends AbstractMap<K, V> {

    private transient volatile Set<K> keySet;

    private transient volatile Set<Entry<K, V>> entrySet;

    /*
     * 
     * (non-Javadoc)
     * 
     * 
     * 
     * @see java.util.Map#entrySet()
     */
    public Set<Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new AbstractSet<Entry<K, V>>() {

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return new Iterator<Entry<K, V>>() {

                        private Enumeration<K> enumeration = getEnumeration();

                        public boolean hasNext() {
                            return enumeration.hasMoreElements();
                        }

                        public Entry<K, V> next() {
                            K key = enumeration.nextElement();
                            return new ContextEntry<K, V>(key, get(key));
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                    };
                }

                @Override
                public int size() {
                    return ContextMap.this.size();
                }

            };
        }

        return this.entrySet;
    }

    protected boolean isValidParameter(String paramName) {
        return true;
    }

    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new EnumerationIterator<K>(getEnumeration());
                }

                public int size() {
                    return ContextMap.this.size();
                }
            };
        }
        return this.keySet;
    }

    public Collection<V> values() {
        return super.values();
    }

    /**
     * 
     * 
     * 
     * Template metod - all maps in ExternalFacesContext creates Set from
     * 
     * parameters <code>Enumeration</code>
     * 
     * 
     * 
     * @return enumeration for current map.
     * 
     */
    protected abstract Enumeration<K> getEnumeration();

    // Unsupported by all Maps.
    public void clear() {
        throw new UnsupportedOperationException();
    }

    // public void putAll(Map t) {
    // for (Iterator entries = t.entrySet().iterator(); entries.hasNext();) {
    // Map.Entry entry = (Entry) entries.next();
    // put(entry.getKey(),entry.getValue());
    // }
    // }

    // Supported by maps if overridden
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    static class ContextEntry<K, V> implements Map.Entry<K, V> {
        // immutable Entry
        private final K key;

        private final V value;

        ContextEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        // No support of setting the value
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof ContextMap)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @return
     */
    public int size() {
        Enumeration<?> enumeration = getEnumeration();
        int size = 0;
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            size++;
        }
        ;
        return size;
    }
}