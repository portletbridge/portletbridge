package org.jboss.portletbridge.it.basic;

import category.JBossASOnly;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.portal.warp.jsf.PortletPhase;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.jsf.AfterPhase;
import org.jboss.arquillian.warp.jsf.Phase;
import org.jboss.portletbridge.it.AbstractPortletTest;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.portletbridge.arquillian.deployment.TestDeployment;

import javax.faces.bean.ManagedProperty;
import java.net.URL;

import static org.jboss.arquillian.portal.warp.Phase.RENDER;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category(JBossASOnly.class)
@WarpTest
@PortalTest
public class WarpSimpleTest extends AbstractPortletTest {

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(WarpSimpleTest.class, true);
        deployment.archive()
                .createFacesPortlet("Warp", "Warp Portlet", "form.xhtml")
                .addAsWebResource("pages/basic/form.xhtml", "form.xhtml")
                .addClass(Bean.class);
        return deployment.getFinalArchive();
    }

    @Drone
    WebDriver browser;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Override
    protected WebDriver getBrowser() {
        return browser;
    }

    @RunAsClient
    @Test
    public void test() {
        Warp
                .initiate(new Activity() {
                    @Override
                    public void perform() {
                        browser.navigate().to(portalURL);
                    }
                })
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ManagedProperty("#{bean}")
                    Bean bean;

                    @PortletPhase(RENDER) @AfterPhase(Phase.RENDER_RESPONSE)
                    public void testBeanValueAfterRenderResponse() {
                        assertEquals(Bean.HELLO_JSF_PORTLET, bean.getText());
                    }
                });
    }
}
