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
package org.jboss.portletbridge.it.stage;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

public abstract class StageAbstractTest extends AbstractPortletTest {

    protected static void getWebXml(WebAppDescriptor webConfig, String value) {
        webConfig.createContextParam()
                 .paramName("javax.faces.PROJECT_STAGE")
                 .paramValue(value);
    }

    public static PortletArchive createDeployment(Class<?> testClass, String value) {
        TestDeployment deployment = new TestDeployment(testClass, true);

        getWebXml(deployment.webXml(), value);

        deployment.archive()
                .createFacesPortlet(testClass.getSimpleName(), testClass.getSimpleName() + " Portlet", "main.xhtml")
                .addAsWebResource("pages/stage/main.xhtml", "main.xhtml");
        return deployment.getFinalArchive();
    }

    @FindBy(xpath = "//span[contains(@id,'outStage')]")
    protected WebElement label;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

}
