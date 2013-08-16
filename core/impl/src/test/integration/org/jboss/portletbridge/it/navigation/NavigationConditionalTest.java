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
package org.jboss.portletbridge.it.navigation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.common.AbstractPortletTest;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigNavigationRuleType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class NavigationConditionalTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(NavigationConditionalTest.class, true);

        getFacesXml(deployment.facesConfig());

        deployment.archive()
                .createFacesPortlet("NavigationConditional", "Navigation Condition Portlet", "mainConditional.xhtml")
                .addAsWebResource("pages/navigation/mainConditional.xhtml", "mainConditional.xhtml")
                .addAsWebResource("pages/navigation/order.xhtml", "order.xhtml")
                .addAsWebResource("pages/navigation/payment.xhtml", "payment.xhtml")
                .addAsWebResource("pages/navigation/register.xhtml", "register.xhtml")
                .addClass(PaymentController.class);
        return deployment.getFinalArchive();
    }

    protected static void getFacesXml(WebFacesConfigDescriptor webConfig) {
        FacesConfigNavigationRuleType<WebFacesConfigDescriptor> rule = webConfig.createNavigationRule();
        rule.fromViewId("/mainConditional.xhtml");
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.orderQty < 100}").toViewId("/order.xhtml");
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.registerCompleted}").toViewId("/payment.xhtml");
        rule.createNavigationCase().fromOutcome("payment").toViewId("/register.xhtml");
    }

    @FindBy(xpath = "//h1[contains(@id,'title')]")
    private WebElement header;

    @FindBy(xpath = "//input[contains(@id,'buttonPay')]")
    private WebElement buttonPayment;

    @FindBy(xpath = "//input[contains(@id,'quantity')]")
    private WebElement inputQuantity;

    @FindBy(xpath = "//input[contains(@id,'check')]")
    private WebElement checkboxReg;

    protected static final String headerName = "MainPage";

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
    public void testNavigationConditionalOrder() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonPayment.isDisplayed());
        assertTrue("Check that page contains input element.", inputQuantity.isDisplayed());
        assertTrue("Check that page contains checkbox element.", checkboxReg.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        inputQuantity.clear();
        inputQuantity.sendKeys("12");

        if (!checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertEquals("Portlet should be on order page.", "order", header.getText());
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalPayment() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonPayment.isDisplayed());
        assertTrue("Check that page contains input element.", inputQuantity.isDisplayed());
        assertTrue("Check that page contains checkbox element.", checkboxReg.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        inputQuantity.clear();
        inputQuantity.sendKeys("121");

        if (!checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertEquals("Portlet should be on payment page.", "payment", header.getText());
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalRegister() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonPayment.isDisplayed());
        assertTrue("Check that page contains input element.", inputQuantity.isDisplayed());
        assertTrue("Check that page contains checkbox element.", checkboxReg.isDisplayed());

        assertEquals("Header value set.", headerName, header.getText());

        inputQuantity.clear();
        inputQuantity.sendKeys("121");

        if (checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertEquals("Portlet should be on register page.", "register", header.getText());
    }
}
