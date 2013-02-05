/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.test.navigation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class NavigationTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(NavigationTest.class, true);

        getFacesXml(deployment.facesConfig());

        deployment.archive()
                .createFacesPortlet("Navigation", "Navigation Portlet", "main.xhtml")
                .addClass(PageController.class)
                .addAsWebResource("pages/navigation/main.xhtml", "main.xhtml")
                .addAsWebResource("pages/navigation/page1.xhtml", "page1.xhtml")
                .addAsWebResource("pages/navigation/page2.xhtml", "page2.xhtml")
                .addAsWebResource("pages/navigation/target.xhtml", "target.xhtml");
        return deployment.getFinalArchive();
    }

    protected static void getFacesXml(WebFacesConfigDescriptor webConfig) {
        addNavigationRule(webConfig, "/main.xhtml", "/target.xhtml");
        addNavigationRuleFromAction(webConfig, "#{pageController.processPage1}","success", "/page1.xhtml");
        addNavigationRuleFromAction(webConfig, "#{pageController.processPage2}","success", "/page2.xhtml");
    }

    private static void addNavigationRule(WebFacesConfigDescriptor webConfig, String fromOutcome, String toViewId) {
        webConfig.createNavigationRule().createNavigationCase().fromOutcome(fromOutcome).toViewId(toViewId);
    }

    private static void addNavigationRuleFromAction(WebFacesConfigDescriptor webConfig, String action, String fromOutcome, String toViewId) {
        webConfig.createNavigationRule().createNavigationCase().fromAction(action).fromOutcome(fromOutcome).toViewId(toViewId);
    }

    @FindBy(xpath = "//h1[contains(@id,'title')]")
    private WebElement header;

    @FindBy(xpath = "//input[contains(@id,'buttonTarget')]")
    private WebElement buttonTarget;

    @FindBy(xpath = "//input[contains(@id,'buttonTargetController')]")
    private WebElement buttonTargetController;

    @FindBy(xpath = "//input[contains(@id,'buttonTargetRedirect')]")
    private WebElement buttonTargetRedirect;

    @FindBy(xpath = "//input[contains(@id,'buttonTargetNav1')]")
    private WebElement buttonTargetNav1;

    @FindBy(xpath = "//input[contains(@id,'buttonTargetNav2')]")
    private WebElement buttonTargetNav2;

    protected static final String headerName = "MainPage";
    protected static final String targetPage = "Target";

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void testNavigationImplicit() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonTarget.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        buttonTarget.click();

        assertEquals("Portlet should be on target page.", targetPage, header.getText());
    }

    @Test
    @RunAsClient
    public void testANavigationImplicitByMBean() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonTarget.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        buttonTargetController.click();

        assertEquals("Portlet should be on target page.", targetPage, header.getText());
    }

    @Test
    @RunAsClient
    public void testNavigationImplicitWithRedirect() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonTarget.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        buttonTargetRedirect.click();

        assertEquals("Portlet should be on target page.", targetPage, header.getText());
    }

    @Test
    @RunAsClient
    public void testNavigationRule1() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonTarget.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        buttonTargetNav1.click();

        assertEquals("Portlet should be on page1 page.", "page1", header.getText());
    }

    @Test
    @RunAsClient
    public void testNavigationRule2() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonTarget.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        buttonTargetNav2.click();

        assertEquals("Portlet should be on page2 page.", "page2", header.getText());
    }

}
