package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

@RunWith(Arquillian.class)
public class JsfFormSubmitTest extends PortalTestBase {

	public static final String NEW_VALUE = "New Value";

	@Deployment(testable = false)
	public static WebArchive createDeployment()

	{

		return TestDeployment.createDeployment()
				.addAsWebResource("output.xhtml", "output.xhtml")
				.addAsWebResource("form.xhtml", "home.xhtml");
	}

	@Test
	public void renderFormPortlet() throws Exception

	{
		HtmlPage portalPage = getPortalPage();

		HtmlForm form = portalPage.getForms().get(0);

		verifyOutput(portalPage, Bean.HELLO_JSF_PORTLET);

		verifyInput(portalPage, Bean.HELLO_JSF_PORTLET);

		HtmlSubmitInput submit = getSubmit(portalPage);

		assertThat(submit.getValueAttribute(), containsString("Ok"));
	}

	private void verifyInput(HtmlPage portalPage, String value) {
		HtmlTextInput input = getInput(portalPage);

		assertThat(input.getValueAttribute(),
				containsString(value));
	}

	private void verifyOutput(HtmlPage portalPage, String value) {
		HtmlElement output = getOutput(portalPage);

		assertThat(output.asText(), containsString(value));
	}

	@Test
	public void testSubmitAndRemainOnPage() throws Exception {
		HtmlPage responsePage = submitForm(NEW_VALUE);
		verifyInput(responsePage, NEW_VALUE);
		verifyOutput(responsePage, NEW_VALUE);
		// Re-render page
		HtmlPage portalPage = getPortalPage();
		verifyInput(portalPage, NEW_VALUE);
		verifyOutput(portalPage, NEW_VALUE);
	}

	private HtmlPage submitForm(String inputValue) throws IOException, MalformedURLException {
		HtmlPage portalPage = getPortalPage();
		getInput(portalPage).setValueAttribute(inputValue);
		HtmlPage responsePage = getSubmit(portalPage).click();
		return responsePage;
	}
	
	private HtmlTextInput getInput(HtmlPage portalPage) {
		HtmlTextInput input = (HtmlTextInput) portalPage.getHtmlElementById(
				"input").getFirstChild();
		return input;
	}

	private HtmlSubmitInput getSubmit(HtmlPage portalPage) {
		HtmlSubmitInput input = (HtmlSubmitInput) portalPage
				.getHtmlElementById("submit").getFirstChild();
		return input;
	}

	private HtmlElement getOutput(HtmlPage portalPage) {
		HtmlElement output = portalPage.getHtmlElementById("output");
		return output;
	}

}
