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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

@RunWith(Arquillian.class)
public class MessagesTest {

	@Deployment()
	public static WebArchive createDeployment() {
		return TestDeployment.createDeploymentWithAll()
				.addAsWebResource("pages/component/h/message/message.xhtml", "home.xhtml")
				.addClass(MessagesBean.class);
		//.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
	}

	protected static final By MESSAGES_FIELD = By.id("messages");
	protected static final By MESSAGE_ONE_FIELD = By.id("message_one");
	protected static final By MESSAGE_TWO_FIELD = By.id("message_two");

	protected static final By INPUT_ONE_FIELD = By.xpath("//input[@type='text']");
	protected static final By INPUT_TWO_FIELD = By.xpath("//input[@type='password']");
	protected static final By SUBMIT_BUTTON = By.xpath("//input[@type='submit']");

	@ArquillianResource
	@PortalURL
	URL portalURL;

	@Test
	@RunAsClient
	public void renderFacesPortlet(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains MESSAGES element", driver.findElement(MESSAGES_FIELD));
		assertNotNull("Check that page contains MESSAGE ONE element", driver.findElement(MESSAGE_ONE_FIELD));
		assertNotNull("Check that page contains MESSAGE TWO element", driver.findElement(MESSAGE_TWO_FIELD));

		// Check that messages start empty
		assertTrue("Messages should be empty at begin.",
				driver.findElement(MESSAGES_FIELD).getText().equals(""));
		assertTrue("MESSAGE ONE should be empty at begin.",
				driver.findElement(MESSAGE_ONE_FIELD).getText().equals(""));
		assertTrue("Message Two should be empty at begin.",
				driver.findElement(MESSAGE_TWO_FIELD).getText().equals(""));

		// Set all to wrong value
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.TWO);
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		// Check that h:messages contains both error messages
		assertTrue("MESSAGES should show SUMMARY error message: " + MessagesBean.ONE_ERROR_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.ONE_ERROR_SUMMARY).apply(driver));
		assertTrue("MESSAGES should show SUMMARY error message: " + MessagesBean.TWO_ERROR_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_ERROR_SUMMARY).apply(driver));

		// Check individual message for each
		assertTrue("MESSAGE ONE should show DETAIL error message: " + MessagesBean.ONE_ERROR_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_ERROR_DETAIL).apply(driver));
		assertTrue("MESSAGE TWO should show DETAIL error message: " + MessagesBean.TWO_ERROR_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_TWO_FIELD, MessagesBean.TWO_ERROR_DETAIL).apply(driver));

		// Fix the input for input one
		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		// Check that h:messages contains correct messages
		assertFalse("MESSAGES should NOT show SUMMARY error message: " + MessagesBean.ONE_ERROR_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.ONE_ERROR_SUMMARY).apply(driver));
		assertTrue("MESSAGES should show SUMMARY error message: " + MessagesBean.TWO_ERROR_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_ERROR_SUMMARY).apply(driver));

		// Check individual message for each
		assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_DETAIL).apply(driver));
		assertTrue("MESSAGE TWO should show DETAIL error message: " + MessagesBean.TWO_ERROR_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_TWO_FIELD, MessagesBean.TWO_ERROR_DETAIL).apply(driver));

		// Set both to their OK value
		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.TWO);
		driver.findElement(SUBMIT_BUTTON).click();

		// Check that h:messages contains correct messages
		assertTrue("Messages should show OK message: " + MessagesBean.ONE_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.ONE_OK_SUMMARY).apply(driver));
		assertTrue("Messages should show OK message: " + MessagesBean.TWO_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_OK_SUMMARY).apply(driver));

		// Check individual message for each
		assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_DETAIL).apply(driver));
		assertTrue("MESSAGE TWO should show DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_TWO_FIELD, MessagesBean.TWO_OK_DETAIL).apply(driver));

		// Set both inputs to neutral value
		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys("abc");
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys("abc");
		driver.findElement(SUBMIT_BUTTON).click();

		// Check that h:messages is now empty
		assertEquals("Messages should be empty at end.",
				driver.findElement(MESSAGES_FIELD).getText(), "");

		// Individual message should be empty as well
		assertEquals("MESSAGE ONE should be empty at end.",
				driver.findElement(MESSAGE_ONE_FIELD).getText(), "");
		assertEquals("Message Two should be empty at end.",
				driver.findElement(MESSAGE_TWO_FIELD).getText(), "");
	}

	// h:message --------------------------------------------------------------

	@Test
	@RunAsClient
	public void testMessageRendered(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				MessagesBean.ONE_OK_DETAIL, driver.findElement(MESSAGE_ONE_FIELD).getText());

		MessagesBean.RENDER_ONE = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should not be rendered.", "", driver.findElement(MESSAGE_ONE_FIELD).getText());
	}

	@Test
	@RunAsClient
	public void testMessageShowDetail(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		// Should display DETAIL message
		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				MessagesBean.ONE_OK_DETAIL, driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		// Should not display any message
		MessagesBean.SHOW_DETAIL_ONE = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should not be rendered as showDetail and showSummary are false", "", driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY + "\r\nSOURCE:\r\n" +driver.getPageSource(),
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());
	}

	@Test
	@RunAsClient
	public void testMessageShowSummary(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		// Should display DETAIL message
		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should be rendered with DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				MessagesBean.ONE_OK_DETAIL, driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		// Should now display both DETAIL and SUMMARY
		MessagesBean.SHOW_SUMMARY_ONE = true;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertTrue("MESSAGE ONE should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_DETAIL).apply(driver));

		assertTrue("MESSAGE ONE should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_SUMMARY).apply(driver));

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		// Should only display SUMMARY message now
		MessagesBean.SHOW_DETAIL_ONE = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertFalse("MESSAGE ONE should NOT show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_DETAIL).apply(driver));

		assertTrue("MESSAGE ONE should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGE_ONE_FIELD, MessagesBean.ONE_OK_SUMMARY).apply(driver));

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY + "\r\nSOURCE:\r\n" +driver.getPageSource(),
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());
	}

	// h:messages -------------------------------------------------------------

	@Test
	@RunAsClient
	public void testMessagesRendered(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should be rendered with message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		MessagesBean.RENDER_MESSAGES = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should NOT be rendered.", "", driver.findElement(MESSAGES_FIELD).getText());
	}

	@Test
	@RunAsClient
	public void testMessagesShowDetail(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should be rendered with ONLY SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		MessagesBean.SHOW_DETAIL_MESSAGES = true;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertTrue("MESSAGES should show DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.ONE_OK_DETAIL).apply(driver));

		assertTrue("MESSAGES should show SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.ONE_OK_SUMMARY).apply(driver));

		MessagesBean.SHOW_SUMMARY_MESSAGES = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should be rendered with ONLY DETAIL message: " + MessagesBean.ONE_OK_DETAIL,
				MessagesBean.ONE_OK_DETAIL, driver.findElement(MESSAGES_FIELD).getText());
	}

	@Test
	@RunAsClient
	public void testMessagesShowSummary(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should be rendered with SUMMARY message: " + MessagesBean.ONE_OK_SUMMARY,
				MessagesBean.ONE_OK_SUMMARY, driver.findElement(MESSAGES_FIELD).getText());

		// Should not display any message
		MessagesBean.SHOW_SUMMARY_MESSAGES = false;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.ONE);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGES should NOT be rendered as showDetail and showSummary are false", "", driver.findElement(MESSAGES_FIELD).getText());
	}

	@Test
	@RunAsClient
	public void testMessagesGlobalOnly(@Drone WebDriver driver) throws Exception {
		MessagesBean.setDefaults();
		driver.get(portalURL.toString());

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.GLOBAL);
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.TWO);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should NOT be rendered.", "", driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
				MessagesBean.TWO_OK_DETAIL, driver.findElement(MESSAGE_TWO_FIELD).getText());

		assertTrue("MESSAGES should show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_OK_SUMMARY).apply(driver));

		assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_SUMMARY).apply(driver));

		assertFalse("MESSAGES should NOT show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_DETAIL).apply(driver));

		MessagesBean.GLOBAL_ONLY_MESSAGES = true;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.GLOBAL);
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.TWO);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should NOT be rendered.", "", driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
				MessagesBean.TWO_OK_DETAIL, driver.findElement(MESSAGE_TWO_FIELD).getText());

		assertFalse("MESSAGES should NOT show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_OK_SUMMARY).apply(driver));

		assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_SUMMARY).apply(driver));

		assertFalse("MESSAGES should NOT show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_DETAIL).apply(driver));

		MessagesBean.SHOW_DETAIL_MESSAGES= true;

		driver.findElement(INPUT_ONE_FIELD).clear();
		driver.findElement(INPUT_ONE_FIELD).sendKeys(MessagesBean.GLOBAL);
		driver.findElement(INPUT_TWO_FIELD).clear();
		driver.findElement(INPUT_TWO_FIELD).sendKeys(MessagesBean.TWO);
		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("MESSAGE ONE should NOT be rendered.", "", driver.findElement(MESSAGE_ONE_FIELD).getText());

		assertEquals("MESSAGE TWO should be rendered with DETAIL message: " + MessagesBean.TWO_OK_DETAIL,
				MessagesBean.TWO_OK_DETAIL, driver.findElement(MESSAGE_TWO_FIELD).getText());

		assertFalse("MESSAGES should NOT show SUMMARY message: " + MessagesBean.TWO_OK_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.TWO_OK_SUMMARY).apply(driver));

		assertTrue("MESSAGES should show GLOBAL SUMMARY message: " + MessagesBean.GLOBAL_SUMMARY,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_SUMMARY).apply(driver));

		assertTrue("MESSAGES should show GLOBAL DETAIL message: " + MessagesBean.GLOBAL_DETAIL,
				ExpectedConditions.textToBePresentInElement(MESSAGES_FIELD, MessagesBean.GLOBAL_DETAIL).apply(driver));
	}
}
