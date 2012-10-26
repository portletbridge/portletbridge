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
package org.jboss.portletbridge.test.component.h.inputText;

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
public class InputTextTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/inputText/inputtext.xhtml", "home.xhtml")
                .addClass(InputTextBean.class)
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css")
                .addAsWebResource("resources/stylesheet.css", "portlet-spec-1.0.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @FindBy(xpath = "//input[contains(@id,':input1')]")
    private WebElement inputOne;

    @FindBy(xpath = "//span[contains(@id,':output1')]")
    private WebElement outputOne;

    @FindBy(xpath = "//span[contains(@id,':output1count')]")
    private WebElement outputOneCounter;

    @FindBy(xpath = "//input[contains(@id,':submit1')]")
    private WebElement submitOne;

    @FindBy(xpath = "//input[contains(@id,':input2')]")
    private WebElement inputTwo;

    @FindBy(xpath = "//span[contains(@id,':output2')]")
    private WebElement outputTwo;

    @FindBy(xpath = "//span[contains(@id,':output2count')]")
    private WebElement outputTwoCounter;

    @FindBy(xpath = "//*[contains(@id,':messages')]")
    private WebElement messages;

    @Test
    @RunAsClient
    public void testInputText(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", Graphene.element(inputOne).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT ONE element.", Graphene.element(outputOne).isVisible().apply(driver));
        assertTrue("Check that page contains SUBMIT ONE element.", Graphene.element(submitOne).isVisible().apply(driver));

        assertTrue("Check that INPUT ONE element starts empty.",
                Graphene.attribute(inputOne, "value").valueEquals("").apply(driver));
        assertTrue("Check that OUTPUT ONE element starts empty.",
                Graphene.element(outputOne).textEquals("").apply(driver));

        String textToInput = "pbr";

        inputOne.sendKeys(textToInput);
        submitOne.click();

        assertTrue("OUTPUT ONE element should have inputed text.",
                Graphene.element(outputOne).textEquals(textToInput).apply(driver));
    }

    @Test
    @RunAsClient
    public void testAjaxInputText(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains INPUT TWO element.", Graphene.element(inputTwo).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT TWO element.", Graphene.element(outputTwo).isVisible().apply(driver));

        assertTrue("Check that INPUT TWO element starts empty.",
                Graphene.attribute(inputTwo, "value").valueEquals("").apply(driver));
        assertTrue("Check that OUTPUT TWO element starts empty.",
                Graphene.element(outputTwo).textEquals("").apply(driver));

        final String textToInput = "pbr";

        for (String s : textToInput.split("")) {
            if (!s.equals("")) {
                inputTwo.sendKeys(s);
            }
        }

        waitAjax(driver).until(element(outputTwo).textEquals(textToInput));

        assertTrue("OUTPUT TWO element should have inputed text.",
                Graphene.element(outputTwo).textEquals(textToInput).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOnChange(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains INPUT TWO element.", Graphene.element(inputTwo).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT TWO element.", Graphene.element(outputTwo).isVisible().apply(driver));

        assertTrue("Check that INPUT TWO element starts empty.",
                Graphene.attribute(inputTwo, "value").valueEquals("").apply(driver));
        assertTrue("Check that OUTPUT TWO element starts empty.",
                Graphene.element(outputTwo).textEquals("").apply(driver));

        final String textToInput = "pbr";

        // Fill input one and submit
        inputOne.sendKeys(textToInput);
        submitOne.click();

        // FIXME: Why doesn't it update in time ?
        //assertTrue("OUTPUT ONE COUNTER element should have inputed text length.",
        //        Graphene.element(outputOneCounter).textEquals(String.valueOf(textToInput.length())).apply(driver));

        // Fill input two, char by char
        for (String s : textToInput.split("")) {
            if (!s.equals("")) {
                inputTwo.sendKeys(s);
            }
        }

        waitAjax(driver).until(element(outputTwo).textEquals(textToInput));

        assertTrue("OUTPUT TWO COUNTER element should have inputed text length.",
                Graphene.element(outputTwoCounter).textEquals(String.valueOf(textToInput.length()/* FIXME: AJAX */- 1)).
                apply(driver));

        assertTrue("OUTPUT ONE COUNTER element should have inputed text length.",
                Graphene.element(outputOneCounter).textEquals(String.valueOf(textToInput.length())).apply(driver));
        // FIXME: ..
        //assertTrue("OUTPUT TWO COUNTER element should have inputed text length.",
        //        Graphene.element(outputTwoCounter).textEquals(String.valueOf(textToInput.length())).apply(driver));
    }

    @Test
    @RunAsClient
    public void testRequired(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", Graphene.element(inputOne).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT ONE element.", Graphene.element(outputOne).isVisible().apply(driver));
        assertTrue("Check that page contains SUBMIT ONE element.", Graphene.element(submitOne).isVisible().apply(driver));

        assertTrue("Check that INPUT ONE element starts empty.",
                Graphene.attribute(inputOne, "value").valueEquals("").apply(driver));
        assertTrue("Check that OUTPUT ONE element starts empty.",
                Graphene.element(outputOne).textEquals("").apply(driver));

        // Submit with no input
        submitOne.click();

        assertTrue("MESSAGES should contain error message: " + InputTextBean.REQUIRED_MESSAGE,
                Graphene.element(messages).textEquals(InputTextBean.REQUIRED_MESSAGE).apply(driver));
    }

    @Test
    @RunAsClient
    public void testValidateLength(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", Graphene.element(inputOne).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT ONE element.", Graphene.element(outputOne).isVisible().apply(driver));
        assertTrue("Check that page contains SUBMIT ONE element.", Graphene.element(submitOne).isVisible().apply(driver));

        assertTrue("Check that INPUT ONE element starts empty.",
                Graphene.attribute(inputOne, "value").valueEquals("").apply(driver));
        assertTrue("Check that OUTPUT ONE element starts empty.",
                Graphene.element(outputOne).textEquals("").apply(driver));

        // Submit a small input
        inputOne.sendKeys("pb");
        submitOne.click();

        assertTrue("MESSAGES should contain error message: " + InputTextBean.MIN_LENGTH_MESSAGE,
                Graphene.element(messages).textEquals(InputTextBean.MIN_LENGTH_MESSAGE).apply(driver));
    }

}
