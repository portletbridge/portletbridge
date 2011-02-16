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
package org.jboss.portletbridge.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.w3c.dom.Element;

public class PortletContainerUtil {

    // Logger
    private static Logger logger = BridgeLogger.CONTEXT.getLogger();
    // Private Constants
    private static final String ELEMENT_ATTR_REL = "rel";
    private static final String ELEMENT_ATTR_SRC = "src";
    private static final String ELEMENT_ATTR_TYPE = "type";
    private static final String ELEMENT_NAME_SCRIPT = "script";
    private static final String ELEMENT_NAME_LINK = "link";
    private static final String ELEMENT_VALUE_CSS = "text/css";
    private static final String ELEMENT_VALUE_JAVASCRIPT = "text/javascript";
    private static final String ELEMENT_VALUE_STYLESHEET = "stylesheet";
	private static final String ELEMENT_ATTR_HREF = "href";

    public static void addScriptResourceToHead(PortletRequest portletRequest, String resourceURL, String id) {
        Object liferayPortlet = LiferayUtil.getLiferayPortlet(portletRequest);

        if (liferayPortlet != null) {
            LiferayUtil.addScriptResourceToLiferayHead(liferayPortlet, resourceURL, id);
        } else {
            logger.log(Level.WARNING,
                    "Unable to add <link /> resource to <head>...</head> section using standard mechanism: url=[{}]",
                    resourceURL);
        }
    }

    /**
     * Adds the specified script resource to the <head>...</head> section of the portal page.
     *
     * @param  portletRequest  The current portlet request.
     * @param  portletResponse  The current portlet response.
     * @param  resource  The script resource to be added.
     */
    public static void addScriptResourceToMarkupHeadElement(PortletRequest portletRequest,
            PortletResponse portletResponse, String resourceURL) {
        Element element = portletResponse.createElement(ELEMENT_NAME_SCRIPT);
        element.setAttribute(ELEMENT_ATTR_TYPE, ELEMENT_VALUE_JAVASCRIPT);
        element.setAttribute(ELEMENT_ATTR_SRC, resourceURL);
        portletResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
        logger.log(Level.FINER, "Added <script /> resource to <head>...</head> section using standard mechanism: url=[{}]",
                resourceURL);
    }

    public static void addScriptTextViaMarkupHeadElement(PortletRequest portletRequest, PortletResponse portletResponse,
            String script) {
        Element element = portletResponse.createElement(ELEMENT_NAME_SCRIPT);
        element.setAttribute(ELEMENT_ATTR_TYPE, ELEMENT_VALUE_JAVASCRIPT);
        element.setNodeValue(script);
        portletResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
        logger.log(Level.FINE, "Added <script /> text to <head>...</head> section using standard mechanism: script=[{}]", script);
    }

    public static void addStyleSheetResourceToHead(PortletRequest portletRequest, String resourceURL, String id) {
        Object liferayPortlet = LiferayUtil.getLiferayPortlet(portletRequest);

        if (liferayPortlet != null) {
            LiferayUtil.addStyleSheetResourceToLiferayHead(liferayPortlet, resourceURL, id);
        } else {
            logger.log(Level.WARNING,
                    "Unable to add <link /> resource to <head>...</head> section using standard mechanism: url=[{}]",
                    resourceURL);
        }
    }

    /**
     * Adds the specified style sheet resource to the <head>...</head> section of the portal page. See section 13.1.1.2
     * of the JSF 2.0 Spec for guidance on rendering resource URLs.
     *
     * @param  portletRequest  The current portlet request.
     * @param  portletResponse  The current portlet response.
     * @param  resource  The style sheet resource to be added.
     */
    public static void addStyleSheetResourceToMarkupHeadElement(PortletRequest portletRequest,
            PortletResponse portletResponse, String resourceURL) {
        Element element = portletResponse.createElement(ELEMENT_NAME_LINK);
        element.setAttribute(ELEMENT_ATTR_REL, ELEMENT_VALUE_STYLESHEET);
        element.setAttribute(ELEMENT_ATTR_TYPE, ELEMENT_VALUE_CSS);
        element.setAttribute(ELEMENT_ATTR_HREF, resourceURL);
        portletResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
        logger.log(Level.FINER, "Added <link /> resource to <head>...</head> section using standard mechanism: url=[{}]",
                resourceURL);
    }

    /**
     * Determines whether or not the portlet container supports the standard Portlet 2.0 mechanism for adding resources
     * to the <head>...</head> section of the rendered portal page. Section PLT.12.5.4 of the Portlet 2.0 spec indicates
     * that this is an "optional" feature for vendors to implement. Liferay Portal does not implement this feature.
     *
     * @param  portletRequest  The current portlet request (generated by the portlet container).
     * @return  True if the portlet container supports the standard Portlet 2.0 mechanism for adding resources.
     */
    public static boolean isMarkupHeadElementSupported(PortletRequest portletRequest) {
        return (portletRequest.getPortalContext().getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT) != null);
    }
}
