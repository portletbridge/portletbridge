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
package org.jboss.portletbridge.test.redirect;

import static org.junit.Assert.assertTrue;

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
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
public class AjaxRedirectTest {

    @Deployment()
    public static WebArchive createDeployment() {
        WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/redirect/ajax.xhtml", "home.xhtml")
                .addAsWebResource("pages/redirect/done1.xhtml", "done1.xhtml")
                .addAsWebResource("pages/redirect/done2.xhtml", "done2.xhtml")
                .addAsWebInfResource(new StringAsset(getFacesConfigXml()), "faces-config.xml");
        TestDeployment.addWebXml(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    private static String getFacesConfigXml() {
        WebFacesConfigDescriptor facesConfig = TestDeployment.createFacesConfigXmlDescriptor();
        facesConfig.createNavigationRule()
                       .fromViewId("/home.xhtml")
                       .createNavigationCase()
                           .fromOutcome("doneRedirect")
                           .toViewId("/done1.xhtml")
                           .getOrCreateRedirect()
                               .up()
                           .up()
                       .up();

        return facesConfig.exportAsString();
    }

    protected static final String DONE1 = "Done1";
    protected static final String DONE2 = "Done2";

    protected static final By OUTPUT_FIELD = By.id("output");
    protected static final By SUBMIT_BUTTON_1 = By.xpath("//input[@value='done1']");
    protected static final By SUBMIT_BUTTON_2 = By.xpath("//input[@value='done2']");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void redirectInFacesConfig() throws Exception {
        driver.get(portalURL.toString());
        driver.findElement(SUBMIT_BUTTON_1).click();

        assertTrue("output text should contain: " + DONE1,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, DONE1).apply(driver));
    }

    @Test
    @RunAsClient
    public void redirectInButtonAction() throws Exception {
        driver.get(portalURL.toString());
        driver.findElement(SUBMIT_BUTTON_2).click();

        assertTrue("output text should contain: " + DONE2,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, DONE2).apply(driver));
    }

}
