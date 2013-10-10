package org.jboss.portletbridge.it.scope.view;

import category.WildflyOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.portletbridge.arquillian.deployment.TestDeployment;

import javax.enterprise.inject.Vetoed;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@Category(WildflyOnly.class)
@PortalTest
@Vetoed
public class ViewScopeCDITest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewScopeCDITest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewScopeCDI", "View Scope CDI Portlet", "users.xhtml")
                .addAsWebResource("pages/scope/view/usersCdi.xhtml", "users.xhtml")
                .addClass(CdiUserList.class)
                .addClasses(AbstractPortletTest.class, Category.class, WildflyOnly.class, PortalTest.class);
        deployment.addEmptyBeansXml();
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
        browser.navigate().to(portalURL);

        assertEquals("Check that table contains 6 rows.", 6, rows.size());

        secondRow.findElement(By.xpath("td/input")).click();

        assertEquals("Check that table contains 5 rows.", 5, rows.size());
    }

    @Test
    @RunAsClient
    public void checkTabsHaveDifferentNumberOfRowsAfterDelete() throws Exception {
        browser.navigate().to(portalURL);

        assertEquals("Check that table contains 6 rows.", 6, rows.size());

        createNewTab();
        switchToTab(2);
        browser.navigate().to(portalURL);

        assertEquals("Check that table contains 6 rows.", 6, rows.size());


        switchToTab(1);
        secondRow.findElement(By.xpath("td/input")).click();

        assertEquals("Check that table contains 5 rows.", 5, rows.size());

        switchToTab(2);
        browser.navigate().refresh();

        assertEquals("Check that table contains 6 rows.", 6, rows.size());
    }

}
