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
package org.jboss.portletbridge.it;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.common.AbstractPortletTest;
import org.jboss.portletbridge.it.common.Bean;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@PortalTest
public class AjaxSubmitTest extends AbstractPortletTest {

    public static final String NEW_VALUE = "New Value";

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(AjaxSubmitTest.class, true);
        deployment.archive()
                .createFacesPortlet("AjaxSubmit", "Ajax Submit Portlet", "ajax.xhtml")
                .addAsWebResource("pages/ajax.xhtml", "ajax.xhtml")
                .addClass(Bean.class);
        return deployment.getFinalArchive();
    }

    @FindBy(id = "output")
    private WebElement outputField;

    @FindBy(xpath = "//input[@type='text']")
    private WebElement inputField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;

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
    public void renderFormPortlet() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains output element", outputField.isDisplayed());

        assertEquals("Output text set.", Bean.HELLO_JSF_PORTLET, outputField.getText());

        assertEquals("Input text set.", Bean.HELLO_JSF_PORTLET, inputField.getAttribute("value"));

        assertTrue("Submit button is not enabled", submitButton.isEnabled());
        assertNotSame("Submit button onclick attribute should not be blank", "", submitButton.getAttribute("onclick"));
    }

    @Test
    @RunAsClient
    public void testSubmitAndRemainOnPage() throws Exception {
        browser.get(portalURL.toString());

        inputField.sendKeys(NEW_VALUE);
        guardXhr(submitButton).click();

        assertTrue("Output text updated.", outputField.getText().contains(NEW_VALUE));

        assertTrue("Input text updated.", inputField.getAttribute("value").contains(NEW_VALUE));

        // Re-render page
        browser.get(portalURL.toString());

        assertTrue("Output text unchanged.", outputField.getText().contains(NEW_VALUE));

        assertTrue("Input text unchanged.", inputField.getAttribute("value").contains(NEW_VALUE));
    }

}
