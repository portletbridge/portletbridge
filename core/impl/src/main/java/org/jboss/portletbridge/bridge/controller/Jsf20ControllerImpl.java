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
package org.jboss.portletbridge.bridge.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRemoveFromViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.ResponseStateManager;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.event.EventNavigationResult;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.event.BridgePostConstructFacesContextSystemEvent;
import org.jboss.portletbridge.bridge.event.BridgePreReleaseFacesContextSystemEvent;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScope;
import org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager;
import org.jboss.portletbridge.context.AbstractExternalContext;
import org.jboss.portletbridge.context.flash.PortletFlash;
import org.jboss.portletbridge.lifecycle.PortalPhaseListener;
import org.jboss.portletbridge.lifecycle.PublicParameterPhaseListener;
import org.jboss.portletbridge.lifecycle.RenderResponsePhaseListener;
import org.jboss.portletbridge.util.BeanWrapper;
import org.jboss.portletbridge.util.FacesMessageWrapper;
import org.jboss.portletbridge.util.ParameterFunction;
import org.jboss.portletbridge.util.PublicParameterUtil;

import com.sun.faces.context.StateContext;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class Jsf20ControllerImpl implements BridgeController {
    protected BridgeConfig bridgeConfig = null;
    protected FacesContextFactory facesContextFactory = null;

    protected static final String RENDER_REDIRECT_VIEW_PARAMS = "org.jboss.portletbridge.renderRedirectViewParams";
    protected static final String RENDER_REDIRECT_PUBLIC_PARAM_MAP = "org.jboss.portletbridge.renderRedirectPublicParamMap";
    private static final String FACES_MESSAGES_WRAPPER = "org.jboss.portletbridge.facesMessagesHolder";
    private static final String MANAGED_BEANS_WRAPPER = "org.jboss.portletbridge.managedBeansHolder";
    private static final String REQUEST_SCOPE_ID = "__pbrReqScopeId";
    private static final String FACES_EXECUTED_DURING_ACTION_REQUEST = "facesDuringAction";

    public Jsf20ControllerImpl(BridgeConfig bridgeConfig) {
        this.bridgeConfig = bridgeConfig;
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#destroy()
     */
    public void destroy() {
        bridgeConfig = null;
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#processPortletAction(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void processPortletAction(BridgeContext bridgeContext) throws BridgeException {

        FacesContext facesContext = null;
        Lifecycle facesLifecycle = null;
        PublicParameterPhaseListener ppPhaseListener = null;

        // Remove any lingering BridgeRequestScopes. Not required by spec, but prevents issues
        bridgeContext.getBridgeRequestScopeManager().removeRequestScope(bridgeContext, bridgeContext.getFacesViewId(true));

        try {
            facesLifecycle = getFacesLifecycle();
            facesContext = getFacesContext(bridgeContext, facesLifecycle);

            ppPhaseListener = new PublicParameterPhaseListener(bridgeConfig, bridgeContext.getPortletRequest());
            facesLifecycle.addPhaseListener(ppPhaseListener);

            performPreExecuteTasks(facesContext, facesLifecycle);

            facesLifecycle.execute(facesContext);

            if (!facesContext.getResponseComplete()) {
                encodeStateAware(bridgeContext, facesContext);
            }
        } catch (Exception e) {
            throwBridgeException(e);
        } finally {
            if (null != facesLifecycle && null != ppPhaseListener) {
                facesLifecycle.removePhaseListener(ppPhaseListener);
            }

            if (null != facesContext) {
                releaseFacesContext(bridgeContext, facesContext);
            }
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#handlePortletEvent(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void handlePortletEvent(BridgeContext bridgeContext) throws BridgeException {

        FacesContext facesContext = null;
        Lifecycle facesLifecycle = null;
        PublicParameterPhaseListener ppPhaseListener = null;
        boolean removeScope = false;

        // Carry forward current render parameters
        ((EventResponse) bridgeContext.getPortletResponse()).setRenderParameters((EventRequest) bridgeContext
                .getPortletRequest());

        // As per spec 5.2.5
        if (null == bridgeConfig.getEventHandler()) {
            bridgeConfig.getLogger().log(
                    Level.ERROR,
                    "The EventHandler is null for " + bridgeConfig.getPortletConfig().getPortletName()
                            + ". Ensure your portlet.xml settings are correct and that you have implemented the "
                            + "BridgeEventHandler in your application.  The event has not been processed.");
            return;
        }

        BridgeRequestScope scope = getBridgeRequestScope(bridgeContext);

        try {
            facesLifecycle = getFacesLifecycle();
            facesContext = getFacesContext(bridgeContext, facesLifecycle);

            if (null != scope) {
                restoreScopeData(bridgeContext, facesContext, scope);
            }

            ppPhaseListener = new PublicParameterPhaseListener(bridgeConfig, bridgeContext.getPortletRequest());
            facesLifecycle.addPhaseListener(ppPhaseListener);

            performPreExecuteTasks(facesContext, facesLifecycle);

            facesLifecycle.execute(facesContext);

            EventNavigationResult eventResult = bridgeConfig.getEventHandler().handleEvent(facesContext,
                    ((EventRequest) bridgeContext.getPortletRequest()).getEvent());

            if (facesContext.getResponseComplete()) {
                // Redirected during event handling
                removeScope = true;
            } else if (null != eventResult) {
                facesContext.getApplication().getNavigationHandler()
                        .handleNavigation(facesContext, eventResult.getFromAction(), eventResult.getOutcome());

                if (facesContext.getResponseComplete()) {
                    // Redirected due to navigation rule
                    removeScope = true;
                }
            }

            BridgeRequestScope currentScope = encodeStateAware(bridgeContext, facesContext);
            if (null != currentScope && !currentScope.equals(scope)) {
                removeScope = true;
            }
        } catch (Exception e) {
            throwBridgeException(e);
        } finally {
            if (removeScope) {
                if (null != scope) {
                    bridgeContext.getBridgeRequestScopeManager().removeRequestScope(bridgeContext, scope);
                }
            }

            if (null != facesLifecycle && null != ppPhaseListener) {
                facesLifecycle.removePhaseListener(ppPhaseListener);
            }

            if (null != facesContext) {
                releaseFacesContext(bridgeContext, facesContext);
            }
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#renderPortletHead(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void renderPortletHead(BridgeContext bridgeContext) throws BridgeException {
        // Do Nothing
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#renderPortletBody(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void renderPortletBody(BridgeContext bridgeContext) throws BridgeException {

        FacesContext facesContext = null;
        Lifecycle facesLifecycle = null;

        BridgeRequestScope scope = getBridgeRequestScope(bridgeContext);

        try {
            facesLifecycle = getFacesLifecycle();
            facesContext = getFacesContext(bridgeContext, facesLifecycle);

            // Restore scope if present and mode not changed
            if (null != scope) {
                if (scope.getPortletMode().equals(bridgeContext.getPortletRequest().getPortletMode().toString())) {
                    restoreScopeData(bridgeContext, facesContext, scope);
                } else {
                    clearBridgeRequestScope(bridgeContext);
                }
            }

            if (facesContext.getExternalContext().getRequestParameterValuesMap()
                    .containsKey(ResponseStateManager.VIEW_STATE_PARAM)) {
                facesContext.getExternalContext().getRequestMap().put(Bridge.IS_POSTBACK_ATTRIBUTE, Boolean.TRUE);
            }

            renderFaces(bridgeContext, facesContext, facesLifecycle, scope, null);
        } catch (Exception e) {
            throwBridgeException(e);
        } finally {
            // PBR-499 Clear Bridge Scope after Render unless config says to retain it
            if (!bridgeConfig.isBridgeScopePreservedPostRender()) {
                clearBridgeRequestScope(bridgeContext);
            }

            if (null != facesContext) {
                releaseFacesContext(bridgeContext, facesContext);
            }
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.controller.BridgeController#renderResource(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void renderResource(BridgeContext bridgeContext) throws BridgeException {
        FacesContext facesContext = null;
        Lifecycle facesLifecycle = null;

        try {
            facesLifecycle = getFacesLifecycle();
            facesContext = getFacesContext(bridgeContext, facesLifecycle);
            ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
            ResourceRequest resourceRequest = (ResourceRequest) bridgeContext.getPortletRequest();
            String resourceId = resourceRequest.getResourceID();

            if (resourceHandler.isResourceRequest(facesContext)) {
                // JSF2 Resource
                resourceHandler.handleResourceRequest(facesContext);
            } else if (null != resourceId) {
                renderNonFacesResource(bridgeContext, resourceId);
            } else {
                renderFacesResource(bridgeContext, facesContext, facesLifecycle);
            }
        } catch (Exception e) {
            throwBridgeException(e);
        } finally {
            if (null != facesContext) {
                releaseFacesContext(bridgeContext, facesContext);
            }
        }
    }

    protected BridgeRequestScope encodeStateAware(BridgeContext bridgeContext, FacesContext facesContext) {
        BridgeRequestScope scope = null;

        String viewId = facesContext.getViewRoot().getViewId();
        String queryString = bridgeContext.getNavigationalQueryString();

        ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
        String viewUrl = viewHandler.getActionURL(facesContext, viewId);
        if (null != queryString && queryString.length() > 1) {
            if (viewUrl.indexOf('?') > 0) {
                viewUrl += "&" + queryString.substring(1);
            } else {
                viewUrl = viewUrl.concat("?").concat(queryString);
            }
        }

        facesContext.getExternalContext().encodeActionURL(viewUrl);

        // Process Public Parameter changes
        processOutgoingParameters(facesContext, bridgeContext.getPortletRequest(),
                (StateAwareResponse) bridgeContext.getPortletResponse());

        if (bridgeContext.isBridgeRequestScopePreserved()) {
            scope = bridgeContext.getBridgeScope();
            if (null == scope) {
                // Action starts new lifecycle
                scope = newBridgeRequestScope(bridgeContext);
            }

            // Remove StateContext from FacesContext to prevent issues with Dynamic Actions being retained between Renders
            facesContext.getAttributes().remove(StateContext.class.getName() + "_KEY");

            // Unregister PostAddToViewEvent listener if it's StateContext.AddRemoveListener
            List<SystemEventListener> postAddListeners = facesContext.getViewRoot().getViewListenersForEventClass(PostAddToViewEvent.class);
            if (null != postAddListeners && !postAddListeners.isEmpty()) {
                for (SystemEventListener listener : postAddListeners) {
                    if (listener.getClass().getName().equals(stateContextListenerClassname())) {
                        facesContext.getViewRoot().unsubscribeFromViewEvent(PostAddToViewEvent.class, listener);
                    }
                }
            }
            // Unregister PreRemoveFromViewEvent listener if it's StateContext.AddRemoveListener
            List<SystemEventListener> preRemoveListeners = facesContext.getViewRoot().getViewListenersForEventClass(PreRemoveFromViewEvent.class);
            if (null != preRemoveListeners && !preRemoveListeners.isEmpty()) {
                for (SystemEventListener listener : preRemoveListeners) {
                    if (listener.getClass().getName().equals(stateContextListenerClassname())) {
                        facesContext.getViewRoot().unsubscribeFromViewEvent(PreRemoveFromViewEvent.class, listener);
                    }
                }
            }

            saveFacesView(scope, facesContext);
            saveMessages(facesContext);

            if (Bridge.PortletPhase.ACTION_PHASE == bridgeContext.getPortletRequestPhase()) {
                // Save View State Param
                String viewState = facesContext.getExternalContext().getRequestParameterMap()
                        .get(ResponseStateManager.VIEW_STATE_PARAM);

                if (null != viewState) {
                    scope.put(AbstractExternalContext.FACES_VIEW_STATE, viewState);
                }

                saveActionParams(bridgeContext, facesContext);

                // Save parameter to let us know Faces Lifecycle was executed
                scope.put(FACES_EXECUTED_DURING_ACTION_REQUEST, "true");
            }

            saveBeans(bridgeContext, facesContext);

            scope.putAll(facesContext.getExternalContext().getRequestMap());

            ((StateAwareResponse) bridgeContext.getPortletResponse()).setRenderParameter(REQUEST_SCOPE_ID, scope.getId());
        }
        return scope;
    }

    protected BridgeRequestScope newBridgeRequestScope(BridgeContext bridgeContext) {
        BridgeRequestScopeManager scopeManager = bridgeContext.getBridgeRequestScopeManager();
        BridgeRequestScope scope = scopeManager.createRequestScope(bridgeContext, bridgeContext.getFacesViewId(true));

        // Add excludes
        scope.setExcludedEntries(bridgeConfig.getExcludedRequestAttributes());
        scope.addExcludedEntries(bridgeContext.getPreFacesRequestAttrNames());
        return scope;
    }

    protected void renderFaces(BridgeContext bridgeContext, FacesContext facesContext, Lifecycle facesLifecycle,
            BridgeRequestScope scope, String redirectViewId) throws BridgeException, NullPointerException {
        PublicParameterPhaseListener ppPhaseListener = null;
        PortalPhaseListener portalPhaseListener = null;
        RenderResponsePhaseListener renderResponsePhaseListener = null;

        if (!bridgeContext.hasRenderRedirect()) {
            try {
                ppPhaseListener = new PublicParameterPhaseListener(bridgeConfig, bridgeContext.getPortletRequest());
                portalPhaseListener = new PortalPhaseListener();
                facesLifecycle.addPhaseListener(ppPhaseListener);
                facesLifecycle.addPhaseListener(portalPhaseListener);

                performPreExecuteTasks(facesContext, facesLifecycle);

                if (scope != null && Boolean.parseBoolean((String) scope.get(FACES_EXECUTED_DURING_ACTION_REQUEST))) {
                    scope.remove(FACES_EXECUTED_DURING_ACTION_REQUEST);
                    PhaseEvent fakeRestoreViewEvent = new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, facesLifecycle);
                    ppPhaseListener.afterPhase(fakeRestoreViewEvent);

                    // PBR-558 Trigger RESTORE_VIEW event for WeldPhaseListener to activate Conversation context
                    for (PhaseListener listener : facesLifecycle.getPhaseListeners()) {
                        if ("WeldPhaseListener".equals(listener.getClass().getSimpleName())) {
                            listener.beforePhase(fakeRestoreViewEvent);
                            break;
                        }
                    }
                } else {
                    // PBR-510 - Only end facesLifecycle.execute() after RESTORE_VIEW if we don't want f:viewParam to work
                    if (bridgeConfig.isViewParamHandlingDisabled()) {
                        renderResponsePhaseListener = new RenderResponsePhaseListener();
                        facesLifecycle.addPhaseListener(renderResponsePhaseListener);
                    }

                    facesLifecycle.execute(facesContext);
                }
            } finally {
                if (null != facesLifecycle) {
                    if (null != ppPhaseListener) {
                        facesLifecycle.removePhaseListener(ppPhaseListener);
                    }
                    if (null != portalPhaseListener) {
                        facesLifecycle.removePhaseListener(portalPhaseListener);
                    }
                    if (null != renderResponsePhaseListener) {
                        facesLifecycle.removePhaseListener(renderResponsePhaseListener);
                    }
                }
            }

            if (!facesContext.getResponseComplete()) {
                facesLifecycle.render(facesContext);
                bridgeContext.setViewHistory(bridgeContext.getPortletRequest().getPortletMode().toString(), facesContext
                        .getViewRoot().getViewId(), true);
            }
        }

        if (bridgeContext.hasRenderRedirect()) {
            redirectViewId = bridgeContext.getRedirectViewId();
            renderRedirect(bridgeContext, facesContext, facesLifecycle, scope, redirectViewId);
            facesContext = FacesContext.getCurrentInstance();
        } else {
            encodeMarkupResponse(bridgeContext, facesContext, scope);
        }
    }

    protected void renderRedirect(BridgeContext bridgeContext, FacesContext facesContext, Lifecycle facesLifecycle,
            BridgeRequestScope scope, String redirectViewId) {
        bridgeContext.setRedirectViewId(redirectViewId);

        releaseFacesContext(bridgeContext, facesContext);

        bridgeContext.setRenderRedirect(false);

        if (null != scope) {
            bridgeContext.getBridgeRequestScopeManager().removeRequestScope(bridgeContext, scope);
        }

        facesContext = getFacesContext(bridgeContext, facesLifecycle);

        ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
        UIViewRoot uiViewRoot = viewHandler.createView(facesContext, redirectViewId);
        facesContext.setViewRoot(uiViewRoot);

        renderFaces(bridgeContext, facesContext, facesLifecycle, null, null);

        facesContext = FacesContext.getCurrentInstance();
    }

    protected void encodeMarkupResponse(BridgeContext bridgeContext, FacesContext facesContext, BridgeRequestScope scope) {
        if (Bridge.PortletPhase.RENDER_PHASE == bridgeContext.getPortletRequestPhase()) {
            if (null == scope) {
                return;
            }

            saveFacesView(scope, facesContext);
        } else {
            // We're in Resource Request
            if (isBridgeScopeAjaxEnabled()) {
                if (null == scope) {
                    scope = newBridgeRequestScope(bridgeContext);
                }

                saveFacesView(scope, facesContext);

                if (isFacesMessagesStoredOnAjax()) {
                    saveMessages(facesContext);
                }
                scope.putAll(facesContext.getExternalContext().getRequestMap());
            }
        }

    }

    protected boolean isBridgeScopeAjaxEnabled() {
        return bridgeConfig.isBridgeScopeEnabledOnAjaxRequest();
    }

    protected boolean isFacesMessagesStoredOnAjax() {
        return bridgeConfig.isFacesMessagesStoredOnAjaxRequest();
    }

    protected void restoreScopeData(BridgeContext bridgeContext, FacesContext facesContext, BridgeRequestScope scope) {
        if (null == scope) {
            return;
        }

        restoreFacesViewFromScope(facesContext, scope);
        restoreMessages(facesContext, scope);
        restoreBeans(bridgeContext, scope);

        Set<Map.Entry<String, Object>> keys = scope.entrySet();
        for (Entry<String, Object> entry : keys) {
            bridgeContext.getPortletRequest().setAttribute(entry.getKey(), entry.getValue());
        }
    }

    protected void restoreFacesViewFromScope(FacesContext facesContext, BridgeRequestScope scope) {
        UIViewRoot viewRoot = null;

        viewRoot = (UIViewRoot) scope.remove(VIEW_ROOT);
        if (null != viewRoot) {
            facesContext.setViewRoot(viewRoot);
        }
    }

    protected void restoreMessages(FacesContext facesContext, BridgeRequestScope scope) {
        if (facesContext.getExternalContext().getRequest() instanceof RenderRequest) {
            FacesMessageWrapper messageWrapper = (FacesMessageWrapper) scope.remove(FACES_MESSAGES_WRAPPER);
            if (null != messageWrapper) {
                for (String clientId : messageWrapper.getClientIds()) {
                    for (FacesMessage msg : messageWrapper.getMessages(clientId)) {
                        facesContext.addMessage(clientId, msg);
                    }
                }
            }
        }
    }

    protected void restoreBeans(BridgeContext bridgeContext, BridgeRequestScope scope) {
        BeanWrapper beanWrapper = (BeanWrapper) scope.remove(MANAGED_BEANS_WRAPPER);

        if (null != beanWrapper) {
            PortletRequest request = bridgeContext.getPortletRequest();
            for (String name : beanWrapper.getBeanNames()) {
                request.setAttribute(name, beanWrapper.getBean(name));
            }
        }
    }

    protected void saveFacesView(BridgeRequestScope scope, FacesContext facesContext) {
        scope.put(VIEW_ROOT, facesContext.getViewRoot());
    }

    protected void saveMessages(FacesContext facesContext) {
        Iterator<String> idsWithMessages = facesContext.getClientIdsWithMessages();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();

        if (idsWithMessages.hasNext()) {
            FacesMessageWrapper messageWrapper = new FacesMessageWrapper();

            while (idsWithMessages.hasNext()) {
                String id = idsWithMessages.next();
                Iterator<FacesMessage> messages = facesContext.getMessages(id);
                while (messages.hasNext()) {
                    FacesMessage message = messages.next();
                    messageWrapper.addMessage(id, message);
                }
            }
            requestMap.put(FACES_MESSAGES_WRAPPER, messageWrapper);
        } else {
            requestMap.remove(FACES_MESSAGES_WRAPPER);
        }
    }

    protected void saveBeans(BridgeContext bridgeContext, FacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();
        List<String> existingAttributes = null;
        if (null != bridgeContext) {
            existingAttributes = bridgeContext.getPreFacesRequestAttrNames();
        }
        if (null == existingAttributes) {
            existingAttributes = new ArrayList<String>();
        } else {
            // Create local copy for merge with initial parameters.
            existingAttributes = new ArrayList<String>(existingAttributes);
        }

        Map<String, Object> requestMap = externalContext.getRequestMap();
        BeanWrapper beanWrapper = new BeanWrapper();

        for (Iterator<Entry<String, Object>> iterator = requestMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, Object> entry = iterator.next();
            String attributeName = entry.getKey();
            if (!existingAttributes.contains(attributeName)) {
                Object bean = entry.getValue();
                if (null != bean && !bridgeContext.getBridgeScope().isExcluded(attributeName, bean)) {
                    beanWrapper.addBean(attributeName, bean);
                }
            }
        }

        if (beanWrapper.getBeanNames().size() > 0) {
            requestMap.put(MANAGED_BEANS_WRAPPER, beanWrapper);
        }
    }

    protected void saveActionParams(BridgeContext bridgeContext, FacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();

        if (bridgeConfig.hasPreserveActionParameters()) {
            BridgeRequestScope scope = bridgeContext.getBridgeScope();
            Map<String, String[]> params = new HashMap<String, String[]>(externalContext.getRequestParameterValuesMap());
            params.remove(ResponseStateManager.VIEW_STATE_PARAM);

            scope.put(ACTION_PARAMETERS, params);
        }
    }

    protected void renderFacesResource(BridgeContext bridgeContext, FacesContext facesContext, Lifecycle facesLifecycle)
            throws BridgeException {
        BridgeRequestScope scope = getBridgeRequestScope(bridgeContext);

        renderFaces(bridgeContext, facesContext, facesLifecycle, scope, null);
    }

    protected void renderNonFacesResource(BridgeContext bridgeContext, String resourceId) throws BridgeException,
            PortletException, IOException {

        if (null != resourceId) {
            PortletContext portletContext = bridgeContext.getPortletContext();
            PortletRequestDispatcher dispatcher = portletContext.getRequestDispatcher(resourceId);

            if (null != dispatcher) {
                String mimeType = portletContext.getMimeType(resourceId);

                if (null == mimeType) {
                    int lastIndexOfSlash = resourceId.lastIndexOf('/');
                    if (lastIndexOfSlash >= 0) {
                        resourceId = resourceId.substring(lastIndexOfSlash + 1);
                    }
                    int indexOfQuestion = resourceId.indexOf('?');
                    if (indexOfQuestion >= 0) {
                        resourceId = resourceId.substring(0, indexOfQuestion);
                    }
                    mimeType = portletContext.getMimeType(resourceId);
                }

                if (null != mimeType) {
                    ((ResourceResponse) bridgeContext.getPortletResponse()).setContentType(mimeType);
                }

                dispatcher.forward(bridgeContext.getPortletRequest(), bridgeContext.getPortletResponse());
            }
        }
    }

    protected FacesContext getFacesContext(BridgeContext bridgeContext, Lifecycle facesLifecycle) throws FacesException {
        FacesContext facesContext = getFacesContextFactory().getFacesContext(bridgeContext.getPortletContext(),
                bridgeContext.getPortletRequest(), bridgeContext.getPortletResponse(), facesLifecycle);

        // Fire Post Construct FacesContext system event
        fireFacesSystemEvent(bridgeContext, BridgePostConstructFacesContextSystemEvent.class);

        return facesContext;
    }

    protected FacesContextFactory getFacesContextFactory() throws FacesException {
        if (null == facesContextFactory) {
            facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        }

        return facesContextFactory;
    }

    protected Lifecycle getFacesLifecycle() throws FacesException {
        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        return lifecycleFactory.getLifecycle(bridgeConfig.getLifecycleId());
    }

    protected void releaseFacesContext(BridgeContext bridgeContext, FacesContext facesContext) {
        fireFacesSystemEvent(bridgeContext, BridgePreReleaseFacesContextSystemEvent.class);

        PortletFlash.needHttpResponse.remove();

        facesContext.release();
    }

    protected void fireFacesSystemEvent(BridgeContext bridgeContext, Class<? extends SystemEvent> eventClass) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        facesContext.getApplication().publishEvent(facesContext, eventClass, bridgeContext);
    }

    protected BridgeRequestScope getBridgeRequestScope(BridgeContext bridgeContext) {
        BridgeRequestScope scope = null;
        if (Bridge.PortletPhase.RENDER_PHASE == bridgeContext.getPortletRequestPhase()) {
            String bridgeRequestScopeId = bridgeContext.getPortletRequest().getParameter(REQUEST_SCOPE_ID);
            if (null != bridgeRequestScopeId) {
                scope = bridgeContext.getBridgeRequestScopeManager().getRequestScopeById(bridgeContext, bridgeRequestScopeId);
            }
        }
        if (null == scope) {
            scope = bridgeContext.getBridgeScope();
        }
        return scope;
    }

    protected void clearBridgeRequestScope(BridgeContext bridgeContext) {
        BridgeRequestScope scope = bridgeContext.getBridgeScope();
        if (null != scope) {
            scope.clear();
            bridgeContext.getBridgeRequestScopeManager().removeRequestScope(bridgeContext, scope);
        }
    }

    protected void throwBridgeException(Exception e) throws BridgeException {
        if (!(e instanceof BridgeException)) {
            e = new BridgeException(e);
        }
        throw (BridgeException) e;
    }

    protected void processOutgoingParameters(FacesContext facesContext, PortletRequest request,
            final StateAwareResponse response) {
        Map<String, String> publicParameterMapping = bridgeConfig.getPublicRenderParameterMappings();
        Enumeration<String> parameterNames = bridgeConfig.getPortletConfig().getPublicRenderParameterNames();

        if (null != publicParameterMapping && publicParameterMapping.size() > 0 && parameterNames.hasMoreElements()) {
            ParameterFunction outgoingFunction = new ParameterFunction() {
                public boolean processParameter(ELContext elContext, Map<String, String[]> publicParameters, String name,
                        ValueExpression valueExpression) {
                    boolean valueChanged = false;
                    String modelValue = (String) valueExpression.getValue(elContext);
                    if (null != modelValue) {
                        String[] values = publicParameters.get(name);
                        String parameterValue = (null != values && values.length > 0) ? values[0] : null;
                        if (null == parameterValue || !modelValue.equals(parameterValue)) {
                            response.setRenderParameter(name, modelValue);
                            valueChanged = true;
                        }
                    } else if (publicParameters.containsKey(name)) {
                        response.removePublicRenderParameter(name);
                        valueChanged = true;
                    }
                    return valueChanged;
                }

            };

            PublicParameterUtil.processPublicParameters(facesContext, request, publicParameterMapping, parameterNames,
                    outgoingFunction, bridgeConfig.getPortletConfig().getPortletName());

        }
    }

    /**
     * Perform any tasks that need to be completed before calling <code>Lifecycle.execute()</code>. For JSF 2.0, nothing
     * is required.
     *
     * @param facesContext      Faces Context for the current portlet request.
     * @param facesLifecycle    Lifecycle for the current portlet request.
     */
    protected void performPreExecuteTasks(FacesContext facesContext, Lifecycle facesLifecycle) {
        // Do Nothing for JSF 2.0
    }

    protected String stateContextListenerClassname() {
        return "com.sun.faces.context.StateContext$AddRemoveListener";
    }
}
