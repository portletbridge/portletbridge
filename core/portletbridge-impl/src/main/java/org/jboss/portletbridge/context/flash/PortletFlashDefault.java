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
package org.jboss.portletbridge.context.flash;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

/**
 * @author asmirnov, kenfinnigan
 */
public class PortletFlashDefault extends Flash {

    public static final String ATTRIBUTE_NAME = "pcsfcff";

    private boolean keepMessages = false;
    private boolean redirect = false;
    private final Map<String, Object> flashParameters;

    public PortletFlashDefault() {
        flashParameters = new ConcurrentHashMap<String, Object>();
    }

    /**
     * @see javax.faces.context.Flash#doPostPhaseActions(javax.faces.context.FacesContext)
     */
    @Override
    public void doPostPhaseActions(FacesContext ctx) {
        // No Op
    }

    /**
     * @see javax.faces.context.Flash#doPrePhaseActions(javax.faces.context.FacesContext)
     */
    @Override
    public void doPrePhaseActions(FacesContext ctx) {
        // No Op
    }

    /**
     * @see javax.faces.context.Flash#isKeepMessages()
     */
    @Override
    public boolean isKeepMessages() {
        return keepMessages;
    }

    /**
     * @see javax.faces.context.Flash#setKeepMessages(boolean)
     */
    @Override
    public void setKeepMessages(boolean keepMsgs) {
        keepMessages = keepMsgs;
    }

    /**
     * @see javax.faces.context.Flash#isRedirect()
     */
    @Override
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * @see javax.faces.context.Flash#setRedirect(boolean)
     */
    @Override
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    /**
     * @see javax.faces.context.Flash#keep(java.lang.String)
     */
    @Override
    public void keep(String key) {
    }

    /**
     * @see javax.faces.context.Flash#putNow(java.lang.String, java.lang.Object)
     */
    @Override
    public void putNow(String key, Object value) {
        flashParameters.put(key, value);
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        flashParameters.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return flashParameters.containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return flashParameters.containsValue(value);
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return flashParameters.entrySet();
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        return flashParameters.get(key);
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return flashParameters.isEmpty();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<String> keySet() {
        return flashParameters.keySet();
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(String key, Object value) {
        return flashParameters.put(key, value);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends String, ? extends Object> params) {
        flashParameters.putAll(params);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return flashParameters.remove(key);
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return flashParameters.size();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<Object> values() {
        return flashParameters.values();
    }

}
