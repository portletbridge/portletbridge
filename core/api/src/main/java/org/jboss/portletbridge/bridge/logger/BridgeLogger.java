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
    String LOGGING_BUNDLE = "org.jboss.portletbridge.LogMessages";
    String LOGGING_ENABLED_PORTLET_INIT_PARAM = "org.jboss.portletbridge.loggingEnabled";

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    void log(String msg);

    void log(String message, Throwable throwable);

    /**
     * Log a message using the Level passed, instead of the default.
     *
     * @param logLevel
     * @param msg
     */
    void log(Level logLevel, String msg);

    /**
     * Log a message and throwable using the Level passed, instead of the default.
     *
     * @param logLevel
     * @param message
     * @param throwable
     */
    void log(Level logLevel, String message, Throwable throwable);

    /**
     * Is Logging enabled for the Bridge?
     *
     * @return
     */
    boolean isEnabled();

    void setEnabled(Boolean enable);

    void setEnabled(boolean enable);

    /**
     * Get the Log Level that all log() calls will use.
     *
     * @return
     */
    Level getLogLevel();

    /**
     * Modify the Log Level that all log() calls will use.
     *
     * @param logLevel
     */
    void setLogLevel(Level logLevel);
}