package org.jboss.portletbridge.test.component.h.outputLink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
public class OutputLinkTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputLink/outputlink.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(OutputLinkBean.class);
        // .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    protected static final By OUTPUT_LINK_ONE = By.xpath("//a[contains(@id,':link1')]");
    protected static final By OUTPUT_LINK_TWO = By.xpath("//a[contains(@id,':link2')]");
    protected static final By OUTPUT_LINK_THREE = By.xpath("//a[contains(@id,':link3')]");
    protected static final By OUTPUT_LINK_THREE_IMAGE = By.xpath("img[contains(@id,':link3img')]");

    @Test
    @RunAsClient
    public void testOutputLink(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains OUTPUT LINK ONE element.", driver.findElement(OUTPUT_LINK_ONE));
        assertNotNull("Check that page contains OUTPUT LINK TWO element.", driver.findElement(OUTPUT_LINK_TWO));
        assertNotNull("Check that page contains OUTPUT LINK THREE element.", driver.findElement(OUTPUT_LINK_THREE));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithValue(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: shouldn't link be .../portal/exit.xhtml instead of .../exit.xhtml ?
        assertTrue("Check that OUTPUT LINK ONE links to the expected location.", driver.findElement(OUTPUT_LINK_ONE)
                .getAttribute("href").contains(OutputLinkBean.OUTPUT_LINK_ONE));
        assertEquals("Check that OUTPUT LINK ONE contains the expected text.", OutputLinkBean.OUTPUT_LINK_ONE_TEXT, driver
                .findElement(OUTPUT_LINK_ONE).getText());
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithConverter(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: shouldn't link be .../portal/10.0.xhtml instead of .../10.0.xhtml ?
        assertTrue(
                "Check that OUTPUT LINK TWO links to a page with the LINK ONE length in Float format.",
                driver.findElement(OUTPUT_LINK_TWO).getAttribute("href")
                        .contains(Float.valueOf(OutputLinkBean.OUTPUT_LINK_ONE.length()).toString()));
        assertEquals("Check that OUTPUT LINK TWO contains the expected text.", "Size Page", driver.findElement(OUTPUT_LINK_TWO)
                .getText());
    }

    // @Test
    // @RunAsClient
    // public void testOutputLinkDefault(@Drone WebDriver driver) throws Exception {
    // driver.get(portalURL.toString());
    //
    // // FIXME: Shouldn't it point to current page rather than top-level ?
    // assertTrue("Check that OUTPUT LINK THREE links to the current page.",
    // driver.findElement(OUTPUT_LINK_THREE).getAttribute("href").contains(driver.getCurrentUrl()));
    // assertEquals("Check that OUTPUT LINK THREE contains the expected text.", "Home",
    // driver.findElement(OUTPUT_LINK_THREE).getText());
    // }

    @Test
    @RunAsClient
    public void testOutputLinkWithParam(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Shouldn't it point to current page rather than top-level ?
        assertTrue("Check that OUTPUT LINK THREE link contains the expected parameter.", driver.findElement(OUTPUT_LINK_THREE)
                .getAttribute("href").contains("from=L3"));
    }

    @Test
    @RunAsClient
    public void testOutputLinkImage(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        // FIXME: Shouldn't it point to current page rather than top-level ?
        assertTrue(
                "Check that OUTPUT LINK THREE link is an image.",
                driver.findElement(OUTPUT_LINK_THREE).findElement(OUTPUT_LINK_THREE_IMAGE).getAttribute("src")
                        .contains("ajax.png"));
    }

    @Test
    @RunAsClient
    public void testOutputLinkRendered(@Drone WebDriver driver) throws Exception {
        // Set outputLink not to render
        OutputLinkBean.OUTPUT_LINK_RENDER = false;

        driver.get(portalURL.toString());

        try {
            assertNull("Check that page does not contains OUTPUT LINK ONE element.", driver.findElement(OUTPUT_LINK_ONE));
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    // TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.
}
