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
package org.jboss.portletbridge.test.facelet;

import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
public class FaceletCompositionParameterTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/facelet/parameter/compositionParameter.xhtml", "home.xhtml")
                .addAsWebResource("pages/facelet/commonContent.xhtml", "commonContent.xhtml")
                .addAsWebResource("pages/facelet/commonFooter.xhtml", "commonFooter.xhtml")
                .addAsWebResource("pages/facelet/parameter/commonHeader.xhtml", "commonHeader.xhtml")
                .addAsWebResource("pages/facelet/parameter/commonLayout.xhtml", "commonLayout.xhtml");
        TestDeployment.addWebXml(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static final By HEADER = By.xpath("//h1[contains(@id,'header')]");
    protected static final By CONTENT = By.xpath("//h1[contains(@id,'content')]");
    protected static final By FOOTER = By.xpath("//h1[contains(@id,'footer')]");
    protected static final By HEADER_PARAM = By.xpath("//h2[contains(@id,'paramHeader')]");
    protected static final By BUTTON_TARGET_PAYMENT = By.xpath("//input[contains(@id,'buttonPay')]");
    protected static final By INPUT_QUANTITY = By.xpath("//input[contains(@id,'quantity')]");
    protected static final By CHECKBOX_REG = By.xpath("//input[contains(@id,'check')]");
    
    protected static final String headerContent = "This is parametrized header";
    protected static final String headerParamContent = "Parameter";
    protected static final String contentContent = "This is default content";
    protected static final String footerContent = "This is default footer";
    
    @ArquillianResource
    @PortalURL
    URL portalURL;
    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testFaceletCompositionParameter() throws Exception {
        driver.get(portalURL.toString());
        
        assertNotNull("Check that page contains header parameter element.", driver.findElement(HEADER_PARAM));
        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains content element.", driver.findElement(CONTENT));
        assertNotNull("Check that page contains footer element.", driver.findElement(FOOTER));

        assertTrue("Header should contain: " + headerContent, ExpectedConditions.textToBePresentInElement(HEADER, headerContent).apply(driver));
        assertTrue("Header parameter should contain: " + headerContent, ExpectedConditions.textToBePresentInElement(HEADER_PARAM, headerParamContent).apply(driver));
        assertTrue("Content should contain: " + contentContent, ExpectedConditions.textToBePresentInElement(CONTENT, contentContent).apply(driver));
        assertTrue("Footer should contain: " + footerContent, ExpectedConditions.textToBePresentInElement(FOOTER, footerContent).apply(driver));
        
    } 
}
