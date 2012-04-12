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
import javax.portlet.Event;
import javax.portlet.faces.event.EventNavigationResult;

/**
 * The <code>BridgeEventHandler</code> interface defines the class the bridge relies on to process portlet events. Because
 * portlet events have arbitrary payloads the bridge provides no automated mappings to managed beans. Instead, the bridge calls
 * the <code>handleEvent</code> method on the <code>BridgeEventHandler</code> instance passed to it (via a
 * <code>PortletContext</code> attrbiute at init time. This method is expected to update any models based on the event's payload
 * and then to perform any needed application recomputation to ensure a consistent state. The method is called after the
 * <code>FacesContext</code> has been established and the <code>Lifecycle</code> has restored the view.
 * <p>
 * A view navigation can be affected by returning a non-null <code>EventNavigationResult</code>. Such an object will contain two
 * <code>String</code> values: a fromAction and an outcome. These correspond to the from action and outcomes in Faces navigation
 * rules. Using this information the bridge affects the navigation by calling the Faces <code>NavigationHandler</code>.
 */

public interface BridgeEventHandler {
    /**
     * Called by the bridge when it needs to process a portlet event.
     * <p>
     * 
     * Because portlet events have arbitrary payloads the bridge provides no automated mappings to managed beans. Instead, the
     * bridge calls the <code>handleEvent</code> method on the <code>BridgeEventHandler</code> instance passed to it (via a
     * <code>PortletContext</code> attrbiute at init time. This method is expected to update any models based on the event's
     * payload and then to perform any needed application recomputation to ensure a consistent state. The method is called after
     * the <code>FacesContext</code> has been established and the <code>Lifecycle</code> has restored the view.
     * <p>
     * A view navigation can be affected by returning a non-null <code>EventNavigationResult</code>. Such an object will contain
     * two <code>String</code> values: a fromAction and an outcome. These correspond to the from action and outcomes in Faces
     * navigation rules. Using this information the bridge affects the navigation by calling the Faces
     * <code>NavigationHandler</code>.
     * 
     * @param context current FacesContext. A Lifecycle has been acquired and the current view restored.
     * @param event the portlet event. Other portlet information (request/response) is accessed via the ExternalContext.
     * @return an object containing the fromAction and outcome of any navigation that resulted from this event. If the event
     *         doesn't cause a navigation, return null.
     */
    public EventNavigationResult handleEvent(FacesContext context, Event event);
}
