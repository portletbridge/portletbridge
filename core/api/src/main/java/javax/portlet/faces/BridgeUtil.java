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

import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge.PortletPhase;

/**
 * Utility class designed to make it easy for Faces subsystems including the bridge itself to determine whether this
 * request is running in a portlet container and/or which portlet request phase it is executing in.
 *
 * @author asmirnov
 *
 */
public class BridgeUtil {

    private BridgeUtil() {
        // There is only static methods in the class.
    }

    /**
     * Indicates whether the current request is executing in the portlet container. If it returns <code>true</code> the
     * request is a portlet request, otherwise it is not.
     */
    public static boolean isPortletRequest() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // This method might be called during App startup (via a context listener) and hence no FacesContext
        // For example a renderkit might createComponents during such time -- as the bridge overrides faces Application
        // which implements createComponent and calls this method (to see if we need to wrap/replace with the
        // NamingContainer
        if (ctx == null) {
            return false;
        }

        return null != getPortletRequestPhase();
    }

    /**
     * Return describes the portlet request phase currently being executed. If <code>null</code> then this request is
     * not being executed in a portlet container.
     */
    public static Bridge.PortletPhase getPortletRequestPhase() {
        return (PortletPhase) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
            .get(Bridge.PORTLET_LIFECYCLE_PHASE);
    }

}
