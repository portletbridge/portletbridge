package org.jboss.portletbridge.logger;

/**
 * Stub -- to be added
 */

public interface BridgeLogger
{
  public static final String LOGGING_ENABLED_PORTLET_INIT_PARAM = "org.apache.myfaces.portlet.faces.loggingEnabled";
  
  public void log(String msg);
  
  public void log(String message, Throwable throwable);
  
  public boolean isEnabled();
  
  public void setEnabled(Boolean enable);
  
  public void setEnabled(boolean enable);
  
  public int getLogLevel();
  
  public void setLogLevel(int logLevel);
  
}