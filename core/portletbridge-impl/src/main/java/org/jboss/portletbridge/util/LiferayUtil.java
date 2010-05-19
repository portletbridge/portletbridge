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
 * Portions created by the Initial Developer are Copyright (C) 2009
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.jboss.portletbridge.context.PortalActionURL;

public class LiferayUtil {

    // Logger
    private static Logger logger = BridgeLogger.CONTEXT.getLogger();
    // Private Constants
    private static final String LIFERAY_PACKAGE_PREFIX = "com.liferay";
    private static final String LIFERAY_REQ_ATTR_RENDER_PORTLET = "RENDER_PORTLET";
    private static final String LIFERAY_METHOD_NAME_GET_PORTLET = "getPortlet";
    private static final String REQUEST_ATTR_PORTLET_REQUEST = "javax.portlet.request";
    private static final String LIFERAY_RESOURCE_UID = "uuid";

    /**
     * Adds the specified scrkpt resource to the <head>...</head> section of the Liferay portal page.
     *
     * @param  liferayPortlet  An instance of com.liferay.portal.model.Portlet
     * @param  resourceURL  The script resource URL.
     */
    @SuppressWarnings("unchecked")
    public static void addScriptResourceToLiferayHead(Object liferayPortlet, String resourceURL, String id) {

        // Note: Use Java Reflection in order to avoid a compile-time dependency.
        try {
            Method getHeaderPortalJavaScriptMethod = liferayPortlet.getClass().getMethod("getHeaderPortalJavaScript",
                    (Class[]) null);
            boolean added = false;

            if (getHeaderPortalJavaScriptMethod != null) {
                List<String> headerPortalJavaScriptList = (List<String>) getHeaderPortalJavaScriptMethod.invoke(
                        liferayPortlet, (Object[]) null);

                PortalActionURL resurl = new PortalActionURL(resourceURL);
                resurl.addParameter(LIFERAY_RESOURCE_UID, id);
                resourceURL = resurl.toString();

                if (headerPortalJavaScriptList != null) {
                    if (headerPortalJavaScriptList.contains(resourceURL)) {
                        added = true;
                    }
                    if (!added) {
                        ListIterator<String> iter = headerPortalJavaScriptList.listIterator();
                        while (iter.hasNext()) {
                            String foundURL = iter.next();
                            PortalActionURL fresurl = new PortalActionURL(foundURL);
                            String fuid = fresurl.getParameter(LIFERAY_RESOURCE_UID);
                            if (fuid != null && fuid.equals(id)) {
                                //iter.set(resourceURL);
                                added = true;
                                break;
                            }
                        }
                    }
                    if (!added) {
                        headerPortalJavaScriptList.add(resourceURL);
                        added = true;
                    }
                }

            }

            if (!added) {
                logger.log(Level.WARNING,
                        "Unable to add <script /> resource to <head>...</head> section using Liferay mechanism: url=[{}]",
                        resourceURL);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the specified style sheet resource to the <head>...</head> section of the Liferay portal page.
     *
     * @param  liferayPortlet  An instance of com.liferay.portal.model.Portlet
     * @param  resourceURL  The style sheet resource URL.
     */
    @SuppressWarnings("unchecked")
    public static void addStyleSheetResourceToLiferayHead(Object liferayPortlet, String resourceURL, String id) {

        // Note: Use Java Reflection in order to avoid a compile-time dependency.
        try {
            Method getHeaderPortalCssMethod = liferayPortlet.getClass().getMethod("getHeaderPortalCss", (Class[]) null);
            boolean added = false;

            if (getHeaderPortalCssMethod != null) {
                List<String> headerPortalCssList = (List<String>) getHeaderPortalCssMethod.invoke(liferayPortlet,
                        (Object[]) null);

                PortalActionURL resurl = new PortalActionURL(resourceURL);
                resurl.addParameter(LIFERAY_RESOURCE_UID, id);
                resourceURL = resurl.toString();

                if (headerPortalCssList != null) {
                    if (headerPortalCssList.contains(resourceURL)) {
                        added = true;
                    }
                    if (!added) {
                        ListIterator<String> iter = headerPortalCssList.listIterator();
                        while (iter.hasNext()) {
                            String foundURL = iter.next();
                            PortalActionURL fresurl = new PortalActionURL(foundURL);
                            String fuid = fresurl.getParameter(LIFERAY_RESOURCE_UID);
                            if (fuid != null && fuid.equals(id)) {
                                //iter.set(resourceURL);
                                added = true;
                                break;
                            }
                        }
                    }
                    if (!added) {
                        headerPortalCssList.add(resourceURL);
                        added = true;
                    }
                }
            }

            if (!added) {
                logger.log(Level.WARNING,
                        "Unable to add <link /> resource to <head>...</head> section using Liferay mechanism: url=[{}]",
                        resourceURL);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Gets the Liferay portlet object associated with the specified portlet request.
     *
     * @param  portletRequest  The current portlet request.
     * @return  An instance of com.liferay.portal.model.Portlet
     */
    public static Object getLiferayPortlet(PortletRequest portletRequest) {

        // Try to get the Liferay portlet object from a typical Liferay request attribute.
        Object portlet = portletRequest.getAttribute(LIFERAY_REQ_ATTR_RENDER_PORTLET);

        // If not found, then
        if (portlet == null) {

            // Try to get it using Java Reflection, assuming that the portlet request is an instance of Liferay's
            // RenderRequestImpl which has a getPortlet() method.
            try {
                Method method = portletRequest.getClass().getMethod(LIFERAY_METHOD_NAME_GET_PORTLET, (Class[]) null);

                if (method != null) {
                    portlet = method.invoke(portletRequest, (Object[]) null);
                }
            } catch (Exception e) {
                // ignore
            }

            // Last chance -- it might be the case that the PortletRequest is being wrapped
            // by a JSF portlet bridge PortletRequest, and so try and get the wrapped Liferay
            // PortletRequest implementation instance from the javax.portlet.request attribute
            // and then try reflection again.
            if (portlet == null) {
                PortletRequest portletRequest2 = (PortletRequest) portletRequest.getAttribute(
                        REQUEST_ATTR_PORTLET_REQUEST);

                if (portletRequest2 != null) {

                    try {
                        Method method = portletRequest2.getClass().getMethod(LIFERAY_METHOD_NAME_GET_PORTLET,
                                (Class[]) null);

                        if (method != null) {
                            portlet = method.invoke(portletRequest2, (Object[]) null);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }

        if (portlet == null) {

            // Note that the Liferay ActionRequestImpl does not have a getPortlet() method
            // so only report an error if this is a RenderRequest or a ResourceRequest.
            if (!(portletRequest instanceof ActionRequest)) {

                if (portletRequest.getClass().getName().indexOf(LIFERAY_PACKAGE_PREFIX) >= 0) {
                    logger.log(Level.SEVERE, "Could not retrieve Liferay portlet object");
                }
            }
        }

        return portlet;
    }

    /**
     * There is a bug in some versions of Liferay's PortalImpl.getStaticResourceURL(...) method in which it appends
     * request parameters with a question-mark instead of an ampersand. This method is a hack-fix.
     *
     * @param value The request parameter value that may need to be fixed.
     * @return The fixed request parameter value.
     */
    public static String fixRequestParameterValue(String value) {
        String fixedValue = value;
        if (value != null) {
            int pos = value.indexOf("?browserId=");
            if (pos > 0) {
                fixedValue = value.substring(0, pos);
            }
        }
        return fixedValue;
    }

    /**
     * There is a bug in some versions of Liferay's PortalImpl.getStaticResourceURL(...) method in which it appends
     * request parameters with a question-mark instead of an ampersand. This method is a hack-fix.
     *
     * @param values The request parameter values that may need to be fixed.
     * @return The fixed request parameter values.
     */
    public static String[] fixRequestParameterValues(String[] values) {

        String[] fixedValues = values;
        if (values != null) {
            fixedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                fixedValues[i] = fixRequestParameterValue(values[i]);
            }
        }
        return fixedValues;
    }
}
