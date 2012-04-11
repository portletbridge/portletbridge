package org.jboss.portletbridge.bridge.logger;

public interface BridgeLogger {
    public static final String LOGGING_BUNDLE = "org.jboss.portletbridge.LogMessages";
    public static final String LOGGING_ENABLED_PORTLET_INIT_PARAM = "org.jboss.portletbridge.loggingEnabled";

    public enum Level {
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }

    public void log(String msg);

    public void log(String message, Throwable throwable);

    /**
     * Log a message using the Level passed, instead of the default.
     *
     * @param logLevel
     * @param msg
     */
    public void log(Level logLevel, String msg);

    /**
     * Log a message and throwable using the Level passed, instead of the default.
     *
     * @param logLevel
     * @param message
     * @param throwable
     */
    public void log(Level logLevel, String message, Throwable throwable);

    /**
     * Is Logging enabled for the Bridge?
     *
     * @return
     */
    public boolean isEnabled();

    public void setEnabled(Boolean enable);

    public void setEnabled(boolean enable);

    /**
     * Get the Log Level that all log() calls will use.
     *
     * @return
     */
    public Level getLogLevel();

    /**
     * Modify the Log Level that all log() calls will use.
     *
     * @param logLevel
     */
    public void setLogLevel(Level logLevel);
}