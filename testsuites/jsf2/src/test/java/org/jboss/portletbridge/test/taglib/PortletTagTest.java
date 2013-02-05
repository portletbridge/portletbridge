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
package org.jboss.portletbridge.test.taglib;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class PortletTagTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(PortletTagTest.class, true);

        deployment.archive()
                .createFacesPortlet("PortletTags", "Tags Portlet", "tags.xhtml", "edit.xhtml")
                .addAsWebResource("taglib/tags.xhtml", "tags.xhtml")
                .addAsWebResource("taglib/edit.xhtml", "edit.xhtml");
        return deployment.getFinalArchive();
    }

    @FindBy(id = "spanId")
    private WebElement spanField;

    @FindBy(id = "namespaceSpanId")
    private WebElement namespaceSpanField;

    @FindBy(id = "modeName")
    private WebElement modeNameField;

    @FindBy(linkText = "Render")
    private WebElement renderLink;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void testNamespace() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("spanId text set.", "spanId", spanField.getText());

        // TODO Figure out how to retrieve the namespace during browser.get call
        //      so that it can be compared against what is in field
        assertTrue("namespaceSpanId text length should be greater than spanId",
                namespaceSpanField.getText().length() > "spanId".length());
    }

    @Test
    @RunAsClient
    public void testRenderUrl() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Should be in Portlet Mode view.", "View", modeNameField.getText());

        renderLink.click();

        assertEquals("Should be in Portlet Mode edit.", "Edit", modeNameField.getText());

        renderLink.click();

        assertEquals("Should be in Portlet Mode view.", "View", modeNameField.getText());
    }
}
