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
package org.jboss.portletbridge.bridge.context;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeInvalidViewPathException;
import javax.portlet.faces.BridgeNotAFacesRequestException;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.factory.BridgeFactoryFinder;
import org.jboss.portletbridge.bridge.factory.BridgeRequestScopeManagerFactory;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManagerImpl;
import org.jboss.portletbridge.context.PortalActionURL;
import org.jboss.portletbridge.el.ELContextImpl;

/**
 * @author kenfinnigan
 */
public class BridgeContextImpl extends BridgeContext implements ELContextListener {

    public static final String REQUEST_SCOPE_MANAGER = BridgeRequestScopeManagerImpl.class.getName();

    private PortletContext portletContext;
    private PortletRequest portletRequest;
    private PortletResponse portletResponse;
    private BridgeConfig bridgeConfig;
    private PortletPhase portletPhase;
    private Map<String, Object> attributes;
    private boolean bridgeRequestScopePreserved = true;
    private String savedViewStateParam;
    private String navigationQueryString;
    private String renderRedirectQueryString;
    private String redirectViewId;
    private boolean renderRedirect = false;
    private boolean renderRedirectOcurredAfterDispatch = false;
    private List<String> preExistingRequestAttributeNames;
    private Map<String, String[]> preservedActionParams;
    private boolean viewHistoryInitialized = false;

    public BridgeContextImpl() {
        BridgeContext.setCurrentInstance(this);

        // Add as ELContextListener to the Faces App so we can add the
        // portletConfig to any newly created contexts.
        ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication()
                .addELContextListener(this);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#release()
     */
    @Override
    public void release() {
        BridgeContext.setCurrentInstance(null);

        // Remove as ELContextListener from the Faces App
        ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication()
                .removeELContextListener(this);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPortletContext(javax.portlet.PortletContext)
     */
    @Override
    public void setPortletContext(PortletContext context) {
        portletContext = context;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPortletContext()
     */
    @Override
    public PortletContext getPortletContext() {
        return portletContext;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPortletRequest(javax.portlet.PortletRequest)
     */
    @Override
    public void setPortletRequest(PortletRequest request) {
        portletRequest = request;

        if (null != getBridgeConfig()) {
            initViewHistory();
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPortletRequest()
     */
    @Override
    public PortletRequest getPortletRequest() {
        return portletRequest;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPortletResponse(javax.portlet.PortletResponse)
     */
    @Override
    public void setPortletResponse(PortletResponse response) {
        portletResponse = response;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPortletResponse()
     */
    @Override
    public PortletResponse getPortletResponse() {
        return portletResponse;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPortletRequestPhase(javax.portlet.faces.Bridge.PortletPhase)
     */
    @Override
    public void setPortletRequestPhase(PortletPhase phase) {
        portletPhase = phase;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPortletRequestPhase()
     */
    @Override
    public PortletPhase getPortletRequestPhase() {
        return portletPhase;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setBridgeConfig(org.jboss.portletbridge.bridge.config.BridgeConfig)
     */
    @Override
    public void setBridgeConfig(BridgeConfig config) {
        bridgeConfig = config;

        if (null != getPortletRequest()) {
            initViewHistory();
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getBridgeConfig()
     */
    @Override
    public BridgeConfig getBridgeConfig() {
        return bridgeConfig;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        if (null == attributes) {
            attributes = new HashMap<String, Object>(10);
        }
        return attributes;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setBridgeRequestScopePreserved(boolean)
     */
    @Override
    public void setBridgeRequestScopePreserved(boolean preserve) {
        bridgeRequestScopePreserved = preserve;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#isBridgeRequestScopePreserved()
     */
    @Override
    public boolean isBridgeRequestScopePreserved() {
        return bridgeRequestScopePreserved;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setSavedViewStateParam(java.lang.String)
     */
    @Override
    public void setSavedViewStateParam(String savedViewStateParam) {
        this.savedViewStateParam = savedViewStateParam;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getSavedViewStateParam()
     */
    @Override
    public String getSavedViewStateParam() {
        return savedViewStateParam;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setNavigationQueryString(java.lang.String)
     */
    @Override
    public void setNavigationQueryString(String queryString) {
        navigationQueryString = queryString;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getNavigationalQueryString()
     */
    @Override
    public String getNavigationalQueryString() {
        return navigationQueryString;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setRenderRedirectQueryString(java.lang.String)
     */
    @Override
    public void setRenderRedirectQueryString(String queryString) {
        renderRedirectQueryString = queryString;
        setRenderRedirect(true);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getRenderRedirectQueryString()
     */
    @Override
    public String getRenderRedirectQueryString() {
        return renderRedirectQueryString;
    }

    @Override
    public String getRedirectViewId() {
        return redirectViewId;
    }

    @Override
    public void setRedirectViewId(String viewId) {
        redirectViewId = viewId;
        setRenderRedirect(true);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setRenderRedirect(boolean)
     */
    @Override
    public void setRenderRedirect(boolean redirect) {
        renderRedirect = redirect;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#hasRenderRedirect()
     */
    @Override
    public boolean hasRenderRedirect() {
        return renderRedirect;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setRenderRedirectAfterDispatch(boolean)
     */
    @Override
    public void setRenderRedirectAfterDispatch(boolean afterDispatch) {
        if (afterDispatch) {
            setRenderRedirect(true);
        }
        renderRedirectOcurredAfterDispatch = afterDispatch;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#hasRenderRedirectAfterDispatch()
     */
    @Override
    public boolean hasRenderRedirectAfterDispatch() {
        return renderRedirectOcurredAfterDispatch;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPreFacesRequestAttrNames(java.util.List)
     */
    @Override
    public void setPreFacesRequestAttrNames(List<String> names) {
        preExistingRequestAttributeNames = null;
        if (null != names) {
            preExistingRequestAttributeNames = new ArrayList<String>(names);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPreFacesRequestAttrNames()
     */
    @Override
    public List<String> getPreFacesRequestAttrNames() {
        return preExistingRequestAttributeNames;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setPreservedActionParams(java.util.Map)
     */
    @Override
    public void setPreservedActionParams(Map<String, String[]> actionParamMap) {
        preservedActionParams = null;

        if (null != actionParamMap) {
            preservedActionParams = new HashMap<String, String[]>(actionParamMap);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getPreservedActionParams()
     */
    @Override
    public Map<String, String[]> getPreservedActionParams() {
        return preservedActionParams;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#setViewHistory(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void setViewHistory(String mode, String viewId, boolean preserveRenderParams) {
        PortalActionURL historyViewId = null;
        try {
            historyViewId = new PortalActionURL(viewId);
        } catch (MalformedURLException e) {
            log("MalformedURL for " + viewId + " in setViewHistory()", e);
        }

        if (null != historyViewId) {
            historyViewId.addParameter(Bridge.PORTLET_MODE_PARAMETER, mode);

            if (preserveRenderParams) {
                // Build a QueryString from the request's render parameters so can preserve
                // with the viewId
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Map<String, String[]> renderParams = null;

                if (null != facesContext) {
                    renderParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
                } else {
                    renderParams = getPortletRequest().getPrivateParameterMap();
                }

                if (!renderParams.isEmpty()) {
                    Set<Map.Entry<String, String[]>> keys = renderParams.entrySet();
                    for (Entry<String, String[]> entry : keys) {
                        if (!entry.getKey().equals(bridgeConfig.getViewIdRenderParameterName())
                                && !entry.getKey().equals(ResponseStateManager.VIEW_STATE_PARAM)) {
                            for (String value : entry.getValue()) {
                                historyViewId.addParameter(entry.getKey(), value);
                            }
                        }
                    }
                }
            }

            String queryString = historyViewId.getQueryString();
            String historyViewPath = historyViewId.getPath() + (queryString != null ? "?" + queryString : "");
            getPortletRequest().getPortletSession(true).setAttribute(Bridge.VIEWID_HISTORY + "." + mode, historyViewPath);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getViewHistory(java.lang.String)
     */
    @Override
    public String getViewHistory(String mode) {
        StringBuffer key = new StringBuffer(100);
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get(key.append(Bridge.VIEWID_HISTORY).append('.').append(mode).toString());
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getFacesViewIdFromRequest(boolean)
     */
    @Override
    public String getFacesViewIdFromRequest(boolean excludeQueryString) throws BridgeInvalidViewPathException {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (null == facesContext) {
            return getViewId(portletRequest, excludeQueryString);
        } else {
            return getViewId((PortletRequest) facesContext.getExternalContext().getRequest(), excludeQueryString);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getFacesViewId(boolean)
     */
    @Override
    public String getFacesViewId(boolean excludeQueryString) throws BridgeInvalidViewPathException {
        String viewId = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (null != facesContext && null != facesContext.getViewRoot()) {
            viewId = facesContext.getViewRoot().getViewId();
        } else {
            viewId = getFacesViewIdFromRequest(excludeQueryString);
            if (null == viewId) {
                viewId = getDefaultFacesViewIdForRequest(excludeQueryString);
            }
        }
        return viewId;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getBridgeScope()
     */
    @Override
    public BridgeRequestScope getBridgeScope() {
        return getBridgeRequestScopeManager().getRequestScope(this);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getBridgeScope(java.lang.String, java.lang.String)
     */
    @Override
    public BridgeRequestScope getBridgeScope(String viewId, String mode) {
        return getBridgeRequestScopeManager().getRequestScope(this, viewId, mode);
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getBridgeRequestScopeManager()
     */
    @Override
    public BridgeRequestScopeManager getBridgeRequestScopeManager() {
        PortletSession session = getPortletRequest().getPortletSession(true);
        BridgeRequestScopeManager scopeManager = (BridgeRequestScopeManager) session.getAttribute(REQUEST_SCOPE_MANAGER);

        if (null == scopeManager) {
            scopeManager = createBridgeRequestScopeManager();
        }

        return scopeManager;
    }

    private synchronized BridgeRequestScopeManager createBridgeRequestScopeManager() {
        BridgeRequestScopeManager scopeManager = (BridgeRequestScopeManager) getPortletRequest().getPortletSession(true)
                .getAttribute(REQUEST_SCOPE_MANAGER);

        if (null == scopeManager) {
            getPortletRequest().getPortletSession(true).setAttribute(
                    REQUEST_SCOPE_MANAGER,
                    ((BridgeRequestScopeManagerFactory) BridgeFactoryFinder
                            .getFactoryInstance(BridgeRequestScopeManagerFactory.class))
                            .getBridgeRequestScopeManager(getBridgeConfig()));

            scopeManager = (BridgeRequestScopeManager) getPortletRequest().getPortletSession(true).getAttribute(
                    REQUEST_SCOPE_MANAGER);
        }

        return scopeManager;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getFacesViewIdFromPath(java.lang.String)
     */
    @Override
    public String getFacesViewIdFromPath(String path) throws BridgeInvalidViewPathException {
        // First remove the query string
        int index = path.indexOf("?");
        if (index != -1) {
            path = path.substring(0, index);
        }

        // Now remove up through the ContextPath
        String ctxPath = getPortletRequest().getContextPath();
        index = path.indexOf(ctxPath);
        if (index != -1) {
            path = path.substring(index + ctxPath.length());
        }

        String viewId = null;
        List<String> facesMappings = getBridgeConfig().getFacesServletMappings();
        List<String> facesSuffixes = getBridgeConfig().getFacesSuffixes();
        String prefix = getPrefix(path, facesMappings);
        if (isSuffixedMapped(path, facesMappings)) {
            viewId = viewIdFromSuffixMapping(path, facesSuffixes);
        } else if (null != prefix) {
            viewId = path.substring(prefix.length());
        } else {
            // Not a Faces URL
            viewId = null;
        }
        return viewId;
    }

    /**
     * @see org.jboss.portletbridge.bridge.context.BridgeContext#getDefaultFacesViewIdForRequest(boolean)
     */
    @Override
    public String getDefaultFacesViewIdForRequest(boolean excludeQueryString) throws BridgeDefaultViewNotSpecifiedException {
        PortletRequest request = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (null != facesContext) {
            request = (PortletRequest) facesContext.getExternalContext().getRequest();
        }

        if (null == request) {
            request = getPortletRequest();
            if (null == request) {
                throw new BridgeNotAFacesRequestException(
                        "No PortletRequest present in Faces.getExternalContext() or BridgeContext.getPortletRequest()");
            }
        }

        String viewId = getBridgeConfig().getDefaultViewMappings().get(getPortletRequest().getPortletMode().toString());

        if (null == viewId) {
            throw new BridgeDefaultViewNotSpecifiedException("No Default View specified for portlet: "
                    + getBridgeConfig().getPortletConfig().getPortletName());
        }

        if (excludeQueryString) {
            viewId = excludeQuery(viewId);
        }
        return viewId;
    }

    protected String excludeQuery(String viewId) {
        int queryStart = viewId.indexOf('?');
        if (queryStart != -1) {
            return viewId.substring(0, queryStart);
        }
        return viewId;
    }

    /**
     * ELContextListener impl
     */
    public void contextCreated(ELContextEvent ece) {
        // Add the portletConfig to the ELContext so it is evaluated
        ELContext elContext = ece.getELContext();

        // FacesContext (where the Faces/Bridge ELContext is created doesn't have
        // access to the PortletConfig which the Bridge ELResolver needs.
        // The config object is added as an attribute here in the ContextListener.
        // However only add to a EL context created within Faces as the JSP
        // ELContext/Resolver will naturally resolve the config (as long as the
        // page devleper has used the <portlet:defineObjects> tag.
        // Because listeners are called at app scope we must ensure that only
        // the active portlet's config is added to the ELContext. To do this, check
        // the portletName previously stored as a request attribute against the config.

        // Make sure our bridge instance is handling this context
        FacesContext fCtx = (FacesContext) elContext.getContext(FacesContext.class);

        if (null == fCtx) {
            return;
        }

        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();

        if (this == bridgeContext) {
            ELContextImpl portletELContext;
            if (elContext instanceof ELContextImpl) {
                // Turns out that by the time my resolver is called the ELContext may
                // have been wrapped -- so mark here as a FacesResolver and then do a put context
                portletELContext = (ELContextImpl) elContext;
                portletELContext.setFacesResolved(true);
                // Put the portletConfig object into this Map
                portletELContext.setPortletConfig(getBridgeConfig().getPortletConfig());

            } else {
                // create a PortletELContext to hold future resolver state and place on this context
                portletELContext = new ELContextImpl(elContext.getELResolver());
                portletELContext.setFacesResolved(false);
            }
            elContext.putContext(ELContextImpl.class, portletELContext);
        }
    }

    protected void initViewHistory() {
        if (!viewHistoryInitialized) {
            Map<String, String> viewIdDefaultMap = bridgeConfig.getDefaultViewMappings();

            for (String mode : viewIdDefaultMap.keySet()) {
                String modeView = viewIdDefaultMap.get(mode);
                if (null != modeView && modeView.length() > 0) {
                    setViewHistory(mode, modeView, false);
                }
            }

            viewHistoryInitialized = true;
        }
    }

    protected boolean isSuffixedMapped(String url, List<String> mappings) {
        // see if the viewId terminates with an extension
        // if non-null value contains *.xxx where xxx is the extension
        String ext = extensionMappingFromViewId(url);
        return null != ext && mappings.contains(ext);
    }

    protected String extensionMappingFromViewId(String viewId) {
        // first remove/ignore any querystring
        int index = viewId.indexOf('?');
        if (index != -1) {
            viewId = viewId.substring(0, index);
        }

        int extLoc = viewId.lastIndexOf('.');

        if (extLoc != -1 && extLoc > viewId.lastIndexOf('/')) {
            StringBuilder sb = new StringBuilder("*");
            sb.append(viewId.substring(extLoc));
            return sb.toString();
        }
        return null;
    }

    protected String viewIdFromSuffixMapping(String url, List<String> suffixes) {
        int index = url.lastIndexOf(".");
        if (index != -1) {
            for (String suffix : suffixes) {
                if (suffix.startsWith(".")) {
                    url = url.substring(0, index) + suffix;
                } else {
                    // shouldn't happen
                    url = url.substring(0, index) + "." + suffix;
                }
                // now verify if this exists
                String testPath = url.startsWith("/") ? url : "/" + url;
                try {
                    if (portletContext.getResource(testPath) != null) {
                        break;
                    }
                } catch (MalformedURLException m) {
                    throw new BridgeException("View Id " + testPath + " does not exist.", m);
                }
            }
        }
        return url;
    }

    protected String getPrefix(String url, List<String> mappings) {
        for (String mapping : mappings) {
            String prefix = null;
            if (mapping.startsWith("/")) {
                int index = mapping.lastIndexOf("/*");
                if (index != -1) {
                    prefix = mapping.substring(0, index);
                }
            }
            if (null != prefix && url.startsWith(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    protected String getViewId(PortletRequest request, boolean excludeQueryString)
            throws BridgeDefaultViewNotSpecifiedException, BridgeInvalidViewPathException {

        String requestedMode = request.getPortletMode().toString();

        String viewId = (String) request.getAttribute(Bridge.VIEW_ID);
        String viewPath = null;
        if (null == viewId) {
            viewPath = (String) request.getAttribute(Bridge.VIEW_PATH);
            if (null != viewPath) {
                // convert the view path into a viewId
                viewId = getFacesViewIdFromPath(viewPath);
                if (null == viewId) {
                    throw new BridgeInvalidViewPathException("Unable to resolve Faces ViewId for path: " + viewPath);
                }
            }
        }

        if (null == viewId) {
            // Read target from request parameter
            if (((Bridge.PortletPhase) portletRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE)) != Bridge.PortletPhase.RESOURCE_PHASE) {
                viewId = portletRequest.getParameter(bridgeConfig.getViewIdRenderParameterName());
            } else {
                viewId = portletRequest.getParameter(bridgeConfig.getViewIdResourceParameterName());
            }

            // ViewIds stored in RenderParams are encoded with the Mode to which they apply
            // Ensure current request Mode matches before using the viewId portion
            if (viewId != null) {
                int i = viewId.indexOf(':');
                if (i >= 0) {

                    String mode = viewId.substring(0, i);
                    viewId = viewId.substring(i + 1);
                    if (!mode.equalsIgnoreCase(requestedMode)) {
                        viewId = null; // didn't match so don't use it
                    }
                }
            }
        }

        if (null != viewId && excludeQueryString) {
            viewId = excludeQuery(viewId);
        }

        return viewId;
    }
}
