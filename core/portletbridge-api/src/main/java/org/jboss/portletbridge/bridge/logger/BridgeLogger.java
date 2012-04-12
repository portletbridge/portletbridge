/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.bridge.logger;

public interface BridgeLogger {
    public static final String LOGGING_BUNDLE = "org.jboss.portletbridge.LogMessages";
    public static final String LOGGING_ENABLED_PORTLET_INIT_PARAM = "org.jboss.portletbridge.loggingEnabled";

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
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