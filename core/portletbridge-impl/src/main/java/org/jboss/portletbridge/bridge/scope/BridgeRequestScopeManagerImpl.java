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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.bridge.config.BridgeConfig;
import org.jboss.portletbridge.bridge.context.BridgeContext;
import org.jboss.portletbridge.bridge.factory.BridgeFactoryFinder;
import org.jboss.portletbridge.bridge.factory.BridgeLoggerFactoryImpl;
import org.jboss.portletbridge.bridge.factory.BridgeRequestScopeFactory;
import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.BridgeLogger.Level;

/**
 * Manages a set of BridgeRequestScopes based on its policies. Clients interact with the manager to create and remove
 * BridgeRequestScopes, and to lookup one by its key.
 *
 * @author kenfinnigan
 */
public class BridgeRequestScopeManagerImpl implements BridgeRequestScopeManager, Serializable {

    private static final int DEFAULT_MAX_MANAGED_REQUEST_SCOPES = 100;

    private static final BridgeLogger logger = BridgeLoggerFactoryImpl.getLogger(BridgeRequestScopeManagerImpl.class
        .getName());

    private transient BridgeRequestScopeFactory scopeFactory;

    private Map<String, BridgeRequestScope> bridgeRequestScopeCache;
    private Map<String, String> scopeIdMap;

    public BridgeRequestScopeManagerImpl(BridgeConfig bridgeConfig) {
        this.scopeFactory = retrieveScopeFactory();
        this.bridgeRequestScopeCache = createBridgeRequestScopeCache(bridgeConfig.getPortletConfig()
            .getPortletContext());
        this.scopeIdMap = new HashMap<String, String>(getCacheMax(bridgeConfig.getPortletConfig().getPortletContext()));
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#createRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public BridgeRequestScope createRequestScope(BridgeContext ctx, String portletName, String sessionId,
        String viewId, String portletMode) {
        BridgeRequestScope scope = getScopeFactory().getBridgeRequestScope(portletName, sessionId, viewId, portletMode);
        this.bridgeRequestScopeCache.put(scope.getId(), scope);
        this.scopeIdMap.put(
            BridgeRequestScopeUtil.generateBridgeRequestScopeIdPrefix(portletName, sessionId, viewId, portletMode),
            scope.getId());
        return scope;
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#createRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public BridgeRequestScope createRequestScope(BridgeContext ctx, String viewId) {
        return createRequestScope(ctx, ctx.getBridgeConfig().getPortletConfig().getPortletName(), ctx
            .getPortletRequest().getPortletSession(true).getId(), viewId, ctx.getPortletRequest().getPortletMode()
            .toString());
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#getRequestScopeById(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public BridgeRequestScope getRequestScopeById(BridgeContext ctx, String id) {
        return this.bridgeRequestScopeCache.get(id);
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#getRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public BridgeRequestScope getRequestScope(BridgeContext ctx, String portletName, String sessionId, String viewId,
        String portletMode) {
        String idPrefix = BridgeRequestScopeUtil.generateBridgeRequestScopeIdPrefix(portletName, sessionId, viewId,
            portletMode);
        String scopeKey = this.scopeIdMap.get(idPrefix);
        BridgeRequestScope scope = null;
        if (null != scopeKey) {
            scope = getRequestScopeById(ctx, scopeKey);
            if (null == scope) {
                this.scopeIdMap.remove(idPrefix);
            }
        }
        return scope;
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#getRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String, java.lang.String)
     */
    public BridgeRequestScope getRequestScope(BridgeContext ctx, String viewId, String portletMode) {
        return getRequestScope(ctx, ctx.getBridgeConfig().getPortletConfig().getPortletName(), ctx.getPortletRequest()
            .getPortletSession(true).getId(), viewId, portletMode);
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#getRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public BridgeRequestScope getRequestScope(BridgeContext ctx, String viewId) {
        return getRequestScope(ctx, viewId, ctx.getPortletRequest().getPortletMode().toString());
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#getRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public BridgeRequestScope getRequestScope(BridgeContext ctx) {
        return getRequestScope(ctx, ctx.getFacesViewId(true));
    }

    /**
     * Per JSR-329 6.8.2, removal of the {@link BridgeRequestScope} from being cached results in clear() being called on
     * the scope, which in turn calls preDestroy on Objects.
     *
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScopeById(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public BridgeRequestScope removeRequestScopeById(BridgeContext ctx, String id) {
        BridgeRequestScope scope = this.bridgeRequestScopeCache.remove(id);
        scope.clear();
        return scope;
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      org.jboss.portletbridge.bridge.scope.BridgeRequestScope)
     */
    public BridgeRequestScope removeRequestScope(BridgeContext ctx, BridgeRequestScope scope) {
        return removeRequestScope(ctx, scope.getId());
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public BridgeRequestScope removeRequestScope(BridgeContext ctx, String portletName, String sessionId,
        String viewId, String portletMode) {
        String idPrefix = BridgeRequestScopeUtil.generateBridgeRequestScopeIdPrefix(portletName, sessionId, viewId,
            portletMode);
        String scopeKey = this.scopeIdMap.get(idPrefix);
        BridgeRequestScope scope = null;
        if (null != scopeKey) {
            scope = removeRequestScopeById(ctx, scopeKey);
            if (null == scope) {
                this.scopeIdMap.remove(idPrefix);
            }
        }
        return scope;
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String, java.lang.String)
     */
    public BridgeRequestScope removeRequestScope(BridgeContext ctx, String viewId, String portletMode) {
        return removeRequestScope(ctx, ctx.getBridgeConfig().getPortletConfig().getPortletName(), ctx
            .getPortletRequest().getPortletSession(true).getId(), viewId, portletMode);
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScope(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public BridgeRequestScope removeRequestScope(BridgeContext ctx, String viewId) {
        return removeRequestScope(ctx, viewId, ctx.getPortletRequest().getPortletMode().toString());
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScopesByPortlet(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public void removeRequestScopesByPortlet(BridgeContext ctx, String portletName) {
        if (null != portletName) {
            Set<Map.Entry<String, BridgeRequestScope>> entries = this.bridgeRequestScopeCache.entrySet();
            if (null != entries) {
                synchronized (this.bridgeRequestScopeCache) {
                    for (Map.Entry<String, BridgeRequestScope> entry : entries) {
                        BridgeRequestScope scope = entry.getValue();
                        if (portletName.equals(scope.getPortletName())) {
                            this.removeRequestScopeById(ctx, entry.getKey());
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScopesByPortlet(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void removeRequestScopesByPortlet(BridgeContext ctx) {
        removeRequestScopesByPortlet(ctx, ctx.getBridgeConfig().getPortletConfig().getPortletName());
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScopesBySession(org.jboss.portletbridge.bridge.context.BridgeContext,
     *      java.lang.String)
     */
    public void removeRequestScopesBySession(BridgeContext ctx, String sessionId) {
        if (null != sessionId) {
            Set<Map.Entry<String, BridgeRequestScope>> entries = this.bridgeRequestScopeCache.entrySet();
            if (null != entries) {
                synchronized (this.bridgeRequestScopeCache) {
                    for (Map.Entry<String, BridgeRequestScope> entry : entries) {
                        BridgeRequestScope scope = entry.getValue();
                        if (sessionId.equals(scope.getSessionId())) {
                            this.removeRequestScopeById(ctx, entry.getKey());
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.scope.BridgeRequestScopeManager#removeRequestScopesBySession(org.jboss.portletbridge.bridge.context.BridgeContext)
     */
    public void removeRequestScopesBySession(BridgeContext ctx) {
        PortletSession portletSession = ctx.getPortletRequest().getPortletSession(false);
        if (null != portletSession) {
            removeRequestScopesBySession(ctx, portletSession.getId());
        }
    }

    protected Map<String, BridgeRequestScope> createBridgeRequestScopeCache(PortletContext portletContext) {
        return Collections.synchronizedMap(new BridgeRequestScopeCache(getCacheMax(portletContext)));
    }

    /**
     * Per JSR-329 3.2, retrieves javax.portlet.faces.MAX_MANAGED_REQUEST_SCOPES portlet init parameter to determine the
     * maximum number of scopes to maintain. If not present, or invalid value, use the default for this implementation.
     *
     * @param portletContext
     * @return Max number of {@link BridgeRequestScope}'s to manage
     */
    private int getCacheMax(PortletContext portletContext) {
        int maxManagedScopes = DEFAULT_MAX_MANAGED_REQUEST_SCOPES;
        String maxManagedScopesInitParam = portletContext.getInitParameter(Bridge.MAX_MANAGED_REQUEST_SCOPES);
        if (null != maxManagedScopesInitParam) {
            try {
                maxManagedScopes = Integer.parseInt(maxManagedScopesInitParam);
            } catch (NumberFormatException e) {
                logger
                    .log(Level.WARNING, "portlet.xml contains invalid value for " + Bridge.MAX_MANAGED_REQUEST_SCOPES);
            }
        }
        return maxManagedScopes;
    }

   public BridgeRequestScopeFactory getScopeFactory() {
      if(scopeFactory == null) {
         scopeFactory = retrieveScopeFactory();
      }
      return scopeFactory;
   }

   private BridgeRequestScopeFactory retrieveScopeFactory() {
      return (BridgeRequestScopeFactory) BridgeFactoryFinder.getFactoryInstance(BridgeRequestScopeFactory.class);
   }

}
