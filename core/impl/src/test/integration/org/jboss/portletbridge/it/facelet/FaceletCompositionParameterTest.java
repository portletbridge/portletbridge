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
package org.jboss.portletbridge.it.facelet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class FaceletCompositionParameterTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(FaceletCompositionParameterTest.class, true);
        deployment.archive()
                .createFacesPortlet("FaceletCompositionParameter", "Facelet Composition Portlet", "compositionParameter.xhtml")
                .addAsWebResource("pages/facelet/parameter/compositionParameter.xhtml", "compositionParameter.xhtml")
                .addAsWebResource("pages/facelet/commonContent.xhtml", "commonContent.xhtml")
                .addAsWebResource("pages/facelet/commonFooter.xhtml", "commonFooter.xhtml")
                .addAsWebResource("pages/facelet/parameter/commonHeader.xhtml", "commonHeader.xhtml")
                .addAsWebResource("pages/facelet/parameter/commonLayout.xhtml", "commonLayout.xhtml");
        return deployment.getFinalArchive();
    }

    @FindBy(xpath = "//h1[contains(@id,'header')]")
    private WebElement header;

    @FindBy(xpath = "//h1[contains(@id,'content')]")
    private WebElement content;

    @FindBy(xpath = "//h1[contains(@id,'footer')]")
    private WebElement footer;

    @FindBy(xpath = "//h2[contains(@id,'paramHeader')]")
    private WebElement headerParam;

    protected static final String headerContent = "This is parametrized header";
    protected static final String headerParamContent = "Parameter";
    protected static final String contentContent = "This is default content";
    protected static final String footerContent = "This is default footer";

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
    public void testFaceletCompositionParameter() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());
        assertTrue("Check that page contains header parameter element.", headerParam.isDisplayed());
        assertTrue("Check that page contains content element.", content.isDisplayed());
        assertTrue("Check that page contains footer element.", footer.isDisplayed());

        assertEquals("Header valid content.", headerContent, header.getText());
        assertEquals("Header parameter valid content.", headerParamContent, headerParam.getText());
        assertEquals("Content valid content.", contentContent, content.getText());
        assertEquals("Footer valid content.", footerContent, footer.getText());
    }
}
