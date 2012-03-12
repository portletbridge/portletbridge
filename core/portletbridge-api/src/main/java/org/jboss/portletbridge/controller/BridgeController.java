package org.jboss.portletbridge.controller;

import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeUninitializedException;

import org.jboss.portletbridge.config.BridgeConfig;
import org.jboss.portletbridge.context.BridgeContext;


public interface BridgeController 
{
  public static final String IGNORE_BRIDGE_SCOPE = "org.apache.myfaces.portlet.bridge.ignoreBridgeScope";
    
  public void init(BridgeConfig config) throws BridgeException;
  
  public void destroy();
  
  public void processPortletAction(BridgeContext ctx)
     throws BridgeException,
            BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException; 
  
  public void handlePortletEvent(BridgeContext ctx)
     throws BridgeException,
            BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException;  
 
 public void renderPortletHead(BridgeContext ctx)
    throws BridgeException,
           BridgeDefaultViewNotSpecifiedException,
           BridgeUninitializedException;

  public void renderPortletBody(BridgeContext ctx)
     throws BridgeException,
            BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException;
  
  public void renderFacesResource(BridgeContext ctx)
     throws BridgeException,
            BridgeDefaultViewNotSpecifiedException,
            BridgeUninitializedException;
  
  public void renderNonFacesResource(BridgeContext ctx)
     throws BridgeException;
  
}