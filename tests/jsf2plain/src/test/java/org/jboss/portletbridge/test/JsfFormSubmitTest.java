package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

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

		System.out.println(portalPage.asXml());

		verifyOutput(portalPage, Bean.HELLO_JSF_PORTLET);

		verifyInput(portalPage, Bean.HELLO_JSF_PORTLET);

		HtmlSubmitInput submit = getSubmit(portalPage);

		assertThat(submit.getValueAttribute(), containsString("Ok"));
	}

	//@Test
	public void testSubmitAndRemainOnPage() throws Exception {
		HtmlPage portalPage = getPortalPage();
		HtmlPage responsePage = submitForm(portalPage,NEW_VALUE);
		verifyInput(responsePage, NEW_VALUE);
		verifyOutput(responsePage, NEW_VALUE);
		// Re-render page
		HtmlPage reRenderPage = getPortalPage();
		verifyInput(reRenderPage, NEW_VALUE);
		verifyOutput(reRenderPage, NEW_VALUE);
	}

}
