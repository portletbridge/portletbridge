/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;
import javax.faces.webapp.FacesServlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import org.jboss.portletbridge.context.InitFacesContext;
import org.jboss.portletbridge.context.PortalActionURL;
import org.jboss.portletbridge.context.PortletBridgeContext;
import org.jboss.portletbridge.lifecycle.PortletLifecycle;
import org.jboss.portletbridge.util.BridgeLogger;
import org.jboss.portletbridge.util.FacesConfig;
import org.jboss.portletbridge.util.Util;
import org.jboss.portletbridge.util.WebXML;

/**
 * @author asmirnov
 */
public class AjaxPortletBridge implements Bridge, BridgeConfig {

	public static final String VIEWID_HISTORY_PREFIX = Bridge.VIEWID_HISTORY
	        + ".";
	private static final Logger log = BridgeLogger.BRIDGE.getLogger();
	private static final String EXCEPTION_HANDLER_CLASS_PARAMETER = ExceptionHandler.class
	        .getName();
	public static final String AJAX_NAMESPACE_PARAMETER = "org.ajax4jsf.portlet.NAMESPACE";
	public static final String VIEW_ID_PARAMETERS = "org.jboss.portletbridge.VIEW_ID_PARAMETERS";
	/**
	 * Flag indicates what bridge has been initialized.
	 */
	private boolean initialized = false;
	/**
	 * Saved portlet configuration.
	 */
	private PortletConfig portletConfig;
	/**
	 * 
	 */
	private ExceptionHandler exceptionHandler;
	/**
	 * 
	 */
	private Lifecycle lifecycle;
	/**
	 * 
	 */
	private FacesContextFactory facesContextFactory;
	/**
	 * 
	 */
	private List<String> facesServletMappings;
	/**
	 * 
	 */
	private Set<ExcludedRequestAttribute> excludedAttributes;
	/**
	 * 
	 */
	private boolean preserveActionParams;

	private int numberOfRequestScopes = RequestScopeManager.DEFAULT_MAX_MANAGED_SCOPES;
	/**
	 * 
	 */
	private Map<String, String> defaultViewIdMap;
	/**
	 * 
	 */
	private Map<Class<? extends Throwable>, String> errorPages;

	// public RichFacesHelper richFacesHelper;
	// public boolean RICHFACES_ENABLED;
	/**
	 * 
	 */
	private Application application;
	/**
	 * 
	 */
	private BridgeEventHandler eventHandler;
	/**
	 * 
	 */
	private BridgePublicRenderParameterHandler publicParameterHandler;
	/**
	 * 
	 */
	private BridgeStrategy strategy;
	private Map<String, String> publicParameterMapping;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.faces.Bridge#destroy()
	 */
	public void destroy() {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Destroy portletbridge "
			        + getPortletConfig().getPortletName());
		}
		this.lifecycle = null;
		this.facesContextFactory = null;
		// this.application.removeELContextListener(this);
		this.application = null;
		this.initialized = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.faces.Bridge#init(javax.portlet.PortletConfig)
	 */
	@SuppressWarnings("unchecked")
	public void init(PortletConfig config) throws BridgeException {
		if (null == config) {
			throw new NullPointerException(
			        "No PortletConfig at the bridge initialization");
		}
		if (initialized) {
			throw new BridgeException("JSF portlet bridge already initialized");
		}
		String portletName = config.getPortletName();
		if (log.isLoggable(Level.FINE)) {
			log.fine("Start portletbridge initialization for " + portletName);
		}
		this.portletConfig = config;
		PortletContext portletContext = config.getPortletContext();
		initFaces(portletContext);
		// add locales to faces application
		transferLocales(config);
		// Register exception handler.
		exceptionHandler = createExceptionHandler();
		// Parse web.xml for a Faces Servlet mappings.
		WebXML webXml = new WebXML();
		webXml.parse(portletContext);
		this.facesServletMappings = webXml.getFacesServletMappings();
		errorPages = webXml.getErrorViews();
		// Get all excluded request attributes names.
		this.excludedAttributes = new HashSet<ExcludedRequestAttribute>();
		String bridgeParametersPrefix = Bridge.BRIDGE_PACKAGE_PREFIX
		        + portletName + ".";
		List<String> excluded = (List<String>) portletContext
		        .getAttribute(bridgeParametersPrefix
		                + Bridge.EXCLUDED_REQUEST_ATTRIBUTES);
		if (null != excluded) {
			for (String name : excluded) {
				excludedAttributes.add(new ExcludedRequestAttribute(name));
			}
		}
		String maxScopesParameter = portletContext
		        .getInitParameter(Bridge.MAX_MANAGED_REQUEST_SCOPES);
		if (null != maxScopesParameter) {
			numberOfRequestScopes = Integer.parseInt(maxScopesParameter);
		}

		FacesConfig facesConfig = new FacesConfig();
		facesConfig.parse(portletContext);
		excluded = facesConfig.getExcludedAttributes();
		if (null != excluded) {
			for (String name : excluded) {
				excludedAttributes.add(new ExcludedRequestAttribute(name));
			}
		}
		publicParameterMapping = facesConfig.getParameterMapping();
		// Get preserve action parameters flag
		Boolean preserveParams = (Boolean) portletContext
		        .getAttribute(bridgeParametersPrefix
		                + Bridge.PRESERVE_ACTION_PARAMS);
		this.preserveActionParams = Boolean.TRUE.equals(preserveParams);
		// Get devault view's Map.
		this.defaultViewIdMap = (Map<String, String>) portletContext
		        .getAttribute(bridgeParametersPrefix
		                + Bridge.DEFAULT_VIEWID_MAP);
		if (null == this.defaultViewIdMap || 0 == this.defaultViewIdMap.size()) {
			throw new BridgeException("No JSF view id's defined in portlet");
		}
		// Event Handler
		eventHandler = (BridgeEventHandler) portletContext
		        .getAttribute(bridgeParametersPrefix
		                + Bridge.BRIDGE_EVENT_HANDLER);
		// Public Parameters Handler
		publicParameterHandler = (BridgePublicRenderParameterHandler) portletContext
		        .getAttribute(bridgeParametersPrefix
		                + Bridge.BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER);
		// Initialization done.
		initialized = true;
		if (log.isLoggable(Level.FINE)) {
			log.fine("Done portletbridge initialization for " + portletName);
		}
	}

	/**
	 * @param portletContext
	 */
	protected void initFaces(PortletContext portletContext) {
		try {
			// get faces lifecycle instance. Name of the Lifecycle can be
			// changed by the init parameter, as described in the JSR 301
			// PLT
			// 3.2
			strategy = BridgeStrategy.getCurrentStrategy(this);
			LifecycleFactory factory = (LifecycleFactory) FactoryFinder
			        .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			String lifecycleId = portletContext
			        .getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
			if (null == lifecycleId) {
            if(Util.compareCurrentJSFVersion("2.0.3") < 0){
               lifecycleId = PortletLifecycle.FIX_PORTLET_LIFECYCLE;
            }else{
               lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
			}
			if (log.isLoggable(Level.FINE)) {
				log.fine("Create instance of a JSF lifecycle " + lifecycleId);
			}
			this.lifecycle = factory.getLifecycle(lifecycleId);
			// get faces context factory instance
			this.facesContextFactory = (FacesContextFactory) FactoryFinder
			        .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			// get Application instance.
			ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder
			        .getFactory(FactoryFinder.APPLICATION_FACTORY);
			application = appFactory.getApplication();

			// Setup appropriate UIViewRoot and ViewRootRenderer
			RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
			        .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
			// Setup renderkit for portlet bridge.
			FacesContext context = new InitFacesContext(application,
			        portletContext);
			strategy.init(context, renderKitFactory);
			context.release();
		} catch (FacesException e) {
			throw new BridgeException("JSF Initialization error", e);
		}
	}

	protected Application getApplication() {
		return application;
	}

	/**
	 * Transfer protlet locales to JSF {@link Application}. JSF framework gets
	 * locales from faces-config.xml but portlet can define its own.
	 * 
	 * @param config
	 */
	protected void transferLocales(PortletConfig config) {
		// Get configured faces locales.
		Iterator<Locale> supportedLocales = getApplication()
		        .getSupportedLocales();
		HashSet<Locale> facesLocales = new HashSet<Locale>();
		while (supportedLocales.hasNext()) {
			facesLocales.add(supportedLocales.next());
		}
		ArrayList<Locale> portletLocales = Collections.list(config
		        .getSupportedLocales());
		// Append portlet locales to Faces if portlet defines additional ones.
		// https://jira.jboss.org/jira/browse/PBR-100
		if (!facesLocales.containsAll(portletLocales)) {
			facesLocales.addAll(portletLocales);
			getApplication().setSupportedLocales(facesLocales);
		}
	}

	protected ExceptionHandler createExceptionHandler() {
		ExceptionHandler handler = createInstance(EXCEPTION_HANDLER_CLASS_PARAMETER);
		if (null == handler) {
			handler = new ExceptionHandlerImpl();
		}
		return handler;
	}

	@SuppressWarnings("unchecked")
	protected <E> E createInstance(String classNameParameter) {
		String className = getPortletConfig().getPortletContext()
		        .getInitParameter(classNameParameter);
		E instance = null;
		if (null != className) {
			ClassLoader classLoader = getClassLoader();
			try {
				Class<? extends E> clazz = (Class<? extends E>) classLoader
				        .loadClass(className);
				instance = clazz.newInstance();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Class load error: " + className, e);
			}
		}
		return instance;
	}

	protected ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread()
		        .getContextClassLoader();
		if (null == classLoader) {
			classLoader = this.getClass().getClassLoader();
		}
		return classLoader;
	}

	public void doFacesRequest(ActionRequest request, ActionResponse response)
	        throws BridgeException {
		assertParameters(request, response);
		if (log.isLoggable(Level.FINE)) {
			log.fine("Start bridge action request processing for portlet "
			        + getPortletName());
		}
		PortletBridgeContext bridgeContext = initRequest(request, response,
		        Bridge.PortletPhase.ACTION_PHASE);
		RequestScopeManager requestStateManager = RequestScopeManager
		        .getInstance(request, numberOfRequestScopes);
		StateId stateId = requestStateManager.getStateId(request, response);
		bridgeContext.setStateId(stateId);
		BridgeRequestScope windowState = new BridgeRequestScope();
		bridgeContext.setRequestScope(windowState);
		FacesContext facesContext = createFacesContext(request, response);
		try {
			strategy.beforeActionRequest(facesContext);
			processIncomingParameters(facesContext, request);
			execute(facesContext);
			strategy.afterActionRequestExecute(facesContext);
			// save request scope variables and Faces Messages.
			if (!facesContext.getResponseComplete()) {
				// Setup portlet modes from parameters.
				Map<String, String[]> viewIdParameters = bridgeContext
				        .getViewIdParameters();
				if (null != viewIdParameters && viewIdParameters.size() > 0) {
					processPortletParameters(response, stateId, facesContext,
					        facesContext.getViewRoot().getViewId(),
					        viewIdParameters);
				}
				processOutgoingParameters(facesContext, request, response);
				// Save view state for a render phases.
				facesContext.getApplication().getStateManager().saveView(
				        facesContext);
				windowState.saveRequest(facesContext, true);
			} else {
				windowState.reset();
				String redirectViewId = bridgeContext.getRedirectViewId();
				if (null != redirectViewId) {
					windowState.setViewId(redirectViewId);
					// Save redirect request parameters.
					Map<String, String[]> newRequestParameters = bridgeContext
					        .getRedirectRequestParameters();
					windowState.setRequestParameters(newRequestParameters);
					processPortletParameters(response, stateId, facesContext,
					        redirectViewId, newRequestParameters);
					processOutgoingParameters(facesContext, request, response);
				}
			}

		} catch (Exception e) {
			// handle exception.
			exceptionHandler.processActionException(facesContext, windowState,
			        e);
		} finally {
			if (null != bridgeContext.getRedirectViewId()
			        || !facesContext.getResponseComplete()) {
				requestStateManager.saveRequestScope(stateId, windowState);
				response.setRenderParameter(
				        RequestScopeManager.STATE_ID_PARAMETER, stateId
				                .toString());
			}
			strategy.afterActionRequest(facesContext);
			facesContext.release();
		}
	}

	private static interface ParameterFunction {
		public boolean processParameter(ELContext elContext,
		        Map<String, String[]> publicParameters, String name,
		        ValueExpression valueExpression);
	}

	private void processOutgoingParameters(FacesContext facesContext,
	        PortletRequest request, final StateAwareResponse response) {
		Enumeration<String> parameterNames = getPortletConfig()
		        .getPublicRenderParameterNames();
		if (null != publicParameterMapping && publicParameterMapping.size() > 0
		        && parameterNames.hasMoreElements()) {
			ParameterFunction outgoingFunction = new ParameterFunction() {
				public boolean processParameter(ELContext elContext,
				        Map<String, String[]> publicParameters, String name,
				        ValueExpression valueExpression) {
					boolean valueChanged = false;
					String modelValue = (String) valueExpression
					        .getValue(elContext);
					if (null != modelValue) {
						String[] values = publicParameters.get(name);
						String parameterValue = (null != values && values.length > 0) ? values[0]
						        : null;
						if (null == parameterValue
						        || !modelValue.equals(parameterValue)) {
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
			processPublicParameters(facesContext, request, parameterNames,
			        outgoingFunction);

		}
	}

	private void processIncomingParameters(FacesContext facesContext,
	        PortletRequest request) {
		Enumeration<String> parameterNames = getPortletConfig()
		        .getPublicRenderParameterNames();
		if (null != publicParameterMapping && publicParameterMapping.size() > 0
		        && parameterNames.hasMoreElements()) {
			ParameterFunction incomingFunction = new ParameterFunction() {
				public boolean processParameter(ELContext elContext,
				        Map<String, String[]> publicParameters, String name,
				        ValueExpression valueExpression) {
					boolean valueChanged = false;
					Object oldValue = valueExpression.getValue(elContext);
					if (publicParameters.containsKey(name)) {
						String[] values = publicParameters.get(name);
						String newValue = (null != values && values.length > 0) ? values[0]
						        : null;
						if (null == oldValue || !oldValue.equals(newValue)) {
							valueExpression.setValue(elContext, newValue);
							valueChanged = true;
						}
					} else if (null != oldValue) {
						valueExpression.setValue(elContext, null);
						valueChanged = true;
					}
					return valueChanged;
				}

			};
			boolean valueChanged = processPublicParameters(facesContext,
			        request, parameterNames, incomingFunction);
			if (valueChanged && null != publicParameterHandler) {
				publicParameterHandler.processUpdates(facesContext);
			}
		}
	}

	private boolean processPublicParameters(FacesContext facesContext,
	        PortletRequest request, Enumeration<String> parameterNames,
	        ParameterFunction function) {
		boolean valueChanged = false;
		ExpressionFactory expressionFactory = facesContext.getApplication()
		        .getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		StringBuilder preffixedName = new StringBuilder(getPortletName())
		        .append(':');
		int preffixLength = preffixedName.length();
		Map<String, String[]> publicParameters = request
		        .getPublicParameterMap();
		// Iterate over configured paremeter names.
		while (parameterNames.hasMoreElements()) {
			// First, check for a common name mapping.
			String name = parameterNames.nextElement();
			// Lookup for parameter name in the mappings table.
			String mappingEl = publicParameterMapping.get(name);
			if (null == mappingEl) {
				// if no common mapping found, check for preffixed name.
				preffixedName.setLength(preffixLength);
				mappingEl = publicParameterMapping.get(preffixedName.append(
				        name).toString());
			}
			if (null != mappingEl) {
				// Found a mapping for the patameter, process it.
				ValueExpression valueExpression = expressionFactory
				        .createValueExpression(elContext, mappingEl,
				                Object.class);
				valueChanged = function.processParameter(elContext,
				        publicParameters, name, valueExpression) ? true
				        : valueChanged;
			}
		}
		return valueChanged;
	}

	public void processPortletParameters(ActionResponse response,
	        StateId stateId, FacesContext facesContext, String redirectViewId,
	        Map<String, String[]> newRequestParameters)
	        throws MalformedURLException, PortletModeException {
		for (Entry<String, String[]> entry : newRequestParameters.entrySet()) {
			String key = entry.getKey();
			if (Bridge.PORTLET_MODE_PARAMETER.equals(key)) {
				String portletModeName = entry.getValue()[0];
				PortletMode mode = new PortletMode(portletModeName);
				PortalActionURL historyViewId = new PortalActionURL(
				        redirectViewId);
				stateId.setMode(mode.toString());
				historyViewId.setParameter(
				        RequestScopeManager.STATE_ID_PARAMETER, stateId
				                .toString());
				historyViewId.setParameter(Bridge.PORTLET_MODE_PARAMETER,
				        portletModeName);
				facesContext.getExternalContext().getSessionMap().put(
				        VIEWID_HISTORY_PREFIX + portletModeName,
				        historyViewId.toString());
				response.setPortletMode(mode);
			} else if (Bridge.PORTLET_WINDOWSTATE_PARAMETER.equals(key)) {
				try {
					WindowState state = new WindowState(entry.getValue()[0]);
					response.setWindowState(state);
				} catch (WindowStateException e) {
					// only valid modes supported.
				}

			}
		}
	}

	private PortletBridgeContext createBridgeContext(PortletRequest request) {
		PortletBridgeContext bridgeContext = new PortletBridgeContext(this);
		request.setAttribute(PortletBridgeContext.REQUEST_PARAMETER_NAME,
		        bridgeContext);
		bridgeContext.setInitialRequestAttributeNames(request
		        .getAttributeNames());
		return bridgeContext;
	}

	public void doFacesRequest(RenderRequest request, RenderResponse response)
	        throws BridgeException {
		assertParameters(request, response);

		if (log.isLoggable(Level.FINE)) {
			log.fine("Start bridge render request processing for portlet "
			        + getPortletName());
		}
		// TODO - detect head rendering.
		Object renderPartAttribute = request
		        .getAttribute(RenderRequest.RENDER_PART);
		RenderResponse wrappedResponse = strategy
		        .createResponseWrapper(response);

		PortletBridgeContext bridgeContext = initRequest(request,
		        wrappedResponse, Bridge.PortletPhase.RENDER_PHASE);
		RequestScopeManager requestStateManager = RequestScopeManager
		        .getInstance(request, numberOfRequestScopes);
		String namespace = wrappedResponse.getNamespace();
		StateId stateId = requestStateManager.getStateId(request, response);
		bridgeContext.setStateId(stateId);
		BridgeRequestScope windowState = requestStateManager
		        .getRequestScope(stateId);
		if (null == windowState) {
			windowState = new BridgeRequestScope();
			requestStateManager.saveRequestScope(stateId, windowState);
		}
		bridgeContext.setRequestScope(windowState);
		FacesContext facesContext = createFacesContext(request, wrappedResponse);
		try {
			windowState.restoreRequest(facesContext, true);
			// set portletbridge title if its set.
			ResourceBundle bundle = portletConfig.getResourceBundle(request
			        .getLocale());
			if (bundle != null) {
				String title = null;
				try {
					title = bundle.getString("javax.portlet.title");
					wrappedResponse.setTitle(title);
				} catch (Exception e) {
					// Ignore MissingResourceException
				}
			}

			try {
				// If we're using RichFaces, setup proper parameters for this
				// render
				// request
				strategy.beforeRenderRequest(facesContext);
				processIncomingParameters(facesContext, request);
				String redirectViewId = bridgeContext.getRedirectViewId();
				if (null == redirectViewId) {
					if (null == facesContext.getViewRoot()) {
						// Restore faces view ( Listener should stop processing
						// after restoreView phase ).
						execute(facesContext);
						// TODO - store changed parameters into url's.
						// processOutgoingParameters(facesContext);
					}
					if (!facesContext.getResponseComplete()) {
						render(facesContext);
					}
					redirectViewId = bridgeContext.getRedirectViewId();
				}
				// detect redirect case. Reset response, clear request
				// variables as far as Seam state.
				// Perform new render phase with a new ViewId.
				// TODO - move page navigation from Seam phase listener to the
				// Strategy.
				if (null != redirectViewId) {
					windowState.reset();
					windowState.setViewId(redirectViewId);

					Map<String, String[]> redirectParams = bridgeContext
					        .getRedirectRequestParameters();

					// release old FacesContext.
					facesContext.release();
					// Reset attributes to initial state
					bridgeContext.resetRequestAttributes(request);
					wrappedResponse.resetBuffer();
					if (redirectParams != null) {
						windowState.setRequestParameters(redirectParams);
					}

					// Create new FacesContext
					facesContext = createFacesContext(request, wrappedResponse);
					ViewHandler viewHandler = facesContext.getApplication()
					        .getViewHandler();
					UIViewRoot viewRoot = viewHandler.createView(facesContext,
					        redirectViewId);
					facesContext.setViewRoot(viewRoot);
					render(facesContext);
				}
				windowState.setViewId(facesContext.getViewRoot().getViewId());
			} catch (Exception e) {
				wrappedResponse.resetBuffer();
				log.log(Level.SEVERE, "Error processing execute lifecycle", e);
				exceptionHandler.processRenderException(facesContext,
				        windowState, e);
				facesContext = FacesContext.getCurrentInstance();
			}
			// Set important Portal parameters to window state.
			String viewId = facesContext.getViewRoot().getViewId();
			//
			windowState.setNamespace(namespace);
			// TODO - encode request attributes, portlet mode and windowId, as
			// required by JSR-301 5.3.3
			String portletModeName = request.getPortletMode().toString();
			PortalActionURL historyViewId = new PortalActionURL(viewId);
			historyViewId.setParameter(RequestScopeManager.STATE_ID_PARAMETER,
			        stateId.toString());
			historyViewId.setParameter(Bridge.PORTLET_MODE_PARAMETER,
			        portletModeName);
			facesContext.getExternalContext().getSessionMap().put(
			        VIEWID_HISTORY_PREFIX + portletModeName,
			        historyViewId.toString());
			if (log.isLoggable(Level.FINE)) {
				log.fine("Finish rendering portletbridge for namespace "
				        + namespace);
			}
			// Disable portletbridge caching.
			// TODO - detect ajax components on page, static views can be
			// cached.
			// wrappedResponse.setProperty(RenderResponse.EXPIRATION_CACHE,
			// "0");
		} catch (Exception e) {
			throwBridgeException(e);
		} finally {
			strategy.afterRenderRequest(facesContext, wrappedResponse);
			facesContext.release();
		}
	}

	protected void assertParameters(PortletRequest request,
	        PortletResponse response) {
		if (null == request) {
			throw new NullPointerException("Request parameter is null");
		}
		if (null == response) {
			throw new NullPointerException("Response parameter is null");
		}
		if (!isInitialized()) {
			throw new BridgeException("JSF Portlet bridge is not initialized");
		}
	}

	public void doFacesRequest(ResourceRequest request,
	        ResourceResponse response) throws BridgeException {
		assertParameters(request, response);

		if (log.isLoggable(Level.FINE)) {
			log.fine("Start bridge resource request processing for portlet "
			        + getPortletName());
		}
		PortletBridgeContext bridgeContext = initRequest(request, response,
		        Bridge.PortletPhase.RESOURCE_PHASE);
		if (!strategy.serveResource(request, response)) {
			if (null == request.getParameter(Bridge.FACES_VIEW_ID_PARAMETER)
			        && null == request
			                .getParameter(Bridge.FACES_VIEW_PATH_PARAMETER)) {
				String target = request.getResourceID();
				if (null != target) {
					try {
						PortletContext portletContext = getPortletConfig()
						        .getPortletContext();
						PortletRequestDispatcher dispatcher = portletContext
						        .getRequestDispatcher(target);
						if (null != dispatcher) {
							String serverInfo = portletContext.getServerInfo();
							if (null != serverInfo
							        && (serverInfo
							                .startsWith("JBossPortletContainer") || serverInfo
							                .startsWith("GateInPortletContainer"))) {
								// HACK - Jboss portal does not handle 'forward'
								// method during resource requests.
								// see
								// https://jira.jboss.org/jira/browse/JBPORTAL-2432
								String mimeType = portletContext
								        .getMimeType(target);
								if (null == mimeType) {
									int lastIndexOfSlash = target
									        .lastIndexOf('/');
									if (lastIndexOfSlash >= 0) {
										target = target
										        .substring(lastIndexOfSlash + 1);
									}
									int indexOfQuestion = target.indexOf('?');
									if (indexOfQuestion >= 0) {
										target = target.substring(0,
										        indexOfQuestion);
									}
									mimeType = portletContext
									        .getMimeType(target);
								}
								if (null != mimeType) {
									response.setContentType(mimeType);
								}
								dispatcher.include(request, response);
							} else {
								dispatcher.forward(request, response);
							}
						}
					} catch (Exception e) {
						throwBridgeException(e);
					}
				} else {
					throw new BridgeException("Unable to serve resource");
				}

			} else {
				// FACES REQUEST.
				ResourceResponse wrappedResponse = strategy
				        .createResponseWrapper(response);
				RequestScopeManager requestStateManager = RequestScopeManager
				        .getInstance(request, numberOfRequestScopes);
				FacesContext facesContext = createFacesContext(request,
				        wrappedResponse);
				StateId stateId = requestStateManager.getStateId(request,
				        wrappedResponse);
				bridgeContext.setStateId(stateId);
				BridgeRequestScope windowState = requestStateManager
				        .getRequestScope(stateId);
				if (null == windowState) {
					windowState = new BridgeRequestScope();
					requestStateManager.saveRequestScope(stateId, windowState);
				} else {
					windowState.reset();
				}
				bridgeContext.setRequestScope(windowState);
				try {
					strategy.beforeResourceRequest(facesContext);
					processIncomingParameters(facesContext, request);
					execute(facesContext);
					// TODO - create applicable processing function.
					// processOutgoingParameters(facesContext,request,response);
					windowState.saveRequest(facesContext, false);
					if (!facesContext.getResponseComplete()) {
						render(facesContext);
					}
				} catch (Exception e) {
					wrappedResponse.resetBuffer();
					log.log(Level.SEVERE, "Error processing execute lifecycle",
					        e);
					exceptionHandler.processResourceException(facesContext,
					        windowState, e);
					facesContext = FacesContext.getCurrentInstance();
				} finally {
					strategy
					        .afterResourceRequest(facesContext, wrappedResponse);
					facesContext.release();
				}
			}
		}
	}

	private void throwBridgeException(Exception e) throws BridgeException {
		if (!(e instanceof BridgeException)) {
			e = new BridgeException(e);
		}
		throw (BridgeException) e;
	}

	public void doFacesRequest(EventRequest request, EventResponse response)
	        throws BridgeException {
		assertParameters(request, response);

		if (log.isLoggable(Level.FINE)) {
			log.fine("Start bridge event request processing for portlet "
			        + getPortletName());
		}
		PortletBridgeContext bridgeContext = initRequest(request, response,
		        Bridge.PortletPhase.EVENT_PHASE);
		RequestScopeManager requestStateManager = RequestScopeManager
		        .getInstance(request, numberOfRequestScopes);
		StateId stateId = requestStateManager.getStateId(request, response);
		bridgeContext.setStateId(stateId);
		BridgeRequestScope windowState = requestStateManager
		        .getRequestScope(stateId);
		if (null == windowState) {
			windowState = new BridgeRequestScope();
		} else {
			windowState.reset();
		}
		bridgeContext.setRequestScope(windowState);
		FacesContext facesContext = createFacesContext(request, response);
		try {
			// Propagate current render parameters.
			strategy.beforeEventRequest(facesContext);
			response.setRenderParameters(request);
			processIncomingParameters(facesContext, request);
			if (null != eventHandler) {
				Event event = request.getEvent();
				eventHandler.handleEvent(facesContext, event);
			}
			execute(facesContext);
			processOutgoingParameters(facesContext, request, response);
			requestStateManager.saveRequestScope(stateId, windowState);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error processing event request", e);
			exceptionHandler
			        .processEventException(facesContext, windowState, e);
		} finally {
			strategy.afterEventRequest(facesContext);
			facesContext.release();
		}

	}

	/**
	 * @param facesContext
	 * @param windowState
	 * @throws FacesException
	 */
	private void renderResponse(FacesContext facesContext,
	        BridgeRequestScope windowState) throws FacesException {
		if (null == facesContext.getViewRoot()) {
			execute(facesContext);
			// TODO - store changed parameters into url's.
			// processOutgoingParameters(facesContext);
		}
		//
		if (!facesContext.getResponseComplete()) {
			render(facesContext);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param currentPhase
	 * @return initialized bridge context instance.
	 * @throws BridgeException
	 */
	protected PortletBridgeContext initRequest(PortletRequest request,
	        PortletResponse response, PortletPhase currentPhase)
	        throws BridgeException {
		request.setAttribute(Bridge.PORTLET_LIFECYCLE_PHASE, currentPhase);
		// Check viewId history sessions attributes
		Map<String, String> viewIdMap = getDefaultViewIdMap();
		String firstMode = viewIdMap.keySet().iterator().next();
		PortletSession portletSession = request.getPortletSession();
		if (null == portletSession.getAttribute(VIEWID_HISTORY_PREFIX
		        + firstMode)) {
			// Fill viewId's history attributes by default values.
			for (Entry<String, String> entry : viewIdMap.entrySet()) {
				portletSession.setAttribute(VIEWID_HISTORY_PREFIX
				        + entry.getKey(), encodeModeParam(entry.getKey(), entry
				        .getValue()));
			}
		}
		return createBridgeContext(request);
	}

	protected void finishRequest(PortletRequest request,
	        PortletResponse response) throws BridgeException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null != facesContext) {
			facesContext.release();
		}
		request.removeAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);
		request.removeAttribute(PortletBridgeContext.REQUEST_PARAMETER_NAME);
	}

	/**
	 * Encode mode parameter into viewId.
	 * 
	 * @param mode
	 * @param viewId
	 * @return
	 */
	private String encodeModeParam(String mode, String viewId) {
		try {
			PortalActionURL viewIdUrl = new PortalActionURL(viewId);
			viewIdUrl.addParameter(Bridge.PORTLET_MODE_PARAMETER, mode);
			return viewIdUrl.toString();
		} catch (MalformedURLException e) {
			throw new BridgeException("Malformed ViewId", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#getPortletConfig()
	 */
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}

	protected Object getContext() {
		return portletConfig.getPortletContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.portletbridge.BridgeConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		String initParameter = portletConfig.getInitParameter(name);
		if (null == initParameter) {
			initParameter = portletConfig.getPortletContext().getInitParameter(
			        name);
		}
		return initParameter;
	}

	/**
	 * @return the initialized
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the exceptionHandler
	 */
	protected ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#getFacesServletMappings()
	 */
	public List<String> getFacesServletMappings() {
		return facesServletMappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#getExcludedAttributes()
	 */
	public Set<ExcludedRequestAttribute> getExcludedAttributes() {
		return excludedAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#getPortletName()
	 */
	public String getPortletName() {
		return portletConfig.getPortletName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#isPreserveActionParams()
	 */
	public boolean isPreserveActionParams() {
		return preserveActionParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.portletbridge.BridgeConfig#getDefaultViewIdMap()
	 */
	public Map<String, String> getDefaultViewIdMap() {
		return defaultViewIdMap;
	}

	/**
	 * @return the numberOfRequestScopes
	 */
	public int getNumberOfRequestScopes() {
		return numberOfRequestScopes;
	}

	public void contextCreated(ELContextEvent event) {
		// Add the portletConfig to the ELContext
		ELContext elContext = event.getELContext();
		if (elContext.getContext(PortletConfig.class) == null) {
			elContext.putContext(PortletConfig.class, portletConfig);
		}
	}

	/**
	 * Get currenf JSF lifecycle instance.
	 * 
	 * @return
	 */
	public Lifecycle getFacesLifecycle() {
		return this.lifecycle;
	}

	/**
	 * Create new faces context instance.
	 * 
	 * @param request
	 * @param response
	 * @return new instance of faces context.
	 */
	public FacesContext createFacesContext(Object request, Object response) {
		FacesContext facesContext = this.facesContextFactory.getFacesContext(
		        getContext(), request, response, getFacesLifecycle());
		return facesContext;
	}

	protected void execute(FacesContext context) throws FacesException {
		getFacesLifecycle().execute(context);
	}

	protected void render(FacesContext context) throws FacesException {
		getFacesLifecycle().render(context);
	}

	/**
	 * @return the errorPages
	 */
	public Map<Class<? extends Throwable>, String> getErrorPages() {
		return errorPages;
	}

	/**
	 * @return the strategy
	 */
	public BridgeStrategy getStrategy() {
		return strategy;
	}
}
