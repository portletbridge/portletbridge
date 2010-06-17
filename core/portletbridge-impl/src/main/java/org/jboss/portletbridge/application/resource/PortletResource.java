/**
 * 
 */
package org.jboss.portletbridge.application.resource;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.context.PortletExternalContextImpl;

/**
 * @author leo
 * 
 */
public class PortletResource extends ResourceWrapper {

	public static final String LIBRARY_NAME = "ln";
	private final Resource wrapped;

	public PortletResource(Resource wrapped) {
		this.wrapped = wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.application.ResourceWrapper#getWrapped()
	 */
	@Override
	public Resource getWrapped() {
		return wrapped;
	}

	public String getLibraryName() {
		return wrapped.getLibraryName();
	}

	public String getResourceName() {
		return wrapped.getResourceName();
	}

	public void setContentType(String contentType) {
		wrapped.setContentType(contentType);
	}

	public void setLibraryName(String libraryName) {
		wrapped.setLibraryName(libraryName);
	}

	public void setResourceName(String resourceName) {
		wrapped.setResourceName(resourceName);
	}

	@Override
	public String getContentType() {
		// ResourceWrapper does not delegate this method
		return wrapped.getContentType();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.application.Resource#getRequestPath()
	 */
	@Override
	public String getRequestPath() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		if (externalContext.getRequestMap().containsKey(
		        Bridge.PORTLET_LIFECYCLE_PHASE)) {
			if (externalContext.getResponse() instanceof MimeResponse) {
	            MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
	            ResourceURL resourceURL = mimeResponse.createResourceURL();
	            resourceURL.setResourceID(ResourceHandler.RESOURCE_IDENTIFIER);
	            resourceURL.setParameter(PortletResourceHandler.RESOURCE_ID, getWrapped().getResourceName());
	            String libraryName = getWrapped().getLibraryName();
	            if(null != libraryName){
	            	resourceURL.setParameter(PortletResourceHandler.LIBRARY_ID, libraryName);
	            }
	            String contentType = getWrapped().getContentType();
	            if(null != contentType){
	            	resourceURL.setParameter(PortletResourceHandler.MIME_PARAM, contentType);
	            }
	            return resourceURL.toString();
            } else {
				return PortletExternalContextImpl.RESOURCE_URL_DO_NOTHITG;
			}
		} else {
			return super.getRequestPath();
		}
	}

}
