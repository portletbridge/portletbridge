package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.*;

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

        return PortletArchive.create().addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                .addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
    }

    @Test
    public void renderFacesPortlet() throws Exception

    {

        // http://localhost:8080/test/

        String body = readAllAndClose(new URL("http://localhost:9090/integrationTest/portal").openStream());

        Assert.assertThat(

        "Verify that the portlet was deployed and returns the expected result",

        body, containsString(TestPortlet.HELLO_FROM_EMBEDDED_PORTAL));

    }

    private String readAllAndClose(InputStream openStream) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(openStream);
        int c;
        try {
            while ((c = reader.read()) >= 0) {
                content.append((char) c);
            }
        } finally {
            openStream.close();
        }
        return content.toString();
    }

}
