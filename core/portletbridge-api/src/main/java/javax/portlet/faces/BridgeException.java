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
package javax.portlet.faces;

import javax.faces.FacesException;

/**
 * Generic exception thrown when the bridge encounters an unexpected error. The message returned in this exeception should
 * contain the details of the specific problem.
 */
public class BridgeException extends FacesException {

    private static final long serialVersionUID = 6758659847475864393L;

    public BridgeException() {
        super();
    }

    /**
     * @param message
     */
    public BridgeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BridgeException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public BridgeException(String message, Throwable cause) {
        super(message, cause);
    }

}
