/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.test.fixes.pbr500;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.portletbridge.test.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@PortalTest
public class PBR500Test extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(PBR500Test.class, true);
        deployment.archive()
                .createFacesPortlet("pbr500", "PBR500 Portlet", "/pages/PBR500Test.xhtml")
                .addAsWebResource("pages/fixes/pbr500/pbr500.xhtml", "/pages/PBR500Test.xhtml")
                .addAsWebResource("pages/fixes/pbr500/pbr500.xhtml", "/pages/page.xhtml")
                .addClass(PBR500Bean.class);
        return deployment.getFinalArchive();
    }

    protected static final String TEXT = "Some Text";

    @FindBy(jquery = "[id$=':input']")
    private WebElement inputField;

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitButton;

    @FindBy(jquery = "[id$=':result']")
    private WebElement resultField;

    @FindBy(jquery = "[id$=':link1']")
    private WebElement linkWithContext;

    @FindBy(jquery = "[id$=':link2']")
    private WebElement linkWithoutContext;

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
    public void viewWithSameNameAsContextPath() throws Exception {
        browser.get(portalURL.toString());

        inputField.sendKeys(TEXT);
        guardXhr(submitButton).click();

        assertEquals("Output Field set.", TEXT, resultField.getText());
    }

    @Test
    @RunAsClient
    public void generateActionUrlWithContextPathInUrl() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Link with and without context should be identical.",
                linkWithoutContext.getAttribute("href"), linkWithContext.getAttribute("href"));
    }
}
