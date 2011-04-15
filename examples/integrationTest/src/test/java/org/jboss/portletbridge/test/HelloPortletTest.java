package org.jboss.portletbridge.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.faces.webapp.FacesServlet;

import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HelloPortletTest {

	@Deployment(testable = false)
	public static WebArchive createDeployment()

	{

		return ShrinkWrap.create(WebArchive.class, "integrationTest.war")
		.addClass(Bean.class)
		.addAsResource("home.xhtml")
		.addAsWebInfResource("WEB-INF/themes/pluto.jsp", "themes/pluto.jsp")
		.addAsWebInfResource("WEB-INF/themes/portlet-skin.jsp", "themes/portlet-skin.jsp")
		.addAsWebInfResource("WEB-INF/tld/pluto.tld", "tld/pluto.tld")
		.addAsWebInfResource("pluto-resources/web.xml", "web.xml")
		.addAsWebInfResource("WEB-INF/pluto-portal-driver-config.xml")
		.addAsWebInfResource("WEB-INF/faces-config.xml")
		.addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
	}

	@Test
	public void shouldBeAbleToCallServlet()
			throws Exception

	{

		// http://localhost:8080/test/

		String body = readAllAndClose(new URL(
				"http://localhost:9090/integrationTest/portal").openStream());

		Assert.assertEquals(

		"Verify that the servlet was deployed and returns the expected result",

		"hello",

		body);

	}

	private String readAllAndClose(InputStream openStream) throws IOException {
		StringBuilder content = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(openStream);
		int c;
		try {
			while ((c = reader.read()) >= 0) {
				content.append((char)c);
			}
		} finally {
			openStream.close();
		}
		return content.toString();
	}

}
