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
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigNavigationRuleType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class NavigationConditionalTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addClass(PaymentController.class)
                .addAsWebResource("pages/navigation/mainConditional.xhtml", "home.xhtml")
                .addAsWebResource("pages/navigation/order.xhtml", "order.xhtml")
                .addAsWebResource("pages/navigation/payment.xhtml", "payment.xhtml")
                .addAsWebResource("pages/navigation/register.xhtml", "register.xhtml")
                .addAsWebInfResource(new StringAsset(getFacesXml()), "faces-config.xml");
        TestDeployment.addWebXml(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static String getFacesXml() {
        WebFacesConfigDescriptor webConfig = TestDeployment.createFacesConfigXmlDescriptor();

        FacesConfigNavigationRuleType<WebFacesConfigDescriptor> rule = webConfig.createNavigationRule();
        rule.fromViewId("/home.xhtml");
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.orderQty < 100}").toViewId("/order.xhtml");
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.registerCompleted}").toViewId("/payment.xhtml");
        rule.createNavigationCase().fromOutcome("payment").toViewId("/register.xhtml");

        return webConfig.exportAsString();
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
    WebDriver driver;

    @Test
    @RunAsClient
    public void testNavigationConditionalOrder() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.", Graphene.element(buttonPayment).isVisible().apply(driver));
        assertTrue("Check that page contains input element.", Graphene.element(inputQuantity).isVisible().apply(driver));
        assertTrue("Check that page contains checkbox element.", Graphene.element(checkboxReg).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        inputQuantity.clear();
        inputQuantity.sendKeys("12");

        if (!checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertTrue("Portlet should be on order page.",
                Graphene.element(header).textEquals("order").apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalPayment() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", Graphene.element(header).isVisible().apply(driver));
        assertTrue("Check that page contains button element.", Graphene.element(buttonPayment).isVisible().apply(driver));
        assertTrue("Check that page contains input element.", Graphene.element(inputQuantity).isVisible().apply(driver));
        assertTrue("Check that page contains checkbox element.", Graphene.element(checkboxReg).isVisible().apply(driver));

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        inputQuantity.clear();
        inputQuantity.sendKeys("121");

        if (!checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertTrue("Portlet should be on payment page.",
                Graphene.element(header).textEquals("payment").apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalRegister() throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains button element.", buttonPayment.isDisplayed());
        assertTrue("Check that page contains input element.", inputQuantity.isDisplayed());
        assertTrue("Check that page contains checkbox element.", checkboxReg.isDisplayed());

        assertTrue("Header should be named: " + headerName,
                Graphene.element(header).textEquals(headerName).apply(driver));

        inputQuantity.clear();
        inputQuantity.sendKeys("121");

        if (checkboxReg.isSelected()) {
            checkboxReg.click();
        }

        buttonPayment.click();

        assertTrue("Portlet should be on register page.",
                Graphene.element(header).textEquals("register").apply(driver));
    }
}
