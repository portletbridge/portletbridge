package org.jboss.portletbridge.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PortalTestBase {

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

	protected HtmlPage getPortalPage() throws IOException,
			MalformedURLException {
		// Seems that Arquillian ignores @Before and @After
		if (null == webClient) {
			webClient = TestDeployment.createWebClient();

		}
		HtmlPage body = webClient
				.getPage("http://localhost:9090/integrationTest/portal");
		return body;
	}

}