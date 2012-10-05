package org.jboss.portletbridge.test.component.h.selectCheckbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(Arquillian.class)
public class SelectCheckboxTest {

	@Deployment()
	public static WebArchive createDeployment() {
		return TestDeployment.createDeploymentWithAll()
				.addAsWebResource("pages/component/h/selectCheckbox/selectcheckbox.xhtml", "home.xhtml")
				.addAsWebResource("resources/ajax.png", "ajax.png")
				.addClass(SelectCheckboxBean.class)
				.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
	}

	protected static final By ACCEPT_CHECKBOX = By.xpath("//input[contains(@id,':sbcAccepted')]");

	protected static final By GENDER_CHECKBOX = By.xpath("//input[contains(@id,':smmGender')]");
	protected static final By GENDER_OPTION_MALE = By.xpath("//input[contains(@id,':smmGender:0')]");
	protected static final By GENDER_OPTION_FEMALE = By.xpath("//input[contains(@id,':smmGender:1')]");

	protected static final By COLORS_CHECKBOX = By.xpath("//select[contains(@id,':smlColors')]");

	protected static final By SUBMIT_BUTTON = By.xpath("//input[contains(@id,':submit')]");
	protected static final By AJAX_BUTTON = By.xpath("//input[contains(@id,':ajax')]");

	protected static final By MESSAGES = By.xpath("//*[contains(@id,':messages')]");
	protected static final By RESULT = By.xpath("//*[contains(@id,':result')]");

	@ArquillianResource
	@PortalURL
	URL portalURL;

	@Test
	@RunAsClient
	public void testSelectCheckboxRender(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains ACCEPT checkbox element.", driver.findElement(ACCEPT_CHECKBOX));

		try {
			assertNull("Check that page does not contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));
		} catch (NoSuchElementException nsee) {
			// expected
		}

		try {
			assertNull("Check that page does not contains COLORS checkbox element.", driver.findElement(COLORS_CHECKBOX));
		} catch (NoSuchElementException nsee) {
			// expected
		}
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxAjax(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(GENDER_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));
		assertNotNull("Check that page contains COLORS checkbox element.", driver.findElement(COLORS_CHECKBOX));
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxRequired(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(GENDER_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));

		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("Check that MESSAGES has indication about missing gender.", 
				"You must select a gender.", driver.findElement(MESSAGES).getText());
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxValidator(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(GENDER_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));

		// Select both genders
		driver.findElement(GENDER_OPTION_MALE).click();
		driver.findElement(GENDER_OPTION_FEMALE).click();

		driver.findElement(SUBMIT_BUTTON).click();

		assertEquals("Check that MESSAGES has indication about missing gender.", 
				"Only one gender can be selected.", driver.findElement(MESSAGES).getText());
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxFromArray(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(COLORS_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		assertNotNull("Check that page contains COLOR checkbox element.", driver.findElement(COLORS_CHECKBOX));

		List<WebElement> options = driver.findElement(COLORS_CHECKBOX).findElements(By.xpath("option"));
		for(int i = 0; i < options.size(); i++) {
			assertEquals("Check that item with value '" + SelectCheckboxBean.colors[i] + "' is present and in correct order.", 
					SelectCheckboxBean.colors[i], options.get(i).getAttribute("value"));
		}
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxSubmit(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(GENDER_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		// Select female gender
		driver.findElement(GENDER_OPTION_MALE).click();

		List<WebElement> options = driver.findElement(COLORS_CHECKBOX).findElements(By.xpath("option"));
		for(int i = 0; i < options.size(); i++) {
			if(i % 2 != 0) {
				// Select even options
				options.get(i).click();
			}
		}
		
		driver.findElement(SUBMIT_BUTTON).click();
		
		assertTrue("Check that 'male' is present in result string.", driver.findElement(RESULT).getText().contains("male"));
		assertFalse("Check that 'female' is not present in result string.", driver.findElement(RESULT).getText().contains("female"));

		for(int i = 0; i < SelectCheckboxBean.colors.length; i++) {
			if(i % 2 != 0) {
				assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.", 
						driver.findElement(RESULT).getText().contains(SelectCheckboxBean.colors[i]));
			}
			else {
				assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.", 
						driver.findElement(RESULT).getText().contains(SelectCheckboxBean.colors[i]));
			}
		}
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxSubmitAjax(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(GENDER_CHECKBOX);
				if (toReturn.isDisplayed()) {
					return toReturn;
				}
				return null;
			}
		});

		// Select female gender
		driver.findElement(GENDER_OPTION_MALE).click();

		List<WebElement> options = driver.findElement(COLORS_CHECKBOX).findElements(By.xpath("option"));
		for(int i = 0; i < options.size(); i++) {
			if(i % 2 != 0) {
				// Select even options
				options.get(i).click();
			}
		}
		
		driver.findElement(AJAX_BUTTON).click();
		
		// Wait for AJAX to do it's work
		new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement toReturn = driver.findElement(RESULT);
				if (toReturn.getText().contains("You are")) {
					return toReturn;
				}
				return null;
			}
		});

		assertTrue("Check that 'male' is present in result string.", driver.findElement(RESULT).getText().contains("male"));
		assertFalse("Check that 'female' is not present in result string.", driver.findElement(RESULT).getText().contains("female"));

		for(int i = 0; i < SelectCheckboxBean.colors.length; i++) {
			if(i % 2 != 0) {
				assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.", 
						driver.findElement(RESULT).getText().contains(SelectCheckboxBean.colors[i]));
			}
			else {
				assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.", 
						driver.findElement(RESULT).getText().contains(SelectCheckboxBean.colors[i]));
			}
		}
	}
}
