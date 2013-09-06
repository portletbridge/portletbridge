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
package org.jboss.portletbridge.it.validator;

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

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class ValidatorAjaxEmailTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ValidatorAjaxEmailTest.class, true);
        deployment.archive()
                .createFacesPortlet("ValidatorAjaxEmail", "Validator Ajax Email Portlet", "mainAjax.xhtml")
                .addAsWebResource("pages/validator/mainAjax.xhtml", "mainAjax.xhtml")
                .addClass(LoginRegisterBean.class);
        return deployment.getFinalArchive();
    }

    protected static final String INPUT1 = "userEmail";
    protected static final String INPUT2 = "user@Email";
    protected static final String INPUT3 = "user@Email.";
    protected static final String INPUT4 = "user@Email.sk";

    protected static final String outInvalid = "Invalid";
    protected static final String outLabelValue = "userEmail";

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @FindBy(xpath = "//input[contains(@id,'userEmail')]")
    private WebElement inputField;

    @FindBy(xpath = "//span[contains(@id,'validator')]")
    private WebElement outputField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;

    @FindBy(xpath = "//label[contains(@id,'outlabel')]")
    private WebElement label;

    @Test
    @RunAsClient
    public void testValidatorAjax() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains output element!", label.isDisplayed());
        assertEquals("Portlet output set.", outLabelValue, label.getText());

        guardXhr(inputField).clear();
        assertEquals("Output Field has required message", "req", outputField.getText());

        inputField.sendKeys(INPUT1);
        submitButton.click();

        assertTrue("Check that page after 1st submit contains output element", outputField.isDisplayed());
        assertEquals("Invalid message set.", outInvalid, outputField.getText());

        guardXhr(inputField).clear();
        assertEquals("Output Field has required message", "req", outputField.getText());

        inputField.sendKeys(INPUT2);
        submitButton.click();

        assertTrue("Check that page after 2nd submit contains output element", outputField.isDisplayed());
        assertEquals("Invalid message set.", outInvalid, outputField.getText());

        guardXhr(inputField).clear();
        assertEquals("Output Field has required message", "req", outputField.getText());

        inputField.sendKeys(INPUT3);
        submitButton.click();

        assertTrue("Check that page after 3rd submit contains output element", outputField.isDisplayed());
        assertEquals("Invalid message set.", outInvalid, outputField.getText());

        guardXhr(inputField).clear();
        assertEquals("Output Field has required message", "req", outputField.getText());

        inputField.sendKeys(INPUT4);
        submitButton.click();

        assertTrue("Check that page after 4th submit contains output element", outputField.isDisplayed());
        assertEquals("Empty string set.", "", outputField.getText());
    }
}
