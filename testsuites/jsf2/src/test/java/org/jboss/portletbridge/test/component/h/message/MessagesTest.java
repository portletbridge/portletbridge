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
package org.jboss.portletbridge.test.component.h.message;

import static org.junit.Assert.assertFalse;
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
public class MessagesTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/message/message.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css")
                .addClass(MessagesBean.class);
    }

    @FindBy(xpath = "//*[contains(@id,':messages')]")
    private WebElement messages;

    @FindBy(xpath = "//*[contains(@id,':message_one')]")
    private WebElement messageOne;

    @FindBy(xpath = "//*[contains(@id,':message_two')]")
    private WebElement messageTwo;

    @FindBy(xpath = "//input[@type='text'][contains(@id,':input_one')]")
    private WebElement inputOne;

    @FindBy(xpath = "//input[@type='password'][contains(@id,':input_two')]")
    private WebElement inputTwo;

    @FindBy(xpath = "//input[@type='submit'][contains(@id,':submit')]")
    private WebElement submitButton;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Test
    @RunAsClient
    public void renderFacesPortlet(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        assertTrue("Check that page contains MESSAGES element", Graphene.element(messages).isVisible().apply(driver));
        assertTrue("Check that page contains MESSAGE ONE element", Graphene.element(messageOne).isVisible().apply(driver));
        assertTrue("Check that page contains MESSAGE TWO element", Graphene.element(messageTwo).isVisible().apply(driver));

        // Check that messages start empty
        assertTrue("MESSAGES should be empty at begin.", Graphene.element(messages).textEquals("").apply(driver));
        assertTrue("MESSAGE ONE should be empty at begin.", Graphene.element(messageOne).textEquals("").apply(driver));
        assertTrue("MESSAGE TWO should be empty at begin.", Graphene.element(messageTwo).textEquals("").apply(driver));

        // Set all to wrong value
        inputOne.sendKeys(MessagesBean.TWO);
        inputTwo.sendKeys(MessagesBean.ONE);
        submitButton.click();

        // Check that h:messages contains both error messages
        assertTrue("MESSAGES should show SUMMARY error message: " + MessagesBean.ONE_ERROR_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.ONE_ERROR_SUMMARY).apply(driver));
        assertTrue("MESSAGES should show SUMMARY error message: " + MessagesBean.TWO_ERROR_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_ERROR_SUMMARY).apply(driver));

        // Check individual message for each
        assertTrue("MESSAGE ONE should show DETAIL error message: " + MessagesBean.ONE_ERROR_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_ERROR_DETAIL).apply(driver));
        assertTrue("MESSAGE TWO should show DETAIL error message: " + MessagesBean.TWO_ERROR_DETAIL,
                Graphene.element(messageTwo).textContains(MessagesBean.TWO_ERROR_DETAIL).apply(driver));

        // Fix the input for input one
        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        inputTwo.clear();
        inputTwo.sendKeys(MessagesBean.ONE);
        submitButton.click();

        // Check that h:messages contains correct messages
        assertFalse("MESSAGES should NOT show SUMMARY error ONE message: " + MessagesBean.ONE_ERROR_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.ONE_ERROR_SUMMARY).apply(driver));
        assertTrue("MESSAGES should show SUMMARY error TWO message: " + MessagesBean.TWO_ERROR_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_ERROR_SUMMARY).apply(driver));

        // Check individual message for each
        assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));
        assertTrue("MESSAGE TWO should show DETAIL error message: " + MessagesBean.TWO_ERROR_DETAIL,
                Graphene.element(messageTwo).textContains(MessagesBean.TWO_ERROR_DETAIL).apply(driver));

        // Set both to their OK value
        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        inputTwo.clear();
        inputTwo.sendKeys(MessagesBean.TWO);
        submitButton.click();

        // Check that h:messages contains correct messages
        assertTrue("Messages should show OK message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.ONE_OK_SUMMARY).apply(driver));
        assertTrue("Messages should show OK message: " + MessagesBean.TWO_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_OK_SUMMARY).apply(driver));

        // Check individual message for each
        assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));
        assertTrue("MESSAGE TWO should show DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
                Graphene.element(messageTwo).textContains(MessagesBean.TWO_OK_DETAIL).apply(driver));

        // Set both inputs to neutral value
        inputOne.clear();
        inputOne.sendKeys("abc");
        inputTwo.clear();
        inputTwo.sendKeys("abc");
        submitButton.click();

        // Check that h:messages is now empty
        assertTrue("Messages should be empty at end.", Graphene.element(messages).textEquals("").apply(driver));

        // Individual message should be empty as well
        assertTrue("MESSAGE ONE should be empty at end.", Graphene.element(messageOne).textEquals("").apply(driver));
        assertTrue("Message Two should be empty at end.", Graphene.element(messageTwo).textEquals("").apply(driver));
    }

    // h:message --------------------------------------------------------------

    @Test
    @RunAsClient
    public void testMessageRendered(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));

        MessagesBean.RENDER_ONE = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should not be rendered.", Graphene.element(messageOne).not().isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testMessageShowDetail(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        // Should display DETAIL message
        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        // Should not display any message
        MessagesBean.SHOW_DETAIL_ONE = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should not have any message as showDetail and showSummary are false",
                Graphene.element(messageOne).textEquals("").apply(driver));

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));
    }

    @Test
    @RunAsClient
    public void testMessageShowSummary(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        // Should display DETAIL message
        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textEquals(MessagesBean.ONE_OK_DETAIL).apply(driver));

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        // Should now display both DETAIL and SUMMARY
        MessagesBean.SHOW_SUMMARY_ONE = true;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));

        assertTrue("MESSAGE ONE should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        // Should only display SUMMARY message now
        MessagesBean.SHOW_DETAIL_ONE = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertFalse("MESSAGE ONE should NOT show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));

        assertTrue("MESSAGE ONE should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messageOne).textContains(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));
    }

    // h:messages -------------------------------------------------------------

    @Test
    @RunAsClient
    public void testMessagesRendered(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        MessagesBean.RENDER_MESSAGES = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should not be rendered.", Graphene.element(messages).not().isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testMessagesShowDetail(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should be rendered with ONLY SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        MessagesBean.SHOW_DETAIL_MESSAGES = true;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messages).textContains(MessagesBean.ONE_OK_DETAIL).apply(driver));

        assertTrue("MESSAGES should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        MessagesBean.SHOW_SUMMARY_MESSAGES = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should be rendered with ONLY DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_DETAIL).apply(driver));
    }

    @Test
    @RunAsClient
    public void testMessagesShowSummary(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
                Graphene.element(messages).textEquals(MessagesBean.ONE_OK_SUMMARY).apply(driver));

        // Should not display any message
        MessagesBean.SHOW_SUMMARY_MESSAGES = false;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.ONE);
        submitButton.click();

        assertTrue("MESSAGES should NOT show any message as showDetail and showSummary are false",
                Graphene.element(messages).textEquals("").apply(driver));
    }

    @Test
    @RunAsClient
    public void testMessagesGlobalOnly(@Drone WebDriver driver) throws Exception {
        MessagesBean.setDefaults();
        driver.get(portalURL.toString());

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.GLOBAL);
        inputTwo.clear();
        inputTwo.sendKeys(MessagesBean.TWO);
        submitButton.click();

        assertTrue("MESSAGE ONE should NOT show any message",
                Graphene.element(messageOne).textEquals("").apply(driver));

        assertTrue("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
                Graphene.element(messageTwo).textEquals(MessagesBean.TWO_OK_DETAIL).apply(driver));

        assertTrue("MESSAGES should show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_OK_SUMMARY).apply(driver));

        assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_SUMMARY).apply(driver));

        assertFalse("MESSAGES should NOT show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_DETAIL).apply(driver));

        MessagesBean.GLOBAL_ONLY_MESSAGES = true;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.GLOBAL);
        inputTwo.clear();
        inputTwo.sendKeys(MessagesBean.TWO);
        submitButton.click();

        assertTrue("MESSAGE ONE should NOT render any message.",
                Graphene.element(messageOne).textEquals("").apply(driver));

        assertTrue("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
                Graphene.element(messageTwo).textEquals(MessagesBean.TWO_OK_DETAIL).apply(driver));

        assertFalse("MESSAGES should NOT show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_OK_SUMMARY).apply(driver));

        assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_SUMMARY).apply(driver));

        assertFalse("MESSAGES should NOT show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_DETAIL).apply(driver));

        MessagesBean.SHOW_DETAIL_MESSAGES = true;

        inputOne.clear();
        inputOne.sendKeys(MessagesBean.GLOBAL);
        inputTwo.clear();
        inputTwo.sendKeys(MessagesBean.TWO);
        submitButton.click();

        assertTrue("MESSAGE ONE should NOT render any message.",
                Graphene.element(messageOne).textEquals("").apply(driver));

        assertTrue("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
                Graphene.element(messageTwo).textEquals(MessagesBean.TWO_OK_DETAIL).apply(driver));

        assertFalse("MESSAGES should NOT show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.TWO_OK_SUMMARY).apply(driver));

        assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_SUMMARY).apply(driver));

        assertTrue("MESSAGES should show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
                Graphene.element(messages).textContains(MessagesBean.GLOBAL_DETAIL).apply(driver));
    }
}
