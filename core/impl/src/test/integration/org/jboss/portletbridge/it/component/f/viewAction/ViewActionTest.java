package org.jboss.portletbridge.it.component.f.viewAction;

import category.GateInOnly;
import category.Jsf22Only;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category({Jsf22Only.class, GateInOnly.class})
@PortalTest
public class ViewActionTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewActionTest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewAction", "View Action Portlet", "home.xhtml")
                .addAsWebResource("pages/component/f/viewAction/home.xhtml", "home.xhtml")
                .addAsWebResource("pages/component/f/viewAction/result1.xhtml", "result1.xhtml")
                .addAsWebResource("pages/component/f/viewAction/result2.xhtml", "result2.xhtml")
                .addAsWebResource("pages/component/f/viewAction/result3.xhtml", "result3.xhtml")
                .addAsWebResource("pages/component/f/viewAction/result4.xhtml", "result4.xhtml")
                .addClass(DisplayItemBean.class)
                .addClass(Item.class)
                .addClass(ListBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Override
    protected WebDriver getBrowser() {
        return browser;
    }

    @Page
    private HomePage homePage;

    @Page
    private ResultPage resultPage;

    @Test
    @RunAsClient
    public void testBasicViewAction() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page does not contain result1 link.", homePage.getBasicViewActionLink().isDisplayed());

        guardHttp(homePage.getBasicViewActionLink()).click();

        assertEquals("Item id is not 1.", "1", resultPage.getItemId().getText());
        assertEquals("Item name is not One.", "One", resultPage.getItemName().getText());
    }
}
