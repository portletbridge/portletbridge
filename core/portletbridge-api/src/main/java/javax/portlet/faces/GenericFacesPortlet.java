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
package javax.portlet.faces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;

/**
 * The <code>GenericFacesPortlet</code> is provided to simplify development of a portlet that in whole or part relies on the
 * Faces bridge to process requests. If all requests are to be handled by the bridge, <code>GenericFacesPortlet</code> is a
 * turnkey implementation. Developers do not need to subclass it. However, if there are some situations where the portlet
 * doesn't require bridge services then <code>GenericFacesPortlet</code> can be subclassed and overriden.
 * <p>
 * Since <code>GenericFacesPortlet</code> subclasses <code>GenericPortlet</code> care is taken to all subclasses to override
 * naturally. For example, though <code>doDispatch()</code> is overriden, requests are only dispatched to the bridge from here
 * if the <code>PortletMode</code> isn't <code>VIEW</code>, <code>EDIT</code>, or <code>HELP</code>.
 * <p>
 * The <code>GenericFacesPortlet</code> recognizes the following portlet initialization parameters:
 * <ul>
 * <li><code>javax.portlet.faces.defaultViewId.[<i>mode</i>]</code>: specifies on a per mode basis the default viewId the Bridge
 * executes when not already encoded in the incoming request. A value must be defined for each <code>PortletMode</code> the
 * <code>Bridge</code> is expected to process.</li>
 * <li><code>javax.portlet.faces.excludedRequestAttributes</code>: specifies on a per portlet basis the set of request
 * attributes the bridge is to exclude from its request scope. The value of this parameter is a comma delimited list of either
 * fully qualified attribute names or a partial attribute name of the form <i>packageName.*</i>. In this later case all
 * attributes exactly prefixed by <i>packageName</i> are excluded, non recursive.</li>
 * <li><code>javax.portlet.faces.preserveActionParams</code>: specifies on a per portlet basis whether the bridge should
 * preserve parameters received in an action request and restore them for use during subsequent renders.</li>
 * <li><code>javax.portlet.faces.defaultContentType</code>: specifies on a per mode basis the content type the bridge should set
 * for all render requests it processes.</li>
 * <li><code>javax.portlet.faces.defaultCharacterSetEncoding</code>: specifies on a per mode basis the default character set
 * encoding the bridge should set for all render requests it processes</li>
 * </ul>
 * The <code>GenericFacesPortlet</code> recognizes the following application (<code>PortletContext</code>) initialization
 * parameters:
 * <ul>
 * <li><code>javax.portlet.faces.BridgeImplClass</code>: specifies the <code>Bridge</code>implementation class used by this
 * portlet. Typically this initialization parameter isn't set as the <code>GenericFacesPortlet</code> defaults to finding the
 * class name from the bridge configuration. However if more then one bridge is configured in the environment such per
 * application configuration is necessary to force a specific bridge to be used.</li>
 * </ul>
 */
public class GenericFacesPortlet extends GenericPortlet {

    /**
     * Application (PortletContext) init parameter that names the bridge class used by this application. Typically not used
     * unless more then 1 bridge is configured in an environment as its more usual to rely on the self detection.
     */
    public static final String BRIDGE_CLASS = Bridge.BRIDGE_PACKAGE_PREFIX + "BridgeClassName";

    /**
     * Portlet init parameter that defines the default ViewId that should be used when the request doesn't otherwise convery the
     * target. There must be one initialization parameter for each supported mode. Each parameter is named
     * DEFAULT_VIEWID.<i>mode</i>, where <i>mode</i> is the name of the corresponding <code>PortletMode</code>
     */
    public static final String DEFAULT_VIEWID = Bridge.BRIDGE_PACKAGE_PREFIX + "defaultViewId";

    /**
     * Portlet init parameter that defines the render response ContentType the bridge sets prior to rendering. If not set the
     * bridge uses the request's preferred content type.
     */
    public static final String DEFAULT_CONTENT_TYPE = Bridge.BRIDGE_PACKAGE_PREFIX + "defaultContentType";

    /**
     * Portlet init parameter that defines the render response CharacterSetEncoding the bridge sets prior to rendering.
     * Typcially only set when the jsp outputs an encoding other then the portlet container's and the portlet container supports
     * response encoding transformation.
     */
    public static final String DEFAULT_CHARACTERSET_ENCODING = Bridge.BRIDGE_PACKAGE_PREFIX + "defaultCharacterSetEncoding";

    /**
     * Portlet init parameter containing the setting for whether the GenericFacesPortlet overrides event processing by
     * dispatching all events to the bridge or delegates all event processing to the GenericPortlet. Default is true.
     */
    public static final String BRIDGE_AUTO_DISPATCH_EVENTS = Bridge.BRIDGE_PACKAGE_PREFIX + "autoDispatchEvents";

    /**
     * Location of the services descriptor file in a brige installation that defines the class name of the bridge
     * implementation.
     */
    public static final String BRIDGE_SERVICE_CLASSPATH = "META-INF/services/javax.portlet.faces.Bridge";

    // ========================================================================================
    // private constants
    private static final int DEFAULT_VIEW_ID_LENGTH = DEFAULT_VIEWID.length() + 1;
    private static final int EXTENDED_ATTR_PREFIX_LENGTH = Bridge.EXTENDED_PORTLET_ATTR_PREFIX.length();
    // ========================================================================================
    // Private fields
    private volatile Class<? extends Bridge> facesBridgeClass = null;
    private volatile Bridge facesBridge = null;
    private volatile String bridgeClassName = null;
    private volatile Map<String, String> defaultViewIdMap;

    private static final Logger log = Logger.getLogger("javax.portlet.faces");
    private boolean initialized = false;

    /**
     * Initialize generic faces portlet from portlet.xml
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        if (initialized) {
            throw new PortletException("GenericFacesPortlet for portlet " + config.getPortletName() + " already initialized");
        }
        if (log.isLoggable(Level.INFO)) {
            log.info("Init GenericFacesPortlet for portlet " + config.getPortletName());
        }
        super.init(config);
        PortletContext portletContext = this.getPortletContext();
        // Calculate bridge implementation class name.
        String bridgeClassName = getBridgeClassName();
        try {
            facesBridgeClass = loadClassForName(bridgeClassName);
        } catch (ClassNotFoundException e) {
            throw new PortletException("Faces Portlet Bridge implementation class not found", e);
        }
        // expose initialization parameters to the application-scope area with portlet name prefix.
        setBridgeParameter(Bridge.PRESERVE_ACTION_PARAMS, isPreserveActionParameters());
        setBridgeParameter(Bridge.EXCLUDED_REQUEST_ATTRIBUTES, getExcludedRequestAttributes());
        setBridgeParameter(Bridge.DEFAULT_RENDERKIT_ID, getDefaultRenderKitId());
        setBridgeParameter(Bridge.DEFAULT_VIEWID_MAP, getDefaultViewIdMap());
        setBridgeParameter(Bridge.BRIDGE_EVENT_HANDLER, getBridgeEventHandler());
        setBridgeParameter(Bridge.BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER, getBridgePublicRenderParameterHandler());
        setBridgeParameter(BRIDGE_AUTO_DISPATCH_EVENTS, isAutoDispatchEvents());
        // Get extension config attributes.
        String portletName = getPortletName();
        Enumeration<String> configNames = config.getInitParameterNames();
        while (configNames.hasMoreElements()) {
            String name = configNames.nextElement();
            if (name.startsWith(Bridge.EXTENDED_PORTLET_ATTR_PREFIX)) {
                int i = name.lastIndexOf('.');
                if (i > EXTENDED_ATTR_PREFIX_LENGTH + 2) {
                    String attribute = name.substring(i);
                    String preffix = name.substring(EXTENDED_ATTR_PREFIX_LENGTH, i + 1);
                    String extensionAttributeName = Bridge.EXTENDED_PORTLET_ATTR_PREFIX + preffix + portletName + attribute;
                    portletContext.setAttribute(extensionAttributeName, config.getInitParameter(name).trim());
                }
            }
        }
        this.initialized = true;
        if (log.isLoggable(Level.FINE)) {
            log.info("GenericFacesPortlet for portlet " + config.getPortletName() + " initialized");
        }
    }

    /**
     * 4.2.7 getExcludedRequestAttributes() As a portlet lifecycle allows multiple (re)renders to occur following an action, the
     * bridge manages an extended notion of a request scope to ensure that such rerenders produces identical results.
     * Specifically, portlet scoped request attributes are saved/restored by the bridge across such rerenders [5.1.2]. However,
     * sometimes a portlet request scoped attribute truly must be removed when the request scope ends. The bridge uses multiple
     * mechanisms for determining which attributes are marked for exclusion from its managed scope. The portlet can directly
     * instruct the bridge to exclude attributes on a per portlet basis by setting a PorletContext attribute [3.2]. This
     * attribute's value is a List containing the excluded attribute names.
     *
     * The GenericFacesPortlet sets this attribute based on the result of calling getExcludedRequestAttributes() in its init()
     * method. The default (GenericFacesPortlet) implementation for getExcludedRequestAttributes() returns a List constructed by
     * parsing the comma delimited String value from the corresponding portlet initialization parameter,
     * javax.portlet.faces.excludedRequestAttributes. If this initialization parameter isn't present null is returned which
     * causes the GenericFacesPortlet to not set the corresponding PortletContext attribute.
     *
     * @return
     */
    public List<String> getExcludedRequestAttributes() {
        List<String> attrsList = null;
        String excludedAttrs = getPortletConfig().getInitParameter(
                Bridge.BRIDGE_PACKAGE_PREFIX + Bridge.EXCLUDED_REQUEST_ATTRIBUTES);
        if (null != excludedAttrs) {
            String[] atrs = excludedAttrs.split(",");
            attrsList = new ArrayList<String>(atrs.length);
            for (String string : atrs) {
                attrsList.add(string.trim());
            }
        }
        return attrsList;
    }

    /**
     * 4.2.8 getPreserveActionParameters() By default the bridge doesn't preserve action parameters into subsequent renders.
     * This can be overridden on a per portlet basis by passing a value of true in the appropriate PortletContext attribute
     * [3.2]. To determine the setting for this attributes for this particular portlet, the GenericFacesPortlet calls
     * getPreserveActionParameters() in its init() method. The default (GenericFacesPortlet) implementation returns the Boolean
     * value corresponding to the String value represented in the portlet initialization parameter,
     * javax.portlet.faces.preserveActionParams. If this initialization parameter doesn't exist, Boolean.FALSE is returned.
     *
     * @return preserve or not action attributes.
     */
    public Boolean isPreserveActionParameters() {
        String preserveActionParams = getPortletConfig().getInitParameter(
                Bridge.BRIDGE_PACKAGE_PREFIX + Bridge.PRESERVE_ACTION_PARAMS);
        if (null == preserveActionParams) {
            return false;
        }
        Boolean isPreserveActionParams = Boolean.valueOf(preserveActionParams);
        return isPreserveActionParams;
    }

    /**
     * Returns a String defining the default render kit id the bridge should ensure for this portlet. If non-null, this value is
     * used to override any default render kit id set on an app wide basis in the faces-config.xml. This default implementation
     * reads the values from the portlet init_param javax.portlet.faces.defaultRenderKitId. If not present, null is returned.
     *
     * @return a boolean indicating whether or not the bridge should preserve all the action parameters in the subsequent
     *         renders that occur in the same scope.
     */
    public String getDefaultRenderKitId() {
        return getPortletConfig().getInitParameter(Bridge.BRIDGE_PACKAGE_PREFIX + Bridge.DEFAULT_RENDERKIT_ID);
    }

    /**
     * Returns the value of the portlet initialization parameter javax.portlet.faces.autoDispatchEvents if non-null or true,
     * otherwise.
     *
     * @return boolean indicating whether to auto-dispatch all events to the bridge or not.
     */
    public Boolean isAutoDispatchEvents() {
        String autoDispatchEvents = getPortletConfig().getInitParameter(BRIDGE_AUTO_DISPATCH_EVENTS);
        if (null != autoDispatchEvents) {
            Boolean isAutoDispatchEvents = Boolean.valueOf(autoDispatchEvents);
            return isAutoDispatchEvents;
        }
        return true;
    }

    /**
     *
     * @return an instance of BridgeEventHandler or null if there is none.
     */
    public BridgeEventHandler getBridgeEventHandler() {
        String eventHandlerClassName = getPortletConfig().getInitParameter(
                Bridge.BRIDGE_PACKAGE_PREFIX + Bridge.BRIDGE_EVENT_HANDLER);
        if (null != eventHandlerClassName) {
            try {
                return createInstanceByClassName(eventHandlerClassName.trim());
            } catch (PortletException e) {
                log.log(Level.WARNING, "Couldn't create BridgeEventHandler instance", e);
            }
        }
        return null;
    }

    /**
     * @return an instance of BridgeRenderParameterHandler or null if there is none.
     */
    public BridgePublicRenderParameterHandler getBridgePublicRenderParameterHandler() {
        String renderParameterHandlerClassName = getPortletConfig().getInitParameter(
                Bridge.BRIDGE_PACKAGE_PREFIX + Bridge.BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER);
        if (null != renderParameterHandlerClassName) {
            try {
                return createInstanceByClassName(renderParameterHandlerClassName);
            } catch (PortletException e) {
                log.log(Level.WARNING, "Couldn't create BridgePublicRenderParameterHandler instance", e);
            }
        }
        return null;
    }

    /**
     * JSR 301 API method
     *
     * @return
     */
    public String getBridgeClassName() throws PortletException {
        if (null == bridgeClassName) {
            synchronized (this) {
                if (null == bridgeClassName) {
                    bridgeClassName = calculateBridgeClassName(getPortletContext());
                }
            }
        }
        return bridgeClassName;
    }

    /**
     * JSR 301 API method
     *
     * @return
     */
    public Map<String, String> getDefaultViewIdMap() {
        if (null == defaultViewIdMap) {
            synchronized (this) {
                if (null == defaultViewIdMap) {
                    defaultViewIdMap = calculateDefaultViewIdMap();
                }
            }
        }
        return defaultViewIdMap;
    }

    /**
     * Returns an initialized bridge instance adequately prepared so the caller can
     * call doFacesRequest directly without further initialization.
     *
     * @return instance of the bridge.
     * @throws PortletException exception acquiring or initializing the bridge.
     */
    public Bridge getFacesBridge(PortletRequest request, PortletResponse response) throws PortletException {
        Bridge facesBridge = getFacesBridge();
        setupBridgeRequest(request, response);
        return facesBridge;
    }

    /**
     * Get current {@link Bridge} instance. Bridge will be created and initialized on first request only.
     *
     * @return the facesPortletBridge
     * @throws PortletException
     */
    public Bridge getFacesBridge() throws PortletException {
        if (null == facesBridge) {
            synchronized (this) {
                if (null == facesBridge) {
                    try {
                        // Do not assign uninitialized instance to field
                        Bridge bridge = (Bridge) facesBridgeClass.newInstance();
                        bridge.init(getPortletConfig());
                        this.facesBridge = bridge;
                    } catch (InstantiationException e) {
                        throw new PortletException("Error on create instance of a JSF Portlet Bridge", e);
                    } catch (IllegalAccessException e) {
                        throw new PortletException("IllegalAccess on create instance of a JSF Portlet Bridge", e);
                    } catch (BridgeException e) {
                        throw new PortletException("Bridge initialization error", e);
                    }
                }
            }
        }
        return facesBridge;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.portlet.GenericPortlet#destroy()
     */
    public void destroy() {
        if (log.isLoggable(Level.INFO)) {
            log.info("Destroy GenericFacesPortlet for portlet " + getPortletName());
        }
        // If bridge was initialized, destroy it.
        if (null != facesBridge) {
            facesBridge.destroy();
            facesBridge = null;
        }
        // clear portlet fields.
        this.bridgeClassName = null;
        this.defaultViewIdMap = null;
        this.facesBridgeClass = null;
        super.destroy();
        this.initialized = false;
    }

    /**
     * If mode is VIEW, EDIT, or HELP -- defer to the doView, doEdit, doHelp so subclasses can override. Otherwise handle mode
     * here if there is a defaultViewId mapping for it.
     */
    @Override
    protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletMode mode = request.getPortletMode();
        if (PortletMode.VIEW.equals(mode) || PortletMode.EDIT.equals(mode) || PortletMode.HELP.equals(mode)) {
            // Portlet serves three standard modes as default.
            super.doDispatch(request, response);
        } else {
            // All other dispatched to bridge directly.
            doFacesDispatch(request, response);
        }

    }

    protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Process edit request for portlet " + getPortletName());
        }
        doFacesDispatch(request, response);
    }

    protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Process help request for portlet " + getPortletName());
        }
        doFacesDispatch(request, response);
    }

    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Process view request for portlet " + getPortletName());
        }
        doFacesDispatch(request, response);
    }

    /**
     * @see javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Process action request for portlet " + getPortletName());
        }
        String defaultViewId = getDefaultViewIdMap().get(request.getPortletMode().toString());
        String nonFacesTargetPath = getNonFacesTargetPath(request, response);
        if (null == defaultViewId || null != nonFacesTargetPath) {
            throw new PortletException("Non-faces Action requests to the GenericFacesPortlet are not supported");
        }

        try {
            getFacesBridge(request, response).doFacesRequest(request, response);
        } catch (BridgeException e) {
            throw new PortletException("Error process faces request", e);
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Process resource request for portlet " + getPortletName());
        }

        try {
            getFacesBridge(request, response).doFacesRequest(request, response);
        } catch (BridgeException e) {
            throw new PortletException("Error process faces request", e);
        }
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException {
        if (isAutoDispatchEvents()) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Process event request for portlet " + getPortletName());
            }

            try {
                getFacesBridge(request, response).doFacesRequest(request, response);
            } catch (BridgeException e) {
                throw new PortletException("Error process faces request", e);
            }
        } else {
            super.processEvent(request, response);
        }
    }

    /**
     * Internal method to redirect all render methods to the {@link Bridge} instance.
     *
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    void doFacesDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String nonFacesTargetPath = getNonFacesTargetPath(request, response);
        if (null != nonFacesTargetPath) {
            // non-faces request, perform it directly.
            try {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Non-faces render request to target " + nonFacesTargetPath);
                }
                PortletRequestDispatcher dispatcher = this.getPortletContext().getRequestDispatcher(nonFacesTargetPath);
                dispatcher.forward(request, response);
            } catch (Exception e) {
                throw new PortletException("Unable to perform non-faces dispatch to: " + nonFacesTargetPath, e);
            }

        } else {
            String defaultViewId = getDefaultViewIdMap().get(request.getPortletMode().toString());
            if (null != defaultViewId && !request.getWindowState().equals(WindowState.MINIMIZED)) {
                try {
                    getFacesBridge(request, response).doFacesRequest(request, response);
                } catch (BridgeException e) {
                    throw new PortletException("Error process faces request", e);
                }
            }
        }
    }

    /**
     * Setup request parameters as required by 3.4
     *
     * @param request
     * @param response
     * @throws PortletException
     */
    private void setupBridgeRequest(PortletRequest request, PortletResponse response) throws PortletException {
        String viewId = request.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);
        if (null != viewId) {
            request.setAttribute(Bridge.VIEW_ID, viewId);
        } else if (null != (viewId = request.getParameter(Bridge.FACES_VIEW_PATH_PARAMETER))) {
            request.setAttribute(Bridge.VIEW_PATH, viewId);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @return path to process request by 'include' content instead of Bridge.
     */
    private String getNonFacesTargetPath(PortletRequest request, PortletResponse response) {
        return request.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER);
    }

    /**
     * Internal method to set application-scope parameter namespaced by portlet name.
     *
     * @param name
     * @param value
     */
    private void setBridgeParameter(String name, Object value) {
        if (null != value) {
            StringBuilder attributeName = new StringBuilder(Bridge.BRIDGE_PACKAGE_PREFIX);
            attributeName.append(getPortletName()).append('.').append(name);
            getPortletContext().setAttribute(attributeName.toString(), value);

        }
    }

    /**
     * @param portletContext
     * @return
     * @throws PortletException
     */
    private String calculateBridgeClassName(PortletContext portletContext) throws PortletException {
        String bridgeClassName = portletContext.getInitParameter(BRIDGE_CLASS);
        if (bridgeClassName == null) {
            ClassLoader loader = getClassLoader();
            URL resource = loader.getResource(BRIDGE_SERVICE_CLASSPATH);
            if (null != resource) {
                InputStream stream = null;
                try {
                    URLConnection connection = resource.openConnection();
                    // Prevent jar locking in the Windows environment.
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        reader = new BufferedReader(new InputStreamReader(stream));
                    }
                    // Parse file content. All empty string and comments ignored.
                    while (null == bridgeClassName) {
                        bridgeClassName = reader.readLine();
                        if (null != bridgeClassName) {
                            int indexOfComment = bridgeClassName.indexOf('#');
                            if (indexOfComment >= 0) {
                                bridgeClassName = bridgeClassName.substring(0, indexOfComment);
                            }
                            bridgeClassName = bridgeClassName.trim();
                            if (bridgeClassName.length() == 0) {
                                // Empty string.
                                bridgeClassName = null;
                            }
                        } else {
                            break;
                        }

                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error reading bridge service definition file", e);
                } catch (SecurityException e) {
                    log.log(Level.SEVERE, "Error reading bridge service definition file", e);
                } finally {
                    if (null != stream) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            log.log(Level.SEVERE, "Error to close input stream for a resource " + BRIDGE_SERVICE_CLASSPATH, e);
                        }

                    }
                }
            }
        }
        if (null == bridgeClassName) {
            throw new PortletException("Can't detect bridge implementation class name");
        }
        if (log.isLoggable(Level.INFO)) {
            log.info("Bridge class name is " + bridgeClassName);
        }
        return bridgeClassName;
    }

    /**
     * @return
     */
    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (null == classLoader) {
            classLoader = GenericFacesPortlet.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * @param <U>
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private <U> Class<? extends U> loadClassForName(String className) throws ClassNotFoundException {
        Class<?> clazz = getClassLoader().loadClass(className);
        return (Class<? extends U>) clazz;
    }

    /**
     * @param <U>
     * @param className
     * @return
     * @throws PortletException
     */
    private <U> U createInstanceByClassName(String className) throws PortletException {
        try {
            U instance = this.<U> loadClassForName(className).newInstance();
            return instance;
        } catch (InstantiationException e) {
            throw new PortletException("Error instantiate class for name " + className);
        } catch (IllegalAccessException e) {
            throw new PortletException("Illegal access to class for name " + className);
        } catch (ClassNotFoundException e) {
            throw new PortletException("Class " + className + " was not found");
        }
    }

    /**
     * @return
     */
    private Map<String, String> calculateDefaultViewIdMap() {
        Map<String, String> viewIdMap = new HashMap<String, String>();
        PortletConfig portletConfig = getPortletConfig();
        Enumeration<String> configNames = portletConfig.getInitParameterNames();
        while (configNames.hasMoreElements()) {
            String name = configNames.nextElement();
            if (name.startsWith(DEFAULT_VIEWID)) {
                // Put viewId with mode name as key.
                viewIdMap.put(name.substring(DEFAULT_VIEW_ID_LENGTH), portletConfig.getInitParameter(name).trim());
            }
        }
        return viewIdMap;
    }

    /**
     * @deprecated -- no longer used or called by the <code>GenericFacesPortlet</code> but retained in case a subclass has
     *             called it.
     *
     *
     * @return null.
     */
    @Deprecated
    public String getResponseCharacterSetEncoding(PortletRequest request) {
        return null;
    }

    /**
     * @deprecated -- no longer used or called by the <code>GenericFacesPortlet</code> but retained in case a subclass has
     *             called it.
     *
     *
     * @return request.getResponseContentType().
     */
    @Deprecated
    public String getResponseContentType(PortletRequest request) {
        return request.getResponseContentType();
    }
}
