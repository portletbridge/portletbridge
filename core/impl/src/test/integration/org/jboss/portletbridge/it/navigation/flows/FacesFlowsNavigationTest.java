/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.it.navigation.flows;

import category.WildflyOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
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
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@Category(WildflyOnly.class)
@PortalTest
public class FacesFlowsNavigationTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(FacesFlowsNavigationTest.class, true);

        deployment.archive()
                .createFacesPortlet("FacesFlowsNavigation", "Faces Flows Navigation Portlet", "index.xhtml")
                .addAsWebResource("pages/navigation/flows/index.xhtml", "index.xhtml")
                .addAsWebResource("pages/navigation/flows/result.xhtml", "result.xhtml")
                .addAsWebResource("pages/navigation/flows/register_start.xhtml", "/register/start.xhtml")
                .addAsWebResource("pages/navigation/flows/register_middle.xhtml", "/register/middle.xhtml")
                .addAsWebResource("pages/navigation/flows/register_end.xhtml", "/register/end.xhtml")
                .addClass(Register.class)
                .addClass(RegisterBean.class)
                .addClasses(AbstractPortletTest.class, Category.class, WildflyOnly.class, PortalTest.class);

        deployment.addEmptyBeansXml();
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    FlowPage flowPage;

    @FindByJQuery("[id$=':register']")
    WebElement registerButton;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @RunAsClient
    public void simplePassageThroughFlowPages() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Index page did not display", registerButton.isDisplayed());

        registerButton.click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page One", "Page One", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        // Set values on Page One
        flowPage.getFirstName().sendKeys("John");
        flowPage.getLastName().sendKeys("Smith");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Two", "Page Two", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        assertEquals("First name was not retained", "John", flowPage.getFirstName().getText());
        assertEquals("Last name was not retained", "Smith", flowPage.getLastName().getText());

        // Set values on Page Two
        flowPage.getUserName().sendKeys("jsmith");
        flowPage.getValue().sendKeys("flowtest");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Three", "Page Three", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());
        assertEquals("Flow value not retained", "flowtest", flowPage.getFlowValue().getText());

        // Complete Flow
        flowPage.getReturnButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Complete", "Complete", flowPage.getTitle().getText());
        assertEquals("In a Flow", "false", flowPage.getFlowPresent().getText());
        assertEquals("Flow value retained", "", flowPage.getFlowValue().getText());
    }

    @Test
    @RunAsClient
    public void returnHomeFromEnd() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Index page did not display", registerButton.isDisplayed());

        registerButton.click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page One", "Page One", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        // Set values on Page One
        flowPage.getFirstName().sendKeys("John");
        flowPage.getLastName().sendKeys("Smith");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Two", "Page Two", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        assertEquals("First name was not retained", "John", flowPage.getFirstName().getText());
        assertEquals("Last name was not retained", "Smith", flowPage.getLastName().getText());

        // Set values on Page Two
        flowPage.getUserName().sendKeys("jsmith");
        flowPage.getValue().sendKeys("flowtest");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Three", "Page Three", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());
        assertEquals("Flow value not retained", "flowtest", flowPage.getFlowValue().getText());

        // Go Home
        flowPage.getHomeButton().click();

        assertTrue("Index page did not display", registerButton.isDisplayed());
    }

    @Test
    @RunAsClient
    public void returnHomeFromStep2() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Index page did not display", registerButton.isDisplayed());

        registerButton.click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page One", "Page One", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        // Set values on Page One
        flowPage.getFirstName().sendKeys("John");
        flowPage.getLastName().sendKeys("Smith");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Two", "Page Two", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        assertEquals("First name was not retained", "John", flowPage.getFirstName().getText());
        assertEquals("Last name was not retained", "Smith", flowPage.getLastName().getText());

        // Go Home
        flowPage.getHomeButton().click();

        assertTrue("Index page did not display", registerButton.isDisplayed());
    }

    @Test
    @RunAsClient
    public void simplePassageThroughFlowPagesWithStepBack() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Index page did not display", registerButton.isDisplayed());

        registerButton.click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page One", "Page One", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        // Set values on Page One
        flowPage.getFirstName().sendKeys("John");
        flowPage.getLastName().sendKeys("Smith");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Two", "Page Two", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        assertEquals("First name was not retained", "John", flowPage.getFirstName().getText());
        assertEquals("Last name was not retained", "Smith", flowPage.getLastName().getText());

        // Go Back
        flowPage.getBackButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page One", "Page One", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        // Set values on Page One
        flowPage.getFirstName().clear();
        flowPage.getFirstName().sendKeys("Gary");
        flowPage.getLastName().clear();
        flowPage.getLastName().sendKeys("Busey");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Two", "Page Two", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());

        assertEquals("First name was not retained", "Gary", flowPage.getFirstName().getText());
        assertEquals("Last name was not retained", "Busey", flowPage.getLastName().getText());

        // Set values on Page Two
        flowPage.getUserName().sendKeys("jsmith");
        flowPage.getValue().sendKeys("flowtest");
        flowPage.getNextButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Page Three", "Page Three", flowPage.getTitle().getText());
        assertEquals("Not in a Flow", "true", flowPage.getFlowPresent().getText());
        assertEquals("Flow value not retained", "flowtest", flowPage.getFlowValue().getText());

        // Complete Flow
        flowPage.getReturnButton().click();

        assertTrue("Title of page is not displayed", flowPage.getTitle().isDisplayed());
        assertEquals("Title of page is not set to Complete", "Complete", flowPage.getTitle().getText());
        assertEquals("In a Flow", "false", flowPage.getFlowPresent().getText());
        assertEquals("Flow value retained", "", flowPage.getFlowValue().getText());
    }

}
