package org.jboss.portletbridge.test.component.h.link;

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
public class LinkTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/link/link.xhtml", "home.xhtml")
                .addAsWebResource("pages/component/h/link/link.xhtml", "exit.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css")
                .addClass(LinkBean.class);
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @FindBy(xpath = "//a[contains(@id,':link1')]")
    private WebElement linkOne;

    @FindBy(xpath = "//a[contains(@id,':link2')]")
    private WebElement linkTwo;

    @FindBy(xpath = "//a[contains(@id,':link3')]")
    private WebElement linkThree;

    protected static final By LINK_THREE_IMAGE = By.xpath("img[contains(@id,':link3img')]");

    @Test
    @RunAsClient
    public void testLink(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains LINK ONE element.", Graphene.element(linkOne).isVisible().apply(driver));
        assertTrue("Check that page contains LINK TWO element.", Graphene.element(linkTwo).isVisible().apply(driver));
        assertTrue("Check that page contains LINK THREE element.", Graphene.element(linkThree).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testLinkWithValue(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        // FIXME: shouldn't link be .../portal/exit.xhtml instead of .../exit.xhtml ?
        assertTrue("Check that LINK ONE links to the expected location.",
                Graphene.attribute(linkOne, "href").valueContains(LinkBean.LINK_ONE).apply(driver));
        assertTrue("Check that LINK ONE contains the expected text.",
                Graphene.element(linkOne).textEquals(LinkBean.LINK_ONE_TEXT).apply(driver));
    }

    @Test
    @RunAsClient
    public void testLinkWithConverter(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.",
                Graphene.attribute(linkTwo, "href").valueContains(LinkBean.LINK_ONE).apply(driver));
        assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.",
                Graphene.attribute(linkTwo, "href").valueContains("#bottom").apply(driver));
        assertTrue("Check that LINK TWO contains the expected text.",
                Graphene.element(linkTwo).textEquals(LinkBean.LINK_ONE_TEXT+ " Bottom").apply(driver));
    }

    @Test
    @RunAsClient
    public void testLinkDefault(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that LINK THREE links to the current page.",
                Graphene.attribute(linkThree, "href").valueContains("home0x2xhtml").apply(driver) ||
                Graphene.attribute(linkThree, "href").valueContains("home.xhtml").apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithParam(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that OUTPUT LINK THREE link contains the expected parameter.",
                Graphene.attribute(linkThree, "href").valueContains("from0xc2FL3").apply(driver) ||
                Graphene.attribute(linkThree, "href").valueContains("from=L3").apply(driver));
    }

    @Test
    @RunAsClient
    public void testLinkImage(@Drone WebDriver driver) throws Exception {
        LinkBean.LINK_RENDER = true;

        driver.get(portalURL.toString());

        assertTrue("Check that LINK THREE link is an image.",
                linkThree.findElement(LINK_THREE_IMAGE).getAttribute("src").contains("ajax.png"));
    }

    @Test
    @RunAsClient
    public void testLinkRendered(@Drone WebDriver driver) throws Exception {
        // Set outputLink not to render
        LinkBean.LINK_RENDER = false;

        driver.get(portalURL.toString());

        assertTrue("Check that page does not contains LINK ONE element.",
                Graphene.element(linkOne).not().isPresent().apply(driver));
    }

    // TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.

}
