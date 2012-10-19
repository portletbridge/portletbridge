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
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
@PortalTest
public class FaceletCompositionCustomTagTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebAppDescriptor webConfig = TestDeployment.createWebXmlDescriptor();        
        webConfig.createContextParam()
                 .paramName("facelets.LIBRARIES")
                 .paramValue("/WEB-INF/pbr.taglib.xml");
        
        WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/facelet/customTag/main.xhtml", "home.xhtml")
                .addAsWebResource("pages/facelet/customTag/button.xhtml", "button.xhtml")
                .addAsWebInfResource(new StringAsset(webConfig.exportAsString()), "web.xml")
                .addAsWebInfResource("pages/facelet/customTag/pbr.taglib.xml", "pbr.taglib.xml");
               
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static final By HEADER = By.xpath("//h1[contains(@id,'header')]");
    protected static final By BUTTON_CUSTOM = By.xpath("//input[contains(@id,'customButton')]");
    
    protected static final String headerContent = "This is default header";
    protected static final String contentContent = "This is defined content";
    protected static final String footerContent = "This is defined footer";
    
    @ArquillianResource
    @PortalURL
    URL portalURL;
    @Drone
    WebDriver driver;
    
    @Test
    @RunAsClient
    public void testFaceletCompositionCustomTag() throws Exception {
        driver.get(portalURL.toString());
        
        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains header element.", driver.findElement(BUTTON_CUSTOM));        
    } 
}
