package org.jboss.portletbridge.it.component.h.outputLink;

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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class OutputLinkTest extends AbstractPortletTest {

    @Deployment()
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(OutputLinkTest.class, true);
        deployment.archive()
                .createFacesPortlet("OutputLink", "Output Link Portlet", "outputLink.xhtml")
                .addAsWebResource("pages/component/h/outputLink/outputlink.xhtml", "outputLink.xhtml")
                .addAsWebResource("resources/ajax.png", "ajax.png")
                .addClass(OutputLinkBean.class);
        deployment.webXml().createServletMapping()
                            .servletName("Faces Servlet")
                            .urlPattern("/faces/*")
                            .up();
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    OutputLinkPage page;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void testOutputLink() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains LINK ONE.", page.getLinkOne().isDisplayed());
        assertTrue("Page contains LINK TWO.", page.getLinkTwo().isDisplayed());
        assertTrue("Page contains LINK THREE.", page.getLinkThree().isDisplayed());
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithValue() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("OUTPUT LINK ONE links to the expected location.", page.getLinkOne().getAttribute("href").contains("exit"));
        assertEquals("OUTPUT LINK ONE contains the expected text.", OutputLinkBean.OUTPUT_LINK_ONE_TEXT, page.getLinkOne().getText());
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithConverter() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("OUTPUT LINK TWO href contains LINK ONE length in Float format.",
                page.getLinkTwo().getAttribute("href").contains(Float.toString(OutputLinkBean.OUTPUT_LINK_ONE.length())));
        assertEquals("OUTPUT LINK TWO contains the expected text.", "Size Page", page.getLinkTwo().getText());
    }

    @Test
    @RunAsClient
    public void testOutputLinkDefault() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("OUTPUT LINK THREE links to the current page.", page.getLinkThree().getAttribute("href").contains(browser.getCurrentUrl()));
    }

    @Test
    @RunAsClient
    public void testOutputLinkWithParam() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("OUTPUT LINK THREE contains the expected parameter.", page.getLinkThree().getAttribute("href").contains("from=L3"));
    }

    @Test
    @RunAsClient
    public void testOutputLinkImage() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("OUTPUT LINK THREE is an image.", page.getLinkThreeImage().getAttribute("src").contains("ajax.png"));
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testOutputLinkRendered() throws Exception {
        browser.get(portalURL.toString());

        page.getLinkZero().isDisplayed();
    }

    @Test
    @RunAsClient
    public void testViewLinksWithDifferentFacesServletMapping() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Whether Faces Suffix or Prefix mapping is used it should generate same url.",
                page.getViewLinkOne().getAttribute("href"), page.getViewLinkTwo().getAttribute("href"));
    }

    // TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.
}
