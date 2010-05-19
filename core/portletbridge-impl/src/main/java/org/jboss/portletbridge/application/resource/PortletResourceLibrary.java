package org.jboss.portletbridge.application.resource;

import javax.faces.application.Resource;

public interface PortletResourceLibrary {

	public Resource createFijiResource(String resourceName, String contentType);
	
}
