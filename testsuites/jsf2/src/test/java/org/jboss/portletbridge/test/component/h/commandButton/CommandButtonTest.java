package org.jboss.portletbridge.test.component.h.commandButton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

@RunWith(Arquillian.class)
public class CommandButtonTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/commandButton/commandbutton.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(CommandButtonBean.class);
        // .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    protected static final By SUBMIT_BUTTON = By.xpath("//input[contains(@id,':submit')]");
    protected static final By RESET_BUTTON = By.xpath("//input[contains(@id,':reset')]");
    protected static final By AJAX_BUTTON = By.xpath("//input[contains(@id,':ajax')]");
    protected static final By ALERT_BUTTON = By.xpath("//input[contains(@id,':alert')]");

    protected static final By OUTPUT_TEXT = By.xpath("//span[contains(@id,':output')]");
    protected static final By INPUT_TEXT = By.xpath("//input[contains(@id,':input')]");

    @Test
    @RunAsClient
    public void testCommandButtonTypes(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains SUBMIT button element.", driver.findElement(SUBMIT_BUTTON));
        assertNotNull("Check that page contains RESET button element.", driver.findElement(RESET_BUTTON));
        assertNotNull("Check that page contains TWEET button element.", driver.findElement(AJAX_BUTTON));
        assertNotNull("Check that page contains ALERT button element.", driver.findElement(ALERT_BUTTON));

        assertEquals("Check that SUBMIT button type is submit (default).", "submit", driver.findElement(SUBMIT_BUTTON)
                .getAttribute("type"));
        assertEquals("Check that RESET button type is reset.", "reset", driver.findElement(RESET_BUTTON).getAttribute("type"));
        assertEquals("Check that TWEET button type is image.", "image", driver.findElement(AJAX_BUTTON).getAttribute("type"));
        assertEquals("Check that ALERT button type is button.", "button", driver.findElement(ALERT_BUTTON).getAttribute("type"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonValue(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertEquals("Check that SUBMIT button value is '" + CommandButtonBean.SUBMIT_LABEL + "'.",
                CommandButtonBean.SUBMIT_LABEL, driver.findElement(SUBMIT_BUTTON).getAttribute("value"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonAction(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        int step = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));
        driver.findElement(SUBMIT_BUTTON).click();
        assertEquals("New value should be " + oldValue + "+" + step + " = " + (oldValue + step), new Integer(oldValue + step),
                Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));
    }

    @Test
    @RunAsClient
    public void testCommandButtonReset(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        String oldText = driver.findElement(INPUT_TEXT).getAttribute("value");
        assertFalse("Check that INPUT text has a value at start.", oldText.equals(""));

        String addedText = "00";
        driver.findElement(INPUT_TEXT).sendKeys(addedText);
        assertEquals("Check that INPUT text has a changed value after inputting '" + addedText + "'.", oldText + addedText,
                driver.findElement(INPUT_TEXT).getAttribute("value"));

        driver.findElement(RESET_BUTTON).click();
        assertEquals("Check that INPUT text has old value after RESET.", oldText,
                driver.findElement(INPUT_TEXT).getAttribute("value"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonSubmit(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldStep = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        driver.findElement(INPUT_TEXT).sendKeys("\u0008"); // delete
        driver.findElement(INPUT_TEXT).sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        driver.findElement(SUBMIT_BUTTON).click();
        assertEquals("New value for Step should be " + newStep, new Integer(newStep),
                Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value")));
        assertEquals("New value should be " + oldValue + "+" + newStep + " = " + (oldValue + newStep), new Integer(oldValue
                + newStep), Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));
        Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
    }

    // @Test
    // @RunAsClient
    // public void testCommandButtonOnClickJS(@Drone WebDriver driver) throws Exception {
    // driver.get(portalURL.toString());
    //
    // // click the submit a few times ...
    // for(int i = 0; i < 3; i++) {
    // driver.findElement(SUBMIT_BUTTON).click();
    // }
    //
    // String curValue = driver.findElement(OUTPUT_TEXT).getText();
    // driver.findElement(ALERT_BUTTON).click();
    // // FIXME: this test fails with HtmlUnitDriver as there's no alert() support
    // assertEquals("Check that Alert text is: " + "Current Value is " + curValue, "Current Value is " + curValue,
    // driver.switchTo().alert().getText());
    // }

    @Test
    @RunAsClient
    public void testCommandButtonAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Ajax only works after one *real* submit...
        driver.findElement(SUBMIT_BUTTON).click();

        String curURL = driver.getCurrentUrl();

        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        int step = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for (int i = 0; i < nTimes; i++) {
            driver.findElement(AJAX_BUTTON).click();
            // wait for ajax
            Thread.sleep(500);
        }

        assertEquals("New value should be " + oldValue + "+" + step + "*" + nTimes + " = " + (oldValue + step * nTimes),
                new Integer(oldValue + step * nTimes), Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));

        assertEquals("Check that URL is the same as this is an ajax request.", curURL, driver.getCurrentUrl());
    }
}
