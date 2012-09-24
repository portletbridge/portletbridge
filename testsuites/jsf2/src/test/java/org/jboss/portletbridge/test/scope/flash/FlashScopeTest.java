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
package org.jboss.portletbridge.test.scope.flash;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
public class FlashScopeTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/scope/flash/home.xhtml", "home.xhtml")
                .addAsWebResource("pages/scope/flash/done.xhtml", "done.xhtml")
                .addClass(RequestBean.class);
        TestDeployment.addWebXml(wa);
        TestDeployment.addFacesConfig(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static final String NEW_VALUE = "new";

    protected static final By INPUT_FIELD = By.xpath("//input[@type='text']");
    protected static final By SUBMIT_BUTTON_1 = By.xpath("//input[@type='submit' and @value='Click1']");
    protected static final By SUBMIT_BUTTON_2 = By.xpath("//input[@type='submit' and @value='Click2']");
    protected static final By OUTPUT_FIELD_1 = By.id("output1");
    protected static final By OUTPUT_FIELD_2 = By.id("output2");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void requestScopeShouldBeReset() throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains input element", driver.findElement(INPUT_FIELD));

        assertTrue("input field should contain: " + RequestBean.ORIG_VALUE,
                ExpectedConditions.textToBePresentInElementValue(INPUT_FIELD, RequestBean.ORIG_VALUE).apply(driver));

        driver.findElement(INPUT_FIELD).sendKeys(NEW_VALUE);
        driver.findElement(SUBMIT_BUTTON_1).click();

        assertTrue("output1 field should contain: " + RequestBean.ORIG_VALUE,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD_1, RequestBean.ORIG_VALUE).apply(driver));
        assertTrue("output2 field should contain: [null]",
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD_2, "").apply(driver));
    }

    @Test
    @RunAsClient
    public void flashScopeShouldRetainBeanThroughRedirect() throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains input element", driver.findElement(INPUT_FIELD));

        assertTrue("input field should contain: " + RequestBean.ORIG_VALUE,
                ExpectedConditions.textToBePresentInElementValue(INPUT_FIELD, RequestBean.ORIG_VALUE).apply(driver));

        driver.findElement(INPUT_FIELD).sendKeys(NEW_VALUE);
        driver.findElement(SUBMIT_BUTTON_2).click();

        assertTrue("output1 field should contain: " + RequestBean.ORIG_VALUE,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD_1, RequestBean.ORIG_VALUE).apply(driver));
        assertTrue("output2 field should contain: " + NEW_VALUE,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD_2, NEW_VALUE).apply(driver));
    }

}
