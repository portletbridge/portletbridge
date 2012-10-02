package org.jboss.portletbridge.test.component.h.outputLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

@RunWith(Arquillian.class)
public class OutputLabelTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputLabel/outputlabel.xhtml", "home.xhtml")
                .addClass(OutputLabelBean.class);
        //.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    //@Drone FIXME: JS not working
    WebDriver driver = new HtmlUnitDriver(true);

    protected static final By OUTPUT_ONE = By.xpath("//label[contains(@id,':output1')]");
    protected static final By OUTPUT_TWO = By.xpath("//label[contains(@id,':output2')]");

    @Test
    @RunAsClient
    public void testOutputLabel() throws Exception {
        driver.get(portalURL.toString());
        System.out.println(driver.getPageSource());

        assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        assertEquals("Check that OUTPUT ONE contains the expected text with HTML markup.", OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML, driver.findElement(OUTPUT_ONE).getText());

        driver.quit();
    }

    @Test
    @RunAsClient
    public void testOutputLabelEscape() throws Exception {
        // Set outputLabel not to escape XML/HTML
        OutputLabelBean.OUTPUT_LABEL_ESCAPE = false;

        driver.get(portalURL.toString());
        System.out.println(driver.getPageSource());

        assertNotNull("Check that page contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        assertEquals("Check that OUTPUT ONE contains the expected text without HTML markup.", OutputLabelBean.OUTPUT_LABEL_DEFAULT_PLAINTEXT, driver.findElement(OUTPUT_ONE).getText());

        driver.quit();
    }

    @Test
    @RunAsClient
    public void testOutputLabelRendered() throws Exception {
        // Set outputLabel not to render
        OutputLabelBean.OUTPUT_LABEL_RENDER = false;

        driver.get(portalURL.toString());
        System.out.println(driver.getPageSource());

        try {
            assertNull("Check that page does not contains OUTPUT ONE element.", driver.findElement(OUTPUT_ONE));
        }
        catch (NoSuchElementException e) {
            // expected
        }

        driver.quit();
    }

    @Test
    @RunAsClient
    public void testOutputLabelConverter() throws Exception {
        driver.get(portalURL.toString());
        System.out.println(driver.getPageSource());

        assertNotNull("Check that page contains OUTPUT TWO element.", driver.findElement(OUTPUT_TWO));
        assertEquals("Check that OUTPUT TWO contains the text length in Float format.", Float.valueOf(OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML.length()).toString(), driver.findElement(OUTPUT_TWO).getText());

        driver.quit();
    }

}
