package org.jboss.portletbridge.test.scope.view;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.portletbridge.test.AbstractPortletTest;
import org.jboss.portletbridge.test.scopes.UserList;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@PortalTest
public class ViewScopeTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewScopeTest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewScope", "View Scope Portlet", "users.xhtml")
                .addAsWebResource("pages/scope/view/users.xhtml", "users.xhtml")
                .addClass(UserList.class);
        return deployment.getFinalArchive();
    }

    @FindBy(xpath = "//tr[2]")
    private WebElement secondRow;

    @FindBy(xpath = "//tr")
    private List<WebElement> rows;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void renderPostBackWithViewScope() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Check that table contains 6 rows.", 6, rows.size());

        secondRow.findElement(By.xpath("td/input")).click();

        assertEquals("Check that table contains 5 rows.", 5, rows.size());
    }

}
