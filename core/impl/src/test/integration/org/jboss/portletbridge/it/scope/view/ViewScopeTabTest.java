package org.jboss.portletbridge.it.scope.view;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class ViewScopeTabTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewScopeTabTest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewScopeTab", "View Scope Tab Portlet", "home.xhtml")
                .addAsWebResource("pages/scope/view/page.xhtml", "home.xhtml")
                .addClass(ViewScopeBean.class);
        return deployment.getFinalArchive();
    }

    @FindByJQuery("[id$=':textId']")
    private WebElement text;

    @FindByJQuery("[id$=':change']")
    private WebElement changeButton;

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
    public void checkTabsHaveDifferentValuesOnRender() throws Exception {
        browser.navigate().to(portalURL);

        String tab1Value = text.getText();

        createNewTab();
        browser.navigate().to(portalURL);

        String tab2Value = text.getText();

        assertFalse("Values on each tab are not different", tab1Value.equals(tab2Value));
        assertTrue("Tab 1 value does not contain " + ViewScopeBean.VALUE, tab1Value.contains(ViewScopeBean.VALUE));
        assertTrue("Tab 2 value does not contain " + ViewScopeBean.VALUE, tab2Value.contains(ViewScopeBean.VALUE));
    }

    @Test
    @RunAsClient
    public void checkTabsHaveDifferentValuesAfterChange() throws Exception {
        browser.navigate().to(portalURL);

        String tab1Value = text.getText();

        createNewTab();
        switchToTab(2);
        browser.navigate().to(portalURL);

        String tab2Value = text.getText();

        assertFalse("Values on each tab are not different", tab1Value.equals(tab2Value));
        assertTrue("Tab 1 value does not contain " + ViewScopeBean.VALUE, tab1Value.contains(ViewScopeBean.VALUE));
        assertTrue("Tab 2 value does not contain " + ViewScopeBean.VALUE, tab2Value.contains(ViewScopeBean.VALUE));

        switchToTab(1);

        changeButton.click();

        tab1Value = text.getText();
        assertTrue("Tab 1 value does not contain " + ViewScopeBean.NEW_VALUE, tab1Value.contains(ViewScopeBean.NEW_VALUE));

        switchToTab(2);
        browser.navigate().refresh();

        tab2Value = text.getText();
        assertFalse("Values on each tab are not different", tab1Value.equals(tab2Value));
        assertTrue("Tab 2 value does not contain " + ViewScopeBean.VALUE, tab2Value.contains(ViewScopeBean.VALUE));
    }
}
