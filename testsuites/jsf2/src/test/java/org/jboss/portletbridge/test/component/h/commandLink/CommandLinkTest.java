package org.jboss.portletbridge.test.component.h.commandLink;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.portletbridge.test.component.h.commandButton.CommandButtonBean;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

@RunWith(Arquillian.class)
public class CommandLinkTest {


    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/commandLink/commandlink.xhtml", "home.xhtml")
                .addClass(CommandLinkBean.class);
        //.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    //@Drone FIXME: JS not working
    WebDriver driver = new HtmlUnitDriver(true);

    @Test
    @RunAsClient
    public void testCommandLinkTypes() throws Exception {
        driver.get(portalURL.toString());
        System.out.println(driver.getPageSource());

    }
}
