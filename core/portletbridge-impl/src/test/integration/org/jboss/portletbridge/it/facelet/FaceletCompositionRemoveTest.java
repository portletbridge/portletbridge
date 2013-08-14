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
import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class FaceletCompositionRemoveTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(FaceletCompositionRemoveTest.class, true);
        deployment.archive()
                .createFacesPortlet("FaceletCompositionRemove", "Facelet Composition Portlet", "main.xhtml")
                .addAsWebResource("pages/facelet/remove/main.xhtml", "main.xhtml");
        return deployment.getFinalArchive();
    }

    @FindBy(jquery = "[id$='portletHeader']")
    private WebElement header;

    @FindBy(jquery = "[id$='unremoved']")
    private WebElement buttonUnremoved;

    @FindBy(jquery = "[id$='deleted']")
    private WebElement buttonRemoved;

    protected static final String headerContent = "UI:Remove";

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test(expected = NoSuchElementException.class)
    @RunAsClient
    public void testFaceletCompositionRemove() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains header element.", header.isDisplayed());

        assertEquals("Header valid content.", headerContent, header.getText());

        assertTrue("Check that page contains unremoved element.", buttonUnremoved.isDisplayed());

        buttonRemoved.isDisplayed();
    }
}
