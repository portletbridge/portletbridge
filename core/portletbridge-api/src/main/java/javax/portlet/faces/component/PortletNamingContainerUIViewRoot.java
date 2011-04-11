/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package javax.portlet.faces.component;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.BridgeUtil;

/** @author asmirnov */
@PortletNamingContainer
public class PortletNamingContainerUIViewRoot extends UIViewRoot implements NamingContainer
{

   private static final String PREFIX = "pb";
private static final long serialVersionUID = -4524288011655837711L;
   private static final String SEPARATOR = (new Character(NamingContainer.SEPARATOR_CHAR))
      .toString();

   public PortletNamingContainerUIViewRoot()
   {
      super();
      getAttributes().put(SEPARATOR, Boolean.FALSE);
   }

   /**
    * Static method that implements NamingContainer semantics.  Ensures that the returned identifier contains the
    * consumer (portal) provided unique portlet id. This ensures that those components in this NamingContainer generate
    * ids which will not collide in the consumer page.<p> This method is provided for existing <code>UIViewRoot</code>
    * implementations that prefer not to subclass <code>PortletNamingContainerUIViewRoot</code>
    */
   public static String convertClientId(FacesContext context, UIViewRoot root, String additionalId)
   {
      ExternalContext ec = context.getExternalContext();
      String namespace = ec.encodeNamespace(SEPARATOR);

      /*
      * In servlet world encodeNamespace does nothing -- so if we get back what we sent in then do
      * not perturn the NamingContainer Id
      *
      * The "_" was added for LifeRay compatibility
      */
      if (namespace.length() > 1)
      {
         if (additionalId != null)
         {
            return PREFIX + namespace + additionalId;
         }
         else
         {
            return PREFIX + namespace;
         }
      }
      else
      {
         return additionalId;
      }
   }

   /**
    * Implements NamingContainer semantics.  Ensures that the returned identifier contains the consumer (portal) provided
    * unique portlet id.  This ensures that those components in this NamingContainer generate ids which will not collide
    * in the consumer page.  Implementation merely calls the static form of this method.
    */

   @Override
   public String getContainerClientId(FacesContext context)
   {
      if (BridgeUtil.isPortletRequest() && (this.getAttributes().get(SEPARATOR)).equals(Boolean.TRUE))
      {
         return PortletNamingContainerUIViewRoot.convertClientId(context, this, super.getContainerClientId(context));
      }
      else
      {
         return null;
      }

   }

   //  findComponent/JSF requires that the clientId be constructed from the serverId.
   //  So getId() must return the portlet namespace token.
   @Override
   public void setId(String id)
   {
      if (BridgeUtil.isPortletRequest())
      {
         // we could end up with a doubly
         // encoded id until the restore overwrites.  To avoid this -- first test if
         // its already encoded and don't re-encode.
         Boolean encoded;
         ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
         id = ec.encodeNamespace(PREFIX + id);
         // now indicate that this id is encoded
         encoded = Boolean.TRUE;
         getAttributes().put(SEPARATOR, encoded);
      }
      super.setId(id);
   }

}
