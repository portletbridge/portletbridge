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
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.faces.Bridge;

/**
 * @author asmirnov
 *
 */
public abstract class MimeExternalContextImpl extends PortletExternalContextImpl {


	private static final String[] STRINGS = new String[]{};

	/**
	 * @param context
	 * @param request
	 * @param response
	 */
	public MimeExternalContextImpl(PortletContext context,
	        PortletRequest request, MimeResponse response) {
		super(context, request, response);
	}

	//============================================================
	// public methods
    @Override
	public PortletRequest getRequest() {
		return (PortletRequest) super.getRequest();
	}

    @Override
	public MimeResponse getResponse() {
		return (MimeResponse) super.getResponse();
	}


	@Override
    protected String createActionUrl(PortalActionURL url) {
    	  String viewId = getViewIdFromUrl(url);
          MimeResponse renderResponse = getResponse();
          PortletURL portletURL = renderResponse.createActionURL();
          portletURL.setParameter(Bridge.FACES_VIEW_ID_PARAMETER, viewId);
    //      portletURL.setParameter(AbstractExternalContext.NAMESPACE_PARAMETER,
    //            renderResponse.getNamespace());
          for (String key : url.getParameters().keySet()) {
    		String value = url.getParameter(key);
    		if(Bridge.PORTLET_MODE_PARAMETER.equals(key)){
    			PortletMode mode = new PortletMode(value);
    			try {
    				portletURL.setPortletMode(mode);
    			} catch (PortletModeException e) {
    				// only valid modes supported.
    			}
    		} else if (Bridge.PORTLET_WINDOWSTATE_PARAMETER.equals(key)) {
    			try {
    				WindowState state = new WindowState(value);
    				portletURL.setWindowState(state);
    			} catch (WindowStateException e) {
    				// only valid modes supported.
    			}
    
    		} else if (Bridge.PORTLET_SECURE_PARAMETER.equals(key)) {
    			try {
    				portletURL.setSecure(Boolean.getBoolean(value));
    			} catch (PortletSecurityException e) {
    				// Just ignore it.
    			}
    
    		} else {
    			portletURL.setParameter(key, value);
    		}
          }
          return portletURL.toString();
       }

	@Override
    protected String createResourceUrl(PortalActionURL portalUrl) {
    	MimeResponse renderResponse = getResponse();
    	ResourceURL resourceURL = renderResponse.createResourceURL();
    	setBaseUrlParameters(portalUrl, resourceURL);
    	resourceURL.setResourceID(portalUrl.getPath());
    	resourceURL.setParameters(portalUrl.getParameters());
    	return resourceURL.toString();
    }
	
	@Override
	protected String createRenderUrl(PortalActionURL portalUrl,
	        Map<String, List<String>> parameters) {
    	MimeResponse renderResponse = getResponse();
    	PortletURL renderURL = renderResponse.createRenderURL();
    	setBaseUrlParameters(portalUrl, renderURL);
    	renderURL.setParameters(portalUrl.getParameters());
    	for (String paramName : parameters.keySet()) {
	        renderURL.setParameter(paramName, parameters.get(paramName).toArray(STRINGS));
        }
    	renderURL.setParameter(Bridge.FACES_VIEW_ID_PARAMETER, getViewIdFromUrl(portalUrl));
    	return renderURL.toString();
	}
	
	protected void setPortletUrlParameters(PortalActionURL portalUrl, PortletURL portletURL) {
    	String modeParameter = portalUrl.getParameter(Bridge.PORTLET_MODE_PARAMETER);
    	if(null != modeParameter){
			try {
				PortletMode mode = new PortletMode(modeParameter);
				portletURL.setPortletMode(mode);
			} catch (PortletModeException e) {
				// only valid modes supported.
			}
    		
    	}
    	String windowParameter = portalUrl.getParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
    	if(null != windowParameter){
			try {
				WindowState state = new WindowState(windowParameter);
				portletURL.setWindowState(state);
			} catch (WindowStateException e) {
				// only valid modes supported.
			}
    	}
    	setBaseUrlParameters(portalUrl, portletURL);
	}

	protected void setBaseUrlParameters(PortalActionURL portalUrl, BaseURL resourceURL) {
	    portalUrl.removeParameter(Bridge.PORTLET_MODE_PARAMETER);
    	portalUrl.removeParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
    	String secure = portalUrl.getParameter(Bridge.PORTLET_SECURE_PARAMETER);
    	if(null != secure){
    		try {
    		if("true".equalsIgnoreCase(secure)){
    			resourceURL.setSecure(true);
    		} else if ("false".equalsIgnoreCase(secure)) {
    			resourceURL.setSecure(false);				
    		}
    		} catch (PortletSecurityException e) {
    			// do nothing
    		}
    		portalUrl.removeParameter(Bridge.PORTLET_SECURE_PARAMETER);
    	}
    }

    @Override
    protected String createPartialActionUrl(PortalActionURL portalUrl) {
        MimeResponse renderResponse = getResponse();
        ResourceURL resourceURL = renderResponse.createResourceURL();
        setBaseUrlParameters(portalUrl, resourceURL);
        resourceURL.setParameters(portalUrl.getParameters());
        resourceURL.setParameter(Bridge.FACES_VIEW_ID_PARAMETER, getViewIdFromUrl(portalUrl));
        return resourceURL.toString();
    }

    @Override
    public String getResponseCharacterEncoding() {
        return getResponse().getCharacterEncoding();
    }

    @Override
    public String getResponseContentType() {
        return getResponse().getContentType();
}

    @Override
    public void setResponseContentType(String contentType) {
        getResponse().setContentType(contentType);
    }

    @Override
    public void setResponseContentLength(int length) {
    }

    @Override
    public int getResponseBufferSize() {
        return getResponse().getBufferSize();
    }

    @Override
    public void setResponseBufferSize(int size) {
        getResponse().setBufferSize(size);
    }

    @Override
    public OutputStream getResponseOutputStream() throws IOException {
        return getResponse().getPortletOutputStream();
    }

    @Override
    public Writer getResponseOutputWriter() throws IOException {
        return getResponse().getWriter();
    }

    @Override
    public boolean isResponseCommitted() {
        return getResponse().isCommitted();
    }

    @Override
    public void responseFlushBuffer() throws IOException {
//        getFlash().doLastPhaseActions(FacesContext.getCurrentInstance(), false);
        getResponse().flushBuffer();
    }

    @Override
    public void responseReset() {
        getResponse().reset();
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
		// TODO - save character encoding from action request.
        return null;
    }

	@Override
    public int getRequestContentLength() {
        return 0;
    }



	@Override
    public void setRequestCharacterEncoding(String encoding)
            throws UnsupportedEncodingException {
            
    }



}
