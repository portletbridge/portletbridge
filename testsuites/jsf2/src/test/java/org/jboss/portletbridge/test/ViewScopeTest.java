package org.jboss.portletbridge.test;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.scopes.UserList;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(Arquillian.class)
public class ViewScopeTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("users.xhtml", "home.xhtml")
                .addClass(UserList.class)
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    protected static final By SECOND_ROW = By.xpath("//tr[2]");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void renderPostBackWithViewScope() throws Exception {
        driver.get(portalURL.toString());
        assertTrue(driver.findElements(By.xpath("//tr")).size() == 6);
        WebElement secondRow = driver.findElement(SECOND_ROW);
        secondRow.findElement(By.xpath("td/input")).click();
        assertTrue(driver.findElements(By.xpath("//tr")).size() == 5);
    }

}
