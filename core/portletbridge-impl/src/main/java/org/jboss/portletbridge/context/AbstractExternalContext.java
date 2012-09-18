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
/*

 * Created on 25.09.2004

 *

 * Copyright 1999-2004 The Apache Software Foundation.

 *

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 *

 *      http://www.apache.org/licenses/LICENSE-2.0

 *

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */
package org.jboss.portletbridge.context;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;

import org.jboss.portletbridge.component.MultipartResourceRequest;
import org.jboss.portletbridge.context.flash.FlashContextFactory;
import org.jboss.portletbridge.context.map.ContextAttributesMap;
import org.jboss.portletbridge.context.map.EnumerationIterator;

/**
 * @author shura
 */
public abstract class AbstractExternalContext extends ExternalContext {

    /**
     * Request parameter to store current View Id.
     */
    public static final String VIEW_ID_PARAMETER = "org.jboss.portletbridge.VIEWID";

    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final String PORTLET_CONFIG_ATTRIBUTE = "org.jboss.portletbridge.CONFIG";
    public static final String INITIAL_REQUEST_ATTRIBUTES_NAMES = "org.jboss.portletbridge.REQUEST_PARAMETERS";
    public static final Object RENDER_POLICY_ATTRIBUTE = "org.jboss.portletbridge.RENDER_POLICY";
    public static final String PORTAL_USER_PRINCIPAL = "org.jboss.portletbridge.USER_PRINCIPAL";
    public static final String FACES_VIEW_STATE = "org.jboss.portletbridge.faces.ViewState";

    // TODO - optimization.
    private Map<String, Object> applicationMap;

    private Map<String, String> initParameterMap;

    private Map<String, String> requestHeaderMap = null;

    private Map<String, String[]> requestHeaderValues;

    private Map<String, Object> requestMap;

    private Map<String, String> requestParameterMap;

    private Map<String, String[]> requestParameterValuesMap;

    private Map<String, Object> sessionMap;

    private Object request;

    private Object response;

    // private Map<String,Object> actionSettings;

    private Object context;

    public static final String CONVERSATION_ID_PARAMETER = "conversationId";
    private Map<String, String> fallbackContentTypeMap = null;

    private enum ALLOWABLE_COOKIE_PROPERTIES {

        domain, maxAge, path, secure
    }

    /**
     *
     * @param context
     * @param request
     * @param response
     * @param defaultContext
     *            -
     *
     *            default implementation of <code>ExternalFacesContext</code>.
     *
     */
    public AbstractExternalContext(Object context, Object request, Object response) {
        super();
        this.context = context;
        this.request = request;
        this.response = response;

        fallbackContentTypeMap = new HashMap<String, String>(3, 1.0f);
        fallbackContentTypeMap.put("js", "text/javascript");
        fallbackContentTypeMap.put("css", "text/css");
        fallbackContentTypeMap.put("groovy", "application/x-groovy");
        fallbackContentTypeMap.put("properties", "text/plain");

    }

    protected abstract String getNamespace();

    public String encodeNamespace(String name) {

        return getNamespace() + name;
    }

    /**
     * @see javax.faces.context.ExternalContext#dispatch(java.lang.String)
     */
    public Map<String, Object> getApplicationMap() {
        if (this.applicationMap == null) {
            this.applicationMap = new ContextAttributesMap<Object>() {

                protected Enumeration<String> getEnumeration() {
                    return getContextAttributeNames();
                }

                protected Object getAttribute(String name) {
                    return getContextAttribute(name);
                }

                protected void setAttribute(String name, Object value) {
                    setContextAttribute(name, value);
                }

                protected void removeAttribute(String name) {
                    removeContextAttribute(name);
                }
            };
        }
        return this.applicationMap;
    }

    protected abstract void removeContextAttribute(String name);

    protected abstract void setContextAttribute(String name, Object value);

    protected abstract Object getContextAttribute(String name);

    protected abstract Enumeration<String> getContextAttributeNames();

    /**
     * @see javax.faces.context.ExternalContext#getAuthType()
     */
    public Object getContext() {
        return this.context;
    }

    /**
     * @see javax.faces.context.ExternalContext#getInitParameter(java.lang.String)
     */
    @Override
    public Map<String, String> getInitParameterMap() {
        if (this.initParameterMap == null) {
            this.initParameterMap = new ContextAttributesMap<String>() {

                protected String getAttribute(String name) {
                    return getInitParameter(name);
                }

                protected void setAttribute(String name, String value) {
                    throw new UnsupportedOperationException();
                }

                protected Enumeration<String> getEnumeration() {
                    return getInitParametersNames();
                }
            };
        }
        return this.initParameterMap;
    }

    /**
     * Hoock method for initialization parameters.
     *
     * @return
     */
    protected abstract Enumeration<String> getInitParametersNames();

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.ExternalContext#getRequest()
     */
    public Object getRequest() {
        return this.request;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.ExternalContext#setRequest(java.lang.Object)
     */
    public void setRequest(Object request) {
        this.requestHeaderMap = null;
        this.requestHeaderValues = null;
        this.requestMap = null;
        this.requestParameterMap = null;
        this.requestParameterValuesMap = null;
        this.sessionMap = null;
        this.request = request;
    }

    public Map<String, Object> getRequestCookieMap() {
        // Portlet environment don't have methods to use cookies.
        return Collections.emptyMap();
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see javax.faces.context.ExternalContext#getRequestHeaderMap()
     */
    public Map<String, String> getRequestHeaderMap() {
        if (this.requestHeaderMap == null) {
            this.requestHeaderMap = new ContextAttributesMap<String>() {

                protected Enumeration<String> getEnumeration() {
                    return getRequestHeaderNames();
                }

                protected String getAttribute(String name) {
                    if(request instanceof MultipartResourceRequest) {
                        if("faces-request".equalsIgnoreCase(name))
                            return "partial/ajax";
                    }
                    return getRequestHeader(name);
                }

                protected void setAttribute(String name, String value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.requestHeaderMap;
    }

    protected abstract String getRequestHeader(String name);

    protected abstract Enumeration<String> getRequestHeaderNames();

    public Map<String, String[]> getRequestHeaderValuesMap() {
        //
        if (this.requestHeaderValues == null) {
            this.requestHeaderValues = new ContextAttributesMap<String[]>() {

                protected Enumeration<String> getEnumeration() {
                    return getRequestHeaderNames();
                }

                protected String[] getAttribute(String name) {
                    return getRequestHeaderValues(name);
                }

                protected void setAttribute(String name, String[] value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.requestHeaderValues;
    }

    protected abstract String[] getRequestHeaderValues(String name);

    public Map<String, Object> getRequestMap() {
        if (this.requestMap == null) {
            this.requestMap = new ContextAttributesMap<Object>() {

                protected Enumeration<String> getEnumeration() {
                    return getRequestAttributeNames();
                }

                protected Object getAttribute(String name) {
                    return getRequestAttribute(name);
                }

                protected void setAttribute(String name, Object value) {
                    setRequestAttribute(name, value);
                }

                protected void removeAttribute(String name) {
                    removeRequestAttribute(name);
                }
            };
        }
        return this.requestMap;
    }

    protected abstract void removeRequestAttribute(String name);

    protected abstract void setRequestAttribute(String name, Object value);

    protected abstract Object getRequestAttribute(String name);

    protected abstract Enumeration<String> getRequestAttributeNames();

    public Map<String, String> getRequestParameterMap() {
        //
        if (this.requestParameterMap == null) {
            this.requestParameterMap = new ContextAttributesMap<String>() {

                protected Enumeration<String> getEnumeration() {
                    return enumerateRequestParameterNames();
                }

                protected String getAttribute(String name) {
                    return getRequestParameter(name);
                }

                protected void setAttribute(String name, String value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.requestParameterMap;
    }

    protected abstract String getRequestParameter(String name);

    protected abstract Enumeration<String> enumerateRequestParameterNames();

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.ExternalContext#getRequestParameterNames()
     */
    public Iterator<String> getRequestParameterNames() {
        return new EnumerationIterator<String>(enumerateRequestParameterNames());
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see javax.faces.context.ExternalContext#getRequestParameterValuesMap()
     */
    public Map<String, String[]> getRequestParameterValuesMap() {
        if (this.requestParameterValuesMap == null) {
            this.requestParameterValuesMap = new ContextAttributesMap<String[]>() {

                protected Enumeration<String> getEnumeration() {
                    return enumerateRequestParameterNames();
                }

                protected String[] getAttribute(String name) {
                    return getRequestParameterValues(name);
                }

                protected void setAttribute(String name, String[] value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.requestParameterValuesMap;
    }

    protected abstract String[] getRequestParameterValues(String name);

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see javax.faces.context.ExternalContext#getResponse()
     */
    @Override
    public void setResponse(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return this.response;
    }

    /*
     *
     * (non-Javadoc)
     *
     *
     *
     * @see javax.faces.context.ExternalContext#getSessionMap()
     */
    public Map<String, Object> getSessionMap() {
        if (this.sessionMap == null) {
            this.sessionMap = new ContextAttributesMap<Object>() {

                protected Enumeration<String> getEnumeration() {
                    return getSessionAttributeNames();
                }

                protected Object getAttribute(String name) {
                    return getSessionAttribute(name);
                }

                protected void setAttribute(String name, Object value) {
                    setSessionAttribute(name, value);
                }

                protected void removeAttribute(String name) {
                    removeSessionAttribute(name);
                }

            };
        }
        return this.sessionMap;
    }

    protected abstract void removeSessionAttribute(String name);

    protected abstract void setSessionAttribute(String name, Object value);

    protected abstract Object getSessionAttribute(String name);

    protected abstract Enumeration<String> getSessionAttributeNames();

    @Override
    public void addResponseCookie(String name, String value, Map<String, Object> properties) {

        Cookie cookie = new Cookie(name, value);
        if (properties != null && properties.size() != 0) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String key = entry.getKey();
                ALLOWABLE_COOKIE_PROPERTIES p = ALLOWABLE_COOKIE_PROPERTIES.valueOf(key);
                Object v = entry.getValue();
                switch (p) {
                    case domain:
                        cookie.setDomain((String) v);
                        break;
                    case maxAge:
                        cookie.setMaxAge((Integer) v);
                        break;
                    case path:
                        cookie.setPath((String) v);
                        break;
                    case secure:
                        cookie.setSecure((Boolean) v);
                        break;
                    default:
                        throw new IllegalStateException(); // shouldn't happen
                }
            }
        }
        ((PortletResponse) response).addProperty(cookie);
    }

    @Override
    public Flash getFlash() {
        return FlashContextFactory.getFlashForBridge(this);
    }

    public String getFallbackMimeType(String file) {

        if (file == null || file.length() == 0) {
            return null;
        }
        int idx = file.lastIndexOf('.');
        if (idx == -1) {
            return null;
        }
        String extension = file.substring(idx + 1);
        if (extension.length() == 0) {
            return null;
        }
        return fallbackContentTypeMap.get(extension);
    }

}
