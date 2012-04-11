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
package org.jboss.portletbridge.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;

/**
 * @author kenfinnigan
 */
public final class FacesMessageWrapper implements Serializable {

    private static final long serialVersionUID = -4624330730994746304L;

    private LinkedHashMap<String, List<FacesMessage>> messages = new LinkedHashMap<String, List<FacesMessage>>();

    public void addMessage(String clientId, FacesMessage message) {
        List<FacesMessage> list = messages.get(clientId);
        if (null == list) {
            list = new ArrayList<FacesMessage>();
            messages.put(clientId, list);
        }
        list.add(message);
    }

    public List<FacesMessage> getMessages(String clientId) {
        List<FacesMessage> list = messages.get(clientId);
        if (null != list) {
            return list;
        }
        return Collections.emptyList();
    }

    public Set<String> getClientIds() {
        return messages.keySet();
    }
}
