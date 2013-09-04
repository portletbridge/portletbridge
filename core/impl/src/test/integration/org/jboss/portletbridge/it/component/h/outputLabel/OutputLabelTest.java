package org.jboss.portletbridge.it.component.h.outputLabel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.spi.annotations.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.common.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class OutputLabelTest extends AbstractPortletTest {

    @Deployment()
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(OutputLabelTest.class, true);
        deployment.archive()
                .createFacesPortlet("OutputLabel", "Output Label Portlet", "outputLabel.xhtml")
                .addAsWebResource("pages/component/h/outputLabel/outputlabel.xhtml", "outputLabel.xhtml")
                .addClass(OutputLabelBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    OutputLabelPage page;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void testOutputLabelAndConverter() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains escaped element.", page.getOutputLabelEscaped().isDisplayed());

        assertEquals("Escaped element contains the expected text with HTML markup.",
                OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML, page.getOutputLabelEscaped().getText());

        assertTrue("Page contains OUTPUT TWO element.", page.getOutputLabel2().isDisplayed());

        assertEquals("OUTPUT TWO contains the text length in Float format.",
                Float.valueOf(OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML.length()).toString(), page.getOutputLabel2().getText());
    }

    @Test
    @RunAsClient
    public void testOutputLabelEscape() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains non escaped element.", page.getOutputLabelNotEscaped().isDisplayed());

        assertEquals("Non escaped element contains the expected text without HTML markup.",
                OutputLabelBean.OUTPUT_LABEL_DEFAULT_PLAINTEXT, page.getOutputLabelNotEscaped().getText());
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testOutputLabelRendered() throws Exception {
        browser.get(portalURL.toString());

        page.getOutputLabelNotRendered().isDisplayed();
    }

}
