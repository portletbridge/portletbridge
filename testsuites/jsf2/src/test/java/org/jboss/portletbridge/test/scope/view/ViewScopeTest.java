package org.jboss.portletbridge.test.scope.view;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.portletbridge.test.scopes.UserList;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class ViewScopeTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/scope/view/users.xhtml", "home.xhtml")
                .addClass(UserList.class)
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @FindBy(xpath = "//tr[2]")
    private WebElement secondRow;

    // FIXME: Only available in Graphene > 2.0.0.Alpha2
    // @FindBy(xpath = "//tr")
    // private List<WebElement> rows;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void renderPostBackWithViewScope() throws Exception {
        driver.get(portalURL.toString());
        assertEquals("Check that table contains 6 rows", 6, /*rows*/driver.findElements(By.xpath("//tr")).size());
        secondRow.findElement(By.xpath("td/input")).click();
        assertEquals("Check that table contains 5 rows", 5, /*rows*/driver.findElements(By.xpath("//tr")).size());
    }

}
