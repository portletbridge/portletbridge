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

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class NavigationTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addClass(PageController.class)
                .addAsWebResource("pages/navigation/main.xhtml", "home.xhtml")
                .addAsWebResource("pages/navigation/page1.xhtml", "page1.xhtml")
                .addAsWebResource("pages/navigation/page2.xhtml", "page2.xhtml")
                .addAsWebResource("pages/navigation/target.xhtml", "target.xhtml")
                .addAsWebInfResource(new StringAsset(getFacesXml()), "faces-config.xml");
        TestDeployment.addWebXml(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static String getFacesXml() {
        WebFacesConfigDescriptor webConfig = TestDeployment.createFacesConfigXmlDescriptor();
        addNavigationRule(webConfig, "/home.xhtml", "/target.xhtml");
        addNavigationRuleFromAction(webConfig, "#{pageController.processPage1}","success", "/page1.xhtml");
        addNavigationRuleFromAction(webConfig, "#{pageController.processPage2}","success", "/page2.xhtml");
        return webConfig.exportAsString();
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
    WebDriver driver;

    @Test
    @RunAsClient
    public void testNavigationImplicit() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.", Graphene.element(buttonTarget).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        buttonTarget.click();
        assertTrue("Portlet should be on target page.",
                Graphene.element(header).textEquals(targetPage).apply(driver));
    }

    @Test
    @RunAsClient
    public void testANavigationImplicitByMBean() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.",
                Graphene.element(buttonTargetController).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        buttonTargetController.click();
        assertTrue("Portlet should be on target page.",
                Graphene.element(header).textEquals(targetPage).apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationImplicitWithRedirect() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.",
                Graphene.element(buttonTargetRedirect).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        buttonTargetRedirect.click();
        assertTrue("Portlet should be on target page.",
                Graphene.element(header).textEquals(targetPage).apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationRule1() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.", Graphene.element(buttonTargetNav1).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        buttonTargetNav1.click();

        assertTrue("Portlet should be on page1 page.",
                Graphene.element(header).textEquals("page1").apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationRule2() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.", Graphene.element(buttonTargetNav2).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        buttonTargetNav2.click();

        assertTrue("Portlet should be on page2 page.",
                Graphene.element(header).textEquals("page2").apply(driver));
    }

}
