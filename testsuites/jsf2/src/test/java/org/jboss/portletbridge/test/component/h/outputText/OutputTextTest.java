package org.jboss.portletbridge.test.component.h.outputText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
@PortalTest
public class OutputTextTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputText/outputtext.xhtml", "home.xhtml")
                .addClass(OutputTextBean.class);
        // .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    protected static final By OUTPUT_ONE = By.xpath("//span[contains(@id,':output1')]");
    protected static final By OUTPUT_TWO = By.xpath("//span[contains(@id,':output2')]");

    @Test
    @RunAsClient
    public void testOutputText(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        assertEquals("Check that OUTPUT ONE contains the expected text with HTML markup.",
                OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML, driver.findElement(OUTPUT_ONE).getText());
    }

    @Test
    @RunAsClient
    public void testOutputTextEscape(@Drone WebDriver driver) throws Exception {
        // Set outputText not to escape XML/HTML
        OutputTextBean.OUTPUT_TEXT_ESCAPE = false;

        driver.get(portalURL.toString());

        assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        assertEquals("Check that OUTPUT ONE contains the expected text without HTML markup.",
                OutputTextBean.OUTPUT_TEXT_DEFAULT_PLAINTEXT, driver.findElement(OUTPUT_ONE).getText());
    }

    @Test
    @RunAsClient
    public void testOutputTextRendered(@Drone WebDriver driver) throws Exception {
        // Set outputText not to render
        OutputTextBean.OUTPUT_TEXT_RENDER = false;

        driver.get(portalURL.toString());

        try {
            assertNull("Check that page does not contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    @RunAsClient
    public void testOutputTextConverter(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains OUTPUT TWO element.", driver.findElement(OUTPUT_TWO));
        assertEquals("Check that OUTPUT TWO contains the text length in Float format.",
                Float.valueOf(OutputTextBean.OUTPUT_TEXT_DEFAULT_HTML.length()).toString(), driver.findElement(OUTPUT_TWO)
                        .getText());
    }
}
