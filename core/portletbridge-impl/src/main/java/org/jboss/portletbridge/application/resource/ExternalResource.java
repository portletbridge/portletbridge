/**
 * 
 */
package org.jboss.portletbridge.application.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

/**
 * @author leo
 *
 */
public class ExternalResource extends Resource {

	private final String path;

	public ExternalResource(String path) {
		this.path = path;
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.Resource#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		// this resource is never served.
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Resource#getRequestPath()
	 */
	@Override
	public String getRequestPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Resource#getResponseHeaders()
	 */
	@Override
	public Map<String, String> getResponseHeaders() {
		// this resource is never served.
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Resource#getURL()
	 */
	@Override
	public URL getURL() {
		// this resource is never used for composite components.
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Resource#userAgentNeedsUpdate(javax.faces.context.FacesContext)
	 */
	@Override
	public boolean userAgentNeedsUpdate(FacesContext context) {
		// this resource is never served.
		return false;
	}

}
