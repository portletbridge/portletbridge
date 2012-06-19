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
package org.jboss.portletbridge.context.map;

import java.util.Collections;
import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletSessionMap extends ContextAttributesMap<Object> {

    private PortletRequest portletRequest;
    private int scope;

    public PortletSessionMap(PortletRequest request) {
        this.portletRequest = request;
        this.scope = PortletSession.PORTLET_SCOPE;
    }

    public PortletSessionMap(PortletRequest request, int scope) {
        this.portletRequest = request;
        this.scope = scope;
    }

    @Override
    protected Object getAttribute(String name) {
        Object retVal = null;
        try {
            retVal = portletRequest.getPortletSession(true).getAttribute(name, scope);
        } catch (IllegalStateException e) {
            // todo - Handle invalidated session state
        }
        return retVal;
    }

    @Override
    protected void setAttribute(String name, Object value) {
        portletRequest.getPortletSession(true).setAttribute(name, value, scope);
    }

    @Override
    protected void removeAttribute(String name) {
        PortletSession session = portletRequest.getPortletSession(false);
        if (null != session) {
            session.removeAttribute(name, scope);
        }
    }

    @Override
    protected Enumeration<String> getEnumeration() {
        PortletSession session = portletRequest.getPortletSession(false);
        if (null != session) {
            return session.getAttributeNames(scope);
        }
        return Collections.enumeration(Collections.<String> emptyList());
    }

}
