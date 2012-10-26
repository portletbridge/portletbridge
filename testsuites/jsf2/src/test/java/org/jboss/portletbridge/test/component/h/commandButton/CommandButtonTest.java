package org.jboss.portletbridge.test.component.h.commandButton;

import static org.jboss.arquillian.graphene.Graphene.element;
import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.junit.Assert.assertEquals;
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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
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

    @FindBy(xpath = "//input[contains(@id,':submit')]")
    private WebElement submitButton;

    @FindBy(xpath = "//input[contains(@id,':reset')]")
    private WebElement resetButton;

    @FindBy(xpath = "//input[contains(@id,':ajax')]")
    private WebElement ajaxButton;

    @FindBy(xpath = "//input[contains(@id,':alert')]")
    private WebElement alertButton;

    @FindBy(xpath = "//span[contains(@id,':output')]")
    private WebElement outputText;

    @FindBy(xpath = "//input[contains(@id,':input')]")
    private WebElement inputText;

    @Test
    @RunAsClient
    public void testCommandButtonTypes(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains SUBMIT button element.",
                Graphene.element(submitButton).isVisible().apply(driver));
        assertTrue("Check that page contains RESET button element.",
                Graphene.element(resetButton).isVisible().apply(driver));
        assertTrue("Check that page contains AJAX button element.",
                Graphene.element(ajaxButton).isVisible().apply(driver));
        assertTrue("Check that page contains ALERT button element.",
                Graphene.element(alertButton).isVisible().apply(driver));

        assertTrue("Check that SUBMIT button type is submit (default).",
                Graphene.attribute(submitButton, "type").valueEquals("submit").apply(driver));
        assertTrue("Check that RESET button type is reset.",
                Graphene.attribute(resetButton, "type").valueEquals("reset").apply(driver));
        assertTrue("Check that AJAX button type is image.",
                Graphene.attribute(ajaxButton, "type").valueEquals("image").apply(driver));
        assertTrue("Check that ALERT button type is button.",
                Graphene.attribute(alertButton, "type").valueEquals("button").apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandButtonValue(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that SUBMIT button value is '" + CommandButtonBean.SUBMIT_LABEL + "'.",
                Graphene.attribute(submitButton, "value").valueEquals(CommandButtonBean.SUBMIT_LABEL).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandButtonAction(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldValue = Integer.valueOf(outputText.getText());
        int step = Integer.valueOf(inputText.getAttribute("value"));
        submitButton.click();

        assertTrue("New value should be " + oldValue + "+" + step + " = " + (oldValue + step),
                Graphene.element(outputText).textEquals(new Integer(oldValue + step).toString()).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandButtonReset(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        String oldText = inputText.getAttribute("value");
        assertFalse("Check that INPUT text has a value at start.", oldText.equals(""));

        String addedText = "00";
        inputText.sendKeys(addedText);
        assertTrue("Check that INPUT text has a changed value after inputting '" + addedText + "'.",
                Graphene.attribute(inputText, "value").valueEquals(oldText + addedText).apply(driver));

        resetButton.click();
        assertTrue("Check that INPUT text has old value after RESET.",
                Graphene.attribute(inputText, "value").valueEquals(oldText).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandButtonSubmit(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldStep = Integer.valueOf(inputText.getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        inputText.sendKeys("\u0008"); // delete
        inputText.sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(outputText.getText());
        submitButton.click();

        assertTrue("New value for Step should be " + newStep,
                Graphene.attribute(inputText, "value").valueEquals(String.valueOf(newStep)).apply(driver));
        assertTrue("New value should be " + oldValue + "+" + newStep + " = " + (oldValue + newStep),
                Graphene.element(outputText).textEquals(String.valueOf(oldValue + newStep)).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandButtonOnClickJS(@Drone WebDriver driver) throws Exception {
        // this test fails with HtmlUnitDriver as there's no alert() support
        if(driver instanceof HtmlUnitDriver) {
            return;
        }

        driver.get(portalURL.toString());

        // click the submit a few times ...
        for(int i = 0; i < 3; i++) {
            submitButton.click();
        }

        String curValue = outputText.getText();
        alertButton.click();

        assertEquals("Check that Alert text is: " + "Current Value is " + curValue,
                "Current Value is " + curValue, driver.switchTo().alert().getText());
    }

    @Test
    @RunAsClient
    public void testCommandButtonAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Ajax only works after one *real* submit...
        submitButton.click();

        String curURL = driver.getCurrentUrl();

        int oldValue = Integer.valueOf(outputText.getText());
        int step = Integer.valueOf(inputText.getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for (int i = 0; i < nTimes; i++) {
            String oldText = outputText.getText();
            ajaxButton.click();
            waitGui(driver).until(element(outputText).not().textEquals(oldText));
        }

        assertTrue("New value should be " + oldValue + "+" + step + "*" + nTimes + " = " + (oldValue + step * nTimes),
                Graphene.element(outputText).textEquals(String.valueOf(oldValue + step * nTimes)).apply(driver));

        assertEquals("Check that URL is the same as this is an ajax request.", curURL, driver.getCurrentUrl());
    }
}
