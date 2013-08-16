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
package org.portletbridge.arquillian.deployment;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.facesconfig20.FacesConfigVersionType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Base for all test deployments.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class TestDeployment {

    private PortletArchive archive;

    private WebFacesConfigDescriptor facesConfig;
    private WebAppDescriptor webXml;
    private PortletDescriptor portletXml;

    private Set<String> mavenDeps = new HashSet<String>();

    public TestDeployment() {
        this("testPortlet.war");
    }

    public TestDeployment(Class<?> testClass) {
        this(testClass.getSimpleName() + ".war");
    }

    public TestDeployment(Class<?> testClass, boolean createWebXmlWithFacesServlet) {
        this(testClass.getSimpleName() + ".war");
        if (createWebXmlWithFacesServlet) {
            this.webXml = Descriptors.create(WebAppDescriptor.class)
                    .addDefaultNamespaces()
                    .version(WebAppVersionType._3_0)
                    .displayName(testClass.getSimpleName())
                    .createServlet()
                        .servletName("Faces Servlet")
                        .servletClass("javax.faces.webapp.FacesServlet")
                        .loadOnStartup(2)
                        .up()
                    .createServletMapping()
                        .servletName("Faces Servlet")
                        .urlPattern("*.jsf")
                        .up();
        }
    }

    private TestDeployment(String deploymentName) {
        this.archive = ShrinkWrap.create(PortletArchive.class, deploymentName);

        this.addMavenDependency("org.jboss.portletbridge:portletbridge-api",
                "org.jboss.portletbridge:portletbridge-impl");
    }

    /**
     * Provides {@link PortletArchive} available for tests to modify.
     */
    public PortletArchive archive() {
        return archive;
    }

    /**
     * Returns final archive for test deployment - packages all resources which were configured separately.
     */
    public PortletArchive getFinalArchive() {
        PortletArchive finalArchive = archive;
        if (null != webXml) {
            finalArchive = finalArchive.setWebXML(new StringAsset(webXml.exportAsString()));
        }
        if (null != facesConfig) {
            finalArchive = finalArchive.addAsWebInfResource(new StringAsset(facesConfig.exportAsString()), "faces-config.xml");
        }
        if (null != portletXml) {
            finalArchive = finalArchive.setPortletXML(new StringAsset(portletXml.exportAsString()));
        }

        includeMavenDependencies(finalArchive);

        return finalArchive;
    }

    /**
     * Return the Faces Config descriptor for the deployment
     */
    public WebFacesConfigDescriptor facesConfig() {
        if (null == this.facesConfig) {
            this.facesConfig = Descriptors.create(WebFacesConfigDescriptor.class)
                                            .addDefaultNamespaces()
                                            .version(FacesConfigVersionType._2_1);
        }

        return this.facesConfig;
    }

    /**
     * Return the Web Xml descriptor for the deployment
     */
    public WebAppDescriptor webXml() {
        if (null == this.webXml) {
            this.webXml = Descriptors.create(WebAppDescriptor.class)
                                        .addDefaultNamespaces()
                                        .version(WebAppVersionType._3_0);
        }

        return this.webXml;
    }

    public PortletDescriptor portletXml() {
        if (null == this.portletXml) {
            this.portletXml = Descriptors.create(PortletDescriptor.class)
                                            .addDefaultNamespaces()
                                            .version("2.0");
        }
        return this.portletXml;
    }

    /**
     * Adds maven artifact(s) as library dependency.
     */
    public TestDeployment addMavenDependency(String... dependencies) {
        mavenDeps.addAll(Arrays.asList(dependencies));
        return this;
    }

    private void includeMavenDependencies(PortletArchive archive) {
        Set<File> jars = new HashSet<File>();

        for (String dependency : mavenDeps) {
            File tempDir = new File("target/shrinkwrap-resolver-cache/" + dependency);

            if (!tempDir.exists()) {
                resolveMavenDependency(dependency, jars);
            } else {
                File[] fileList = tempDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                });

                jars.addAll(Arrays.asList(fileList));
            }
        }

        File[] files = jars.toArray(new File[jars.size()]);
        archive.addAsLibraries(files);
    }

    private void resolveMavenDependency(String dependency, Set<File> jars) {
        jars.addAll(
                Arrays.asList(
                        Maven.resolver()
                             .loadPomFromFile("pom.xml")
                             .resolve(dependency)
                             .withoutTransitivity()
                             .asFile()
                )
        );
    }
}
