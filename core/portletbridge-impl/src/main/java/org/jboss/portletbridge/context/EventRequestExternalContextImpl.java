/**
 * 
 */
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;

/**
 * @author asmirnov
 *
 */
public class EventRequestExternalContextImpl extends PortletExternalContextImpl {

	/**
	 * @param context
	 * @param request
	 * @param response
	 */
	public EventRequestExternalContextImpl(PortletContext context,
			EventRequest request, EventResponse response) {
		super(context, request, response);
	}

	@Override
    protected String createActionUrl(PortalActionURL url) {
	    return ACTION_URL_DO_NOTHITG;
    }

	
	@Override
	protected String createRenderUrl(PortalActionURL portalUrl,
	        Map<String, List<String>> parameters) {
	    return ACTION_URL_DO_NOTHITG;
	}

	@Override
	protected String createResourceUrl(PortalActionURL portalUrl) {
	    return RESOURCE_URL_DO_NOTHITG;
	}
	@Override
    public void redirect(String url) throws IOException {
	    // TODO Auto-generated method stub
    }


	@Override
    protected String createPartialActionUrl(PortalActionURL portalUrl) {
	    return RESOURCE_URL_DO_NOTHITG;
    }

	@Override
    public boolean isResponseCommitted() {
        return true;
    }

	@Override
    public void addResponseHeader(String name, String value) {
        getResponse().addProperty(name, value);
    }

	@Override
    public void setResponseHeader(String name, String value) {
        getResponse().setProperty(name, value);
    }

	@Override
    public void responseSendError(int statusCode, String message)
            throws IOException {
            
            }

	@Override
    public void setResponseStatus(int statusCode) {
    
    }

	@Override
    public String getRequestCharacterEncoding() {
        return null;
    }

	@Override
    public int getRequestContentLength() {
        return 0;
    }

	@Override
    public int getResponseBufferSize() {
        return 0;
    }

	@Override
    public OutputStream getResponseOutputStream() throws IOException {
        return null;
    }

	@Override
    public Writer getResponseOutputWriter() throws IOException {
        return null;
    }

	@Override
    public void responseFlushBuffer() throws IOException {
    
    }

	@Override
    public void responseReset() {
    
    }

	@Override
    public void setRequestCharacterEncoding(String encoding)
            throws UnsupportedEncodingException {
            
            }

	@Override
    public void setResponseBufferSize(int size) {
    
    }

	@Override
    public void setResponseContentLength(int length) {
    
    }

	@Override
    public void setResponseContentType(String contentType) {
    
    }

}
