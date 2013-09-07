package org.jboss.portletbridge.it.component.f.viewParam;

import category.GateInOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
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
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category(GateInOnly.class)
@PortalTest
public class ViewParamTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewParamTest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewParam", "View Param Portlet", "page1.xhtml")
                .addAsWebResource("pages/component/f/viewParam/page1.xhtml", "page1.xhtml")
                .addAsWebResource("pages/component/f/viewParam/page2.xhtml", "page2.xhtml")
                .addClass(ViewParamBean.class);
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

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitButton;

    @FindBy(jquery = "[id$=':param1']")
    private WebElement param1;

    @Test
    @RunAsClient
    public void testViewParam() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page contains SUBMIT button.", submitButton.isDisplayed());

        submitButton.click();

        assertEquals("Param value is not 10", "10", param1.getText());
    }
}
