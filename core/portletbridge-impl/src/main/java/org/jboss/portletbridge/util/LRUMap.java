/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.portletbridge.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Waldemar Kłaczyński
 */
public class LRUMap<K,V> extends LinkedHashMap<K,V> {

    private int maxCapacity;

    public LRUMap(int maxCapacity) {
        super(maxCapacity, 1.0f, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return (size() > maxCapacity);
    }

}
