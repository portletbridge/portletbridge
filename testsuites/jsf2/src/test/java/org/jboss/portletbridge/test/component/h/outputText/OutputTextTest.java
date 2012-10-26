package org.jboss.portletbridge.test.component.h.outputText;

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
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class OutputTextTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputText/outputtext.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css")
                .addClass(OutputTextBean.class);
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @FindBy(xpath = "//span[contains(@id,':output1')]")
    private WebElement outputTextOne;

    @FindBy(xpath = "//span[contains(@id,':output2')]")
    private WebElement outputTextTwo;

    @Test
    @RunAsClient
    public void testOutputText(@Drone WebDriver driver) throws Exception {
        OutputTextBean.OUTPUT_TEXT_ESCAPE = true;
        OutputTextBean.OUTPUT_TEXT_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT ONE element.",
                Graphene.element(outputTextOne).isVisible().apply(driver));

        assertTrue("Check that OUTPUT ONE contains the expected text with HTML markup.",
                Graphene.element(outputTextOne).textEquals(OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputTextEscape(@Drone WebDriver driver) throws Exception {
        // Set outputText not to escape XML/HTML
        OutputTextBean.OUTPUT_TEXT_ESCAPE = false;
        OutputTextBean.OUTPUT_TEXT_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT ONE element.",
                Graphene.element(outputTextOne).isVisible().apply(driver));

        assertTrue("Check that OUTPUT ONE contains the expected text without HTML markup.",
                Graphene.element(outputTextOne).textEquals(OutputTextBean.OUTPUT_TEXT_DEFAULT_PLAINTEXT).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputTextRendered(@Drone WebDriver driver) throws Exception {
        // Set outputText not to render
        OutputTextBean.OUTPUT_TEXT_RENDER = false;
        OutputTextBean.OUTPUT_TEXT_ESCAPE = true;

        driver.get(portalURL.toString());

        assertFalse("Check that page does not contains OUTPUT ONE element.",
                Graphene.element(outputTextOne).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputTextConverter(@Drone WebDriver driver) throws Exception {
        OutputTextBean.OUTPUT_TEXT_ESCAPE = true;
        OutputTextBean.OUTPUT_TEXT_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT TWO element.",
                Graphene.element(outputTextTwo).isVisible().apply(driver));

        assertTrue("Check that OUTPUT TWO contains the text length in Float format.",
                Graphene.element(outputTextTwo).
                textEquals(Float.valueOf(OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML.length()).toString()).apply(driver));
    }
}
