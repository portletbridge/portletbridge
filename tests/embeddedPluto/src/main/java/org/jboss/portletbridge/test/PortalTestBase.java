package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

    protected HtmlPage getPortalPage() throws IOException, MalformedURLException {
        // Seems that Arquillian ignores @Before and @After
        if (null == webClient) {
            webClient = TestDeployment.createWebClient();

        }
        HtmlPage body = webClient.getPage("http://localhost:9090/integrationTest/portal");
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