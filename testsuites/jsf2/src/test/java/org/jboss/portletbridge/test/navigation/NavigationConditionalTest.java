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

import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigNavigationRuleType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
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
        
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.orderQty < 100}").toViewId("/order.xhtml");
        rule.createNavigationCase().fromOutcome("payment")._if("#{paymentController.registerCompleted}").toViewId("/payment.xhtml");
        rule.createNavigationCase().fromOutcome("payment").toViewId("/register.xhtml");
                
        return webConfig.exportAsString();
    }

    protected static final By HEADER = By.xpath("//h1[contains(@id,'title')]");
    protected static final By BUTTON_TARGET_PAYMENT = By.xpath("//input[contains(@id,'buttonPay')]");
    protected static final By INPUT_QUANTITY = By.xpath("//input[contains(@id,'quantity')]");
    protected static final By CHECKBOX_REG = By.xpath("//input[contains(@id,'check')]");
    
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

        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains button element.", driver.findElement(BUTTON_TARGET_PAYMENT));
        assertNotNull("Check that page contains input element.", driver.findElement(INPUT_QUANTITY));
        assertNotNull("Check that page contains checkbox element.", driver.findElement(CHECKBOX_REG));        

        assertTrue("Header should be named: " + headerName, ExpectedConditions.textToBePresentInElement(HEADER, headerName).apply(driver));

        driver.findElement(INPUT_QUANTITY).sendKeys("12");
        
        if (!driver.findElement(CHECKBOX_REG).isEnabled()) {
            driver.findElement(CHECKBOX_REG).click();
        }
        
        driver.findElement(BUTTON_TARGET_PAYMENT).click();
       
        assertTrue("Portlet should be on order page.", ExpectedConditions.textToBePresentInElement(HEADER, "order").apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalPayment() throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains button element.", driver.findElement(BUTTON_TARGET_PAYMENT));
        assertNotNull("Check that page contains input element.", driver.findElement(INPUT_QUANTITY));
        assertNotNull("Check that page contains checkbox element.", driver.findElement(CHECKBOX_REG));       

        assertTrue("Header should be named: " + headerName, ExpectedConditions.textToBePresentInElement(HEADER, headerName).apply(driver));
                
        driver.findElement(INPUT_QUANTITY).clear();
        driver.findElement(INPUT_QUANTITY).sendKeys("121");
        
        if (!driver.findElement(CHECKBOX_REG).isEnabled()) {
            driver.findElement(CHECKBOX_REG).click();
        }
        
        driver.findElement(BUTTON_TARGET_PAYMENT).click();
        
        assertTrue("Portlet should be on payment page.", ExpectedConditions.textToBePresentInElement(HEADER, "payment").apply(driver));
    }

    @Test
    @RunAsClient
    public void testNavigationConditionalRegister() throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains button element.", driver.findElement(BUTTON_TARGET_PAYMENT));
        assertNotNull("Check that page contains input element.", driver.findElement(INPUT_QUANTITY));
        assertNotNull("Check that page contains checkbox element.", driver.findElement(CHECKBOX_REG));       

        assertTrue("Header should be named: " + headerName, ExpectedConditions.textToBePresentInElement(HEADER, headerName).apply(driver));

        driver.findElement(INPUT_QUANTITY).sendKeys("121");
        
        if (driver.findElement(CHECKBOX_REG).isEnabled()) {
            driver.findElement(CHECKBOX_REG).click();
        }
        
        driver.findElement(BUTTON_TARGET_PAYMENT).click();       
        
        assertTrue("Portlet should be on register page.", ExpectedConditions.textToBePresentInElement(HEADER, "register").apply(driver));
    }
}
