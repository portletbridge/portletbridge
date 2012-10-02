package org.jboss.portletbridge.test.component.h.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
public class LinkTest {

	@Deployment()
	public static WebArchive createDeployment() {
		return TestDeployment.createDeploymentWithAll()
				.addAsWebResource("pages/component/h/link/link.xhtml", "home.xhtml")
				.addAsWebResource("pages/component/h/link/link.xhtml", "exit.xhtml")
				.addAsWebResource("resources/ajax.png", "ajax.png")
				.addClass(LinkBean.class);
		//.addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
	}

	@ArquillianResource
	@PortalURL
	URL portalURL;

    @Drone
	WebDriver driver;

	protected static final By LINK_ONE = By.xpath("//a[contains(@id,':link1')]");
	protected static final By LINK_TWO = By.xpath("//a[contains(@id,':link2')]");
	protected static final By LINK_THREE = By.xpath("//a[contains(@id,':link3')]");
	protected static final By LINK_THREE_IMAGE = By.xpath("img[contains(@id,':link3img')]");

	@Test
	@RunAsClient
	public void testLink() throws Exception {
		driver.get(portalURL.toString());

		assertNotNull("Check that page contains LINK ONE element.", driver.findElement(LINK_ONE));
		assertNotNull("Check that page contains LINK TWO element.", driver.findElement(LINK_TWO));
		assertNotNull("Check that page contains LINK THREE element.", driver.findElement(LINK_THREE));
	}

	@Test
	@RunAsClient
	public void testLinkWithValue() throws Exception {
		driver.get(portalURL.toString());

		// FIXME: shouldn't link be .../portal/exit.xhtml instead of .../exit.xhtml ?
		assertTrue("Check that LINK ONE links to the expected location.", driver.findElement(LINK_ONE).getAttribute("href").contains(LinkBean.LINK_ONE));
		assertEquals("Check that LINK ONE contains the expected text.", LinkBean.LINK_ONE_TEXT, driver.findElement(LINK_ONE).getText());
	}

	@Test
	@RunAsClient
	public void testLinkWithConverter() throws Exception {
		driver.get(portalURL.toString());

		assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.", 
				driver.findElement(LINK_TWO).getAttribute("href").contains(LinkBean.LINK_ONE));
		assertTrue("Check that LINK TWO links to the #bottom of LINK ONE.", 
				driver.findElement(LINK_TWO).getAttribute("href").endsWith("#bottom"));
		assertEquals("Check that LINK TWO contains the expected text.", LinkBean.LINK_ONE_TEXT + " Bottom", driver.findElement(LINK_TWO).getText());
	}

	@Test
	@RunAsClient
	public void testLinkDefault() throws Exception {
		driver.get(portalURL.toString());

		assertTrue("Check that LINK THREE links to the current page.", 
				driver.findElement(LINK_THREE).getAttribute("href").contains("home0x2xhtml") || 
				driver.findElement(LINK_THREE).getAttribute("href").contains("home.xhtml"));
	}

	@Test
	@RunAsClient
	public void testOutputLinkWithParam() throws Exception {
		driver.get(portalURL.toString());

		assertTrue("Check that OUTPUT LINK THREE link contains the expected parameter.", 
				driver.findElement(LINK_THREE).getAttribute("href").contains("from0xc2FL3") || 
				driver.findElement(LINK_THREE).getAttribute("href").contains("from=L3"));
	}

	@Test
	@RunAsClient
	public void testLinkImage() throws Exception {
		driver.get(portalURL.toString());

		assertTrue("Check that LINK THREE link is an image.", 
				driver.findElement(LINK_THREE).findElement(LINK_THREE_IMAGE).getAttribute("src").contains("ajax.png"));
	}

	@Test
	@RunAsClient
	public void testLinkRendered() throws Exception {
		// Set outputLink not to render
		LinkBean.LINK_RENDER = false;

		driver.get(portalURL.toString());

		try {
			assertNull("Check that page does not contains LINK ONE element.", driver.findElement(LINK_ONE));
		}
		catch (NoSuchElementException e) {
			// expected
		}
	}

	// TODO: Add more tests, clicking on links, but only after fixing the above FIXMEs.

}
