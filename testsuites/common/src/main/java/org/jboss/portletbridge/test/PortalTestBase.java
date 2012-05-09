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
package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class PortalTestBase {

    public static final String OUTPUT = "output";
    public static final String SUBMIT = "submit";
    public static final String INPUT = "input";
    private WebClient webClient;

    public PortalTestBase() {
        super();
    }

    @Before
    public void createClient() {
        webClient = TestDeployment.createWebClient();
    }

    @After
    public void destroyClient() {
        webClient.closeAllWindows();
    }

    protected HtmlPage getPortalPage(String portalUrl) throws IOException, MalformedURLException {
        // Seems that Arquillian ignores @Before and @After
        if (null == webClient) {
            webClient = TestDeployment.createWebClient();

        }
        HtmlPage body = webClient.getPage(portalUrl);
        return body;
    }

    protected void verifyInput(HtmlPage portalPage, String value) {
        HtmlTextInput input = getInput(portalPage);

        assertThat(input.getValueAttribute(), containsString(value));
    }

    protected void verifyOutput(HtmlPage portalPage, String value) {
        HtmlElement output = getOutput(portalPage);

        assertThat(output.asText(), containsString(value));
    }

    protected HtmlPage submitForm(HtmlPage portalPage, String inputValue) throws IOException, MalformedURLException {
        getInput(portalPage).setValueAttribute(inputValue);
        HtmlPage responsePage = getFirstChildById(portalPage, SUBMIT).click();
        return responsePage;
    }

    protected HtmlTextInput getInput(HtmlPage portalPage) {
        HtmlTextInput input = (HtmlTextInput) getFirstChildById(portalPage, INPUT);
        return input;
    }

    protected HtmlElement getFirstChildById(HtmlPage portalPage, String id) {
        return (HtmlElement) portalPage.getHtmlElementById(id).getFirstChild();
    }

    protected HtmlSubmitInput getSubmit(HtmlPage portalPage) {
        HtmlSubmitInput input = (HtmlSubmitInput) getFirstChildById(portalPage, SUBMIT);
        return input;
    }

    protected HtmlElement getOutput(HtmlPage portalPage) {
        HtmlElement output = portalPage.getHtmlElementById(OUTPUT);
        return output;
    }

}