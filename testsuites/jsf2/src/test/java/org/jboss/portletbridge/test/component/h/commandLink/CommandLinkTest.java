package org.jboss.portletbridge.test.component.h.commandLink;

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
public class CommandLinkTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/commandLink/commandlink.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(CommandLinkBean.class);
        // .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @FindBy(xpath = "//a[contains(@id,':submit')]")
    private WebElement submitLink;

    @FindBy(xpath = "//a[contains(@id,':reset_counter')]")
    private WebElement resetCounterLink;

    @FindBy(xpath = "//a[contains(@id,':ajax')]")
    private WebElement ajaxLink;

    @FindBy(xpath = "//a[contains(@id,':alert')]")
    private WebElement alertLink;

    @FindBy(xpath = "//span[contains(@id,':output')]")
    private WebElement outputText;

    @FindBy(xpath = "//input[contains(@id,':input')]")
    private WebElement inputText;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Test
    @RunAsClient
    public void testCommandButtonValue(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that SUBMIT button value is '" + CommandLinkBean.SUBMIT_LABEL + "'.",
                Graphene.element(submitLink).textEquals(CommandLinkBean.SUBMIT_LABEL).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandLinkAction(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldValue = Integer.valueOf(outputText.getText());
        int step = Integer.valueOf(inputText.getAttribute("value"));
        submitLink.click();

        assertTrue("New value should be " + oldValue + "+" + step + " = " + (oldValue + step),
                Graphene.element(outputText).textEquals(new Integer(oldValue + step).toString()).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandLinkResetCounter(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // increase step and add, just to make sure
        inputText.sendKeys("5");
        submitLink.click();

        assertFalse("Check that OUTPUT text is not 0 after adding at least 5.",
                Graphene.element(outputText).textEquals("0").apply(driver));

        resetCounterLink.click();

        assertTrue("Check that OUTPUT text is '0' after resetting.",
                Graphene.element(outputText).textEquals("0").apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandLinkSubmit(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldStep = Integer.valueOf(inputText.getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        inputText.sendKeys("\u0008"); // delete
        inputText.sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(outputText.getText());
        submitLink.click();

        assertTrue("New value for Step should be " + newStep,
                Graphene.attribute(inputText, "value").valueEquals(String.valueOf(newStep)).apply(driver));
        assertTrue("New value should be " + oldValue + "+" + newStep + " = " + (oldValue + newStep),
                Graphene.element(outputText).textEquals(String.valueOf(oldValue + newStep)).apply(driver));
    }

    @Test
    @RunAsClient
    public void testCommandLinkOnClickJS(@Drone WebDriver driver) throws Exception {
        // FIXME: this test fails with HtmlUnitDriver as there's no alert() support
        if(driver instanceof HtmlUnitDriver) {
            return;
        }
        driver.get(portalURL.toString());

        // click the submit a few times ...
        for(int i = 0; i < 3; i++) {
            submitLink.click();
        }

        String curValue = outputText.getText();
        alertLink.click();

        assertEquals("Check that Alert text is: " + "Current Value is " + curValue,
                "Current Value is " + curValue, driver.switchTo().alert().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Ajax only works after one *real* submit...
        submitLink.click();

        String curURL = driver.getCurrentUrl();

        int oldValue = Integer.valueOf(outputText.getText());
        int step = Integer.valueOf(inputText.getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for (int i = 0; i < nTimes; i++) {
            String oldText = outputText.getText();
            ajaxLink.click();
            waitGui(driver).until(element(outputText).not().textEquals(oldText));
        }

        assertTrue("New value should be " + oldValue + "+" + step + "*" + nTimes + " = " + (oldValue + step * nTimes),
                Graphene.element(outputText).textEquals(String.valueOf(oldValue + step * nTimes)).apply(driver));

        assertEquals("Check that URL is the same as this is an ajax request.", curURL, driver.getCurrentUrl());
    }
}
