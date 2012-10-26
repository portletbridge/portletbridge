package org.jboss.portletbridge.test.component.h.outputLink;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class OutputLinkTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputLink/outputlink.xhtml", "home.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(OutputLinkBean.class)
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @FindBy(xpath = "//a[contains(@id,':link1')]")
    private WebElement outputLinkOne;

    @FindBy(xpath = "//a[contains(@id,':link2')]")
    private WebElement outputLinkTwo;

    @FindBy(xpath = "//a[contains(@id,':link3')]")
    private WebElement outputLinkThree;

    protected static final By OUTPUT_LINK_THREE_IMAGE = By.xpath("img[contains(@id,':link3img')]");

    @Test
    @RunAsClient
    public void testOutputLink(@Drone WebDriver driver) throws Exception {
        OutputLinkBean.OUTPUT_LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT LINK ONE element.",
                Graphene.element(outputLinkOne).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT LINK TWO element.",
                Graphene.element(outputLinkTwo).isVisible().apply(driver));
        assertTrue("Check that page contains OUTPUT LINK THREE element.",
                Graphene.element(outputLinkThree).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithValue(@Drone WebDriver driver) throws Exception {
        OutputLinkBean.OUTPUT_LINK_RENDER = true;

        driver.get(portalURL.toString());

        // FIXME: shouldn't link be .../portal/exit.xhtml instead of .../exit.xhtml ?
        assertTrue("Check that OUTPUT LINK ONE links to the expected location.",
                Graphene.attribute(outputLinkOne, "href").valueContains(OutputLinkBean.OUTPUT_LINK_ONE).apply(driver));
        assertTrue("Check that OUTPUT LINK ONE contains the expected text.",
                Graphene.element(outputLinkOne).textEquals(OutputLinkBean.OUTPUT_LINK_ONE_TEXT).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithConverter(@Drone WebDriver driver) throws Exception {
        OutputLinkBean.OUTPUT_LINK_RENDER = true;

        driver.get(portalURL.toString());

        // FIXME: shouldn't link be .../portal/10.0.xhtml instead of .../10.0.xhtml ?
        assertTrue("Check that OUTPUT LINK TWO links to a page with the LINK ONE length in Float format.",
                Graphene.attribute(outputLinkTwo, "href").
                valueContains(Float.valueOf(OutputLinkBean.OUTPUT_LINK_ONE.length()).toString()).apply(driver));
        assertTrue("Check that OUTPUT LINK TWO contains the expected text.",
                Graphene.element(outputLinkTwo).textEquals("Size Page").apply(driver));
    }

    //@Test
    //@RunAsClient
    //public void testOutputLinkDefault(@Drone WebDriver driver) throws Exception {
    //    driver.get(portalURL.toString());
    //
    //    // FIXME: Shouldn't it point to current page rather than top-level ?
    //    assertTrue("Check that OUTPUT LINK THREE links to the current page.",
    //            Graphene.attribute(outputLinkThree, "href").valueEquals(driver.getCurrentUrl()).apply(driver));
    //    assertTrue("Check that OUTPUT LINK THREE contains the expected text.",
    //            Graphene.element(outputLinkThree).textEquals("Home").apply(driver));
    //}

    @Test
    @RunAsClient
    public void testOutputLinkWithParam(@Drone WebDriver driver) throws Exception {
        OutputLinkBean.OUTPUT_LINK_RENDER = true;

        driver.get(portalURL.toString());

        // FIXME: Shouldn't it point to current page rather than top-level ?
        assertTrue("Check that OUTPUT LINK THREE link contains the expected parameter.",
                Graphene.attribute(outputLinkThree, "href").valueContains("from=L3").apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLinkImage(@Drone WebDriver driver) throws Exception {
        OutputLinkBean.OUTPUT_LINK_RENDER = true;

        driver.get(portalURL.toString());

        // FIXME: Shouldn't it point to current page rather than top-level ?
        assertTrue("Check that OUTPUT LINK THREE link is an image.",
                outputLinkThree.findElement(OUTPUT_LINK_THREE_IMAGE).getAttribute("src").contains("ajax.png"));
    }

    @Test
    @RunAsClient
    public void testOutputLinkRendered(@Drone WebDriver driver) throws Exception {
        // Set outputLink not to render
        OutputLinkBean.OUTPUT_LINK_RENDER = false;

        driver.get(portalURL.toString());

        assertFalse("Check that page does not contains OUTPUT LINK ONE element.",
                Graphene.element(outputLinkOne).isVisible().apply(driver));
    }

    // TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.
}
