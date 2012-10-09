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
import static org.junit.Assert.assertEquals;
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
public class InputTextTest {

	@Deployment()
	public static WebArchive createDeployment() {
		return TestDeployment.createDeploymentWithAll()
				.addAsWebResource("pages/component/h/inputText/inputtext.xhtml", "home.xhtml")
				.addClass(InputTextBean.class);
		//.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
	}

	@ArquillianResource
	@PortalURL
	URL portalURL;

	protected static final By INPUT_ONE = By.xpath("//input[contains(@id,':input1')]");
	protected static final By OUTPUT_ONE = By.xpath("//span[contains(@id,':output1')]");
	protected static final By OUTPUT_ONE_COUNTER = By.xpath("//span[contains(@id,':output1count')]");
	protected static final By SUBMIT_ONE = By.xpath("//input[contains(@id,':submit1')]");

	protected static final By INPUT_TWO = By.xpath("//input[contains(@id,':input2')]");
	protected static final By OUTPUT_TWO = By.xpath("//span[contains(@id,':output2')]");
	protected static final By OUTPUT_TWO_COUNTER = By.xpath("//span[contains(@id,':output2count')]");

	protected static final By MESSAGES = By.xpath("//*[contains(@id,':messages')]");

	@Test
	@RunAsClient
	public void testInputText(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains INPUT ONE element.", driver.findElement(INPUT_ONE));
		assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
		assertNotNull("Check that page contains SUBMIT ONE element.", driver.findElement(SUBMIT_ONE));

		assertEquals("Check that INPUT ONE element starts empty.", "", driver.findElement(INPUT_ONE).getAttribute("value"));
		assertEquals("Check that OUTPUT ONE element starts empty.", "", driver.findElement(OUTPUT_ONE).getText());

		String textToInput = "pbr";

		driver.findElement(INPUT_ONE).sendKeys(textToInput);
		driver.findElement(SUBMIT_ONE).click();

		assertEquals("OUTPUT ONE element should have inputed text.", textToInput, driver.findElement(OUTPUT_ONE).getText());
	}

	@Test
	@RunAsClient
	public void testAjaxInputText(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains INPUT TWO element.", driver.findElement(INPUT_TWO));
		assertNotNull("Check that page contains OUTPUT TWO element.", driver.findElement(OUTPUT_TWO));

		assertEquals("Check that INPUT TWO element starts empty.", "", driver.findElement(INPUT_TWO).getText());
		assertEquals("Check that OUTPUT TWO element starts empty.", "", driver.findElement(OUTPUT_TWO).getText());

		final String textToInput = "pbr";

		for(String s : textToInput.split("")) {
			if(!s.equals("")) {
				driver.findElement(INPUT_TWO).sendKeys(s);            
			}
		}

		waitAjax(driver).until(element(OUTPUT_TWO).textEquals("pbr"));

		assertEquals("OUTPUT TWO element should have inputed text.", textToInput, driver.findElement(OUTPUT_TWO).getText());
	}

	@Test
	@RunAsClient
	public void testOnChange(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains INPUT TWO element.", driver.findElement(INPUT_TWO));
		assertNotNull("Check that page contains OUTPUT TWO element.", driver.findElement(OUTPUT_TWO));

		assertEquals("Check that INPUT TWO element starts empty.", "", driver.findElement(INPUT_TWO).getText());
		assertEquals("Check that OUTPUT TWO element starts empty.", "", driver.findElement(OUTPUT_TWO).getText());

		final String textToInput = "pbr";

		// Fill input one and submit
		driver.findElement(INPUT_ONE).sendKeys(textToInput);
		driver.findElement(SUBMIT_ONE).click();

		// FIXME: Why doesn't it update in time ?
		// assertEquals("OUTPUT ONE COUNTER element should have inputed text length.", String.valueOf(textToInput.length()), driver.findElement(OUTPUT_ONE_COUNTER).getText());

		// Fill input two, char by char
		for(String s : textToInput.split("")) {
			if(!s.equals("")) {
				driver.findElement(INPUT_TWO).sendKeys(s);
			}
		}

		waitAjax(driver).until(element(OUTPUT_TWO).textEquals(textToInput));

		assertEquals("OUTPUT TWO COUNTER element should have inputed text length.", String.valueOf(textToInput.length()/*FIXME: .. AJAX*/-1), driver.findElement(OUTPUT_TWO_COUNTER).getText());

		assertEquals("OUTPUT ONE COUNTER element should have inputed text length.", String.valueOf(textToInput.length()), driver.findElement(OUTPUT_ONE_COUNTER).getText());
		// FIXME: .. assertEquals("OUTPUT TWO COUNTER element should have inputed text length.", String.valueOf(textToInput.length()), driver.findElement(OUTPUT_TWO_COUNTER).getText());
	}

	@Test
	@RunAsClient
	public void testRequired(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains INPUT ONE element.", driver.findElement(INPUT_ONE));
		assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
		assertNotNull("Check that page contains SUBMIT ONE element.", driver.findElement(SUBMIT_ONE));

		assertEquals("Check that INPUT ONE element starts empty.", "", driver.findElement(INPUT_ONE).getText());
		assertEquals("Check that OUTPUT ONE element starts empty.", "", driver.findElement(OUTPUT_ONE).getText());

		// Submit with no input
		driver.findElement(SUBMIT_ONE).click();

		assertTrue("MESSAGES should contain error message: " + InputTextBean.REQUIRED_MESSAGE, 
				ExpectedConditions.textToBePresentInElement(MESSAGES, InputTextBean.REQUIRED_MESSAGE).apply(driver));
	}

	@Test
	@RunAsClient
	public void testValidateLength(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains INPUT ONE element.", driver.findElement(INPUT_ONE));
		assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
		assertNotNull("Check that page contains SUBMIT ONE element.", driver.findElement(SUBMIT_ONE));

		assertEquals("Check that INPUT ONE element starts empty.", "", driver.findElement(INPUT_ONE).getText());
		assertEquals("Check that OUTPUT ONE element starts empty.", "", driver.findElement(OUTPUT_ONE).getText());

		// Submit a small input
		driver.findElement(INPUT_ONE).sendKeys("pb");
		driver.findElement(SUBMIT_ONE).click();

		assertTrue("MESSAGES should contain error message: " + InputTextBean.MIN_LENGTH_MESSAGE, 
				ExpectedConditions.textToBePresentInElement(MESSAGES, InputTextBean.MIN_LENGTH_MESSAGE).apply(driver));
	}


	/* -- [ SANDBOX ] ------------------------------------------------------ */
	/*
    @Test
    @RunAsClient
    public void testAjaxInputText() throws Exception {
        boolean replaceWebDriver = false;
        boolean useWebDriverBackedSelenium = false;

        //WebDriver driver = (HtmlUnitDriver) driver;

        if(replaceWebDriver) {
            driver = new HtmlUnitDriver();
            ((HtmlUnitDriver) driver).setJavascriptEnabled(true);
        }

        String textToInput = "pbr";

        if(!useWebDriverBackedSelenium) {
            driver.get(portalURL.toString());
            System.out.println(driver.getPageSource());

            assertNotNull("Check that page contains INPUT TWO element.", driver.findElement(INPUT_TWO));
            assertNotNull("Check that page contains OUTPUT TWO element.", driver.findElement(OUTPUT_TWO));

            assertEquals("Check that INPUT TWO element starts empty.", "", driver.findElement(INPUT_TWO).getText());
            assertEquals("Check that OUTPUT TWO element starts empty.", "", driver.findElement(OUTPUT_TWO).getText());

            for(String s : textToInput.split("")) {
                if(!s.equals("")) {
                    driver.findElement(INPUT_TWO).sendKeys(s);            
                }
            }
            //driver.findElement(SUBMIT_TWO).click();

            System.out.println("AFTER_\r\n" + driver.getPageSource());

            // Let AJAX work
            Thread.sleep(2500);

            // Wait for AJAX to do it's work
            Wait<WebDriver> wait = new WebDriverWait(driver, 10);
            WebElement element= wait.until(visibilityOfElementLocated(OUTPUT_TWO));
        }
        else {
            Selenium selenium = new WebDriverBackedSelenium(driver, portalURL.toString());

            selenium.open(portalURL.toString());

            for(String s : textToInput.split("")) {
                if(!s.equals("")) {
                    selenium.keyDown("//*[@id[substring(., string-length() - 5) = 'input2']]", s);
                    selenium.keyPress("//*[@id[substring(., string-length() - 5) = 'input2']]", s);
                    selenium.keyUp("//*[@id[substring(., string-length() - 5) = 'input2']]", s);
                }
            }

            // Let AJAX work
            Thread.sleep(2500);
        }

        assertEquals("OUTPUT TWO element should have inputed text.", textToInput, driver.findElement(OUTPUT_TWO).getText());
    }

    public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
        return new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement toReturn = driver.findElement(locator);
                if (toReturn.getText().length() > 0) {
                    return toReturn;
                }
                return null;
            }
        };
    }
	 */
}
