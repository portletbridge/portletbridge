package org.jboss.portletbridge.test.component.h.commandLink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
public class CommandLinkTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/commandLink/commandlink.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(CommandLinkBean.class);
        //.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    protected static final By SUBMIT_LINK = By.xpath("//a[contains(@id,':submit')]");
    protected static final By RESET_COUNTER_LINK = By.xpath("//a[contains(@id,':reset_counter')]");
    protected static final By AJAX_LINK = By.xpath("//a[contains(@id,':ajax')]");
    protected static final By ALERT_LINK = By.xpath("//a[contains(@id,':alert')]");

    protected static final By OUTPUT_TEXT = By.xpath("//span[contains(@id,':output')]");
    protected static final By INPUT_TEXT = By.xpath("//input[contains(@id,':input')]");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Test
    @RunAsClient
    public void testCommandButtonValue(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertEquals("Check that SUBMIT button value is '" + CommandLinkBean.SUBMIT_LABEL + "'.", CommandLinkBean.SUBMIT_LABEL, driver.findElement(SUBMIT_LINK).getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkAction(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        int step = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));
        driver.findElement(SUBMIT_LINK).click();
        assertEquals("New value should be " + oldValue + "+" + step + " = " + (oldValue + step), 
                new Integer(oldValue + step), Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));
    }

    @Test
    @RunAsClient
    public void testCommandLinkReset(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // increase step and add, just to make sure
        driver.findElement(INPUT_TEXT).sendKeys("5");
        driver.findElement(SUBMIT_LINK).click();

        String oldText = driver.findElement(OUTPUT_TEXT).getText();
        assertFalse("Check that the Output is not 0 after adding at least 5.", "0".equals(oldText));

        driver.findElement(RESET_COUNTER_LINK).click();
        String newText = driver.findElement(OUTPUT_TEXT).getText();

        assertEquals("Check that OUTPUT text is '0' after resetting.", "0", newText);
    }

    @Test
    @RunAsClient
    public void testCommandLinkSubmit(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        int oldStep = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        driver.findElement(INPUT_TEXT).sendKeys("\u0008"); // delete
        driver.findElement(INPUT_TEXT).sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        driver.findElement(SUBMIT_LINK).click();
        assertEquals("New value for Step should be " + newStep, new Integer(newStep), Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value")));
        assertEquals("New value should be " + oldValue + "+" + newStep + " = " + (oldValue + newStep), new Integer(oldValue + newStep), Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));
        Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
    }

//    @Test
//    @RunAsClient
//    public void testCommandLinkOnClickJS(@Drone WebDriver driver) throws Exception {
//        driver.get(portalURL.toString());
//
//        // click the submit a few times ...
//        for(int i = 0; i < 3; i++) {
//            driver.findElement(SUBMIT_LINK).click();
//        }
//
//        String curValue = driver.findElement(OUTPUT_TEXT).getText();
//        driver.findElement(ALERT_LINK).click();
//        // FIXME: this test fails with HtmlUnitDriver as there's no alert() support
//        assertEquals("Check that Alert text is: " + "Current Value is " + curValue, "Current Value is " + curValue, driver.switchTo().alert().getText());
//    }

    @Test
    @RunAsClient
    public void testCommandLinkAjax(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Ajax only works after one *real* submit...
        driver.findElement(SUBMIT_LINK).click();

        String curURL = driver.getCurrentUrl();
        
        int oldValue = Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText());
        int step = Integer.valueOf(driver.findElement(INPUT_TEXT).getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for(int i = 0; i < nTimes; i++) {
            driver.findElement(AJAX_LINK).click();
            // wait for ajax
            Thread.sleep(500);
        }

        assertEquals("New value should be " + oldValue + "+" + step + "*" + nTimes + " = " + (oldValue + step * nTimes), 
                new Integer(oldValue + step * nTimes), Integer.valueOf(driver.findElement(OUTPUT_TEXT).getText()));

        assertEquals("Check that URL is the same as this is an ajax request.", curURL, driver.getCurrentUrl());
    }
}
