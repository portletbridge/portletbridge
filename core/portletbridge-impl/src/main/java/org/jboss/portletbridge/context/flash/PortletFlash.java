/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.context.flash;

import com.sun.faces.context.flash.ELFlash;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.PhaseId;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author asmirnov, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletFlash extends PortletFlashWrapper {

    static final String FLASH_ATTRIBUTE_NAME = "pbrf";

    private ELFlash wrappedFlash = null;

    private PortletFlash(ELFlash mojarraFlash) {
        wrappedFlash = mojarraFlash;
    }

    private static PortletFlash createFlash(ExternalContext externalContext, boolean create, ELFlash mojarraFlash) {
        Map<String, Object> appMap = externalContext.getApplicationMap();
        PortletFlash flash = (PortletFlash) appMap.get(FLASH_ATTRIBUTE_NAME);
        if (null == flash && create) {
            synchronized (externalContext.getContext()) {
                if (null == (flash = (PortletFlash) appMap.get(FLASH_ATTRIBUTE_NAME))) {
                    flash = new PortletFlash(mojarraFlash);
                    appMap.put(FLASH_ATTRIBUTE_NAME, flash);
                }
            }
        }
        return flash;
    }

    public static PortletFlash getFlash(ExternalContext externalContext, boolean create, ELFlash mojarraFlash) {
        if (null == mojarraFlash) {
            try {
                Method getFlashMethod = ELFlash.class.getMethod("getFlash", ExternalContext.class, Boolean.TYPE);
                Object obj = getFlashMethod.invoke(null, externalContext, create);
                if (obj instanceof ELFlash) {
                    mojarraFlash = (ELFlash) obj;
                }
            }
            catch (Exception e) {
                //TODO
            }
        }

        return createFlash(externalContext, create, mojarraFlash);
    }

    @Override
    public void doPrePhaseActions(FacesContext ctx) {
        PhaseId currentPhase = ctx.getCurrentPhaseId();
        boolean resetPhase = false;

        if (Bridge.PortletPhase.RENDER_PHASE == BridgeUtil.getPortletRequestPhase()
                && currentPhase.equals(PhaseId.RENDER_RESPONSE)) {
            // Need to trick ELFlash into thinking it's in the Restore View Phase otherwise the Previous Flash Manager
            // will not be retrieved from the Cookie
            ctx.setCurrentPhaseId(PhaseId.RESTORE_VIEW);
            resetPhase = true;
        }

        wrappedFlash.doPrePhaseActions(ctx);

        if (resetPhase) {
            ctx.setCurrentPhaseId(PhaseId.RENDER_RESPONSE);
        }
    }

    @Override
    public Flash getWrapped() {
        return wrappedFlash;
    }

    @Override
    public void doPostPhaseActions(FacesContext ctx) {
//        if (Bridge.PortletPhase.RENDER_PHASE == BridgeUtil.getPortletRequestPhase()) {
            wrappedFlash.doLastPhaseActions(ctx, false);
//        }
    }

    public void doLastPhaseActions(FacesContext context, boolean isRedirect) {
        wrappedFlash.doLastPhaseActions(context, isRedirect);
    }

}
