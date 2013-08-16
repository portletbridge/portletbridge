/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Essentially identical to {@link javax.faces.context.FlashWrapper} provided within JSF 2.2,
 * but creating our own version enables us to use it with JSF 2.1 as well.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class PortletFlashWrapper extends Flash implements FacesWrapper<Flash> {
    @Override
    public abstract Flash getWrapped();

    /**
     * The default behavior of this method
     * is to call {@link Flash#doPostPhaseActions(javax.faces.context.FacesContext)} on the
     * wrapped {@link Flash} object.
     */
    @Override
    public void doPostPhaseActions(FacesContext ctx) {
        getWrapped().doPostPhaseActions(ctx);

    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#doPrePhaseActions(FacesContext)} on the
     * wrapped {@link Flash} object.
     */
    @Override
    public void doPrePhaseActions(FacesContext ctx) {
        getWrapped().doPrePhaseActions(ctx);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#isKeepMessages()} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public boolean isKeepMessages() {
        return getWrapped().isKeepMessages();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#isRedirect()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public boolean isRedirect() {
        return getWrapped().isRedirect();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#keep(String)} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public void keep(String key) {
        getWrapped().keep(key);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#putNow(String, Object)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public void putNow(String key, Object value) {
        getWrapped().putNow(key, value);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#setKeepMessages(boolean)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public void setKeepMessages(boolean newValue) {
        getWrapped().setKeepMessages(newValue);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#setRedirect(boolean)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public void setRedirect(boolean newValue) {
        getWrapped().setRedirect(newValue);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#clear()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public void clear() {
        getWrapped().clear();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#containsKey(Object)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public boolean containsKey(Object key) {
        return getWrapped().containsKey(key);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#containsValue(Object)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public boolean containsValue(Object value) {
        return getWrapped().containsValue(value);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#entrySet()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return getWrapped().entrySet();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#get(Object)} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public Object get(Object key) {
        return getWrapped().get(key);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#isEmpty()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public boolean isEmpty() {
        return getWrapped().isEmpty();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#keySet()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public Set<String> keySet() {
        return getWrapped().keySet();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#put} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public Object put(String key, Object value) {
        return getWrapped().put(key, value);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#putAll(java.util.Map)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        getWrapped().putAll(m);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#remove(Object)} on the wrapped
     * {@link Flash} object.
     */
    @Override
    public Object remove(Object key) {
        return getWrapped().remove(key);
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#size()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public int size() {
        return getWrapped().size();
    }

    /**
     * The default behavior of this method
     * is to call {@link Flash#values()} on the wrapped {@link Flash}
     * object.
     */
    @Override
    public Collection<Object> values() {
        return getWrapped().values();
    }
}
