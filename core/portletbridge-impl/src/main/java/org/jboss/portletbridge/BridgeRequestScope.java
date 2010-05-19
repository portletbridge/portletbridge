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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.annotation.ExcludeFromManagedRequestScope;

import org.jboss.portletbridge.context.AbstractExternalContext;
import org.jboss.portletbridge.context.PortletBridgeContext;

/**
 * That class keep all request attributes that requires to store between portlet requests.
 * These parameters described by the chapter 5.1.2 "Managing Lifecycle State"
 * @author asmirnov
 */
/**
 * @author asmirnov
 * 
 */
public class BridgeRequestScope implements Serializable {

    private static final String EVAL_MAP_KEY = "com.sun.faces.el.CompositeComponentAttributesELResolver_EVAL_MAP";
	public static final String AJAX_PARAMETER_NAME = "AJAXREQUEST";

	/**
    *
    */
	private static final long serialVersionUID = 5630637804542426709L;

	/**
	 * Array of classes that are not stored in the request scope. See PP5.1.2
	 */
	private static final Class<?>[] excludedClasses = {
			javax.portlet.PortletConfig.class,
			javax.portlet.PortletContext.class,
			javax.portlet.PortletRequest.class,
			javax.portlet.PortletResponse.class,
			javax.portlet.PortletSession.class,
			javax.portlet.PortletPreferences.class,
			javax.portlet.PortalContext.class,
			javax.faces.context.FacesContext.class,
			javax.faces.context.ExternalContext.class,
			javax.servlet.ServletConfig.class,
			javax.servlet.ServletContext.class,
			javax.servlet.ServletRequest.class,
			javax.servlet.ServletResponse.class,
			javax.servlet.http.HttpSession.class };

	/**
	 * Names of request attributes that are not stored in the request scope.
	 */
	private static final String[] excludedRequestAttributes = { "ajaxContext",
			"javax.servlet.", "javax.portlet.", "javax.faces.",
			ResponseStateManager.VIEW_STATE_PARAM,
			AbstractExternalContext.INITIAL_REQUEST_ATTRIBUTES_NAMES };


	/**
	 * Saved {@link FacesMessage}
	 */
	private Map<String, List<FacesMessage>> messages;

	/**
	 * Request scope beans. That map saved by the {@code writeObject} method to
	 * avoid serialization exceptions.
	 */
	private transient Map<String, Object> beans;

	/**
	 * JSF View state , used if bridge stores view state in its own
	 * {@link StateManager}
	 */
	private Object componentsState;

	/**
	 * JSF View Tree from the last ActionRerquest
	 */
	private transient UIViewRoot viewRoot;

	/**
	 * Latest viewId
	 */
	private String viewId;

	/**
	 * Request paremeters saved for consequentual render requests.
	 */
	private Map<String, String[]> requestParameters;

	/**
	 * Namespace from the last render request or default portlet namespace.
	 */
	private String namespace;

	/**
	 * Captured value of the {@link ResponseStateManager#VIEW_STATE_PARAM} to
	 * proper restore view state during consequentual render requests, as
	 * required bu PP 5.1.2
	 */
	private String viewStateParameter;

	private String conversationIdParameter = "conversationId";

	private String conversationId;

    private transient Map<Object, Object> attributes;

    private boolean validationFailed;

	public BridgeRequestScope() {
	}

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * @param viewId
	 *            the viewId to set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * @return the _messages
	 */
	Map<String, List<FacesMessage>> getMessages() {
		return messages;
	}

	/**
	 * @param _messages
	 *            the _messages to set
	 */
	public void setMessages(Map<String, List<FacesMessage>> _messages) {
		this.messages = _messages;
	}

	/**
	 * @return the beans
	 */
	Map<String, Object> getBeans() {
		return beans;
	}

	/**
	 * @param beans the beans to set
	 */
	void setBeans(Map<String, Object> beans) {
		this.beans = beans;
	}

    Map<Object, Object> getAttributes() {
        return attributes;
    }

    void setAttributes(Map<Object, Object> attributes) {
        this.attributes = attributes;
    }

	/**
	 * @return the _viewRoot
	 */
	public UIViewRoot getViewRoot() {
		return viewRoot;
	}

	/**
	 * @param root
	 *            the _viewRoot to set
	 */
	public void setViewRoot(UIViewRoot root) {
		viewRoot = root;
	}

	/**
	 * @return the componentsState
	 */
	public Object getComponentsState() {
		return componentsState;
	}

	/**
	 * @param componentsState
	 *            the componentsState to set
	 */
	public void setComponentsState(Object componentsState) {
		this.componentsState = componentsState;
	}

	/**
	 * @return the viewStateParameter
	 */
	public String getViewStateParameter() {
		return viewStateParameter;
	}

	/**
	 * @param viewStateParameter
	 *            the viewStateParameter to set
	 */
	public void setViewStateParameter(String viewStateParameter) {
		this.viewStateParameter = viewStateParameter;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the conversationIdParameter
	 */
	public String getConversationIdParameter() {
		return conversationIdParameter;
	}

	/**
	 * @param conversationIdParameter
	 *            the conversationIdParameter to set
	 */
	public void setConversationIdParameter(String conversationIdParameter) {
		this.conversationIdParameter = conversationIdParameter;
	}

	/**
	 * @return the conversationId
	 */
	public String getConversationId() {
		return conversationId;
	}

	/**
	 * @param conversationId
	 *            the conversationId to set
	 */
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public Map<String, String[]> getRequestParameters() {
		if (null == requestParameters) {
			requestParameters = new HashMap<String, String[]>(4);
		}
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String[]> requestParameters) {
		this.requestParameters = requestParameters;
	}

	/**
	 * @param facesContext
	 * @param render
	 */
	public void restoreRequest(FacesContext facesContext, boolean render) {
		if (render) {
            if (getViewRoot() != null) {
                facesContext.setViewRoot(getViewRoot());
                setViewRoot(null);
                restoreAttributes(facesContext);
            }
        }
		restoreMessages(facesContext);
		restoreBeans(facesContext);
        if (validationFailed) {
            facesContext.validationFailed();
        }
    }

	/**
	 * @param facesContext
	 * @param withViewRoot
	 */
	public void saveRequest(FacesContext facesContext, boolean withViewRoot) {
		if (withViewRoot) {
			UIViewRoot root = facesContext.getViewRoot();
			setViewRoot(root);
			if (null != root) {
				setViewId(root.getViewId());
			}
                        saveAttributes(facesContext);
		}
		saveBeans(facesContext);
		saveMessages(facesContext);
		saveRequestParameters(facesContext);
        validationFailed = facesContext.isValidationFailed();
	}

	/**
	 * @param facesContext
	 */
	public void saveMessages(FacesContext facesContext) {
		messages = new HashMap<String, List<FacesMessage>>();
		Iterator<String> idsWithMessages = facesContext
				.getClientIdsWithMessages();
		while (idsWithMessages.hasNext()) {
			String id = idsWithMessages.next();
			Iterator<FacesMessage> messages = facesContext.getMessages(id);
			while (messages.hasNext()) {
				FacesMessage message = messages.next();
				addMessage(id, message);
			}
		}
	}

	/**
	 * @param facesContext
	 */
	public void restoreMessages(FacesContext facesContext) {
		if (null != messages) {
			Iterator<String> idsWithMessages = getClientIdsWithMessages();
			while (idsWithMessages.hasNext()) {
				String id = idsWithMessages.next();
				Iterator<FacesMessage> messages = getMessages(id);
				while (messages.hasNext()) {
					FacesMessage message = messages.next();
					facesContext.addMessage(id, message);
				}
			}

		}
	}

	/**
     * @param facesContext
     */
    public void saveAttributes(FacesContext facesContext) {
        attributes = new HashMap<Object, Object>();
        Map<Object, Object> fattributes = facesContext.getAttributes();
        attributes.putAll(fattributes);
    }

    /**
     * @param facesContext
     */
    public void restoreAttributes(FacesContext facesContext) {
        if (null != attributes) {
            Map<UIComponent, Object> topMap = (Map<UIComponent, Object>) attributes.get(EVAL_MAP_KEY);
            if (topMap != null) {
                for (Object exp : topMap.values()) {
                    try {
                        Class aClass = exp.getClass();
                        Field field = aClass.getDeclaredField("ctx");
                        field.setAccessible(true);
                        field.set(exp, facesContext);
                        field.setAccessible(false);
                    } catch (Exception ex) {
                        Logger.getLogger(BridgeRequestScope.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

            facesContext.getAttributes().putAll(attributes);
        }
    }

    /**
	 * Save request-scope beans, as described in the JSR-301 5.1
	 * 
	 * @param facesContext
	 */
	public void saveBeans(FacesContext facesContext) {
		beans = null;
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletBridgeContext bridgeContext = PortletBridgeContext
				.getCurrentInstance(facesContext);
		BridgeConfig bridgeConfig = bridgeContext.getBridgeConfig();
		Set<String> existingAttributes = null;
		if (null != bridgeContext) {
			existingAttributes = bridgeContext
					.getInitialRequestAttributeNames();
		}
		if (null == existingAttributes) {
			existingAttributes = new HashSet<String>();
		} else {
			// Create local copy for merge with initial parameters.
			existingAttributes = new HashSet<String>(existingAttributes);
		}
		// Get list of request attributes preventing to save.
		// in the JSR 301 PLT 5.1
		Set<ExcludedRequestAttribute> excluded = bridgeConfig
				.getExcludedAttributes();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		for (Iterator<Entry<String, Object>> iterator = requestMap.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			boolean include = true;
			String attributeName = entry.getKey();
			if (existingAttributes.contains(attributeName)) {
				include = false;
			}
			Object bean = entry.getValue();
			if (null != bean) {
				if (bean.getClass().isAnnotationPresent(
						ExcludeFromManagedRequestScope.class)) {
					include = false;
				}
				for (int i = 0; (i < excludedClasses.length) && include; i++) {
					if (excludedClasses[i].isInstance(bean)) {
						include = false;
						break;
					}
				}
				for (int i = 0; (i < excludedRequestAttributes.length)
						&& include; i++) {
					if (attributeName.startsWith(excludedRequestAttributes[i])) {
						include = false;
						break;
					}
				}
				if (include && null != excluded) {
					for (ExcludedRequestAttribute excludedRequestAttribute : excluded) {
						if (excludedRequestAttribute.match(attributeName)) {
							include = false;
							break;
						}
					}
				}

				if (include) {
					if (null == beans) {
						beans = new HashMap<String, Object>();
					}
					beans.put(attributeName, bean);
				}

			}
		}
	}

	public void saveRequestParameters(FacesContext facesContext) {
		// Save request parameters ( all or a View state only ) restored as
		// requered
		// in the JSR 301 PLT 5.1
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletBridgeContext bridgeContext = PortletBridgeContext
				.getCurrentInstance(facesContext);
		BridgeConfig bridgeConfig = bridgeContext.getBridgeConfig();
		boolean preserveActionParam = bridgeConfig.isPreserveActionParams();
		if (preserveActionParam) {
			requestParameters = new HashMap<String, String[]>(externalContext
					.getRequestParameterValuesMap());

			requestParameters.remove(AJAX_PARAMETER_NAME);

			requestParameters.remove(ResponseStateManager.VIEW_STATE_PARAM);
		} else {
			requestParameters = new HashMap<String, String[]>();
		}
	}


	public void restoreBeans(FacesContext facesContext) {
		Map<String, Object> requestMap = facesContext.getExternalContext()
				.getRequestMap();
		if (null != beans) {
			requestMap.putAll(beans);
		}
		Map<String, String[]> requestParameters = getRequestParameters();
		if (requestParameters
				.containsKey(ResponseStateManager.VIEW_STATE_PARAM)) {
			requestMap.put(Bridge.IS_POSTBACK_ATTRIBUTE, Boolean.TRUE);
		}
		if (null != conversationId) {
			requestParameters.put(conversationIdParameter,
					new String[] { conversationId });
		}
	}

	public void reset() {
		this.requestParameters = null;
		this.beans = null;
		this.componentsState = null;
		this.messages = null;
		this.viewRoot = null;
		this.viewId = null;
		this.conversationId = null;
		this.conversationIdParameter = null;
        this.attributes = null;
        this.validationFailed = false;
	}

	private void addMessage(String clientId, FacesMessage message) {
		List<FacesMessage> list = messages.get(clientId);
		if (list == null) {
			list = new ArrayList<FacesMessage>();
			messages.put(clientId, list);
		}
		list.add(message);
	}

	private Iterator<String> getClientIdsWithMessages() {
		return (messages.keySet().iterator());
	}

	private Iterator<FacesMessage> getMessages(String clientId) {
		List<FacesMessage> list = messages.get(clientId);
		if (list != null) {
			return (list.iterator());
		} else {
			return Collections.<FacesMessage> emptyList().iterator();
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// Save all default fields
		out.defaultWriteObject();
		// Save serializable request-scope beans.
		if (null != beans) {
			for (Map.Entry<String, Object> entry : beans.entrySet()) {
				String key = entry.getKey();
				Object bean = entry.getValue();
				if (null != key && key.length() > 0 && null != bean
						&& bean instanceof Serializable) {
					out.writeUTF(key);
					out.writeObject(bean);
				}
			}

		} // End of request scope beans marker.
		out.writeUTF("");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		String key = in.readUTF();
		if (key.length() > 0) {
			beans = new HashMap<String, Object>();
			while (key.length() > 0) {
				Object bean = in.readObject();
				beans.put(key, bean);
				key = in.readUTF();
			}
		}
	}

}
