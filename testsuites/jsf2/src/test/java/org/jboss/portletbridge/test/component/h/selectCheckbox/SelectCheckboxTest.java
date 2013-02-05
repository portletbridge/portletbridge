package org.jboss.portletbridge.test.component.h.selectCheckbox;

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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.List;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@PortalTest
public class SelectCheckboxTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(SelectCheckboxTest.class, true);
        deployment.archive()
                .createFacesPortlet("SelectCheckbox", "Select Checkbox Portlet", "selectcheckbox.xhtml")
                .addAsWebResource("pages/component/h/selectCheckbox/selectcheckbox.xhtml", "selectcheckbox.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(SelectCheckboxBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    SelectCheckboxPage page;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testSelectCheckboxGenderNotRendered() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains ACCEPT checkbox element.", page.getAcceptCheckbox().isDisplayed());

        page.getGenderCheckbox().isDisplayed();
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testSelectCheckboxColorsNotRendered() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains ACCEPT checkbox element.", page.getAcceptCheckbox().isDisplayed());

        page.getColorsCheckbox().isDisplayed();
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxAjax() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertTrue("Check that page contains GENDER checkbox element.", page.getGenderCheckbox().isDisplayed());
        assertTrue("Check that page contains COLORS checkbox element.", page.getColorsCheckbox().isDisplayed());
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxRequired() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertTrue("Check that page contains GENDER checkbox element.", page.getGenderCheckbox().isDisplayed());

        page.getSubmitButton().click();

        assertTrue("Check that MESSAGES has indication about missing gender.",
                page.getMessages().getText().contains("You must select a gender."));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxValidator() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertTrue("Check that page contains GENDER checkbox element.", page.getGenderCheckbox().isDisplayed());

        // Select both genders
        page.getGenderOptionMale().click();
        page.getGenderOptionFemale().click();

        page.getSubmitButton().click();

        assertTrue("Check that MESSAGES has indication about multiple gender selected.",
                page.getMessages().getText().contains("Only one gender can be selected."));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxFromArray() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertTrue("Check that page contains COLORS checkbox element.", page.getColorsCheckbox().isDisplayed());

        List<WebElement> options = page.getColorsCheckbox().findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            assertEquals("Check that item is valid and correct.", SelectCheckboxBean.colors[i], options.get(i).getAttribute("value"));
        }
    }

    @Test
    @RunAsClient
    public void testSelectOneRadio() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertFalse("Check that no option in AGE RADIO is selected by default.",
                page.getAgeOptionYoung().isSelected() &&
                page.getAgeOptionAdult().isSelected() &&
                page.getAgeOptionSenior().isSelected());

        // Select "Young"
        page.getAgeOptionYoung().click();

        // Check that is the only option checked
        assertTrue(page.getAgeOptionYoung().isSelected());
        assertFalse(page.getAgeOptionAdult().isSelected());
        assertFalse(page.getAgeOptionSenior().isSelected());

        // Select "Senior"
        page.getAgeOptionSenior().click();

        // Check that is the only option checked
        assertFalse(page.getAgeOptionYoung().isSelected());
        assertFalse(page.getAgeOptionAdult().isSelected());
        assertTrue(page.getAgeOptionSenior().isSelected());

        // Select "Adult"
        page.getAgeOptionAdult().click();

        // Check that is the only option checked
        assertFalse(page.getAgeOptionYoung().isSelected());
        assertTrue(page.getAgeOptionAdult().isSelected());
        assertFalse(page.getAgeOptionSenior().isSelected());
    }

    @Test
    @RunAsClient
    public void testSelectOneMenuNoSelectionOption() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        assertTrue("Check that noSelectionOption is selected by default.", page.getContinentNoSel().isSelected());

        page.getSubmitButton().click();

        assertTrue("Check that missing continent message is present.",
                page.getMessages().getText().contains("somContinent: Validation Error: Value is not valid"));

        page.getContinentAsia().click();
        page.getSubmitButton().click();

        assertFalse("Check that missing continent message is not present.",
                page.getMessages().getText().contains("somContinent: Validation Error: Value is not valid"));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxSubmit() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        // Select male gender
        page.getGenderOptionMale().click();

        // Select "young" and correct to "adult"
        page.getAgeOptionYoung().click();
        page.getAgeOptionAdult().click();

        page.getContinentEurope().click();

        List<WebElement> options = page.getColorsCheckbox().findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            if (i % 2 != 0) {
                // Select even options
                options.get(i).click();
            }
        }

        page.getSubmitButton().click();

        assertTrue("Check that 'male' is present in result string.", page.getResult().getText().contains("male"));
        assertFalse("Check that 'female' is not present in result string.", page.getResult().getText().contains("female"));

        assertFalse("Check that 'young' is not present in result string.", page.getResult().getText().contains("young"));
        assertTrue("Check that 'adult' is present in result string.", page.getResult().getText().contains("adult"));
        assertFalse("Check that 'senior' is not present in result string.", page.getResult().getText().contains("senior"));

        for (int i = 0; i < SelectCheckboxBean.colors.length; i++) {
            if (i % 2 != 0) {
                assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.",
                        page.getResult().getText().contains(SelectCheckboxBean.colors[i]));
            } else {
                assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.",
                        page.getResult().getText().contains(SelectCheckboxBean.colors[i]));
            }
        }
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxSubmitAjax() throws Exception {
        browser.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        guardXhr(page.getAcceptCheckbox()).click();

        // Select male gender
        page.getGenderOptionMale().click();

        // Select "young" and correct to "adult"
        page.getAgeOptionYoung().click();
        page.getAgeOptionAdult().click();

        page.getContinentEurope().click();

        List<WebElement> options = page.getColorsCheckbox().findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            if (i % 2 != 0) {
                // Select even options
                options.get(i).click();
            }
        }

        guardXhr(page.getAjaxButton()).click();

        assertTrue("Check that 'male' is present in result string.", page.getResult().getText().contains("male"));
        assertFalse("Check that 'female' is not present in result string.", page.getResult().getText().contains("female"));

        assertFalse("Check that 'young' is not present in result string.", page.getResult().getText().contains("young"));
        assertTrue("Check that 'adult' is present in result string.", page.getResult().getText().contains("adult"));
        assertFalse("Check that 'senior' is not present in result string.", page.getResult().getText().contains("senior"));

        for (int i = 0; i < SelectCheckboxBean.colors.length; i++) {
            if (i % 2 != 0) {
                assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.",
                        page.getResult().getText().contains(SelectCheckboxBean.colors[i]));
            } else {
                assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.",
                        page.getResult().getText().contains(SelectCheckboxBean.colors[i]));
            }
        }
    }
}
