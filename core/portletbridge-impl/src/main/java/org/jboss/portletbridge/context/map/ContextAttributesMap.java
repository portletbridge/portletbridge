/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.context.map;


/**
 * Abstract base class for all attributes/sessions/cookies maps in external contexts.
 *
 * @author asmirnov
 */
public abstract class ContextAttributesMap<V> extends ContextMap<String, V> {

    @Override
    public V get(Object key) {
        if (null == key) {
            throw new NullPointerException();
        }
        return getAttribute(key.toString());
    }

    @Override
    public boolean containsKey(Object key) {
        if (null == key) {
            throw new NullPointerException();
        }
        return null != getAttribute(key.toString());
    }

    @Override
    public V put(String key, V value) {
        if (null == key) {
            throw new NullPointerException();
        }
        String stringKey = key.toString();
        V oldValue = getAttribute(stringKey);
        setAttribute(stringKey, value);
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        if (null == key) {
            throw new NullPointerException();
        }
        String stringKey = key.toString();
        V oldValue = getAttribute(stringKey);
        removeAttribute(stringKey);
        return oldValue;
    }

    protected abstract V getAttribute(String name);

    protected abstract void setAttribute(String name, V value);

    protected void removeAttribute(String name) {
        setAttribute(name, null);
    }
}
