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
package org.jboss.portletbridge.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class TestDeployment {

    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.jboss.portletbridge:portletbridge-impl")
                                .withTransitivity()
                                .as(File.class));
    }

    public static WebArchive createDeploymentWithAll() {
        WebArchive archive = createDeployment();
        addWebXml(archive);
        addFacesConfig(archive);
        addPortletXml(archive);
        return archive;
    }

    public static WebArchive createDeploymentWithFacesConfig() {
        WebArchive archive = createDeployment();
        addFacesConfig(archive);
        return archive;
    }

    public static WebArchive createDeploymentWithWebXmlAndPortletXml() {
        WebArchive archive = createDeployment();
        addWebXml(archive);
        addPortletXml(archive);
        return archive;
    }

    public static WebArchive addWebXml(WebArchive archive) {
        return archive.addAsWebInfResource("WEB-INF/web.xml", "web.xml");
    }

    public static WebArchive addFacesConfig(WebArchive archive) {
        return archive.addAsWebInfResource("WEB-INF/faces-config.xml");
    }

    public static WebArchive addPortletXml(WebArchive archive) {
        return archive.addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
    }

    public static WebAppDescriptor createWebXmlDescriptor() {
        WebAppDescriptor webConfig = Descriptors.create(WebAppDescriptor.class);
        webConfig.addDefaultNamespaces()
                 .version("3.0")
                 .displayName("integrationTest")
                 .createServlet()
                     .servletName("Faces Servlet")
                     .servletClass("javax.faces.webapp.FacesServlet")
                     .loadOnStartup(2)
                 .up()
                 .createServletMapping()
                     .servletName("Faces Servlet")
                     .urlPattern("*.jsf")
                 .up();

        return webConfig;
    }

    public static WebFacesConfigDescriptor createFacesConfigXmlDescriptor() {
        WebFacesConfigDescriptor facesConfig = Descriptors.create(WebFacesConfigDescriptor.class);
        facesConfig.addDefaultNamespaces()
                   .version("2.1");

        return facesConfig;
    }

    public static PortletDescriptor createPortletXmlDescriptor(String portletName) {
        PortletDescriptor portlet = Descriptors.create(PortletDescriptor.class);
        portlet.addDefaultNamespaces()
                .version("2.0")
                .createPortlet()
                    .portletName(portletName)
                    .portletClass("javax.portlet.faces.GenericFacesPortlet")
                    .createInitParam()
                        .name("javax.portlet.faces.defaultViewId.view")
                        .value("/home.xhtml")
                        .up()
                    .createInitParam()
                        .name("javax.portlet.faces.preserveActionParams")
                        .value("true")
                        .up()
                    .expirationCache(0)
                    .createSupports()
                        .mimeType("text/html")
                        .portletMode("VIEW")
                        .up()
                    .getOrCreatePortletInfo()
                        .title(portletName)
                        .up()
                    .up();

        return portlet;
    }
}
