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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.PhaseId;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.sun.faces.context.flash.ELFlash;

/**
 * @author asmirnov, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletFlash extends Flash {

    static final String FLASH_ATTRIBUTE_NAME = "pbrf";

    private ELFlash wrappedFlash = null;

    private boolean servletResponse = false;

    private PortletFlash(ExternalContext externalContext) {
        wrappedFlash = ELFlash.getFlash(externalContext, true);
    }

    public static PortletFlash getFlash(ExternalContext externalContext, boolean create) {
        Map<String, Object> appMap = externalContext.getApplicationMap();
        PortletFlash flash = (PortletFlash) appMap.get(FLASH_ATTRIBUTE_NAME);
        if (null == flash && create) {
            synchronized (externalContext.getContext()) {
                if (null == (flash = (PortletFlash) appMap.get(FLASH_ATTRIBUTE_NAME))) {
                    flash = new PortletFlash(externalContext);
                    appMap.put(FLASH_ATTRIBUTE_NAME, flash);
                }
            }
        }
        return flash;
    }

    public boolean isServletResponse() {
        return servletResponse;
    }

    @Override
    public void clear() {
        wrappedFlash.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return wrappedFlash.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return wrappedFlash.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return wrappedFlash.entrySet();
    }

    @Override
    public Object get(Object key) {
        return wrappedFlash.get(key);
    }

    @Override
    public boolean isEmpty() {
        return wrappedFlash.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return wrappedFlash.keySet();
    }

    @Override
    public Object put(String key, Object value) {
        servletResponse = true;
        Object resp = wrappedFlash.put(key, value);
        servletResponse = false;
        return resp;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        wrappedFlash.putAll(map);
    }

    @Override
    public Object remove(Object key) {
        return wrappedFlash.remove(key);
    }

    @Override
    public int size() {
        return wrappedFlash.size();
    }

    @Override
    public Collection<Object> values() {
        return wrappedFlash.values();
    }

    @Override
    public boolean isKeepMessages() {
        return wrappedFlash.isKeepMessages();
    }

    @Override
    public void setKeepMessages(boolean newValue) {
        wrappedFlash.setKeepMessages(newValue);
    }

    @Override
    public boolean isRedirect() {
        return wrappedFlash.isRedirect();
    }

    @Override
    public void setRedirect(boolean newValue) {
        wrappedFlash.setRedirect(newValue);
    }

    @Override
    public void putNow(String key, Object value) {
        wrappedFlash.putNow(key, value);
    }

    @Override
    public void keep(String key) {
        wrappedFlash.keep(key);
    }

    @Override
    public void doPrePhaseActions(FacesContext ctx) {
        PhaseId currentPhase = ctx.getCurrentPhaseId();
        boolean resetPhase = false;

        if (Bridge.PortletPhase.RENDER_PHASE == BridgeUtil.getPortletRequestPhase()
                && currentPhase.equals(PhaseId.RENDER_RESPONSE)) {
            // Need to trick ELFlash into thinking it's in the Restore View Phase otherwise the Previous Flash Manager
            // will not be retrieved from the Cookie
            ctx.setCurrentPhaseId(PhaseId.RESTORE_VIEW);
            resetPhase = true;
        }

        wrappedFlash.doPrePhaseActions(ctx);

        if (resetPhase) {
            ctx.setCurrentPhaseId(PhaseId.RENDER_RESPONSE);
        }
    }

    @Override
    public void doPostPhaseActions(FacesContext ctx) {
        if (Bridge.PortletPhase.RENDER_PHASE == BridgeUtil.getPortletRequestPhase()) {
            servletResponse = true;
            wrappedFlash.doLastPhaseActions(ctx, false);
            servletResponse = false;
        }
    }

    public void doLastPhaseActions(FacesContext context, boolean isRedirect) {
        servletResponse = true;
        wrappedFlash.doLastPhaseActions(context, isRedirect);
        servletResponse = false;
    }

}
