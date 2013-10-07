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
package org.jboss.portletbridge.it.resources;

import category.WildflyOnly;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@Category(WildflyOnly.class)
@PortalTest
public class ResourceLibraryContractTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ResourceLibraryContractTest.class, true);

        deployment.archive()
                .createFacesPortlet("ResourceLibraryContract", "Resource Library Contract Portlet", "main.xhtml")
                .addAsWebResource("pages/resources/main.xhtml", "main.xhtml")
                .addAsWebResource("pages/resources/default_template.xhtml", "contracts/default/template.xhtml")
                .addAsWebResource("pages/resources/enhanced_template.xhtml", "contracts/enhanced/template.xhtml")
                .addClass(ThemeSwitchBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @FindByJQuery("[id$=':title']")
    WebElement pageTitle;

    @FindByJQuery("[id$=':selectDefault']")
    WebElement selectDefaultContract;

    @FindByJQuery("[id$=':selectEnhanced']")
    WebElement selectEnhancedContract;

    @FindByJQuery("[id$=':selectInvalid']")
    WebElement selectInvalidContract;

    @Test
    @RunAsClient
    public void switchToNewResourceLibraryContract() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectEnhancedContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Enhanced Template", "Enhanced Template", pageTitle.getText());
    }

    @Test
    @RunAsClient
    public void switchToNewResourceLibraryContractThenBackAgain() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectEnhancedContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Enhanced Template", "Enhanced Template", pageTitle.getText());

        selectDefaultContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());
    }

    @Test
    @RunAsClient
    public void reselectOriginalContract() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectDefaultContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());
    }

    @Test
    @RunAsClient
    public void reselectOriginalContractThenSwitch() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectDefaultContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectEnhancedContract.click();

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Enhanced Template", "Enhanced Template", pageTitle.getText());
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void switchToInvalidContract() throws Exception {
        browser.navigate().to(portalURL);

        assertTrue("Title of page is not displayed", pageTitle.isDisplayed());
        assertEquals("Title of page is not set to Default Template", "Default Template", pageTitle.getText());

        selectInvalidContract.click();

        pageTitle.getText();
    }

}
