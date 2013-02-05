package org.jboss.portletbridge.test.component.h.commandButton;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@PortalTest
public class CommandButtonTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(CommandButtonTest.class, true);
        deployment.archive()
                .createFacesPortlet("CommandButton", "Command Button Portlet", "commandButton.xhtml")
                .addAsWebResource("pages/component/h/commandButton/commandbutton.xhtml", "commandButton.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(CommandButtonBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    CommandButtonPage page;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void testCommandButtonTypes() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains SUBMIT button.", page.getSubmitButton().isDisplayed());
        assertTrue("Page contains RESET button element.", page.getResetButton().isDisplayed());
        assertTrue("Page contains AJAX button element.", page.getAjaxButton().isDisplayed());
        assertTrue("Page contains ALERT button element.", page.getAlertButton().isDisplayed());

        assertEquals("SUBMIT button type is submit (default).", "submit", page.getSubmitButton().getAttribute("type"));
        assertEquals("RESET button type is reset.", "reset", page.getResetButton().getAttribute("type"));
        assertEquals("AJAX button type is image.", "image", page.getAjaxButton().getAttribute("type"));
        assertEquals("ALERT button type is button.", "button", page.getAlertButton().getAttribute("type"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonValue() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("SUBMIT button value set", CommandButtonBean.SUBMIT_LABEL, page.getSubmitButton().getAttribute("value"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonAction() throws Exception {
        browser.get(portalURL.toString());

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        int step = Integer.valueOf(page.getInputText().getAttribute("value"));
        page.getSubmitButton().click();

        assertEquals("New value set", new Integer(oldValue + step).toString(), page.getOutputText().getText());
    }

    @Test
    @RunAsClient
    public void testCommandButtonReset() throws Exception {
        browser.get(portalURL.toString());

        String oldText = page.getInputText().getAttribute("value");
        assertFalse("INPUT text has a value at start.", oldText.equals(""));

        String addedText = "00";
        page.getInputText().sendKeys(addedText);
        assertEquals("INPUT text has changed after inputting ", oldText + addedText, page.getInputText().getAttribute("value"));

        page.getResetButton().click();
        assertEquals("INPUT text has old value after RESET.", oldText, page.getInputText().getAttribute("value"));
    }

    @Test
    @RunAsClient
    public void testCommandButtonSubmit() throws Exception {
        browser.get(portalURL.toString());

        int oldStep = Integer.valueOf(page.getInputText().getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        page.getInputText().clear();
        page.getInputText().sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        page.getSubmitButton().click();

        assertEquals("New value on input text.", String.valueOf(newStep), page.getInputText().getAttribute("value"));
        assertEquals("New value on output text", String.valueOf(oldValue + newStep), page.getOutputText().getText());
    }

    @Test
    @RunAsClient
    public void testCommandButtonOnClickJS() throws Exception {
        // this test fails with HtmlUnitDriver as there's no alert() support
        if (browser instanceof HtmlUnitDriver) {
            return;
        }

        browser.get(portalURL.toString());

        // click the submit a few times ...
        for (int i = 0; i < 3; i++) {
            page.getSubmitButton().click();
        }

        String curValue = page.getOutputText().getText();
        page.getAlertButton().click();

        assertEquals("Alert text correct", curValue, browser.switchTo().alert().getText());
    }

    @Test
    @RunAsClient
    public void testCommandButtonAjax() throws Exception {
        browser.get(portalURL.toString());

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        int step = Integer.valueOf(page.getInputText().getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for (int i = 0; i < nTimes; i++) {
            String oldText = page.getOutputText().getText();
            guardXhr(page.getAjaxButton()).click();
            assertNotSame("Output Text set to new value", oldText, page.getOutputText().getText());
        }

        assertEquals("New value set after loop", String.valueOf(oldValue + step * nTimes), page.getOutputText().getText());

        assertEquals("Verify url the same.", portalURL.toString(), browser.getCurrentUrl());
    }
}
