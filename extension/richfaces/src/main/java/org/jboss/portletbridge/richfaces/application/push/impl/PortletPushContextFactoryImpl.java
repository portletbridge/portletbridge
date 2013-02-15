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
package org.jboss.portletbridge.richfaces.application.push.impl;

import org.richfaces.application.CoreConfiguration;
import org.richfaces.application.configuration.ConfigurationServiceHelper;
import org.richfaces.application.push.PushContext;
import org.richfaces.application.push.PushContextInitializationException;
import org.richfaces.application.push.impl.PushContextFactoryImpl;
import org.richfaces.application.push.impl.PushContextImpl;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletPushContextFactoryImpl extends PushContextFactoryImpl {
    private static final AtomicReference<PushContext> PUSH_CONTEXT_HOLDER = new AtomicReference<PushContext>();

    private static String convertToUrl(FacesContext facesContext, String mapping) {
        if (mapping == null) {
            return mapping;
        }

        String url = mapping.replaceAll(Pattern.quote("*"), PUSH_CONTEXT_RESOURCE_NAME);
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        return facesContext.getExternalContext().encodeResourceURL(facesContext.getExternalContext().getRequestContextPath() + url);
    }

    private static PushContext createInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext == null) {
            throw new PushContextInitializationException(
                    "FacesContext is not available when trying to initialize PushContext - use startup initialization (web.xml context-param org.richfaces.push.initializeOnStartup = true)");
        }

        String pushHandlerMapping = (String) facesContext.getExternalContext().getApplicationMap()
                .get(PUSH_HANDLER_MAPPING_ATTRIBUTE);

        if (pushHandlerMapping == null) {
            pushHandlerMapping = ConfigurationServiceHelper.getStringConfigurationValue(facesContext,
                    CoreConfiguration.Items.pushHandlerMapping);
        }

        PushContextImpl pushContext = new PushContextImpl(convertToUrl(facesContext, pushHandlerMapping));
        pushContext.init(facesContext);

        return pushContext;
    }

    public PushContext getPushContext() {
        if (PUSH_CONTEXT_HOLDER.get() == null) {
            synchronized (PUSH_CONTEXT_HOLDER) {
                if (PUSH_CONTEXT_HOLDER.get() == null) {
                    PushContext pushContext = createInstance();
                    PUSH_CONTEXT_HOLDER.set(pushContext);
                }
            }
        }
        return PUSH_CONTEXT_HOLDER.get();
    }
}
