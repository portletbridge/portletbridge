package org.jboss.portletbridge.test.component.h.outputText;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class OutputTextTest {

    @Deployment()
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(OutputTextTest.class, true);
        deployment.archive()
                .createFacesPortlet("OutputText", "Output Text Portlet", "outputText.xhtml")
                .addAsWebResource("pages/component/h/outputText/outputtext.xhtml", "outputText.xhtml")
                .addClass(OutputTextBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    OutputTextPage page;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void testOutputTextAndConverter() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains escaped element.", page.getOutputTextEscaped().isDisplayed());

        assertEquals("Escaped element contains the expected text with HTML markup.",
                OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML, page.getOutputTextEscaped().getText());

        assertTrue("Page contains OUTPUT TWO element.", page.getOutputText2().isDisplayed());

        assertEquals("OUTPUT TWO contains the text length in Float format.",
                Float.valueOf(OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML.length()).toString(), page.getOutputText2().getText());
    }

    @Test
    @RunAsClient
    public void testOutputTextEscape() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains non escaped element.", page.getOutputTextNotEscaped().isDisplayed());

        assertEquals("Non escaped element contains the expected text without HTML markup.",
                OutputTextBean.OUTPUT_TEXT_DEFAULT_PLAINTEXT, page.getOutputTextNotEscaped().getText());
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testOutputTextNotRendered() throws Exception {
        browser.get(portalURL.toString());

        page.getOutputTextNotRendered().isDisplayed();
    }
}
