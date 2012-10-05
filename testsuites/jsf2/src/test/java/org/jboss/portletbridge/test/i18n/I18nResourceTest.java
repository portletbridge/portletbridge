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
package org.jboss.portletbridge.test.i18n;

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
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigApplicationResourceBundleType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigApplicationType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(Arquillian.class)
public class I18nResourceTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addClass(LanguageBean.class)
                .addAsWebResource("pages/i18n/main.xhtml", "home.xhtml")
                .addAsResource("resources/welcome.properties", "org/jboss/resources/welcome.properties")
                .addAsResource("resources/welcome_zh_CN.properties", "org/jboss/resources/welcome_zh_CN.properties")
                .addAsWebInfResource(new StringAsset(getFacesXml()), "faces-config.xml");
        TestDeployment.addWebXml(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    protected static String getFacesXml() {
        WebFacesConfigDescriptor webConfig = TestDeployment.createFacesConfigXmlDescriptor();
        
        FacesConfigApplicationType<WebFacesConfigDescriptor> fcat = webConfig.createApplication();        
        fcat.createLocaleConfig().defaultLocale("en");
        
        FacesConfigApplicationResourceBundleType<FacesConfigApplicationType<WebFacesConfigDescriptor>> resourceBundle = fcat.createResourceBundle();
        resourceBundle.baseName("org.jboss.resources.welcome");
        resourceBundle.var("i18n");
        
        return webConfig.exportAsString();
    }
    
    protected static final By HEADER = By.xpath("//h1[contains(@id,'header')]");
    protected static final By MESSAGE = By.xpath("//span[contains(@id,'output')]");
    protected static final By SELECTOR = By.xpath("//select[contains(@id,'selector')]");
    
    protected static final String headerContent = "I18n";
    protected static final String enContent = "english";
    protected static final String zhContent = "chinese";
    
    @ArquillianResource
    @PortalURL
    URL portalURL;
    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testI18n() throws Exception {        
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains header element.", driver.findElement(HEADER));
        assertNotNull("Check that page contains message element.", driver.findElement(MESSAGE));
        assertNotNull("Check that page contains select element.", driver.findElement(SELECTOR));

        assertTrue("Header should be named: " + headerContent, ExpectedConditions.textToBePresentInElement(HEADER, headerContent).apply(driver));

        Select select = new Select(driver.findElement(SELECTOR));
        assertTrue("En language message should be present.", ExpectedConditions.textToBePresentInElement(MESSAGE, enContent).apply(driver));

        select.selectByValue("zh_CN");
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement toReturn = driver.findElement(MESSAGE);
                if (toReturn.getText().equals(zhContent)) {
                    return toReturn;
                }             
                return null;
            }
        });

        assertTrue("Zh_cn language message should be present.", ExpectedConditions.textToBePresentInElement(MESSAGE, zhContent).apply(driver));
    }
}
