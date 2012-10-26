package org.jboss.portletbridge.test.component.h.selectCheckbox;

import static org.jboss.arquillian.graphene.Graphene.element;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class SelectCheckboxTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/selectCheckbox/selectcheckbox.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(SelectCheckboxBean.class)
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @FindBy(xpath = "//input[contains(@id,':sbcAccepted')]")
    private WebElement acceptCheckbox;

    @FindBy(xpath = "//input[contains(@id,':smcGender')]")
    private WebElement genderCheckbox;
    @FindBy(xpath = "//input[contains(@id,':smcGender:0')]")
    private WebElement genderOptionMale;
    @FindBy(xpath = "//input[contains(@id,':smcGender:1')]")
    private WebElement genderOptionFemale;

    @FindBy(xpath = "//input[contains(@id,':sorAge:0')]")
    private WebElement ageOptionYoung;
    @FindBy(xpath = "//input[contains(@id,':sorAge:1')]")
    private WebElement ageOptionAdult;
    @FindBy(xpath = "//input[contains(@id,':sorAge:2')]")
    private WebElement ageOptionSenior;

    @FindBy(xpath = "//option[contains(@value,'no_continent')]")
    private WebElement continentNoSel;
    @FindBy(xpath = "//option[contains(@value,'africa')]")
    private WebElement continentAfrica;
    //@FindBy(xpath = "//option[contains(@value,'america')]")
    //private WebElement continentAmerica;
    @FindBy(xpath = "//option[contains(@value,'asia')]")
    private WebElement continentAsia;
    //@FindBy(xpath = "//option[contains(@value,'australia')]")
    //private WebElement continentAustralia;
    @FindBy(xpath = "//option[contains(@value,'europe')]")
    private WebElement continentEurope;

    @FindBy(xpath = "//select[contains(@id,':smlColors')]")
    private WebElement colorsCheckbox;

    @FindBy(xpath = "//input[contains(@id,':submit')]")
    private WebElement submitButton;
    @FindBy(xpath = "//input[contains(@id,':ajax')]")
    private WebElement ajaxButton;

    @FindBy(xpath = "//*[contains(@id,':messages')]")
    private WebElement messages;
    @FindBy(xpath = "//*[contains(@id,':result')]")
    private WebElement result;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Test
    @RunAsClient
    public void testSelectCheckboxRender(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains ACCEPT checkbox element.",
                Graphene.element(acceptCheckbox).isVisible().apply(driver));

        assertFalse("Check that page does not contains GENDER checkbox element.",
                Graphene.element(genderCheckbox).isVisible().apply(driver));

        assertFalse("Check that page does not contains COLORS checkbox element.",
                Graphene.element(colorsCheckbox).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        assertTrue("Check that page contains GENDER checkbox element.",
                Graphene.element(genderCheckbox).isVisible().apply(driver));
        assertTrue("Check that page contains COLORS checkbox element.",
                Graphene.element(colorsCheckbox).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxRequired(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        assertTrue("Check that page contains GENDER checkbox element.",
                Graphene.element(genderCheckbox).isVisible().apply(driver));

        submitButton.click();

        assertTrue("Check that MESSAGES has indication about missing gender.",
                Graphene.element(messages).textContains("You must select a gender.").apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxValidator(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        assertTrue("Check that page contains GENDER checkbox element.",
                Graphene.element(genderCheckbox).isVisible().apply(driver));

        // Select both genders
        genderOptionMale.click();
        genderOptionFemale.click();

        submitButton.click();

        assertTrue("Check that MESSAGES has indication about multiple gender selected.",
                Graphene.element(messages).textContains("Only one gender can be selected.").apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxFromArray(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        assertTrue("Check that page contains COLOR checkbox element.",
                Graphene.element(colorsCheckbox).isVisible().apply(driver));

        List<WebElement> options = colorsCheckbox.findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            assertTrue("Check that item with value '" + SelectCheckboxBean.colors[i] + "' is present and in correct order.",
                    Graphene.attribute(options.get(i), "value").valueEquals(SelectCheckboxBean.colors[i]).apply(driver));
        }
    }

    @Test
    @RunAsClient
    public void testSelectOneRadio(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(ageOptionYoung).isPresent());

        assertTrue("Check that no option in AGE RADIO is selected by default.",
                Graphene.element(ageOptionYoung).not().isSelected().apply(driver) &&
                Graphene.element(ageOptionAdult).not().isSelected().apply(driver) &&
                Graphene.element(ageOptionSenior).not().isSelected().apply(driver));

        // Select "Young"
        ageOptionYoung.click();

        // Check that is the only option checked
        assertTrue(Graphene.element(ageOptionYoung).isSelected().apply(driver));
        assertFalse(Graphene.element(ageOptionAdult).isSelected().apply(driver));
        assertFalse(Graphene.element(ageOptionSenior).isSelected().apply(driver));

        // Select "Senior"
        ageOptionSenior.click();

        // Check that is the only option checked
        assertFalse(Graphene.element(ageOptionYoung).isSelected().apply(driver));
        assertFalse(Graphene.element(ageOptionAdult).isSelected().apply(driver));
        assertTrue(Graphene.element(ageOptionSenior).isSelected().apply(driver));

        // Select "Adult"
        ageOptionAdult.click();

        // Check that is the only option checked
        assertFalse(Graphene.element(ageOptionYoung).isSelected().apply(driver));
        assertTrue(Graphene.element(ageOptionAdult).isSelected().apply(driver));
        assertFalse(Graphene.element(ageOptionSenior).isSelected().apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectOneMenuNoSelectionOption(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(continentAfrica).isPresent());

        assertTrue("Check that noSelectionOption is selected by default.",
                Graphene.element(continentNoSel).isSelected().apply(driver));

        submitButton.click();
        assertTrue("Check that missing continent message is present.",
                Graphene.element(messages).textContains("somContinent: Validation Error: Value is not valid").apply(driver));

        continentAsia.click();
        submitButton.click();
        assertFalse("Check that missing continent message is not present.",
                Graphene.element(messages).textContains("somContinent: Validation Error: Value is not valid").apply(driver));
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxSubmit(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        // Select male gender
        genderOptionMale.click();

        // Select "young" and correct to "adult"
        ageOptionYoung.click();
        ageOptionAdult.click();

        continentEurope.click();

        List<WebElement> options = colorsCheckbox.findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            if (i % 2 != 0) {
                // Select even options
                options.get(i).click();
            }
        }

        submitButton.click();

        assertTrue("Check that 'male' is present in result string.",
                Graphene.element(result).textContains("male").apply(driver));
        assertFalse("Check that 'female' is not present in result string.",
                Graphene.element(result).textContains("female").apply(driver));

        assertFalse("Check that 'young' is not present in result string.",
                Graphene.element(result).textContains("young").apply(driver));
        assertTrue("Check that 'adult' is present in result string.",
                Graphene.element(result).textContains("adult").apply(driver));
        assertFalse("Check that 'senior' is not present in result string.",
                Graphene.element(result).textContains("senior").apply(driver));

        for (int i = 0; i < SelectCheckboxBean.colors.length; i++) {
            if (i % 2 != 0) {
                assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.",
                        Graphene.element(result).textContains(SelectCheckboxBean.colors[i]).apply(driver));
            } else {
                assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.",
                        Graphene.element(result).textContains(SelectCheckboxBean.colors[i]).apply(driver));
            }
        }
    }

    @Test
    @RunAsClient
    public void testSelectCheckboxSubmitAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // at first only accept is shown, must be clicked to show remaining
        acceptCheckbox.click();

        waitAjax(driver).until(element(genderCheckbox).isPresent());

        // Select male gender
        genderOptionMale.click();

        // Select "young" and correct to "adult"
        ageOptionYoung.click();
        ageOptionAdult.click();

        continentEurope.click();

        List<WebElement> options = colorsCheckbox.findElements(By.xpath("option"));
        for (int i = 0; i < options.size(); i++) {
            if (i % 2 != 0) {
                // Select even options
                options.get(i).click();
            }
        }

        ajaxButton.click();

        waitAjax(driver).until(element(result).textContains("You are"));

        assertTrue("Check that 'male' is present in result string.",
                Graphene.element(result).textContains("male").apply(driver));
        assertFalse("Check that 'female' is not present in result string.",
                Graphene.element(result).textContains("female").apply(driver));

        assertFalse("Check that 'young' is not present in result string.",
                Graphene.element(result).textContains("young").apply(driver));
        assertTrue("Check that 'adult' is present in result string.",
                Graphene.element(result).textContains("adult").apply(driver));
        assertFalse("Check that 'senior' is not present in result string.",
                Graphene.element(result).textContains("senior").apply(driver));

        for (int i = 0; i < SelectCheckboxBean.colors.length; i++) {
            if (i % 2 != 0) {
                assertTrue("Check that color '" + SelectCheckboxBean.colors[i] + "' is present in result string.",
                        Graphene.element(result).textContains(SelectCheckboxBean.colors[i]).apply(driver));
            } else {
                assertFalse("Check that color '" + SelectCheckboxBean.colors[i] + "' is not present in result string.",
                        Graphene.element(result).textContains(SelectCheckboxBean.colors[i]).apply(driver));
            }
        }
    }
}
