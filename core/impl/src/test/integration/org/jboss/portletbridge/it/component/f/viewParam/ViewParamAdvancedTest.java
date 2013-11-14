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
package org.jboss.portletbridge.it.component.f.viewParam;

import category.GateInOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
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

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category(GateInOnly.class)
@PortalTest
public class ViewParamAdvancedTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(ViewParamAdvancedTest.class, true);
        deployment.archive()
                .createFacesPortlet("ViewParamAdvanced", "View Param Advanced Portlet", "self.xhtml")
                .addAsWebResource("pages/component/f/viewParam/self.xhtml", "self.xhtml");
        return deployment.getFinalArchive();
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Override
    protected WebDriver getBrowser() {
        return browser;
    }

    @FindByJQuery("[id$=':paramValue']")
    private WebElement paramValue;

    @FindByJQuery("[id$=':scopeValue']")
    private WebElement scopeValue;

    @FindByJQuery("[id$=':setValue']")
    private WebElement setValue;

    @FindByJQuery("[id$=':unsetValue']")
    private WebElement unsetValue;

    @FindByJQuery("[id$=':postback']")
    private WebElement postbackSubmit;

    @FindByJQuery("[id$=':linkParams']")
    private WebElement linkWithViewParams;

    @FindByJQuery("[id$=':linkNoParams']")
    private WebElement linkWithNoViewParams;

    @Test
    @InSequence(1)
    @RunAsClient
    public void setAndUnset() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());

        guardAjax(setValue).click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        guardAjax(unsetValue).click();

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void postbackClearsParams() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());

        guardAjax(setValue).click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        postbackSubmit.click();

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void linkWithViewParameters() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());

        guardAjax(setValue).click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        linkWithViewParams.click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        // Reset for next test
        linkWithNoViewParams.click();
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void linkWithViewParametersAndPostback() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());

        guardAjax(setValue).click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        linkWithViewParams.click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        postbackSubmit.click();

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        // Reset for next test
        linkWithNoViewParams.click();
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void linkWithNoViewParameters() throws Exception {
        browser.get(portalURL.toString());

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());

        guardAjax(setValue).click();

        assertEquals("Param value is not 'value'", "Param:=value", paramValue.getText());
        assertEquals("Scope value is not 'value'", "requestScope:=value", scopeValue.getText());

        linkWithNoViewParams.click();

        assertEquals("Param value is set", "Param:=", paramValue.getText());
        assertEquals("Scope value is set", "requestScope:=", scopeValue.getText());
    }
}
