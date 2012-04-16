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
package org.jboss.portletbridge.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides a {@link java.util.Map} style interface for managing a Least Recently Used (LRU) cache. Note that
 * it extends {@link LinkedHashMap} which is not synchronized. Therefore instances of this class should be wrapped by
 * calling {@link Collections#synchronizedMap(java.util.Map)}.
 *
 * @author Waldemar Kłaczyński, kenfinnigan
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -1808002146637448020L;

    private int maxCapacity;

    public LRUMap(int maxCapacity) {
        super(maxCapacity, 1.0f, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return (size() > maxCapacity);
    }

}
