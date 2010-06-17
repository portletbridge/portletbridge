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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.context.PortalActionURL;
import org.jboss.portletbridge.util.BridgeLogger;

/**
 * @author asmirnov
 * 
 */
public class RequestScopeManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8380848257623284922L;

	public static final String REQUEST_STATE_MANAGER = RequestScopeManager.class
			.getName();

	private static final Logger log = BridgeLogger.BRIDGE.getLogger();

	public static final String WINDOW_ID_RETRIVER = "org.jboss.portletbridge.WINDOW_ID_RETRIVER";

	public static final String STATE_ID_PARAMETER = "javax.faces.portletbridge.STATE_ID";

	public static final int DEFAULT_MAX_MANAGED_SCOPES = 1000;

	/**
	 * View states for a different portlet modes, Views and serial numbers.
	 */
	private final Map<StateId, BridgeRequestScope> states;

	/**
	 * Private constructor - instance must be stored in the session, and created
	 * on nessesary by factory method.
	 * 
	 * @param context
	 */
	private RequestScopeManager(int max) {
		states = new LRUMap<StateId, BridgeRequestScope>(max);
	}


	/**
	 * Get instance of portlet states, stored in the session. If no previsious
	 * instance in session, create default value.
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static RequestScopeManager getInstance(FacesContext context) {
		ExternalContext externalContext = context.getExternalContext();
		Map<String, Object> sessionMap = externalContext
				.getSessionMap();
		RequestScopeManager requestStateManager = (RequestScopeManager) sessionMap.get(REQUEST_STATE_MANAGER);
		if (null == requestStateManager) {
			// TODO - use mutex synchronization
			int maxScopes = DEFAULT_MAX_MANAGED_SCOPES;
			String maxScopesParameter = externalContext.getInitParameter(Bridge.MAX_MANAGED_REQUEST_SCOPES);
			if (null != maxScopesParameter) {
				maxScopes = Integer.parseInt(maxScopesParameter);
			}
			requestStateManager = new RequestScopeManager(
					maxScopes);
			sessionMap.put(REQUEST_STATE_MANAGER, requestStateManager);
		}
		return requestStateManager;
	}

	public synchronized static RequestScopeManager getInstance(PortletRequest request,int maxScopes) {
		PortletSession session = request.getPortletSession(true);
		RequestScopeManager requestStateManager = (RequestScopeManager)session.getAttribute(REQUEST_STATE_MANAGER);
		if (null == requestStateManager) {
			// TODO - use mutex synchronization
			requestStateManager = new RequestScopeManager(
					maxScopes);
			session.setAttribute(REQUEST_STATE_MANAGER, requestStateManager);
		}
		return requestStateManager;
	}



	public void saveRequestScope(StateId stateId, BridgeRequestScope state) {
		// TODO - use mutex lock for syncronization.
		states.put(stateId, state);
		// update RequestStateManager object in session to enforce propogation in cluster.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if(null != facesContext){
			facesContext.getExternalContext().getSessionMap().put(REQUEST_STATE_MANAGER, this);
		}
	}
	
	/**
	 * @param stateId
	 * @return
	 */
	public BridgeRequestScope getRequestScope(StateId stateId) {
		BridgeRequestScope state = null;
		if (null != stateId) {
			state = (BridgeRequestScope) states.get(stateId);
		}
		return state;
	}

	public StateId getStateId(ActionRequest request,ActionResponse response) {
		return getStateIdForAction(request);
	}


	public StateId getStateId(EventRequest request,EventResponse response) {
		return getStateIdForAction(request);
	}

	public StateId getStateId(RenderRequest request, RenderResponse response) {
		return getStateIdForRender(request, response);
	}

	public StateId getStateId(ResourceRequest request,ResourceResponse response) {
		return getStateIdForRender(request, response);
	}

	
	private StateId getStateIdForAction(PortletRequest request) {
		UUID uuid = UUID.randomUUID();
		PortletMode portletMode = request.getPortletMode();
		StateId stateId = new StateId(portletMode,uuid.toString());
		return stateId;
	}


	private StateId getStateIdForRender(PortletRequest request, PortletResponse response){
		PortletMode portletMode = request.getPortletMode();
		StateId stateId = null ;
		PortletSession session = request.getPortletSession(false);
		String stateIdParameter = request.getParameter(STATE_ID_PARAMETER);
		if (null == stateIdParameter) {
			stateId = getStateIdFromViewHistory(portletMode, 
					session);
		} else {
			stateId = new StateId(stateIdParameter);
			// Check portlet mode for a changes:
			if (!portletMode.toString().equals(stateId.getMode())) {
				StateId historyStateId = getStateIdFromViewHistory(portletMode, 
						session);
				if(null != historyStateId){
					stateId = historyStateId;
				} else {
					stateId.setMode(portletMode.toString());
				}
			}
		}
		if (null == stateId) {
			stateId = new StateId(portletMode,response.getNamespace());
		}
		return stateId;		
	}
	
	private StateId getStateIdFromViewHistory(PortletMode portletModeName,
			 PortletSession session) {
		StateId stateId = null;
		if(null != session){
			String modeViewId = (String) session.getAttribute(Bridge.VIEWID_HISTORY+"."+portletModeName);
			if(null != modeViewId){
				try {
					PortalActionURL viewUrl = new PortalActionURL(modeViewId);
					String stateIdParameter = viewUrl.getParameter(STATE_ID_PARAMETER);
					if(null != stateIdParameter){
						stateId = new StateId(stateIdParameter);
					}
				} catch (MalformedURLException e) {
					// Ignore.
				}
				
			}
		}
		return stateId;
	}

	/**
	 * Last Recent Used Map cache. See {@link LinkedHashMap} for details.
	 * 
	 * @author asmirnov
	 * 
	 */
	private static class LRUMap<K, V> extends LinkedHashMap<K, V> implements
			Serializable {

		/**
   	 * 
   	 */
		private static final long serialVersionUID = -7232885382582796665L;
		private int capacity;

		/**
		 * @param capacity
		 *            - maximal cache capacity.
		 */
		public LRUMap(int capacity) {
			super(capacity, 1.0f, true);
			this.capacity = capacity;
		}

		protected boolean removeEldestEntry(Entry<K, V> entry) {
			// Remove last entry if size exceeded.
			return size() > capacity;
		}

		/**
		 * Get most recent used element
		 * 
		 * @return the most Recent value
		 */
		public Object getMostRecent() {
			Iterator<V> iterator = values().iterator();
			Object mostRecent = null;
			while (iterator.hasNext()) {
				mostRecent = iterator.next();

			}
			return mostRecent;
		}
	}

}
