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
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.render.ResponseStateManager;
import javax.portlet.BaseURL;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;

import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.controller.BridgeController;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.context.map.EnumerationIterator;

/**
 * Version of the {@link ExternalContext} for a Portlet request. {@link FacesContextFactory} will create instance of this class
 * for a portal <code>action</code> phase.
 *
 * @author asmirnov
 */
public abstract class PortletExternalContextImpl extends AbstractExternalContext {

    public static final String SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
    public static final String PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
    public static final String ACTION_URL_DO_NOTHITG = "/JBossPortletBridge/actionUrl/do/nothing";
    public static final String RESOURCE_URL_DO_NOTHITG = "/JBossPortletBridge/resourceUrl/do/nothing";
    public static final String PARTIAL_URL_DO_NOTHITG = "/JBossPortletBridge/resourceUrl/do/nothing";
    public static final String WSRP_REWRITE = "wsrp_rewrite?";

    private String namespace;
    private String servletPath = null;
    private String pathInfo = null;
    private String servletMappingSuffix;
    private String defaultJsfSuffix;
    private String servletMappingPrefix;
    private String viewId;
    private boolean hasNavigationRedirect = false;
    protected final Map<String, Map<String, String[]>> encodedActionUrlParameters = new HashMap<String, Map<String, String[]>>();
    private Map<String, String[]> extraRequestParameters = new HashMap<String, String[]>();
    protected BridgeContext bridgeContext;

    protected String acceptHeader;
    protected String acceptLangHeader;
    protected String contentType;
    protected String contentLength;

    enum Scheme {
        action, render, resource
    }

    public PortletExternalContextImpl(PortletContext context, PortletRequest request, PortletResponse response) {
        super(context, request, response);

        bridgeContext = BridgeContext.getCurrentInstance();
        if (null == bridgeContext) {
            throw new FacesException("No BridgeContext instance found");
        }

        String defaultRenderKitId = bridgeContext.getBridgeConfig().getDefaultRenderKitId();
        if (null != defaultRenderKitId && null == request.getParameter(ResponseStateManager.RENDER_KIT_ID_PARAM)) {
            extraRequestParameters.put(ResponseStateManager.RENDER_KIT_ID_PARAM, new String[] { defaultRenderKitId });
        }

        BridgeRequestScope scope = bridgeContext.getBridgeScope();

        String viewStateParam = request.getParameter(ResponseStateManager.VIEW_STATE_PARAM);
        if (null == viewStateParam) {
            if (null != scope) {
                viewStateParam = (String) scope.get(FACES_VIEW_STATE);
                if (null != viewStateParam) {
                    extraRequestParameters.put(ResponseStateManager.VIEW_STATE_PARAM, new String[] { viewStateParam });
                }
            }
        }

        if (null != scope) {
            // Restore Action Parameters
            @SuppressWarnings("unchecked")
            Map<String, String[]> params = (Map<String, String[]>) scope.get(BridgeController.ACTION_PARAMETERS);
            if (null != params && params.size() > 0) {
                extraRequestParameters.putAll(params);
            }
        }
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
        // Do nothing
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        // Do nothing
    }

    @Override
    public void setRequest(Object request) {
        super.setRequest(request);

        if (null != viewId) {
            calculateViewId();
        }
    }

    @Override
    public void redirect(String url) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getResponseCharacterEncoding() {
        throw new IllegalStateException(
                "PortletExternalContextImpl.getResponseCharacterEncoding(): Response must be a MimeResponse");
    }

    @Override
    public String getResponseContentType() {
        throw new IllegalStateException("PortletExternalContextImpl.getResponseContentType(): Response must be a MimeResponse");
    }

    @Override
    public String getRequestContentType() {
        return null;
    }

    @Override
    public PortletContext getContext() {
        return (PortletContext) super.getContext();
    }

    @Override
    public PortletRequest getRequest() {
        return (PortletRequest) super.getRequest();
    }

    @Override
    public PortletResponse getResponse() {
        return (PortletResponse) super.getResponse();
    }

    public String getInitParameter(String name) {
        return getContext().getInitParameter(name);
    }

    protected String getNamespace() {
        if (null == namespace) {
            namespace = getResponse().getNamespace();
        }
        return namespace;
    }

    public URL getResource(String path) throws MalformedURLException {
        return getContext().getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return getContext().getResourceAsStream(path);
    }

    public Set<String> getResourcePaths(String path) {
        return getContext().getResourcePaths(path);
    }

    protected Enumeration<String> enumerateRequestParameterNames() {
        List<String> names = new ArrayList<String>();
        Enumeration<String> paramNames = getRequest().getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            names.add(name);
        }
        names.addAll(extraRequestParameters.keySet());
        return Collections.enumeration(names);
    }

    protected Object getContextAttribute(String name) {
        return getContext().getAttribute(name);
    }

    protected Enumeration<String> getContextAttributeNames() {
        return getContext().getAttributeNames();
    }

    protected Enumeration<String> getInitParametersNames() {
        return getContext().getInitParameterNames();
    }

    protected Object getRequestAttribute(String name) {
        if (PATH_INFO_ATTRIBUTE.equals(name)) {
            return getRequestPathInfo();
        } else if (SERVLET_PATH_ATTRIBUTE.equals(name)) {
            return getRequestServletPath();
        } else {
            return getRequest().getAttribute(name);
        }
    }

    protected Enumeration<String> getRequestAttributeNames() {
        return getRequest().getAttributeNames();
    }

    protected String[] getRequestParameterValues(String name) {
        String[] temp = getRequest().getParameterValues(name);
        if (null == temp || temp.length == 0) {
            temp = extraRequestParameters.get(name);
        }
        return temp;
    }

    protected void constructAcceptLanguageHeader() {
        Enumeration<Locale> locales = getRequest().getLocales();
        StringBuilder acceptLangHeader = new StringBuilder(64);

        boolean found = false;
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            if (locale != null) {
                if (found) {
                    acceptLangHeader.append(',');
                } else {
                    found = true;
                }

                String temp = locale.getLanguage();
                if (temp.length() > 0) {
                    acceptLangHeader.append(temp);
                    temp = locale.getCountry();
                    if (temp.length() > 0) {
                        acceptLangHeader.append('-');
                        acceptLangHeader.append(temp);
                    }
                }
            }
        }

        this.acceptLangHeader = found ? acceptLangHeader.toString() : null;
    }

    protected void constructAcceptHeader() {
        Enumeration<String> contentTypes = getRequest().getResponseContentTypes();
        StringBuilder acceptHeader = new StringBuilder(64);

        boolean found = false;
        while (contentTypes.hasMoreElements()) {
            String type = contentTypes.nextElement();
            if (type != null) {
                if (found) {
                    acceptHeader.append(',');
                } else {
                    found = true;
                }

                acceptHeader.append(type);
            }
        }

        this.acceptHeader = found ? acceptHeader.toString() : null;
    }

    protected void constructContentType() {
        StringBuilder contentTypeBuilder = new StringBuilder(64);

        String contentType = ((ClientDataRequest) getRequest()).getContentType();
        String charset = ((ClientDataRequest) getRequest()).getCharacterEncoding();

        if (null != contentType) {
            if (null != charset) {
                int index = contentType.indexOf(";");
                if (index < 0) {
                    contentTypeBuilder.append(contentType);
                } else {
                    contentTypeBuilder.append(contentType, 0, index);
                }
                contentTypeBuilder.append("; charset=");
                contentTypeBuilder.append(charset);

                this.contentType = contentTypeBuilder.toString();
            }
        }
        this.contentType = null;
    }

    protected void constructContentLength() {
        int contentLength = ((ClientDataRequest) getRequest()).getContentLength();

        if (contentLength != -1) {
            this.contentLength = String.valueOf(contentLength);
        }
    }

    protected String getRequestHeader(String name) {
        if ("ACCEPT".equalsIgnoreCase(name)) {
            if (null == acceptHeader) {
                constructAcceptHeader();
            }
            return acceptHeader;
        }
        if ("ACCEPT-LANGUAGE".equalsIgnoreCase(name)) {
            if (null == acceptLangHeader) {
                constructAcceptLanguageHeader();
            }
            return acceptLangHeader;
        }
        if ("CONTENT-TYPE".equalsIgnoreCase(name)) {
            return null;
        }
        if ("CONTENT-LENGTH".equalsIgnoreCase(name)) {
            return null;
        }

        String headerValue = getRequest().getProperty(name);
        if (null == headerValue) {
            // HACK - GateIn converts all request header names to the lower case.
            headerValue = getRequest().getProperty(name.toLowerCase());
        }
        return headerValue;
    }

    protected Enumeration<String> getRequestHeaderNames() {
        List<String> names = new ArrayList<String>();
        Enumeration<String> propNames = getRequest().getPropertyNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            names.add(name);
        }
        names.add("ACCEPT");
        names.add("ACCEPT-LANGUAGE");
        return Collections.enumeration(names);
    }

    protected String[] getRequestHeaderValues(String name) {
        if ("ACCEPT".equalsIgnoreCase(name)) {
            if (null == acceptHeader) {
                constructAcceptHeader();
            }
            return new String[] { acceptHeader };
        }
        if ("ACCEPT-LANGUAGE".equalsIgnoreCase(name)) {
            if (null == acceptLangHeader) {
                constructAcceptLanguageHeader();
            }
            return new String[] { acceptLangHeader };
        }
        if ("CONTENT-TYPE".equalsIgnoreCase(name)) {
            return null;
        }
        if ("CONTENT-LENGTH".equalsIgnoreCase(name)) {
            return null;
        }

        Enumeration<String> properties = getRequest().getProperties(name);
        if (!properties.hasMoreElements()) {
            // HACK - GateIn converts all request header names to the lower case.
            properties = getRequest().getProperties(name.toLowerCase());
        }
        if (properties.hasMoreElements()) {
            List<String> values = new ArrayList<String>();
            while (properties.hasMoreElements()) {
                String value = properties.nextElement();
                values.add(value);
            }
            return (String[]) values.toArray(EMPTY_STRING_ARRAY);
        } else {
            return null;
        }
    }

    protected String getRequestParameter(String name) {
        String temp = getRequest().getParameter(name);
        if (null == temp) {
            String[] tempArray = extraRequestParameters.get(name);
            if (null != tempArray && tempArray.length > 0) {
                temp = extraRequestParameters.get(name)[0];
            }
        }
        return temp;
    }

    protected Object getSessionAttribute(String name) {
        return getSessionAttribute(name, getScopeForName(name));
    }

    protected int getScopeForName(String name) {
        return PortletSession.PORTLET_SCOPE;
    }

    protected Object getSessionAttribute(String name, int scope) {
        return getRequest().getPortletSession(true).getAttribute(name, scope);
    }

    protected Enumeration<String> getSessionAttributeNames() {
        class AttributeEnumeration implements Enumeration<String> {
            int scope;
            Enumeration<String> attributes;

            public AttributeEnumeration() {
                scope = PortletSession.PORTLET_SCOPE;
                attributes = getSessionAttributeNames(scope);
                if (!attributes.hasMoreElements()) {
                    scope = PortletSession.APPLICATION_SCOPE;
                    attributes = getSessionAttributeNames(scope);
                }
            }

            public boolean hasMoreElements() {
                return attributes.hasMoreElements();
            }

            public String nextElement() {
                final String result = attributes.nextElement();

                if (!attributes.hasMoreElements() && scope == PortletSession.PORTLET_SCOPE) {
                    scope = PortletSession.APPLICATION_SCOPE;
                    attributes = getSessionAttributeNames(scope);
                }

                return result;
            }
        }

        return new AttributeEnumeration();

        // return getSessionAttributeNames(PortletSession.PORTLET_SCOPE);
    }

    protected Enumeration<String> getSessionAttributeNames(int scope) {
        return getRequest().getPortletSession(true).getAttributeNames(scope);
    }

    protected void removeContextAttribute(String name) {
        getContext().removeAttribute(name);
    }

    protected void removeRequestAttribute(String name) {
        getRequest().removeAttribute(name);
    }

    protected void removeSessionAttribute(String name) {
        removeSessionAttribute(name, getScopeForName(name));
    }

    protected void removeSessionAttribute(String name, int scope) {
        getRequest().getPortletSession(true).removeAttribute(name, scope);
    }

    protected void setContextAttribute(String name, Object value) {
        getContext().setAttribute(name, value);
    }

    protected void setRequestAttribute(String name, Object value) {
        getRequest().setAttribute(name, value);
    }

    protected void setSessionAttribute(String name, Object value) {
        setSessionAttribute(name, value, getScopeForName(name));
    }

    protected void setSessionAttribute(String name, Object value, int scope) {
        getRequest().getPortletSession(true).setAttribute(name, value, scope);
    }

    /**
     * @param url
     * @return
     */
    protected String encodeURL(String url) {
        return getResponse().encodeURL(url);
    }

    protected String replaceUrlWhitespace(String url) {
        return url.replace(" ", "%20");
    }

    public String getAuthType() {
        return getRequest().getAuthType();
    }

    public String getRemoteUser() {
        String user = getRequest().getRemoteUser();
        if (user == null) {
            Principal userPrincipal = getUserPrincipal();
            if (null != userPrincipal) {
                user = userPrincipal.getName();

            }
        }
        return user;
    }

    public String getRequestContextPath() {
        return getRequest().getContextPath();
    }

    public Locale getRequestLocale() {
        return getRequest().getLocale();
    }

    public Iterator<Locale> getRequestLocales() {
        return new EnumerationIterator<Locale>(getRequest().getLocales());
    }

    public String getRequestPathInfo() {
        if (null == viewId) {
            calculateViewId();
        }
        return pathInfo;
    }

    public String getRequestServletPath() {
        if (null == viewId) {
            calculateViewId();
        }
        return servletPath;
    }

    public Object getSession(boolean create) {
        return getRequest().getPortletSession(create);
    }

    public Principal getUserPrincipal() {
        return getRequest().getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return getRequest().isUserInRole(role);
    }

    public void log(String message) {
        getContext().log(message);
    }

    public void log(String message, Throwable exception) {
        getContext().log(message, exception);
    }

    protected void calculateViewId() {
        String newViewId = bridgeContext.getFacesViewIdFromRequest(false);

        if (null == newViewId) {
            newViewId = bridgeContext.getDefaultFacesViewIdForRequest(false);

            if (null == newViewId) {
                throw new BridgeDefaultViewNotSpecifiedException();
            }

            newViewId = processViewParameters(newViewId);
        }

        if (null != newViewId && newViewId.equals(viewId)) {
            // No ViewId change
            return;
        }

        viewId = newViewId;

        calculateServletPath(viewId, bridgeContext.getBridgeConfig().getFacesServletMappings());
    }

    protected String processViewParameters(String newViewId) {
        try {
            PortalActionURL portalUrl = new PortalActionURL(newViewId);
            if (portalUrl.parametersSize() > 0) {
                Map<String, String[]> params = portalUrl.getParameters();
                for (Entry<String, String[]> entry : params.entrySet()) {
                    extraRequestParameters.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
        }
        return newViewId;
    }

    protected void calculateServletPath(String viewId, List<String> servletMappings) {
        if (null != servletMappings && servletMappings.size() > 0) {
            String mapping = servletMappings.get(0);
            if (mapping.startsWith("*")) {
                // Suffix Mapping
                servletMappingSuffix = mapping.substring(mapping.indexOf('.'));
                viewId = viewId.substring(0, viewId.lastIndexOf('.')) + servletMappingSuffix;
                servletPath = viewId;
                pathInfo = null;

                getRequest().setAttribute(SERVLET_PATH_ATTRIBUTE, servletPath);
                getRequest().setAttribute("com.sun.faces.INVOCATION_PATH", servletMappingSuffix);
            } else if (mapping.endsWith("*")) {
                // Prefix Mapping
                mapping = mapping.substring(0, mapping.length() - 1);
                if (mapping.endsWith("/")) {
                    mapping = mapping.substring(0, mapping.length() - 1);
                }
                servletMappingPrefix = servletPath = mapping;
                pathInfo = viewId;
                getRequest().setAttribute("com.sun.faces.INVOCATION_PATH", servletMappingSuffix);
            } else {
                servletPath = null;
                pathInfo = viewId;
            }
        } else {
            servletPath = null;
            pathInfo = viewId;
        }
    }

    /**
     * @param actionURL
     */
    protected void internalRedirect(PortalActionURL actionURL) {
        // Detect ViewId from URL and create new view for them.
        String viewId = actionURL.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);
        if (null != viewId) {
            bridgeContext.setRenderRedirect(true);
            bridgeContext.setRedirectViewId(viewId);
            Map<String, String[]> requestParameters = actionURL.getParameters();
            if (requestParameters.size() > 0) {
                bridgeContext.setRenderRedirectQueryString(actionURL.getQueryString());
            }
        }
    }

    /**
     * @return the servletMappingSuffix
     */
    public String getServletMappingSuffix() {
        return servletMappingSuffix;
    }

    /**
     * @return the defaultJsfSuffix
     */
    public String getDefaultJsfSuffix() {
        return defaultJsfSuffix;
    }

    /**
     * @return the defaultJsfPrefix
     */
    public String getServletMappingPrefix() {
        return servletMappingPrefix;
    }

    protected String getViewIdFromUrl(PortalActionURL url) {
        String viewId;
        viewId = url.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);
        if (null == viewId) {
            viewId = url.getPath();
            if (viewId.startsWith(getRequestContextPath())) {
                viewId = viewId.substring(getRequestContextPath().length());
            }
            viewId = bridgeContext.getFacesViewIdFromPath(viewId);
        }
        return viewId;
    }

    public void dispatch(String path) throws IOException {
        if (null == path) {
            throw new NullPointerException("Path to new view is null");
        }
        PortletRequestDispatcher dispatcher = getContext().getRequestDispatcher(path);
        if (null == dispatcher) {
            throw new IllegalStateException("Dispatcher for render request is not created");
        }

        try {
            boolean hasRenderRedirectedAfterForward = bridgeContext.hasRenderRedirectAfterDispatch();
            if (!hasRenderRedirectedAfterForward) {
                dispatcher.forward(getRequest(), getResponse());
            } else {
                dispatcher.include(getRequest(), getResponse());
            }
        } catch (PortletException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public String getMimeType(String file) {
        String mimeType = getContext().getMimeType(file);
        if (mimeType == null) {
            mimeType = getFallbackMimeType(file);
        }
        return mimeType;
    }

    @Override
    public String getContextName() {
        return getContext().getPortletContextName();
    }

    @Override
    public String getRealPath(String path) {
        return getContext().getRealPath(path);
    }

    @Override
    public String getRequestScheme() {
        return getRequest().getScheme();
    }

    @Override
    public String getRequestServerName() {
        return getRequest().getServerName();
    }

    @Override
    public int getRequestServerPort() {
        return getRequest().getServerPort();
    }

    @Override
    public void invalidateSession() {
        PortletSession session = getRequest().getPortletSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * @return the hasNavigationRedirect
     */
    boolean isHasNavigationRedirect() {
        return hasNavigationRedirect;
    }

    /**
     * @param hasNavigationRedirect the hasNavigationRedirect to set
     */
    void setHasNavigationRedirect(boolean hasNavigationRedirect) {
        this.hasNavigationRedirect = hasNavigationRedirect;
    }

    public String encodeActionURL(String url) {
        if (null == url) {
            getLogger().log(Level.WARNING, "Unable to encode ActionURL for url=[null]");
            return null;
        }
        String actionUrl = null;
        Map<String, String[]> actionParameters;

        if (url.startsWith("#")) {
            actionParameters = Collections.emptyMap();
        } else if (url.startsWith(PortletExternalContextImpl.WSRP_REWRITE)) {
            actionParameters = Collections.emptyMap();
        } else {
            try {
                boolean escapedUrl = isStrictEscaped(url);
                PortalActionURL portalUrl = new PortalActionURL(escapedUrl ? unescapeUrl(url) : url);
                if (!isInContext(portalUrl)) {
                    if ("portlet:".equals(portalUrl.getProtocol())) {
                        /*
                         * * The scheme "portlet:" indicates that the target of this action is the portlet itself. Though
                         * generally used to generate links to nonFaces views in this portlet it can also be used to generate
                         * action or render links to a Faces view (including the current view). The scheme is followed by either
                         * the keyword action, render or resource. render indicates a portlet renderURL should be encoded[6.8].
                         * action indicates a portlet actionURL should be encoded[6.9]. resource indicates a portlet resourceURL
                         * should be encoded[6.102]. Following this url type indicator is an optional query string. Parameter
                         * value pairs in the query string are the parameters that are to be encoded into the portletURL.
                         */
                        try {
                            Scheme scheme = Scheme.valueOf(portalUrl.getPath());
                            actionUrl = createPortletUrl(scheme, portalUrl, escapedUrl);
                        } catch (IllegalArgumentException e) {
                            actionUrl = url;
                        }
                    } else {
                        String directLink = portalUrl.getParameter(Bridge.DIRECT_LINK);
                        /*
                         * If the inputURL contains the parameter javax.portlet.faces.DirectLink (with a value of "true") return
                         * an absolute path derived from the inputURL. Don't remove the DirectLink parameter if it exists[6.6].
                         * If the inputURL contains the parameter javax.portlet.faces.DirectLink and its value is false then
                         * remove the javax.portlet.faces.DirectLink parameter and its value from the query string and continue
                         * processing (using the next step concerning determining the target of the URL)[6.7].
                         */
                        if (null != directLink && Boolean.parseBoolean(directLink)) {
                            // make absolute url
                            PortletRequest request = getRequest();
                            portalUrl.setProtocol(request.getScheme() + ":");
                            portalUrl.setHost("//" + request.getServerName());
                            portalUrl.setPort(request.getServerPort());
                        }
                        /*
                         * if the inputURL is an absolute path external to this portlet application[6.5] return the inputURL
                         * unchanged.
                         */
                        actionUrl = escapeUrl(escapedUrl, portalUrl.toString());
                    }
                } else {
                    String directLink = portalUrl.getParameter(Bridge.DIRECT_LINK);
                    if (null != directLink) {
                        /*
                         * If the inputURL contains the parameter javax.portlet.faces.DirectLink (with a value of "true") return
                         * an absolute path derived from the inputURL. Don't remove the DirectLink parameter if it exists[6.6].
                         * If the inputURL contains the parameter javax.portlet.faces.DirectLink and its value is false then
                         * remove the javax.portlet.faces.DirectLink parameter and its value from the query string and continue
                         * processing (using the next step concerning determining the target of the URL)[6.7].
                         */
                        if (Boolean.parseBoolean(directLink)) {
                            // make absolute url
                            PortletRequest request = getRequest();
                            portalUrl.setProtocol(request.getScheme() + ":");
                            portalUrl.setHost("//" + request.getServerName());
                            portalUrl.setPort(request.getServerPort());
                            String directUrl = portalUrl.toString();
                            return escapeUrl(escapedUrl, directUrl);
                        }
                    }

                    if (!isInContext(portalUrl)) {
                        /*
                         * if the inputURL is an absolute path external to this portlet application[6.5] return the inputURL
                         * unchanged.
                         */
                        actionUrl = escapeUrl(escapedUrl, portalUrl.toString());
                    } else {
                        String pathInContext = calculatePathInContext(portalUrl);
                        if (isFacesPath(pathInContext)) {
                            portalUrl.setParameter(Bridge.FACES_VIEW_ID_PARAMETER,
                                    bridgeContext.getFacesViewIdFromPath(pathInContext));
                            actionUrl = createActionUrl(portalUrl, escapedUrl);
                        } else {
                            // TODO cleanup
                            portalUrl.setParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER, pathInContext);
                            pathInContext = calculatePathInContext(portalUrl);
                            portalUrl.setParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER, pathInContext);
                            actionUrl = createRenderUrl(portalUrl, escapedUrl, Collections.<String, List<String>> emptyMap());
                        }
                    }
                }
                actionParameters = portalUrl.getParameters();
            } catch (MalformedURLException e) {
                actionUrl = url;
                actionParameters = Collections.emptyMap();
            }
        }
        // Store url parameters to reuse in redirect()
        encodedActionUrlParameters.put(actionUrl, actionParameters);
        return actionUrl;
    }

    @Override
    public String encodePartialActionURL(String url) {
        if (null == url) {
            throw new NullPointerException();
        }
        String actionUrl = url;
        if (!actionUrl.startsWith("#")) {
            try {
                PortalActionURL portalUrl = new PortalActionURL(url);
                boolean inContext = isInContext(portalUrl);
                if (inContext) {
                    actionUrl = createPartialActionUrl(portalUrl);
                } else {
                    return encodeURL(portalUrl.toString());
                }
            } catch (MalformedURLException e) {
                throw new FacesException(e);
            }
        }
        return actionUrl.replaceAll("\\&amp\\;", "&");
    }

    public String encodeResourceURL(String url) {
        try {
            boolean escapedUrl = isStrictEscaped(url);
            PortalActionURL portalUrl = new PortalActionURL(url);
            // JSR-301 chapter 6.1.3.1 requirements:
            String path = portalUrl.getPath();
            if (null != portalUrl.getProtocol() && "portlet:".equalsIgnoreCase(portalUrl.getProtocol())) {
                // Portlet Scheme URL
                portalUrl.removeParameter(Bridge.VIEW_LINK);
                encodeBackLink(portalUrl);
                return replaceUrlWhitespace(encodeActionURL(portalUrl.toString()));
            } else if (isOpaqueURL(url)) {
                // Opaque URL
                return url;
            } else if (!isInContext(portalUrl)) {
                // Hierarchial url outside context.
                portalUrl.removeParameter(Bridge.VIEW_LINK);
                encodeBackLink(portalUrl);
                return replaceUrlWhitespace(encodeURL(portalUrl.toString()).replace("&amp;", "&"));
            } else if ("true".equalsIgnoreCase(portalUrl.getParameter(Bridge.VIEW_LINK))) {
                // Hierarchical and targets a resource that is within this application
                portalUrl.removeParameter(Bridge.VIEW_LINK);
                encodeBackLink(portalUrl);
                return replaceUrlWhitespace(encodeActionURL(portalUrl.toString()));
            } else {
                // For resources in the portletbridge application context add
                // namespace as URL parameter, to restore portletbridge session.
                // Remove context path from resource ID.

                portalUrl.removeParameter(Bridge.VIEW_LINK);
                encodeBackLink(portalUrl);

                if (path.startsWith("/")) {
                    if (null == portalUrl.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER)) {
                        // absolute path, remove context path from ID.
                        portalUrl.setPath(path.substring(getRequestContextPath().length()));
                    }
                } else {
                    // resolve relative URL against current view.
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    UIViewRoot viewRoot = facesContext.getViewRoot();
                    if (null != viewRoot && null != viewRoot.getViewId() && viewRoot.getViewId().length() > 0) {
                        String viewId = viewRoot.getViewId();
                        int indexOfSlash = viewId.lastIndexOf('/');
                        if (indexOfSlash >= 0) {
                            portalUrl.setPath(viewId.substring(0, indexOfSlash + 1) + path);
                        } else {
                            portalUrl.setPath('/' + path);
                        }
                    } else {
                        // No clue where we are
                        portalUrl.setPath('/' + path);
                    }
                }
                portalUrl.setPath(URI.create(portalUrl.getPath()).normalize().getPath());

                String facesViewId = getViewIdFromUrl(portalUrl);
                if (null != portalUrl.getParameter(Bridge.IN_PROTOCOL_RESOURCE_LINK)) {
                    url = createResourceUrl(portalUrl, escapedUrl);
                } else if (null != facesViewId) {
                    portalUrl.setParameter(Bridge.FACES_VIEW_ID_PARAMETER, facesViewId);
                    url = createResourceUrl(portalUrl, escapedUrl);
                } else {
                    portalUrl.setPath(getRequestContextPath() + portalUrl.getPath());
                    url = encodeURL(portalUrl.toString());
                }
            }
        } catch (MalformedURLException e) {
            throw new FacesException(e);
        }
        return url.replaceAll("\\&amp\\;", "&");
    }

    @Override
    public String encodeBookmarkableURL(String baseUrl, Map<String, List<String>> parameters) {
        if (null == baseUrl) {
            throw new NullPointerException();
        }
        String actionUrl = baseUrl;
        if (!actionUrl.startsWith("#")) {
            try {
                PortalActionURL portalUrl = new PortalActionURL(baseUrl);
                boolean inContext = isInContext(portalUrl);
                if (inContext) {
                    actionUrl = createRenderUrl(portalUrl, isStrictEscaped(baseUrl), parameters);
                } else {
                    return encodeURL(portalUrl.toString());
                }
            } catch (MalformedURLException e) {
                throw new FacesException(e);
            }
        }
        return actionUrl.replaceAll("\\&amp\\;", "&");
    }

    @Override
    public String encodeRedirectURL(String baseUrl, Map<String, List<String>> parameters) {
        try {
            PortalActionURL portalUrl = new PortalActionURL(baseUrl);
            if (null != parameters && !parameters.isEmpty()) {
                for (Entry<String, List<String>> entry : parameters.entrySet()) {
                    for (String value : entry.getValue()) {
                        portalUrl.addParameter(entry.getKey(), value);
                    }
                }
            }
            return encodeURL(portalUrl.toString());
        } catch (MalformedURLException e) {
            throw new FacesException(e);
        }
    }

    protected boolean isAbsoluteURL(String url) {
        url = url.toLowerCase();
        if (url.startsWith("http:") || url.startsWith("https:")) {
            return true;
        }

        int i = url.indexOf(":");
        if (i == -1) {
            return false;
        }

        String scheme = url.substring(0, i);

        if (scheme.indexOf(";") != -1) {
            return false;
        } else if (scheme.indexOf("/") != -1) {
            return false;
        } else if (scheme.indexOf("#") != -1) {
            return false;
        } else if (scheme.indexOf("?") != -1) {
            return false;
        } else if (scheme.indexOf(" ") != -1) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isOpaqueURL(String url) {
        if (!isAbsoluteURL(url)) {
            return false;
        }

        return (!url.startsWith("portlet:") && (url.indexOf(':') != (url.indexOf('/') - 1)));
    }

    protected String unescapeUrl(String actionUrl) {
        // TODO - unescape query string only
        return actionUrl.replaceAll("\\&amp\\;", "&");
    }

    protected boolean isStrictEscaped(String url) {
        int queryStart = url.indexOf('?');
        if (queryStart < 0) {
            return false;
        } else {
            return url.indexOf("&amp;", queryStart) > 0;
        }
    }

    protected String escapeUrl(boolean escapedUrl, String directUrl) {
        if (escapedUrl) {
            directUrl = directUrl.replaceAll("\\&", "&amp;");
        }
        return directUrl;
    }

    protected boolean isInContext(PortalActionURL portalUrl) {
        String directLink = portalUrl.getParameter(Bridge.DIRECT_LINK);
        if (null != directLink) {
            if (Boolean.parseBoolean(directLink)) {
                return false;
            }
            portalUrl.removeParameter(Bridge.DIRECT_LINK);
        }
        return portalUrl.isInContext(getRequestContextPath());
    }

    protected boolean isFacesPath(String pathInContext) {
        if (null != getServletMappingPrefix()) {
            return pathInContext.startsWith(getServletMappingPrefix());
        } else if (null != getServletMappingSuffix()) {
            return pathInContext.endsWith(getServletMappingSuffix());
        }
        // No Faces preffix/suffix defined, all request came to JSF
        return true;
    }

    protected String calculatePathInContext(PortalActionURL portalURL) {
        String inContextPath;
        String path = portalURL.getPath();
        if (path.startsWith("/")) {
            // absolute path, remove context path from ID.
            // TCK compliance - return the full nonfaces view prepended with context path
            if (portalURL.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER) != null) {
                return path;
            }
            inContextPath = path.substring(getRequestContextPath().length());
            // return path;
        } else {
            // resolve relative URL aganist current view.
            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIViewRoot viewRoot = facesContext.getViewRoot();
            if (null != viewRoot && null != viewRoot.getViewId() && viewRoot.getViewId().length() > 0) {
                String viewId = viewRoot.getViewId();
                int indexOfSlash = viewId.lastIndexOf('/');
                if (indexOfSlash >= 0) {
                    inContextPath = viewId.substring(0, indexOfSlash + 1) + path;
                } else {
                    inContextPath = '/' + path;
                }
            } else {
                // No clue where we are
                inContextPath = '/' + path;
            }
        }
        inContextPath = URI.create(inContextPath).normalize().getPath();
        return inContextPath;
    }

    protected String createPortletUrl(Scheme protocol, PortalActionURL portalUrl, boolean escape) {
        /*
         * # The scheme is followed by either the keyword action, render or resource. render indicates a portlet renderURL
         * should be encoded[6.8]. action indicates a portlet actionURL should be encoded[6.9]. resource indicates a portlet
         * resourceURL should be encoded[6.102]. # Following this url type indicator is an optional query string. Parameter
         * value pairs in the query string are the parameters that are to be encoded into the portletURL.
         *
         * To generate a link to a Faces view, encode the view as the value of either the _jsfBridgeViewId or _jsfBridgeViewPath
         * parameter (depending on whether you are encoding the viewId or the viewPath). Targets of such references are run in
         * new empty scopes. An exception is made when the target is the current view and either of the above parameters is
         * included in the query string with a value of _jsfBridgeCurrentView. In this case the url is encoded with the current
         * render parameters and hence retains access to its state/scope. In all cases the bridge removes the above parameter(s)
         * from the query string before generating the encoded url.
         *
         * For a resource url, a Faces view is only encoded if one of the _jsfBridgeViewId or _jsfBridgeViewPath parameters is
         * included in the query string, otherwise a nonFaces resource url is generated. The _jsfBridgeCurrentView value is used
         * as a shortcut to indicate the resource targets the current view.
         */
        processJsfViewParameter(portalUrl, Bridge.FACES_VIEW_ID_PARAMETER);
        processJsfViewParameter(portalUrl, Bridge.FACES_VIEW_PATH_PARAMETER);
        switch (protocol) {
            case action:
                return createActionUrl(portalUrl, escape);
            case resource:
                return createResourceUrl(portalUrl, escape);
            case render:
                return createRenderUrl(portalUrl, escape, Collections.<String, List<String>> emptyMap());
            default:
                return portalUrl.toString();
        }
    }

    protected void processJsfViewParameter(PortalActionURL portalUrl, String facesViewParameter) {
        if (portalUrl.hasParameter(facesViewParameter)) {
            if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(portalUrl.getParameter(Bridge.FACES_VIEW_ID_PARAMETER))) {
                portalUrl.removeParameter(Bridge.FACES_VIEW_ID_PARAMETER);
                if (this instanceof RenderPortletExternalContextImpl) {
                    portalUrl.getParameters().putAll(getRequest().getPrivateParameterMap());
                    portalUrl.getParameters().putAll(getRequest().getPublicParameterMap());
                }
            }
        }
    }

    protected void encodeBackLink(PortalActionURL portalUrl) {
        String backLink = portalUrl.getParameter(Bridge.BACK_LINK);
        if (null != backLink) {
            portalUrl.removeParameter(Bridge.BACK_LINK);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewId;
            if (null != facesContext.getViewRoot() && null != (viewId = facesContext.getViewRoot().getViewId())) {
                ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
                String actionURL = viewHandler.getActionURL(facesContext, viewId);
                portalUrl.addParameter(backLink, encodeActionURL(actionURL));
            }
        }
    }

    protected String encodePortletUrl(BaseURL portletURL, boolean escape) {
        StringWriter out = new StringWriter();
        try {
            portletURL.write(out, escape);
            return out.toString();
        } catch (IOException e) {
            throw new FacesException(e);
        }
    }

    protected BridgeLogger getLogger() {
        return bridgeContext.getBridgeConfig().getLogger();
    }

    protected abstract String createRenderUrl(PortalActionURL portalUrl, boolean escape, Map<String, List<String>> parameters);

    protected abstract String createResourceUrl(PortalActionURL portalUrl, boolean escape);

    protected abstract String createPartialActionUrl(PortalActionURL portalUrl);

    protected abstract String createActionUrl(PortalActionURL url, boolean escape);

}
