/******************************************************************************
 * $Id$
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

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.BridgeException;

/**
 * @author asmirnov
 *
 */
public abstract class BridgeStrategyWrapper extends BridgeStrategy {

	public BridgeStrategyWrapper(BridgeConfig config) {
	    super(config);
    }

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#initRenderKit(javax.faces.context.FacesContext, javax.faces.render.RenderKitFactory)
	 */
	@Override
	public void init(FacesContext context,
	        RenderKitFactory renderKitFactory) {
		getWrapped().init(context, renderKitFactory);
	}

	/**
	 * @return
	 */
	protected abstract BridgeStrategy getWrapped();
	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#createResponseWrapper(javax.portlet.RenderResponse)
	 */
	@Override
	public RenderResponse createResponseWrapper(RenderResponse response) {
		return getWrapped().createResponseWrapper(response);
	}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#createResponseWrapper(javax.portlet.ResourceResponse)
	 */
	@Override
	public ResourceResponse createResponseWrapper(ResourceResponse response) {
		return getWrapped().createResponseWrapper(response);
	}
	
	@Override
    public void beforeActionRequest(FacesContext facesContext) {
	    getWrapped().beforeActionRequest(facesContext);
	    
    }

	@Override
	public void afterActionRequestExecute(FacesContext facesContext) {
	    getWrapped().afterActionRequestExecute(facesContext);
	}
	@Override
    public void afterActionRequest(FacesContext facesContext) {
	    getWrapped().afterActionRequest(facesContext);
	    
    }

	@Override
    public void beforeEventRequest(FacesContext facesContext) {
	    getWrapped().beforeEventRequest(facesContext);
	    
    }

	@Override
    public void afterEventRequest(FacesContext facesContext) {
	    getWrapped().afterEventRequest(facesContext);
	    
    }

	/* (non-Javadoc)
     * @see org.jboss.portletbridge.BridgeStrategy#setupRenderParams(javax.faces.context.FacesContext, org.jboss.portletbridge.StateId)
     */
    @Override
    public void beforeRenderRequest(FacesContext facesContext) {
    	getWrapped().beforeRenderRequest(facesContext);
    }

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#finishResponse(javax.faces.context.FacesContext, javax.portlet.RenderResponse)
	 */
	@Override
	public void afterRenderRequest(FacesContext facesContext,
	        RenderResponse wrappedResponse) {
		getWrapped().afterRenderRequest(facesContext, wrappedResponse);

	}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#beforeResourceRequest(javax.faces.context.FacesContext)
	 */
	@Override
    public void beforeResourceRequest(FacesContext facesContext) {
        getWrapped().beforeResourceRequest(facesContext);

    }

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#afterResourceRequestExecute(javax.faces.context.FacesContext)
	 */
	//@Override
	//public void afterResourceRequestExecute(FacesContext facesContext) {
	    //getWrapped().afterResourceRequestExecute(facesContext);

	//}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#finishResponse(javax.faces.context.FacesContext, javax.portlet.ResourceResponse)
	 */
	@Override
	public void afterResourceRequest(FacesContext facesContext,
	        ResourceResponse wrappedResponse) {
		getWrapped().afterResourceRequest(facesContext, wrappedResponse);

	}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#getPortletSessionScopeForName(java.lang.String)
	 */
	@Override
	public int getPortletSessionScopeForName(String name) {
		return getWrapped().getPortletSessionScopeForName(name);
	}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#serveResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	@Override
	public boolean serveResource(ResourceRequest request,
	        ResourceResponse response) throws BridgeException {
		return getWrapped().serveResource(request, response);
	}

	/* (non-Javadoc)
	 * @see org.jboss.portletbridge.BridgeStrategy#createViewRoot()
	 */
	//@Override
	//public UIViewRoot createViewRoot() {
		//return getWrapped().createViewRoot();
//}
}
