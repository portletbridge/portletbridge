/**
 * 
 */
package org.jboss.portletbridge.application;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.StateManager;
import javax.faces.application.StateManagerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.ResponseStateManager;
import javax.portlet.faces.BridgeUtil;

import org.jboss.portletbridge.context.PortletBridgeContext;

/**
 * @author asmirnov
 * 
 */
public class PortletStateManager extends StateManagerWrapper {

	private StateManager parent;
	
	static final 		Pattern PATTERN = Pattern.compile(".*<input.*(?:\\svalue=[\"\'](\\S*)[\"\']\\s).*name=[\"']"+ResponseStateManager.VIEW_STATE_PARAM+"[\"'].*>");
	static final 		Pattern PATTERN2 = Pattern.compile(".*<input .*name=[\"']"+ResponseStateManager.VIEW_STATE_PARAM+"[\"'].*(?:\\svalue=[\"\'](\\S*)[\"\']\\s).*>");

	public interface StateWriter {
		void writeState(FacesContext context) throws IOException;
	}
	
	public PortletStateManager(StateManager parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void writeState(FacesContext context, final SerializedView state)
	        throws IOException {
		if (BridgeUtil.isPortletRequest()) {
			captureStateParameter(context, new StateWriter() {
				
				public void writeState(FacesContext context) throws IOException {
					getWrapped().writeState(context, state);
				}
			});
		} else {
			super.writeState(context, state);
		}
	}
	
	@Override
	public void writeState(FacesContext context, final Object state)
			throws IOException {
		if (BridgeUtil.isPortletRequest()) {
			captureStateParameter(context, new StateWriter() {
				
				public void writeState(FacesContext context) throws IOException {
					getWrapped().writeState(context, state);
				}
			});
		} else {
			super.writeState(context, state);
		}
	}

	private void captureStateParameter(FacesContext context, StateWriter delegate)
            throws IOException {
	    // Capture writed state into string.
	    ResponseWriter originalWriter = context.getResponseWriter();
	    StringWriter buff = new StringWriter(128);
	    try {
	    	ResponseWriter stateResponseWriter = originalWriter
	    			.cloneWithWriter(buff);
	    	context.setResponseWriter(stateResponseWriter);
	    	delegate.writeState(context);
	    	stateResponseWriter.flush();
	    	String stateString = buff.toString();
	    	originalWriter.write(stateString);
	    	String stateValue = getStateValue(stateString);
	    		PortletBridgeContext bridgeContext = PortletBridgeContext.getCurrentInstance(context);
	    		if(null != bridgeContext){
	    			Map<String, String[]> requestParameters = bridgeContext.getRequestScope().getRequestParameters();
	    			if(null != stateValue){
	    			requestParameters.put(ResponseStateManager.VIEW_STATE_PARAM, new String[]{stateValue});
	    		} else {
	    			requestParameters.remove(ResponseStateManager.VIEW_STATE_PARAM);
	    		}
	    	}
	    } finally {
	    	context.setResponseWriter(originalWriter);
	    }
    }

	static String getStateValue(String input) {
		Matcher matcher = PortletStateManager.PATTERN.matcher(input);
		if(!matcher.matches()){
			matcher = PortletStateManager.PATTERN2.matcher(input);
			if(!matcher.matches()){
				return null;
			}
		}
		return matcher.group(1);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.application.StateManagerWrapper#getWrapped()
	 */
	@Override
    public StateManager getWrapped() {
		return this.parent;
	}

}
