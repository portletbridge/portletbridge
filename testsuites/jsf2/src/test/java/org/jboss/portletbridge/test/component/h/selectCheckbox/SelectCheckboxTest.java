package org.jboss.portletbridge.test.component.h.selectCheckbox;

import static org.jboss.arquillian.graphene.Graphene.element;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
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

	protected static final By GENDER_CHECKBOX = By.xpath("//input[contains(@id,':smcGender')]");
	protected static final By GENDER_OPTION_MALE = By.xpath("//input[contains(@id,':smcGender:0')]");
	protected static final By GENDER_OPTION_FEMALE = By.xpath("//input[contains(@id,':smcGender:1')]");

	protected static final By AGE_OPTION_YOUNG = By.xpath("//input[contains(@id,':sorAge:0')]");
	protected static final By AGE_OPTION_ADULT = By.xpath("//input[contains(@id,':sorAge:1')]");
	protected static final By AGE_OPTION_SENIOR = By.xpath("//input[contains(@id,':sorAge:2')]");

	protected static final By CONTINENT_NO_SEL = By.xpath("//option[contains(@value,'no_continent')]");
	protected static final By CONTINENT_AFRICA = By.xpath("//option[contains(@value,'africa')]");
	protected static final By CONTINENT_AMERICA = By.xpath("//option[contains(@value,'america')]");
	protected static final By CONTINENT_ASIA = By.xpath("//option[contains(@value,'asia')]");
	protected static final By CONTINENT_AUSTRALIA = By.xpath("//option[contains(@value,'australia')]");
	protected static final By CONTINENT_EUROPE = By.xpath("//option[contains(@value,'europe')]");
	
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

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));
		assertNotNull("Check that page contains COLORS checkbox element.", driver.findElement(COLORS_CHECKBOX));
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxRequired(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));

		driver.findElement(SUBMIT_BUTTON).click();

		assertTrue("Check that MESSAGES has indication about missing gender.", 
				driver.findElement(MESSAGES).getText().contains("You must select a gender."));
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxValidator(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		assertNotNull("Check that page contains GENDER checkbox element.", driver.findElement(GENDER_CHECKBOX));

		// Select both genders
		driver.findElement(GENDER_OPTION_MALE).click();
		driver.findElement(GENDER_OPTION_FEMALE).click();

		driver.findElement(SUBMIT_BUTTON).click();

		assertTrue("Check that MESSAGES has indication about multiple gender selected.", 
				driver.findElement(MESSAGES).getText().contains("Only one gender can be selected."));
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxFromArray(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		assertNotNull("Check that page contains COLOR checkbox element.", driver.findElement(COLORS_CHECKBOX));

		List<WebElement> options = driver.findElement(COLORS_CHECKBOX).findElements(By.xpath("option"));
		for(int i = 0; i < options.size(); i++) {
			assertEquals("Check that item with value '" + SelectCheckboxBean.colors[i] + "' is present and in correct order.", 
					SelectCheckboxBean.colors[i], options.get(i).getAttribute("value"));
		}
	}

	@Test
	@RunAsClient
	public void testSelectOneRadio(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(AGE_OPTION_YOUNG).isPresent());

		assertTrue("Check that no option in AGE RADIO is selected by default.", 
				!driver.findElement(AGE_OPTION_YOUNG).isSelected() && !driver.findElement(AGE_OPTION_ADULT).isSelected() && 
				!driver.findElement(AGE_OPTION_SENIOR).isSelected());
		
		// Select "Young"
		driver.findElement(AGE_OPTION_YOUNG).click();
		
		// Check that is the only option checked
		assertTrue(driver.findElement(AGE_OPTION_YOUNG).isSelected());
		assertFalse(driver.findElement(AGE_OPTION_ADULT).isSelected());
		assertFalse(driver.findElement(AGE_OPTION_SENIOR).isSelected());
		
		// Select "Senior"
		driver.findElement(AGE_OPTION_SENIOR).click();
		
		// Check that is the only option checked
		assertFalse(driver.findElement(AGE_OPTION_YOUNG).isSelected());
		assertFalse(driver.findElement(AGE_OPTION_ADULT).isSelected());
		assertTrue(driver.findElement(AGE_OPTION_SENIOR).isSelected());
		
		// Select "Adult"
		driver.findElement(AGE_OPTION_ADULT).click();
		
		// Check that is the only option checked
		assertFalse(driver.findElement(AGE_OPTION_YOUNG).isSelected());
		assertTrue(driver.findElement(AGE_OPTION_ADULT).isSelected());
		assertFalse(driver.findElement(AGE_OPTION_SENIOR).isSelected());
	}

	@Test
	@RunAsClient
	public void testSelectOneMenuNoSelectionOption(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(CONTINENT_AFRICA).isPresent());
		
		assertTrue("Check that noSelectionOption is selected by default.", driver.findElement(CONTINENT_NO_SEL).isSelected());

		driver.findElement(SUBMIT_BUTTON).click();
		assertTrue("Check that missing continent message is present.", driver.findElement(MESSAGES).getText().contains("somContinent: Validation Error: Value is not valid"));

		driver.findElement(CONTINENT_ASIA).click();
		driver.findElement(SUBMIT_BUTTON).click();
		assertFalse("Check that missing continent message is not present.", driver.findElement(MESSAGES).getText().contains("somContinent: Validation Error: Value is not valid"));
	}

	@Test
	@RunAsClient
	public void testSelectCheckboxSubmit(@Drone WebDriver driver) throws Exception {
		driver.get(portalURL.toString());

		// at first only accept is shown, must be clicked to show remaining
		driver.findElement(ACCEPT_CHECKBOX).click();

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		// Select male gender
		driver.findElement(GENDER_OPTION_MALE).click();

		// Select "young" and correct to "adult"
		driver.findElement(AGE_OPTION_YOUNG).click();
		driver.findElement(AGE_OPTION_ADULT).click();

		driver.findElement(CONTINENT_EUROPE).click();

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

		assertFalse("Check that 'young' is not present in result string.", driver.findElement(RESULT).getText().contains("young"));
		assertTrue("Check that 'adult' is present in result string.", driver.findElement(RESULT).getText().contains("adult"));
		assertFalse("Check that 'senior' is not present in result string.", driver.findElement(RESULT).getText().contains("senior"));

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

		waitAjax(driver).until(element(GENDER_CHECKBOX).isPresent());

		// Select male gender
		driver.findElement(GENDER_OPTION_MALE).click();

		// Select "young" and correct to "adult"
		driver.findElement(AGE_OPTION_YOUNG).click();
		driver.findElement(AGE_OPTION_ADULT).click();

		driver.findElement(CONTINENT_EUROPE).click();

		List<WebElement> options = driver.findElement(COLORS_CHECKBOX).findElements(By.xpath("option"));
		for(int i = 0; i < options.size(); i++) {
			if(i % 2 != 0) {
				// Select even options
				options.get(i).click();
			}
		}
		
		driver.findElement(AJAX_BUTTON).click();
		
		waitAjax(driver).until(element(RESULT).textContains("You are"));

		assertTrue("Check that 'male' is present in result string.", driver.findElement(RESULT).getText().contains("male"));
		assertFalse("Check that 'female' is not present in result string.", driver.findElement(RESULT).getText().contains("female"));

		assertFalse("Check that 'young' is not present in result string.", driver.findElement(RESULT).getText().contains("young"));
		assertTrue("Check that 'adult' is present in result string.", driver.findElement(RESULT).getText().contains("adult"));
		assertFalse("Check that 'senior' is not present in result string.", driver.findElement(RESULT).getText().contains("senior"));

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
