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
package org.jboss.portletbridge.config;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import java.util.Map;

/**
 * If a Faces Servlet has not been read by {@link WebXmlProcessor} then retrieve the
 * information from the {@link ServletContext}.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class BridgeConfigureListener implements ServletContextListener {

    private static final String FACES_SERVLET_CLASS = FacesServlet.class.getName();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (null == WebXmlProcessor.facesServlet) {
            ServletRegistration reg = getFacesServlet(sce.getServletContext());

            if (null != reg) {
                ServletBean facesServlet = new ServletBean(reg.getName(), reg.getClassName());
                facesServlet.getMappings().addAll(reg.getMappings());
                WebXmlProcessor.facesServlet = facesServlet;
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Do Nothing
    }

    private ServletRegistration getFacesServlet(ServletContext ctx) {
        Map<String,? extends ServletRegistration> existing = ctx.getServletRegistrations();

        for (ServletRegistration registration : existing.values()) {
            if (FACES_SERVLET_CLASS.equals(registration.getClassName())) {
                return registration;
            }
        }

        return null;
    }
}
