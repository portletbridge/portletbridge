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
package org.jboss.portletbridge;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface PortletBridgeConstants {

    String FACES_REQUEST_HEADER_PARAM = "Faces-Request";

    String FACES_REQUEST_PARTIAL = "partial/ajax";

    String AJAX_PARAM = "_pbrAjax";

    String WSRP_REQUEST_PARAM = "org.gatein.invocation.fromWSRP";

    String PREVENT_SELF_CLOSING_SCRIPT_TAG_PARAM = "org.jboss.portletbridge.PREVENT_SELF_CLOSING_SCRIPT_TAG";

    /**
     * Parameter to disable the functionality of f:viewParam. Default value is enabled.
     * Disabling will prevent FacesLifecycle.execute() from performing more than RESTORE_VIEW during
     * portlet RENDER_RESPONSE. (See PBR-510).
     */
    String VIEW_PARAM_DISABLED = "org.jboss.portletbridge.VIEW_PARAMETERS_DISABLED";

    /**
     * Parameter to enable data created from an Ajax Request to be stored in the Bridge Request Scope,
     * allowing it to be used on subsequent Render Requests.
     */
    String SCOPE_ENABLED_ON_AJAX = "org.jboss.portletbridge.BRIDGE_SCOPE_ENABLED_ON_AJAX_REQUEST";

    /**
     * Parameter to enable Faces Messages to be stored at the end of an Ajax Request. Value of <code>true</code> is
     * only applicable if SCOPE_ENABLED_ON_AJAX is also <code>true</code>.
     */
    String FACES_MESSAGES_STORED_ON_AJAX = "org.jboss.portletbridge.FACES_MESSAGES_STORED_ON_AJAX_REQUEST";

    /**
     * Parameter to specify the name of the Session ID parameter. By default it is <code>jsessionid</code>.
     */
    String SESSION_ID_PARAMETER_NAME = "org.jboss.portletbridge.SESSION_ID_PARAMETER_NAME";

    /**
     * Parameter to specify whether the Bridge Scope is preserved on completion of a Render Request.
     */
    String REQUEST_SCOPE_PRESERVED = "org.jboss.portletbridge.BRIDGE_SCOPE_PRESERVED_POST_RENDER";
}
