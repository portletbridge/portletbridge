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
package org.jboss.portletbridge.bridge.scope;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.faces.annotation.BridgePreDestroy;

import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactoryImpl;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeUtil;

/**
 * This class keeps all request attributes that are required to be stored between portlet requests. These parameters are
 * described in the chapter 5.1.2 "Managing Lifecycle State".
 * 
 * @author asmirnov, kenfinnigan
 */
public class BridgeRequestScopeImpl extends ConcurrentHashMap<String, Object> implements BridgeRequestScope {

    private static final long serialVersionUID = -5796085561862187555L;

    private static final BridgeLogger logger = BridgeLoggerFactoryImpl.getLogger(BridgeRequestScopeImpl.class.getName());

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = .75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;

    private String uniqRequestScopeId;
    private String portletName;
    private String sessionId;
    private String viewId;
    private String portletMode;
    private Vector<String> excludedEntries;

    public BridgeRequestScopeImpl(String portletName, String sessionId, String viewId, String portletMode) {
        super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        initScope(portletName, sessionId, viewId, portletMode);
    }

    public BridgeRequestScopeImpl(String portletName, String sessionId, String viewId, String portletMode, int initialCapacity) {
        super(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        initScope(portletName, sessionId, viewId, portletMode);
    }

    public BridgeRequestScopeImpl(String portletName, String sessionId, String viewId, String portletMode, int initialCapacity,
            float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        initScope(portletName, sessionId, viewId, portletMode);
    }

    public BridgeRequestScopeImpl(String portletName, String sessionId, String viewId, String portletMode,
            Map<String, Object> requestScopeDataMap) {
        super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        initScope(portletName, sessionId, viewId, portletMode);
        putAll(requestScopeDataMap);
    }

    private void initScope(String portletName, String sessionId, String viewId, String portletMode) {
        this.portletName = portletName;
        this.sessionId = sessionId;
        this.viewId = viewId;
        this.portletMode = portletMode;
    }

    public String getId() {
        if (null == this.uniqRequestScopeId) {
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            this.uniqRequestScopeId = new StringBuffer(BridgeRequestScopeUtil.generateBridgeRequestScopeIdPrefix(portletName,
                    sessionId, viewId, portletMode)).append(':').append(Long.toString(timeInMillis)).toString();
        }
        return this.uniqRequestScopeId;
    }

    public String getPortletName() {
        return this.portletName;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getViewId() {
        return this.viewId;
    }

    public String getPortletMode() {
        return this.portletMode;
    }

    public void setExcludedEntries(List<String> excludedNames) {
        this.excludedEntries = new Vector<String>(excludedNames);
    }

    public void addExcludedEntries(List<String> excludedNames) {
        if (null != this.excludedEntries) {
            this.excludedEntries.addAll(excludedNames);
        } else {
            this.setExcludedEntries(excludedNames);
        }
    }

    public List<String> getExcludedEntries(List<String> excludedNames) {
        return this.excludedEntries;
    }

    public boolean isExcluded(String key, Object value) {
        return BridgeRequestScopeUtil.isExcluded(key, value, this.excludedEntries);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        if (!isExcluded(key, value)) {
            return callPreDestroy(super.putIfAbsent(key, value));
        }
        return null;
    }

    /**
     * Only put the value into the map if it isn't excluded.
     */
    @Override
    public Object put(String key, Object value) {
        if (!isExcluded(key, value)) {
            return callPreDestroy(super.put(key, value));
        }
        return null;
    }

    /**
     * Only put the values from the map parameter into the map if they aren't excluded.
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        for (Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(String key) {
        return callPreDestroy(super.remove(key));
    }

    public boolean remove(String key, Object value) {
        callPreDestroy(value);
        return super.remove(key, value);
    }

    @Override
    public Object replace(String key, Object value) {
        return callPreDestroy(super.replace(key, value));
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        if (!super.replace(key, oldValue, newValue)) {
            return false;
        }
        callPreDestroy(oldValue);
        return true;
    }

    @Override
    public void clear() {
        for (Object obj : values()) {
            callPreDestroy(obj);
        }
        super.clear();
    }

    /**
     * Per JSR-329 6.8.2, when terminating the Bridge Request Scope, any managed attributes with public, no-arg, void return
     * methods annotated with BridgePreDestroy need to be called.
     * 
     * @param obj Object requiring call to PreDestroy annotated methods
     * @return Original Object
     */
    private Object callPreDestroy(Object obj) {
        if (null != obj) {
            for (Method method : obj.getClass().getMethods()) {
                // Check for Method with BridgePreDestroy annotation
                if (method.isAnnotationPresent(BridgePreDestroy.class)) {
                    // Check for Public method only
                    if (!Modifier.isPublic(method.getModifiers())) {
                        continue;
                    }

                    // Check for no arg method
                    if (method.getParameterTypes().length > 0) {
                        continue;
                    }

                    // Check for void return type
                    if (method.getReturnType() != Void.class) {
                        continue;
                    }

                    try {
                        // Invoke pre destroy method
                        method.invoke(obj, (Object) null);
                    } catch (Exception e) {
                        logger.log(Level.ERROR, "Error invoking @PreDestroy method: " + method.getName() + " on: "
                                + obj.getClass().getName(), e);
                    }
                }
            }
        }
        return obj;
    }

}
