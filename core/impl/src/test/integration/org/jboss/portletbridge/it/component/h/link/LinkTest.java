package org.jboss.portletbridge.it.component.h.link;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class LinkTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(LinkTest.class, true);
        deployment.archive()
                .createFacesPortlet("LinkTest", "Link Test Portlet", "linkTest.xhtml")
                .addAsWebResource("pages/component/h/link/link.xhtml", "linkTest.xhtml")
                .addAsWebResource("pages/component/h/link/link.xhtml", "exit.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(LinkBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @FindByJQuery("[id$=':link1']")
    private WebElement linkOne;

    @FindByJQuery("[id$=':link2']")
    private WebElement linkTwo;

    @FindByJQuery("[id$=':link3']")
    private WebElement linkThree;

    @FindByJQuery("[id$=':link4']")
    private WebElement linkFour;

    @FindByJQuery("[id$=':hideLink1']")
    private WebElement hideLink1;

    protected static final By LINK_THREE_IMAGE = By.xpath("img[contains(@id,':link3img')]");

    @Test
    @RunAsClient
    public void testLink() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains LINK ONE element.", linkOne.isDisplayed());
        assertTrue("Check that page contains LINK TWO element.", linkTwo.isDisplayed());
        assertTrue("Check that page contains LINK THREE element.", linkThree.isDisplayed());
    }

    @Test
    @RunAsClient
    public void testLinkWithValue() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("LINK ONE links to the expected location.", linkOne.getAttribute("href").contains(LinkBean.LINK_ONE));
        assertEquals("OUTPUT LINK ONE contains the expected text.", LinkBean.LINK_ONE_TEXT, linkOne.getText());
    }

    @Test
    @RunAsClient
    public void testLinkWithConverter() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.", linkTwo.getAttribute("href").contains(LinkBean.LINK_ONE));
        assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.", linkTwo.getAttribute("href").contains("#bottom"));
        assertEquals("Check that LINK TWO contains the expected text.", LinkBean.LINK_ONE_TEXT + " Bottom", linkTwo.getText());
    }

    @Test
    @RunAsClient
    public void testLinkDefault() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that LINK THREE links to the current page.", linkThree.getAttribute("href").contains("linkTest"));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithParam() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that OUTPUT LINK THREE link contains the expected parameter.",
                linkThree.getAttribute("href").contains("from=L3")
                        || linkThree.getAttribute("href").contains("from0xc2FL3"));
    }

    @Test
    @RunAsClient
    public void testLinkImage() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that LINK THREE link is an image.",
                linkThree.findElement(LINK_THREE_IMAGE).getAttribute("src").contains("ajax.png"));
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testLinkRendered() throws Exception {
        browser.get(portalURL.toString());

        linkFour.isDisplayed();
    }

    // TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.

}
