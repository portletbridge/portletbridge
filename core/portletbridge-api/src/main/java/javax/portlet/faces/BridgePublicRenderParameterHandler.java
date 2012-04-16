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

/**
 * The <code>BridgePublicRenderParameterHandler</code> interface defines the class the bridge relies on to post process
 * portlet public render parameters. The handler provides the portlet a means for resynching application state following
 * any model updates that resulted from the bridge pushing changed public render parameter values based on declarative
 * mappings. After the bridge pushes such values the bridge calls the handler if it has been configured during bridge
 * <code>init()</code>. Though the FacesContext has been acquired before the portlet is called to process these updates,
 * the Lifecycle has not been acquired or run. Because of this is is no current active view. Unlike events, one can't
 * navigate based on a public render parameter change.
 */

public interface BridgePublicRenderParameterHandler {
    /**
     * Called by the bridge after pushing incoming public render parameter values into mapped managed beans. Only called
     * if there is at least one public render parameter in the incoming request whose value is different (updates) the
     * underlying bean. This give the portlet an opportunity to perform further computations based on these changes to
     * resynchronize its application state.
     *
     * @param context
     *            current FacesContext. A Lifecycle has been acquired and the current view restored.
     */
    void processUpdates(FacesContext context);
}
