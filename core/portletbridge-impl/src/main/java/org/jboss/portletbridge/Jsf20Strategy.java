/**
 * 
 */
package org.jboss.portletbridge;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.BridgeException;

import org.jboss.portletbridge.application.resource.PortletResourceHandler;
import org.jboss.portletbridge.context.flash.PortletFlash;
import org.jboss.portletbridge.util.BridgeLogger;

/**
 * @author asmirnov
 * 
 */
public class Jsf20Strategy extends BridgeStrategy {

	private static final Logger log = BridgeLogger.BRIDGE.getLogger();
	private Application application;
	public Jsf20Strategy(BridgeConfig config) {
		super(config);
	}

	public void init(FacesContext context,
			RenderKitFactory renderKitFactory) {
		// Init Flash object.
		PortletFlash flash = new PortletFlash();
		context.getExternalContext().getApplicationMap().put(PortletFlash.ATTRIBUTE_NAME, flash);
		application = context.getApplication();
	}

	public void beforeRenderRequest(FacesContext facesContext) {
		// Do nothing

	}

	public RenderResponse createResponseWrapper(RenderResponse response) {
		return response;
	}

	public ResourceResponse createResponseWrapper(ResourceResponse response) {
		return response;
	}

	public void afterRenderRequest(FacesContext facesContext,
			RenderResponse wrappedResponse) {
		// DO nothing
		
	}

	public void afterResourceRequest(FacesContext facesContext,
			ResourceResponse wrappedResponse) {
		
	}

	@Override
    public boolean serveResource(ResourceRequest request,
            ResourceResponse response) throws BridgeException {
	    if(ResourceHandler.RESOURCE_IDENTIFIER.equals(request.getResourceID())){
	    	FacesContext facesContext = getConfig().createFacesContext(request, response);
	    	try {
		    	// serve resource.
	    		application.getResourceHandler().handleResourceRequest(facesContext);
	    	} catch (IOException e) {
	            throw new BridgeException(e);
            } finally {
	    		facesContext.release();
	    	}
	    	return true;
	    }
	    return false;
    }
	
	@Override
    public int getPortletSessionScopeForName(String name) {
	    return PortletSession.PORTLET_SCOPE;
    }

	@Override
    public void beforeActionRequest(FacesContext facesContext) {
		// DO nothing
	    
    }

	@Override
    public void afterActionRequestExecute(FacesContext facesContext) {
        // DO Nothing
        
    }

	@Override
    public void afterActionRequest(FacesContext facesContext) {
		// DO nothing
	    
    }

	@Override
    public void beforeEventRequest(FacesContext facesContext) {
		// DO nothing
    }

	@Override
    public void afterEventRequest(FacesContext facesContext) {
		// DO nothing
    }

	@Override
    public void beforeResourceRequest(FacesContext facesContext) {
		// DO nothing
    }


}
