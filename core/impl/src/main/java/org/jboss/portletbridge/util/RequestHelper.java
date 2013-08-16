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
package org.jboss.portletbridge.util;

import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;

import org.jboss.portletbridge.PortletBridgeConstants;
import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public final class RequestHelper {
    public static final String MARKUP_HEAD_ENABLED = "org.jboss.portletbridge.markupHead.enabled";

    private RequestHelper() {
    }

    public static boolean canMarkupHead(PortletRequest request) {
        // Check if Portlet App has requested it to be disabled
        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
        String bridgeMarkupHead = bridgeContext.getPortletContext().getInitParameter(MARKUP_HEAD_ENABLED);
        if (null != bridgeMarkupHead) {
            Boolean bridgeMarkup = Boolean.valueOf(bridgeMarkupHead);
            if (!bridgeMarkup) {
                return false;
            }
        }

        // Check for WSRP Request and disable head markup if it is
        if (isWSRPRequest()) {
            return false;
        }

        // Check if Portlet Container supports it
        String containerMarkupHeadSupport = request.getPortalContext().getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT);
        if (null != containerMarkupHeadSupport) {
            Boolean container = Boolean.valueOf(containerMarkupHeadSupport);
            if (container) {
                return true;
            }
        }

        return false;
    }

    public static boolean isWSRPRequest() {
        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
        Boolean wsrpRequestParam = (Boolean)bridgeContext.getPortletRequest().getAttribute(PortletBridgeConstants.WSRP_REQUEST_PARAM);
        if (Boolean.TRUE.equals(wsrpRequestParam)) {
            return true;
        }
        return false;
    }
}
