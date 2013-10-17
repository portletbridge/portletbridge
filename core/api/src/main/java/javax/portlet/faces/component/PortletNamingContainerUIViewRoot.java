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
package javax.portlet.faces.component;

import java.io.Serializable;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;

/**
 * <code>UIViewRoot</code> that implements portlet specific <code>NamingContainer</code> that ensures the consumer's unique
 * portlet Id is encoded in all tree components. The class is annotated by
 * <code>javax.portlet.faces.annotation.PortletNamingContainer</code> allowing the bridge to recognize that this specific
 * <code>UIViewRoot</code> implements the behavior.
 */
@PortletNamingContainer
public class PortletNamingContainerUIViewRoot extends UIViewRoot implements Serializable, NamingContainer {
    private static final long serialVersionUID = -690876000289020800L;

    private String namespace;

    public PortletNamingContainerUIViewRoot() {
        super();
    }

    @Override
    public void setId(String id) {
        if (BridgeUtil.isPortletRequest()) {
            if (namespace == null) {
                getContainerClientId(FacesContext.getCurrentInstance());
            }

            id = namespace;
        }
        super.setId(id);
    }

    /**
     * Implements NamingContainer semantics. Ensures that the returned identifier contains the consumer (portal) provided unique
     * portlet id. This ensures that those components in this NamingContainer generate ids which will not collide in the
     * consumer page.
     */
    @Override
    public String getContainerClientId(FacesContext context) {
        if (BridgeUtil.isPortletRequest()) {
            if (namespace == null) {
                ExternalContext externalContext = context.getExternalContext();
                namespace = externalContext.encodeNamespace("");
            }
            return namespace;
        } else {
            return null;
        }
    }
}
