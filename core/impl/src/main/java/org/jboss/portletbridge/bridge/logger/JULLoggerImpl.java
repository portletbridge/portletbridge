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

import java.util.logging.Logger;

/**
 * @author kenfinnigan
 */
public class JULLoggerImpl implements BridgeLogger {
    private Logger logger = null;

    private boolean enabled = false;

    public JULLoggerImpl() {
        this.logger = Logger.getLogger(JULLoggerImpl.class.getName(), LOGGING_BUNDLE);
    }

    public JULLoggerImpl(String className) {
        this.logger = Logger.getLogger(className, LOGGING_BUNDLE);
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#log(java.lang.String)
     */
    public void log(String msg) {
        if (isEnabled()) {
            logger.log(logger.getLevel(), msg);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String message, Throwable throwable) {
        if (isEnabled()) {
            logger.log(logger.getLevel(), message, throwable);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#log(Level, java.lang.String)
     */
    public void log(Level logLevel, String msg) {
        if (isEnabled()) {
            logger.log(mapToLogger(logLevel), msg);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#log(Level, java.lang.String, java.lang.Throwable)
     */
    public void log(Level logLevel, String message, Throwable throwable) {
        if (isEnabled()) {
            logger.log(mapToLogger(logLevel), message, throwable);
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#isEnabled()
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#setEnabled(java.lang.Boolean)
     */
    public void setEnabled(Boolean enable) {
        if (null != enable) {
            this.enabled = enable;
        } else {
            this.enabled = false;
        }
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#setEnabled(boolean)
     */
    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#getLogLevel()
     */
    public Level getLogLevel() {
        return mapFromLogger(logger.getLevel());
    }

    /**
     * @see org.jboss.portletbridge.bridge.logger.BridgeLogger#setLogLevel(int)
     */
    public void setLogLevel(Level logLevel) {
        logger.setLevel(mapToLogger(logLevel));
    }

    private Level mapFromLogger(java.util.logging.Level julLevel) {
        Level ourLevel = null;

        if (julLevel == java.util.logging.Level.SEVERE) {
            ourLevel = Level.ERROR;
        } else if (julLevel == java.util.logging.Level.WARNING) {
            ourLevel = Level.WARNING;
        } else if (julLevel == java.util.logging.Level.INFO) {
            ourLevel = Level.INFO;
        } else {
            ourLevel = Level.DEBUG;
        }
        return ourLevel;
    }

    private java.util.logging.Level mapToLogger(Level ourLevel) {
        java.util.logging.Level julLevel = null;

        if (ourLevel == Level.ERROR) {
            julLevel = java.util.logging.Level.SEVERE;
        } else if (ourLevel == Level.WARNING) {
            julLevel = java.util.logging.Level.WARNING;
        } else if (ourLevel == Level.INFO) {
            julLevel = java.util.logging.Level.INFO;
        } else {
            julLevel = java.util.logging.Level.FINE;
        }
        return julLevel;
    }
}
