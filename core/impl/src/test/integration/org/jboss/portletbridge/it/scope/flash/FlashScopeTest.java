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
package org.jboss.portletbridge.it.scope.flash;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@PortalTest
public class FlashScopeTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(FlashScopeTest.class, true);
        deployment.archive()
                .createFacesPortlet("FlashScope", "Flash Scope Portlet", "home.xhtml")
                .addAsWebResource("pages/scope/flash/home.xhtml", "home.xhtml")
                .addAsWebResource("pages/scope/flash/done.xhtml", "done.xhtml")
                .addClass(RequestBean.class);
        return deployment.getFinalArchive();
    }

    protected static final String NEW_VALUE = "new";

    @FindBy(jquery = "[id$=':in']")
    private WebElement inputField;

    @FindBy(jquery = "[id$=':sub1']")
    private WebElement submitButton1;

    @FindBy(jquery = "[id$=':sub2']")
    private WebElement submitButton2;

    @FindBy(id = "output1")
    private WebElement outputField1;

    @FindBy(id = "output2")
    private WebElement outputField2;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void requestScopeShouldBeReset() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains input element", inputField.isDisplayed());

        assertEquals("input field set.", RequestBean.ORIG_VALUE, inputField.getAttribute("value"));

        inputField.sendKeys(NEW_VALUE);
        submitButton1.click();

        assertEquals("output1 field set to original.", RequestBean.ORIG_VALUE, outputField1.getText());
        assertEquals("output2 field should be empty", "", outputField2.getText());
    }

    @Test
    @RunAsClient
    public void flashScopeShouldRetainBeanThroughRedirect() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains input element", inputField.isDisplayed());

        assertEquals("input field set.", RequestBean.ORIG_VALUE, inputField.getAttribute("value"));

        inputField.sendKeys(NEW_VALUE);
        submitButton2.click();

        assertEquals("output1 field set to original.", RequestBean.ORIG_VALUE, outputField1.getText());
        assertTrue("output2 field updated.", outputField2.getText().contains(NEW_VALUE));
    }

}
