package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.faces.webapp.FacesServlet;

import org.apache.tools.ant.filters.TokenFilter.ContainsString;
import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class HelloJsfPortletTest extends PortalTestBase {

    @Deployment(testable = false)
    public static WebArchive createDeployment()

    {

        return TestDeployment.createDeployment().addAsWebResource("output.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @Test
    public void renderFacesPortlet() throws Exception

    {

        HtmlPage body = getPortalPage();
        HtmlElement element = body.getElementById("output");
        assertNotNull("Check what page contains output element", element);
        Assert.assertThat(

        "Verify that the portlet was deployed and returns the expected result",

        element.asText(), containsString(Bean.HELLO_JSF_PORTLET));
        List<HtmlElement> links = body.getElementsByTagName("link");
        assertThat(links, contains(TestDeployment.htmlAttributeMatcher("href", containsString("stylesheet.css"))));
    }

}
