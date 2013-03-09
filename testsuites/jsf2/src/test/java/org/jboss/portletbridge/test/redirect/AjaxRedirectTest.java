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
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.jboss.arquillian.graphene.Graphene.guardXhr;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@PortalTest
public class AjaxRedirectTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(AjaxRedirectTest.class, true);

        getFacesConfigXml(deployment.facesConfig());

        deployment.archive()
                .createFacesPortlet("AjaxRedirect", "Ajax Redirect Portlet", "ajax.xhtml")
                .addAsWebResource("pages/redirect/ajax.xhtml", "ajax.xhtml")
                .addAsWebResource("pages/redirect/done1.xhtml", "done1.xhtml")
                .addAsWebResource("pages/redirect/done2.xhtml", "done2.xhtml");
        return deployment.getFinalArchive();
    }

    private static void getFacesConfigXml(WebFacesConfigDescriptor facesConfig) {
        facesConfig.createNavigationRule()
                       .fromViewId("/ajax.xhtml")
                       .createNavigationCase()
                           .fromOutcome("doneRedirect")
                           .toViewId("/done1.xhtml")
                           .getOrCreateRedirect()
                               .up()
                           .up()
                       .up();
    }

    protected static final String DONE1 = "Done1";
    protected static final String DONE2 = "Done2";

    @FindBy(id = "output")
    private WebElement outputField;

    @FindBy(jquery = "[id$=':submit1']")
    private WebElement submitButton1;

    @FindBy(jquery = "[id$=':submit2']")
    private WebElement submitButton2;

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
    public void redirectInFacesConfig() throws Exception {
        browser.get(portalURL.toString());

        submitButton1.click();

        waitAjax().until().element(outputField).is().present();

        assertEquals("Output text should contain: " + DONE1, DONE1, outputField.getText());
    }

    @Test
    @RunAsClient
    public void redirectInButtonAction() throws Exception {
        browser.get(portalURL.toString());

        submitButton2.click();

        waitAjax().until().element(outputField).is().present();

        assertEquals("Output text should contain: " + DONE2, DONE2, outputField.getText());
    }

}
