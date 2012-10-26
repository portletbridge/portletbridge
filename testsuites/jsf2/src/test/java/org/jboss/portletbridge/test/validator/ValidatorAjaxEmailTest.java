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
package org.jboss.portletbridge.test.validator;

import static org.jboss.arquillian.graphene.Graphene.element;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class ValidatorAjaxEmailTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addClass(LoginRegisterBean.class)
                .addAsWebResource("pages/validator/mainAjax.xhtml", "home.xhtml");
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
    WebDriver driver;

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
        driver.get(portalURL.toString());

        assertTrue("Check that page contains output element", Graphene.element(label).isVisible().apply(driver));
        assertTrue("Portlet should return: " + outLabelValue, Graphene.element(label).textEquals(outLabelValue).apply(driver));

        inputField.clear();
        waitAjax(driver).until(element(outputField).textEquals("req"));

        inputField.sendKeys(INPUT1);
        submitButton.click();
        waitAjax(driver).until(element(outputField).textEquals(outInvalid));
        assertTrue("Check that page after 1st submit contains output element",
                Graphene.element(outputField).isVisible().apply(driver));
        assertTrue("Portlet should 1st return: " + outInvalid,
                Graphene.element(outputField).textEquals(outInvalid).apply(driver));

        inputField.clear();
        waitAjax(driver).until(element(outputField).textEquals("req"));

        inputField.sendKeys(INPUT2);
        submitButton.click();
        waitAjax(driver).until(element(outputField).textEquals(outInvalid));
        assertTrue("Check that page after 2nd submit contains output element",
                Graphene.element(outputField).isVisible().apply(driver));
        assertTrue("Portlet should 2nd return: " + outInvalid,
                Graphene.element(outputField).textEquals(outInvalid).apply(driver));

        inputField.clear();
        waitAjax(driver).until(element(outputField).textEquals("req"));

        inputField.sendKeys(INPUT3);
        submitButton.click();
        waitAjax(driver).until(element(outputField).textEquals(outInvalid));
        assertTrue("Check that page after 3rd submit contains output element",
                Graphene.element(outputField).isVisible().apply(driver));
        assertTrue("Portlet should 3rd return: " + outInvalid,
                Graphene.element(outputField).textEquals(outInvalid).apply(driver));

        inputField.clear();
        waitAjax(driver).until(element(outputField).textEquals("req"));
        inputField.sendKeys(INPUT4);
        submitButton.click();
        waitAjax(driver).until(element(outputField).textEquals(""));
        assertTrue("Check that page after 4th submit contains output element",
                Graphene.element(outputField).isVisible().apply(driver));
        assertTrue("Portlet should 4th return empty string",
                Graphene.element(outputField).textEquals("").apply(driver));
    }
}
