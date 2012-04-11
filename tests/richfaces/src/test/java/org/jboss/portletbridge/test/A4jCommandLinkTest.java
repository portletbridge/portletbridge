package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.hamcrest.Matchers;
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

//@RunWith(Arquillian.class)
public class A4jCommandLinkTest extends PortalTestBase {

	public static final String NEW_VALUE = "New Value";

	@Deployment(testable = false)
	public static WebArchive createDeployment()

	{

		return TestDeployment.createDeployment()
				.addAsWebResource("output.xhtml", "output.xhtml")
				.addAsWebResource("a4jLink.xhtml", "home.xhtml");
	}

	//@Test
	public void renderFormPortlet() throws Exception

	{
		HtmlPage portalPage = getPortalPage();


		verifyOutput(portalPage, Bean.HELLO_JSF_PORTLET);

		verifyInput(portalPage, Bean.HELLO_JSF_PORTLET);

		HtmlElement submit = getFirstChildById(portalPage,"submit");

		assertThat(submit.asText(), containsString("Ok"));

		Iterable<HtmlElement> links = portalPage.getElementsByTagName("script");
		assertThat(links, Matchers.<HtmlElement>hasItem(TestDeployment.htmlAttributeMatcher("src",containsString("richfaces"))));
	}



	//@Test
	public void testSubmitAndRemainOnPage() throws Exception {
		HtmlPage portalPage = getPortalPage();
		HtmlPage responsePage = submitForm(portalPage,NEW_VALUE);
		assertSame(portalPage, responsePage);
		verifyInput(responsePage, NEW_VALUE);
		verifyOutput(responsePage, NEW_VALUE);
		// Re-render page
		HtmlPage reRenderPage = portalPage;
		verifyInput(reRenderPage, NEW_VALUE);
		verifyOutput(reRenderPage, NEW_VALUE);
	}


}
