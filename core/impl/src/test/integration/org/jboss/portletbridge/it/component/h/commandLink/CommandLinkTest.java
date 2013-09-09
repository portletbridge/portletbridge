package org.jboss.portletbridge.it.component.h.commandLink;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardAjax;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Arquillian.class)
@PortalTest
public class CommandLinkTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(CommandLinkTest.class, true);
        deployment.archive()
                .createFacesPortlet("CommandLink", "Command Link Portlet", "commandLink.xhtml")
                .addAsWebResource("pages/component/h/commandLink/commandlink.xhtml", "commandLink.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(CommandLinkBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    CommandLinkPage page;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void testCommandButtonValue() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Check that SUBMIT button value set.", CommandLinkBean.SUBMIT_LABEL, page.getSubmitLink().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkAction() throws Exception {
        browser.get(portalURL.toString());

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        int step = Integer.valueOf(page.getInputText().getAttribute("value"));
        page.getSubmitLink().click();

        assertEquals("Output Text updated.", new Integer(oldValue + step).toString(), page.getOutputText().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkResetCounter() throws Exception {
        browser.get(portalURL.toString());

        // increase step and add, just to make sure
        page.getInputText().sendKeys("5");
        page.getSubmitLink().click();

        assertNotSame("Check that OUTPUT text updated.", "0", page.getOutputText().getText());

        page.getResetCounterLink().click();

        assertEquals("Check that OUTPUT text reset.", "0", page.getOutputText().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkSubmit() throws Exception {
        browser.get(portalURL.toString());

        int oldStep = Integer.valueOf(page.getInputText().getAttribute("value"));
        int newStep = oldStep + 2; // just to make sure it's not the same
        page.getInputText().sendKeys("\u0008"); // delete
        page.getInputText().sendKeys(String.valueOf(newStep)); // set to new value

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        page.getSubmitLink().click();

        assertEquals("New value for Step.", String.valueOf(newStep), page.getInputText().getAttribute("value"));
        assertEquals("New value for Output Text.", String.valueOf(oldValue + newStep), page.getOutputText().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkOnClickJS() throws Exception {
        // FIXME: this test fails with HtmlUnitDriver as there's no alert() support
        if(browser instanceof HtmlUnitDriver) {
            return;
        }
        browser.get(portalURL.toString());

        // click the submit a few times ...
        for(int i = 0; i < 3; i++) {
            page.getSubmitLink().click();
        }

        String curValue = page.getOutputText().getText();
        page.getAlertLink().click();

        assertEquals("Check Alert text.", "Current Value is " + curValue, browser.switchTo().alert().getText());
    }

    @Test
    @RunAsClient
    public void testCommandLinkAjax() throws Exception {
        browser.get(portalURL.toString());

        int oldValue = Integer.valueOf(page.getOutputText().getText());
        int step = Integer.valueOf(page.getInputText().getAttribute("value"));

        // click the ajax button a few times ...
        int nTimes = 4;
        for (int i = 0; i < nTimes; i++) {
            String oldText = page.getOutputText().getText();
            guardAjax(page.getAjaxLink()).click();
            assertNotSame("Output Text set to new value", oldText, page.getOutputText().getText());
        }

        assertEquals("New value set after loop", String.valueOf(oldValue + step * nTimes), page.getOutputText().getText());

        assertEquals("Verify url the same.", portalURL.toString(), browser.getCurrentUrl());
    }
}
