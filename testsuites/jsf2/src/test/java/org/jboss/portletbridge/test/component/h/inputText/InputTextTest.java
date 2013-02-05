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
package org.jboss.portletbridge.test.component.h.inputText;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.spi.annotations.Page;
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

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@PortalTest
public class InputTextTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(InputTextTest.class, true);
        deployment.archive()
                .createFacesPortlet("InputText", "Input Text Portlet", "inputText.xhtml")
                .addAsWebResource("pages/component/h/inputText/inputtext.xhtml", "inputText.xhtml")
                .addClass(InputTextBean.class);
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Page
    InputTextPage page;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    @Test
    @RunAsClient
    public void testInputText() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", page.getInputOne().isDisplayed());
        assertTrue("Check that page contains OUTPUT ONE element.", page.getOutputOne().isDisplayed());
        assertTrue("Check that page contains SUBMIT ONE element.", page.getSubmitOne().isDisplayed());

        assertEquals("Check that INPUT ONE element starts empty.", "", page.getInputOne().getText());
        assertEquals("Check that OUTPUT ONE element starts empty.", "", page.getOutputOne().getText());

        String textToInput = "pbr";

        page.getInputOne().sendKeys(textToInput);
        page.getSubmitOne().click();

        assertEquals("OUTPUT ONE element should have inputted text.", textToInput, page.getOutputOne().getText());
    }

    @Test
    @RunAsClient
    public void testAjaxInputText() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains INPUT TWO element.", page.getInputTwo().isDisplayed());
        assertTrue("Check that page contains OUTPUT TWO element.", page.getOutputTwo().isDisplayed());

        assertEquals("Check that INPUT TWO element starts empty.", "", page.getInputTwo().getText());
        assertEquals("Check that OUTPUT TWO element starts empty.", "", page.getOutputTwo().getText());

        final String textToInput = "pbr";

        for (String s : textToInput.split("")) {
            if (!s.equals("")) {
                guardXhr(page.getInputTwo()).sendKeys(s);
            }
        }

        assertEquals("OUTPUT TWO element should have inputted text.", textToInput, page.getOutputTwo().getText());
    }

    @Test
    @RunAsClient
    public void testOnChange() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains INPUT TWO element.", page.getInputTwo().isDisplayed());
        assertTrue("Check that page contains OUTPUT TWO element.", page.getOutputTwo().isDisplayed());

        assertEquals("Check that INPUT TWO element starts empty.", "", page.getInputTwo().getText());
        assertEquals("Check that OUTPUT TWO element starts empty.", "", page.getOutputTwo().getText());

        final String textToInput = "pbr";

        // Fill input one and submit
        page.getInputOne().sendKeys(textToInput);
        page.getSubmitOne().click();

        // Fill input two, char by char
        for (String s : textToInput.split("")) {
            if (!s.equals("")) {
                guardXhr(page.getInputTwo()).sendKeys(s);
            }
        }

        assertEquals("OUTPUT ONE COUNTER element should have inputted text length.",
                String.valueOf(textToInput.length()), page.getOutputOneCounter().getText());
    }

    @Test
    @RunAsClient
    public void testRequired() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", page.getInputOne().isDisplayed());
        assertTrue("Check that page contains OUTPUT ONE element.", page.getOutputOne().isDisplayed());
        assertTrue("Check that page contains SUBMIT ONE element.", page.getSubmitOne().isDisplayed());

        assertEquals("Check that INPUT ONE element starts empty.", "", page.getInputOne().getText());
        assertEquals("Check that OUTPUT ONE element starts empty.", "", page.getOutputOne().getText());

        // Submit with no input
        page.getSubmitOne().click();

        assertEquals("MESSAGES should contain error message.", InputTextBean.REQUIRED_MESSAGE, page.getMessages().getText());
    }

    @Test
    @RunAsClient
    public void testValidateLength() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("Check that page contains INPUT ONE element.", page.getInputOne().isDisplayed());
        assertTrue("Check that page contains OUTPUT ONE element.", page.getOutputOne().isDisplayed());
        assertTrue("Check that page contains SUBMIT ONE element.", page.getSubmitOne().isDisplayed());

        assertEquals("Check that INPUT ONE element starts empty.", "", page.getInputOne().getText());
        assertEquals("Check that OUTPUT ONE element starts empty.", "", page.getOutputOne().getText());

        // Submit a small input
        page.getInputOne().sendKeys("pb");
        page.getSubmitOne().click();

        assertEquals("MESSAGES should contain error message.", InputTextBean.MIN_LENGTH_MESSAGE, page.getMessages().getText());
    }

}
