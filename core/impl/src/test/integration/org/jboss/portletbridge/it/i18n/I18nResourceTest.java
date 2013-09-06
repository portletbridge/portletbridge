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
package org.jboss.portletbridge.it.i18n;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigApplicationResourceBundleType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.FacesConfigApplicationType;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class I18nResourceTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(I18nResourceTest.class, true);

        getFacesXml(deployment.facesConfig());

        deployment.archive()
                .createFacesPortlet("I18nResource", "I18n Resource Portlet", "main.xhtml")
                .addAsWebResource("pages/i18n/main.xhtml", "main.xhtml")
                .addAsResource("resources/welcome.properties", "org/jboss/resources/welcome.properties")
                .addAsResource("resources/welcome_zh_CN.properties", "org/jboss/resources/welcome_zh_CN.properties")
                .addClass(LanguageBean.class);
        return deployment.getFinalArchive();
    }

    protected static void getFacesXml(WebFacesConfigDescriptor webConfig) {
        FacesConfigApplicationType<WebFacesConfigDescriptor> fcat = webConfig.createApplication();
        fcat.createLocaleConfig().defaultLocale("en");

        FacesConfigApplicationResourceBundleType<FacesConfigApplicationType<WebFacesConfigDescriptor>> resourceBundle = fcat.createResourceBundle();
        resourceBundle.baseName("org.jboss.resources.welcome");
        resourceBundle.var("i18n");
    }

    @FindBy(jquery = "[id$='portletHeader']")
    private WebElement header;

    @FindBy(jquery = "[id$='output']")
    private WebElement message;

    @FindBy(jquery = "[id$='selector']")
    private WebElement selector;

    protected static final String headerContent = "I18n";
    protected static final String enContent = "english";
    protected static final String zhContent = "chinese";

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
    public void testI18n() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains message element.", message.isDisplayed());
        assertTrue("Check that page contains select element.", selector.isDisplayed());

        assertEquals("Header has valid content.", headerContent, header.getText());

        Select select = new Select(selector);
        assertEquals("En language message should be present.", enContent, message.getText());

        guardXhr(select).selectByValue("zh_CN");

        assertEquals("Zh_cn language message should be present.", zhContent, message.getText());
    }
}