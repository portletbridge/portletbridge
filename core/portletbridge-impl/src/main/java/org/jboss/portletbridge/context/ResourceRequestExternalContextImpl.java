/**
 * 
 */
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.servlet.http.HttpServletRequest;


/**
 * @author asmirnov
 *
 */
public class ResourceRequestExternalContextImpl extends
		MimeExternalContextImpl {

	/**
	 * @param context
	 * @param request
	 * @param response
	 */
	public ResourceRequestExternalContextImpl(PortletContext context,
			ResourceRequest request, ResourceResponse response) {
		super(context, request, response);
		// TODO Auto-generated constructor stub
	}

	@Override
    public String getRequestCharacterEncoding() {
		// TODO - save character encoding from action request.
        return getRequest().getCharacterEncoding();
    }

	@Override
    public void setRequestCharacterEncoding(String encoding)
            throws UnsupportedEncodingException {
    	try {
    		getRequest().setCharacterEncoding(encoding);
    	} catch (IllegalStateException e) {
    		// TODO: handle exception
    	}
    }
	
	
	@Override
    public int getRequestContentLength() {
        return getRequest().getContentLength();
    }



	public void redirect(String url) throws IOException {
		if (null == url || url.length() < 0) {
			throw new NullPointerException("Path to redirect is null");
		}
		PortalActionURL actionURL = new PortalActionURL(url);
		if ((!actionURL.isInContext(getRequestContextPath()) && null == actionURL
            .getParameter(Bridge.FACES_VIEW_ID_PARAMETER))
				|| "true".equalsIgnoreCase(actionURL
						.getParameter(Bridge.DIRECT_LINK))) {
			dispatch(actionURL.getPath());
		} else {
			internalRedirect(actionURL);
		}
	}
	public ResourceRequest getRequest() {
		return (ResourceRequest) super.getRequest();
	}

	public ResourceResponse getResponse() {
		return (ResourceResponse) super.getResponse();
	}
	
	private HttpServletRequest getMultipartRequest() {
		return (HttpServletRequest) getRequest().getAttribute("org.ajax4jsf.request.MultipartRequest");
	}
	
	@Override
	protected String getRequestParameter(String name) {
		HttpServletRequest multipartRequest = getMultipartRequest();
		if(multipartRequest!= null) {
			return multipartRequest.getParameter(name);
		} else {
			return super.getRequestParameter(name);
		}
	}
	@Override
	protected String[] getRequestParameterValues(String name) {
		HttpServletRequest multipartRequest = getMultipartRequest();
		if(multipartRequest!= null) {
			return multipartRequest.getParameterValues(name);
		} else {
			return super.getRequestParameterValues(name);
		}
		
	}
	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		HttpServletRequest multipartRequest = getMultipartRequest();
		if(multipartRequest!= null) {
			return multipartRequest.getParameterMap();
		} else {
			return super.getRequestParameterValuesMap();
		}
	}
	
	

	
}
