/**
 * Copyright (C) 2010 portletfaces.org
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *               http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is PortletFaces open source software code.
 *
 * The Initial Developer of the Original Code is mimacom ag, Switzerland.
 * Portions created by the Initial Developer are Copyright (C) 2010
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License.
 */
package org.jboss.portletbridge.renderkit.portlet;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.jboss.portletbridge.context.PortletBridgeContext;
import org.jboss.portletbridge.util.BridgeLogger;
import org.jboss.portletbridge.util.PortletContainerUtil;

/**
 * This class is a JSF renderer that is designed for use with the h:head
 * component tag. Portlets are forbidden from rendering the <head>...</head>
 * section, which is what is done by the JSF implementation's version of this
 * renderer. This renderer avoids rendering the <head>...</head> section and
 * instead delegates that responsibility to the portal.
 * 
 * @author Neil Griffin
 */
public class PortletHeadRenderer extends Renderer {

	// Logger
	private static Logger logger = BridgeLogger.CONTEXT.getLogger();
	private static final String COMPONENT_ATTR_NAME = "name";
	private static final String COMPONENT_ATTR_LIBRARY = "library";
	private static final String MSG_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
	private static final String RENDERER_TYPE_SCRIPT = "javax.faces.resource.Script";
	private static final String RENDERER_TYPE_STYLESHEET = "javax.faces.resource.Stylesheet";
	private static final String CONTENT_TYPE_SCRIPT = "text/javascript";
	private static final String CONTENT_TYPE_STYLESHEET = "text/css";
	private static final String TARGET_BODY = "body";
	private static final String TARGET_HEAD = "head";

	/**
	 * Rather than render the <head>...</head> section to the response, this
	 * method attempts to delegate this responsibility to the portlet container.
	 * 
	 * @see Renderer#encodeBegin(FacesContext, UIComponent)
	 */
	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
			throws IOException {
		logger.log(Level.FINEST, "encodeBegin()");
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		List<UIComponent> uiComponentResources = uiViewRoot
				.getComponentResources(facesContext, TARGET_HEAD);
		PortletBridgeContext portletBridgeContext = PortletBridgeContext
				.getCurrentInstance(facesContext);
		String portletName = portletBridgeContext.getBridgeConfig()
				.getPortletName();

		if (uiComponentResources != null) {

			for (UIComponent uiComponentResource : uiComponentResources) {
				String rendererType = uiComponentResource.getRendererType();
				String resourceName = getResourceName(uiComponentResource);

				if (resourceName != null) {
					String resourceLibraryName = getResourceLibraryName(uiComponentResource);
					String resourceKey = (resourceLibraryName != null ? resourceLibraryName
							: "default")
							+ ":" + resourceName;
					String uid = String.valueOf(Math
							.abs((portletName + ":" + resourceKey).hashCode()));

					if (!isResourceAlreadyRendered(facesContext, resourceKey)) {
						Resource resource = createResource(facesContext,
								resourceName, resourceLibraryName);
						if (null != resource) {
							String contentType = resource.getContentType();
							ExternalContext externalContext = facesContext
									.getExternalContext();
							String resourceURL = getEncodedResourceURL(
									externalContext, resource);
							PortletRequest portletRequest = (PortletRequest) externalContext
									.getRequest();
							PortletResponse portletResponse = (PortletResponse) externalContext
									.getResponse();

							if (CONTENT_TYPE_SCRIPT.equals(contentType)) {
								if (PortletContainerUtil
										.isMarkupHeadElementSupported(portletRequest)) {
									PortletContainerUtil
											.addScriptResourceToMarkupHeadElement(
													portletRequest,
													portletResponse,
													resourceURL);
								} else {
									PortletContainerUtil
											.addScriptResourceToHead(
													portletRequest,
													resourceURL, uid);
								}
							} else if (CONTENT_TYPE_STYLESHEET
									.equals(contentType)) {
								if (PortletContainerUtil
										.isMarkupHeadElementSupported(portletRequest)) {
									PortletContainerUtil
											.addStyleSheetResourceToMarkupHeadElement(
													portletRequest,
													portletResponse,
													resourceURL);
								} else {
									PortletContainerUtil
											.addStyleSheetResourceToHead(
													portletRequest,
													resourceURL, uid);
								}
							} else {
								logger.log(
										Level.WARNING,
										"Unable to add UIComponent class=["
												+ uiComponent.getClass()
												+ "] with rendererType=["
												+ rendererType
												+ "] and contentType=["
												+ contentType
												+ "] to <head>...</head> section of portal page");
							}

						}
						setResourceAlreadyRendered(facesContext, resourceKey);
					}
				} else {

					// If the resource is an externally loaded script, then
					if (RENDERER_TYPE_SCRIPT.equals(rendererType)) {
						ExternalContext externalContext = facesContext
								.getExternalContext();
						PortletRequest portletRequest = (PortletRequest) externalContext
								.getRequest();

						// If the portlet container supports the Portlet 2.0
						// standard mechanism for adding elements to the
						// <head>...</head> section of the rendered page, then
						// get the script text from the resource and add it via
						// the standard mechanism.
						if (PortletContainerUtil
								.isMarkupHeadElementSupported(portletRequest)) {
							PortletResponse portletResponse = (PortletResponse) externalContext
									.getResponse();
							String resourceValueAsString = getResourceValueAsString(uiComponentResource);
							PortletContainerUtil
									.addScriptTextViaMarkupHeadElement(
											portletRequest, portletResponse,
											resourceValueAsString);
						} // Otherwise, we have no choice but to relocate the
							// resource to the body, since there is no way to
							// render it in the <head>...</head> section.
						else {
							logger.log(
									Level.FINER,
									"Relocating resource to body, rendererType=[{}]",
									rendererType);
							uiViewRoot.addComponentResource(facesContext,
									uiComponentResource, TARGET_BODY);
						}
					} // Otherwise, we have no choice but to relocate the
						// resource to the body, since there is no way to
						// render it in the <head>...</head> section.
					else {
						logger.log(
								Level.FINER,
								"Relocating resource to body, rendererType=[{}]",
								rendererType);
						uiViewRoot.addComponentResource(facesContext,
								uiComponentResource, TARGET_BODY);
					}
				}
			}
		}
	}

	/**
	 * Creates a resource associated with the specified name and library.
	 * 
	 * @param facesContext
	 *            The current faces context.
	 * @param resourceName
	 *            The resource name.
	 * @param resourceLibraryName
	 *            The resource library name.
	 * @return An instance of a new resource associated with the specified name
	 *         and library.
	 */
	protected Resource createResource(FacesContext facesContext,
			String resourceName, String resourceLibraryName) {
		return facesContext.getApplication().getResourceHandler()
				.createResource(resourceName, resourceLibraryName);
	}

	/**
	 * Returns a URL for the specified resource that can be used to download the
	 * resource from the browser. See section 13.1.1.2 of the JSF 2.0 Spec for
	 * guidance on rendering resource URLs.
	 * 
	 * @param resource
	 *            The JSF resource.
	 * @return The URL of the JSF resource.
	 */
	protected String getEncodedResourceURL(ExternalContext externalContext,
			Resource resource) {
		String resourceURL = resource.getRequestPath();

		if (resourceURL == null) {
			resourceURL = MSG_RESOURCE_NOT_FOUND;
		} else {
			// TODO - check resource url for double encoding.
			// resourceURL = externalContext.encodeResourceURL(resourceURL);
		}

		return resourceURL;
	}

	/**
	 * Gets the resource "library" name associated with the specified component
	 * resource.
	 * 
	 * @param uiComponent
	 *            The component resource.
	 * @return The library associated with the specified component resource.
	 */
	protected String getResourceLibraryName(UIComponent resourceUIComponent) {
		return (String) resourceUIComponent.getAttributes().get(
				COMPONENT_ATTR_LIBRARY);
	}

	/**
	 * Gets the resource "name" associated with the specified component
	 * resource. Note that if the specified component has a query-string, then
	 * the query-string will not appear in the return value. This is because
	 * script resources may specify query-strings for internal purposes, but
	 * those query-strings have no meaning for rendered URLs.
	 * 
	 * @param uiComponent
	 *            The component resource.
	 * @return The name associated with the specified component resource.
	 */
	protected String getResourceName(UIComponent resourceUIComponent) {
		String resourceName = (String) resourceUIComponent.getAttributes().get(
				COMPONENT_ATTR_NAME);

		if (resourceName != null) {
			int pos = resourceName.indexOf('?');

			if (pos > 0) {
				resourceName = resourceName.substring(0, pos);
			}
		}

		return resourceName;
	}

	protected String getResourceValueAsString(UIComponent uiComponent) {
		String value = null;

		if (uiComponent instanceof ValueHolder) {
			ValueHolder valueHolder = (ValueHolder) uiComponent;
			Object valueAsObject = valueHolder.getValue();

			if (valueAsObject != null) {
				value = valueAsObject.toString();
			}
		}

		return value;
	}

	/**
	 * Checks a request attribute in order to determine whether or not the
	 * resource associated with the specified key has already been rendered
	 * (added) for inclusion in the <head>...</head> section of the portal page.
	 * 
	 * @param facesContext
	 *            The current faces context.
	 * @param resourceKey
	 *            The key associated with the resource.
	 * @return True if the resource has been rendered, otherwise false.
	 */
	protected boolean isResourceAlreadyRendered(FacesContext facesContext,
			String resourceKey) {
		return (facesContext.getAttributes().get(resourceKey) != null);
	}

	/**
	 * Sets a request attribute indicating that the resoruce associated with the
	 * specified key has already been rendered (added) for inclusion in the
	 * <head>...</head> section of the portal page.
	 * 
	 * @param facesContext
	 *            The current faces context.
	 * @param resourceKey
	 *            The key associated with the resource.
	 */
	protected void setResourceAlreadyRendered(FacesContext facesContext,
			String resourceKey) {
		facesContext.getAttributes().put(resourceKey, Boolean.TRUE);
	}
}
