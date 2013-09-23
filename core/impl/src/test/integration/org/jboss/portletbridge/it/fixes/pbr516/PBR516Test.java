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
package org.jboss.portletbridge.it.fixes.pbr516;

import category.GateInOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.PortletBridgeConstants;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.portletbridge.arquillian.deployment.TestDeployment;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardAjax;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category(GateInOnly.class)
@PortalTest
public class PBR516Test extends AbstractPortletTest {

    @Deployment(name = "messageSavingDisabled")
    public static PortletArchive createDeploymentDisabled() {
        TestDeployment deployment = new TestDeployment("PBR516Test1", true);
        deployment.archive()
                .createFacesPortlet("pbr516-1", "PBR516 Portlet 1", "/pages/PBR516Test.xhtml")
                .addAsWebResource("pages/fixes/pbr516/pbr516.xhtml", "/pages/PBR516Test.xhtml")
                .addClass(PBR516Bean.class);
        return deployment.getFinalArchive();
    }

    @Deployment(name = "messageSavingEnabled")
    public static PortletArchive createDeploymentEnabled() {
        TestDeployment deployment = new TestDeployment("PBR516Test2", true);
        deployment.archive()
                .createFacesPortlet("pbr516-2", "PBR516 Portlet 2", "/pages/PBR516Test.xhtml")
                .addAsWebResource("pages/fixes/pbr516/pbr516.xhtml", "/pages/PBR516Test.xhtml")
                .addClass(PBR516Bean.class);
        deployment.webXml().createContextParam()
                .paramName(PortletBridgeConstants.SCOPE_ENABLED_ON_AJAX)
                .paramValue("true");
        return deployment.getFinalArchive();
    }

    @FindByJQuery("[id$=':messages']")
    private WebElement messages;

    @FindByJQuery("[id$=':submit']")
    private WebElement submitButton;

    @Drone
    WebDriver browser;

    protected WebDriver getBrowser() {
        return browser;
    }

    @Test
    @OperateOnDeployment("messageSavingDisabled")
    @RunAsClient
    public void facesMessagesFromAjaxCallDisappearsAfterRefresh(@ArquillianResource @PortalURL URL savingDisabledURL) throws Exception {
        browser.get(savingDisabledURL.toString());

        guardAjax(submitButton).click();

        assertTrue("Messages are not displayed.", messages.isDisplayed());
        assertTrue("Message does not contain detail.", messages.getText().contains(PBR516Bean.DETAIL_MSG));
        assertTrue("Message does not contain summary.", messages.getText().contains(PBR516Bean.SUMMARY_MSG));

        browser.navigate().refresh();

        assertFalse("Messages should not be displayed.", messages.isDisplayed());
        assertEquals("Message was not empty.", "", messages.getText());
    }

    @Test
    @OperateOnDeployment("messageSavingEnabled")
    @RunAsClient
    public void facesMessagesFromAjaxCallRetainedAfterRefresh(@ArquillianResource @PortalURL URL savingEnabledURL) throws Exception {
        browser.get(savingEnabledURL.toString());

        guardAjax(submitButton).click();

        assertTrue("Messages are not displayed.", messages.isDisplayed());
        assertTrue("Message does not contain detail.", messages.getText().contains(PBR516Bean.DETAIL_MSG));
        assertTrue("Message does not contain summary.", messages.getText().contains(PBR516Bean.SUMMARY_MSG));

        browser.navigate().refresh();

        assertTrue("Messages should be displayed.", messages.isDisplayed());
        assertTrue("Message does not contain detail.", messages.getText().contains(PBR516Bean.DETAIL_MSG));
        assertTrue("Message does not contain summary.", messages.getText().contains(PBR516Bean.SUMMARY_MSG));
    }
}
