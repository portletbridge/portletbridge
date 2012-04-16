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
import javax.portlet.faces.Bridge;
import javax.portlet.faces.annotation.PortletNamingContainer;

/**
 * <code>UIViewRoot</code> that implements portlet specific <code>NamingContainer</code> that ensures the consumer's
 * unique portlet Id is encoded in all tree components. The class is annotated by
 * <code>javax.portlet.faces.annotation.PortletNamingContainer</code> allowing the bridge to recognize that this
 * specific <code>UIViewRoot</code> implements the behavior.
 */
@PortletNamingContainer
public class PortletNamingContainerUIViewRoot extends UIViewRoot implements Serializable, NamingContainer {
    private static final long serialVersionUID = -690876000289020800L;

    private static final String NAMESPACE_PREFIX = "pb";

    public PortletNamingContainerUIViewRoot() {
        super();
    }

    /**
     * Static method that implements NamingContainer semantics. Ensures that the returned identifier contains the
     * consumer (portal) provided unique portlet id. This ensures that those components in this NamingContainer generate
     * ids which will not collide in the consumer page.
     * <p>
     * This method is provided for existing <code>UIViewRoot</code> implementations that prefer not to subclass
     * <code>PortletNamingContainerUIViewRoot</code>
     */
    private static String convertClientId(FacesContext context, String additionalId) {
        ExternalContext ec = context.getExternalContext();
        String namespace = ec.encodeNamespace("");

        /*
         * In servlet world encodeNamespace does nothing -- so if we get back what we sent in then do not perturn the
         * NamingContainer Id
         *
         * The PREFIX was added for LifeRay compatibility
         */
        if (namespace.length() > 0) {
            if (additionalId != null) {
                return NAMESPACE_PREFIX + namespace + additionalId;
            } else {
                return NAMESPACE_PREFIX + namespace;
            }
        } else {
            return additionalId;
        }
    }

    /**
     * Implements NamingContainer semantics. Ensures that the returned identifier contains the consumer (portal)
     * provided unique portlet id. This ensures that those components in this NamingContainer generate ids which will
     * not collide in the consumer page. Implementation merely calls the static form of this method.
     */

    @Override
    public String getContainerClientId(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext.getRequestMap().containsKey(Bridge.PORTLET_LIFECYCLE_PHASE)) {
            String rootId = getId();
            if (null == rootId || !rootId.startsWith(NAMESPACE_PREFIX)) {
                setId(convertClientId(context, rootId));
            }
        }
        return super.getContainerClientId(context);
    }
}
