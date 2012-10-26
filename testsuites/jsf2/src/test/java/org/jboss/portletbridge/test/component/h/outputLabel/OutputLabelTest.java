package org.jboss.portletbridge.test.component.h.outputLabel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
@PortalTest
public class OutputLabelTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addAsWebResource("pages/component/h/outputLabel/outputlabel.xhtml", "home.xhtml")
                .addClass(OutputLabelBean.class);
        // .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @FindBy(xpath = "//label[contains(@id,':output1')]")
    private WebElement outputOne;

    @FindBy(xpath = "//label[contains(@id,':output2')]")
    private WebElement outputTwo;

    @Test
    @RunAsClient
    public void testOutputLabel(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT ONE element.", Graphene.element(outputOne).isVisible().apply(driver));

        assertTrue("Check that OUTPUT ONE contains the expected text with HTML markup.",
                Graphene.element(outputOne).textEquals(OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLabelEscape(@Drone WebDriver driver) throws Exception {
        // Set outputLabel not to escape XML/HTML
        OutputLabelBean.OUTPUT_LABEL_ESCAPE = false;

        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT ONE element.", Graphene.element(outputOne).isVisible().apply(driver));

        assertTrue("Check that OUTPUT ONE contains the expected text without HTML markup.",
                Graphene.element(outputOne).textEquals(OutputLabelBean.OUTPUT_LABEL_DEFAULT_PLAINTEXT).apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLabelRendered(@Drone WebDriver driver) throws Exception {
        // Set outputLabel not to render
        OutputLabelBean.OUTPUT_LABEL_RENDER = false;

        driver.get(portalURL.toString());

        assertFalse("Check that page does not contains OUTPUT ONE element.",
                Graphene.element(outputOne).isVisible().apply(driver));
    }

    @Test
    @RunAsClient
    public void testOutputLabelConverter(@Drone WebDriver driver) throws Exception {
        driver.get(portalURL.toString());

        assertTrue("Check that page contains OUTPUT TWO element.", Graphene.element(outputTwo).isVisible().apply(driver));

        assertTrue("Check that OUTPUT TWO contains the text length in Float format.",
                Graphene.element(outputTwo).
                textEquals(Float.valueOf(OutputLabelBean.OUTPUT_LABEL_DEFAULT_HTML.length()).toString()).apply(driver));
    }

}
