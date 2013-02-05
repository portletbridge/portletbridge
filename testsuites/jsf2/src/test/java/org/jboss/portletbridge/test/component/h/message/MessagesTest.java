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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.spi.annotations.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@PortalTest
public class MessagesTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(MessagesTest.class, true);
        deployment.archive()
                .createFacesPortlet("Messages", "Messages Portlet", "message.xhtml")
                .addAsWebResource("pages/component/h/message/message.xhtml", "message.xhtml")
                .addClass(MessagesBean.class)
                .addClass(RenderActionBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    MessagesPage page;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void renderFacesPortlet() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains MESSAGES element", page.getMessages().isDisplayed());
        assertTrue("Page contains MESSAGE ONE element", page.getMessageOne().isDisplayed());
        assertTrue("Page contains MESSAGE TWO element", page.getMessageTwo().isDisplayed());

        // Check that messages start empty
        assertEquals("MESSAGES should be empty at begin.", "", page.getMessages().getText());
        assertEquals("MESSAGE ONE should be empty at begin.", "", page.getMessageOne().getText());
        assertEquals("MESSAGE TWO should be empty at begin.", "", page.getMessageTwo().getText());

        // Set all to wrong value
        page.getInputOne().sendKeys(MessagesBean.TWO);
        page.getInputTwo().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        // Check that h:messages contains both error messages
        assertTrue("MESSAGES should show SUMMARY ONE error message", page.getMessages().getText().contains(MessagesBean.ONE_ERROR_SUMMARY));
        assertTrue("MESSAGES should show SUMMARY TWO error message", page.getMessages().getText().contains(MessagesBean.TWO_ERROR_SUMMARY));

        // Check individual message for each
        assertEquals("MESSAGE ONE should show DETAIL error message", MessagesBean.ONE_ERROR_DETAIL, page.getMessageOne().getText());
        assertEquals("MESSAGE TWO should show DETAIL error message", MessagesBean.TWO_ERROR_DETAIL, page.getMessageTwo().getText());

        // Fix the input for input one
        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        // Check that h:messages contains correct messages
        assertFalse("MESSAGES should NOT show SUMMARY error ONE message", page.getMessages().getText().contains(MessagesBean.ONE_ERROR_SUMMARY));
        assertTrue("MESSAGES should show SUMMARY error TWO message", page.getMessages().getText().contains(MessagesBean.TWO_ERROR_SUMMARY));

        // Check individual message for each
        assertEquals("MESSAGE ONE should show DETAIL ok message", MessagesBean.ONE_OK_DETAIL, page.getMessageOne().getText());
        assertEquals("MESSAGE TWO should show DETAIL error message", MessagesBean.TWO_ERROR_DETAIL, page.getMessageTwo().getText());

        // Set both to their OK value
        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys(MessagesBean.TWO);
        page.getSubmitButton().click();

        // Check that h:messages contains correct messages
        assertTrue("MESSAGES should show OK message", page.getMessages().getText().contains(MessagesBean.ONE_OK_SUMMARY));
        assertTrue("MESSAGES should show OK message", page.getMessages().getText().contains(MessagesBean.TWO_OK_SUMMARY));

        // Check individual message for each
        assertEquals("MESSAGE ONE should show DETAIL ok message", MessagesBean.ONE_OK_DETAIL, page.getMessageOne().getText());
        assertEquals("MESSAGE TWO should show DETAIL ok message", MessagesBean.TWO_OK_DETAIL, page.getMessageTwo().getText());

        // Set both inputs to neutral value
        page.getInputOne().clear();
        page.getInputOne().sendKeys("abc");
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys("abc");
        page.getSubmitButton().click();

        // Check that h:messages is now empty
        assertEquals("Messages should be empty at end.", "", page.getMessages().getText());

        // Individual message should be empty as well
        assertEquals("MESSAGE ONE should be empty at end.", "", page.getMessageOne().getText());
        assertEquals("Message Two should be empty at end.", "", page.getMessageTwo().getText());
    }

    // h:message --------------------------------------------------------------

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testMessageRenderedThenNot() throws Exception {
        browser.get(portalURL.toString());

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should be rendered with DETAIL message", MessagesBean.ONE_OK_DETAIL, page.getMessageOne().getText());

        page.getDisableMessageOneRender().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        page.getMessageOne().isDisplayed();
    }

    @Test
    @RunAsClient
    public void testMessageShowDetailThenHidden() throws Exception {
        browser.get(portalURL.toString());

        // Should display DETAIL message
        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should be rendered with DETAIL message", MessagesBean.ONE_OK_DETAIL, page.getMessageOne().getText());

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        // Should not display any message
        page.getDisableMessageOneDetail().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        assertTrue("MESSAGE ONE is part of page", page.getMessageOne().isDisplayed());
        assertEquals("MESSAGE ONE should be empty", "", page.getMessageOne().getText());
    }

    @Test
    @RunAsClient
    public void testMessageShowSummaryAndDetail() throws Exception {
        browser.get(portalURL.toString());

        // Should display DETAIL message
        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should be rendered with DETAIL message", MessagesBean.ONE_OK_DETAIL, page.getMessageOne().getText());

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        // Should now display both DETAIL and SUMMARY
        page.getEnableMessageOneSummary().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertTrue("MESSAGE ONE should show DETAIL message", page.getMessageOne().getText().contains(MessagesBean.ONE_OK_DETAIL));

        assertTrue("MESSAGE ONE should show SUMMARY message", page.getMessageOne().getText().contains(MessagesBean.ONE_OK_SUMMARY));

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        // Should only display SUMMARY message now
        page.getDisableMessageOneDetail().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertFalse("MESSAGE ONE should NOT show DETAIL message", page.getMessageOne().getText().contains(MessagesBean.ONE_OK_DETAIL));

        assertEquals("MESSAGE ONE should show SUMMARY message", MessagesBean.ONE_OK_SUMMARY, page.getMessageOne().getText());

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());
    }

    // h:messages -------------------------------------------------------------

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testMessagesRendered() throws Exception {
        browser.get(portalURL.toString());

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should show SUMMARY ONE ok message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        page.getDisableMessages().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        page.getMessages().isDisplayed();
    }

    @Test
    @RunAsClient
    public void testMessagesShowDetail() throws Exception {
        browser.get(portalURL.toString());

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should show SUMMARY message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        page.getEnableMessagesDetail().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertTrue("MESSAGES should show DETAIL message", page.getMessages().getText().contains(MessagesBean.ONE_OK_DETAIL));

        assertTrue("MESSAGES should show SUMMARY message", page.getMessages().getText().contains(MessagesBean.ONE_OK_SUMMARY));

        page.getDisableMessagesSummary().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should show DETAIL message", MessagesBean.ONE_OK_DETAIL, page.getMessages().getText());
    }

    @Test
    @RunAsClient
    public void testMessagesShowSummary() throws Exception {
        browser.get(portalURL.toString());

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should show SUMMARY message", MessagesBean.ONE_OK_SUMMARY, page.getMessages().getText());

        // Should not display any message
        page.getDisableMessagesSummary().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.ONE);
        page.getSubmitButton().click();

        assertEquals("MESSAGES should NOT show any message as showDetail and showSummary are false", "", page.getMessages().getText());
    }

    @Test
    @RunAsClient
    public void testMessagesGlobalOnly() throws Exception {
        browser.get(portalURL.toString());

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.GLOBAL);
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys(MessagesBean.TWO);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should NOT show any message", "", page.getMessageOne().getText());

        assertEquals("MESSAGE TWO should be rendered with DETAIL message", MessagesBean.TWO_OK_DETAIL, page.getMessageTwo().getText());

        assertTrue("MESSAGES should show SUMMARY TWO message", page.getMessages().getText().contains(MessagesBean.TWO_OK_SUMMARY));

        assertTrue("MESSAGES should show GLOBAL SUMMARY message", page.getMessages().getText().contains(MessagesBean.GLOBAL_SUMMARY));

        assertFalse("MESSAGES should NOT show GLOBAL DETAIL message", page.getMessages().getText().contains(MessagesBean.GLOBAL_DETAIL));

        page.getEnableMessagesGlobal().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.GLOBAL);
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys(MessagesBean.TWO);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should NOT show any message", "", page.getMessageOne().getText());

        assertEquals("MESSAGE TWO should be rendered with DETAIL message", MessagesBean.TWO_OK_DETAIL, page.getMessageTwo().getText());

        assertFalse("MESSAGES should NOT show SUMMARY message", page.getMessages().getText().contains(MessagesBean.TWO_OK_SUMMARY));

        assertEquals("MESSAGES should show GLOBAL SUMMARY message", MessagesBean.GLOBAL_SUMMARY, page.getMessages().getText());

        assertFalse("MESSAGES should NOT show GLOBAL DETAIL message", page.getMessages().getText().contains(MessagesBean.GLOBAL_DETAIL));

        page.getEnableMessagesDetail().click();

        page.getInputOne().clear();
        page.getInputOne().sendKeys(MessagesBean.GLOBAL);
        page.getInputTwo().clear();
        page.getInputTwo().sendKeys(MessagesBean.TWO);
        page.getSubmitButton().click();

        assertEquals("MESSAGE ONE should NOT show any message", "", page.getMessageOne().getText());

        assertEquals("MESSAGE TWO should be rendered with DETAIL message", MessagesBean.TWO_OK_DETAIL, page.getMessageTwo().getText());

        assertFalse("MESSAGES should NOT show SUMMARY message", page.getMessages().getText().contains(MessagesBean.TWO_OK_SUMMARY));

        assertTrue("MESSAGES should show GLOBAL SUMMARY message", page.getMessages().getText().contains(MessagesBean.GLOBAL_SUMMARY));

        assertTrue("MESSAGES should show GLOBAL DETAIL message", page.getMessages().getText().contains(MessagesBean.GLOBAL_DETAIL));
    }
}
