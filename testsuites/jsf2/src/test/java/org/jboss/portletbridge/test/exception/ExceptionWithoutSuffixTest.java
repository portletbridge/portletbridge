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
package org.jboss.portletbridge.test.exception;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.faces.component.UpdateModelException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
public class ExceptionWithoutSuffixTest {

    public static final String NEW_VALUE = "New Value";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/exception/test.xhtml", "home.xhtml")
                .addAsWebResource("pages/exception/error.xhtml", "updateModelException.xhtml")
                .addAsWebInfResource(new StringAsset(getWebXml()), "web.xml")
                .addClass(ExceptionBean.class);
        TestDeployment.addFacesConfig(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    private static String getWebXml() {
        WebAppDescriptor webConfig = TestDeployment.createWebXmlDescriptor();
        webConfig.createErrorPage()
                     .exceptionType(UpdateModelException.class.getName())
                     .location("/updateModelException.jsf")
                     .up();

        return webConfig.exportAsString();
    }

    protected static final By INPUT_FIELD = By.xpath("//input[@type='text']");
    protected static final By SUBMIT_BUTTON = By.xpath("//input[@type='submit']");
    protected static final By HEADING = By.id("heading");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testErrorPageWithoutDefaultSuffixSet() throws Exception {
        driver.get(portalURL.toString());
        driver.findElement(INPUT_FIELD).sendKeys(NEW_VALUE);
        driver.findElement(SUBMIT_BUTTON).click();

        assertTrue("Should have redirected to error page",
                ExpectedConditions.textToBePresentInElement(HEADING, "UpdateModelException").apply(driver));
    }

}
