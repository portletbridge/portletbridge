package org.jboss.portletbridge.it.component.f.passThrough;

import category.Jsf22Only;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.portletbridge.it.component.f.viewParam.ViewParamBean;
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
@Category(Jsf22Only.class)
@PortalTest
public class PassThroughTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(PassThroughTest.class, true);
        deployment.archive()
                .createFacesPortlet("PassThrough", "Pass Through Portlet", "home.xhtml")
                .addAsWebResource("pages/component/f/passThrough/home.xhtml", "home.xhtml")
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

    @FindByJQuery("[id$=':input1']")
    private WebElement input1;

    @FindByJQuery("[id$=':input2']")
    private WebElement input2;

    @Test
    @RunAsClient
    public void testInlinePassThrough() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page does not contain h:inputText for input1.", input1.isDisplayed());

        assertEquals("Placeholder text does not match for input1.", "Input Help 1", input1.getAttribute("placeholder"));
    }

    @Test
    @RunAsClient
    public void testChildTagPassThrough() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Page does not contain h:inputText for input2.", input2.isDisplayed());

        assertEquals("Placeholder text does not match for input2.", "Input Help 2", input2.getAttribute("placeholder"));
    }
}
