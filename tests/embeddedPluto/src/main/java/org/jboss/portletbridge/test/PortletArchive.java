package org.jboss.portletbridge.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class PortletArchive {

	private PortletArchive() {
		// utility class
	}
	
	public static WebArchive create() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class)
		.addAsManifestResource("META-INF/pluto.tld","pluto.tld")
		.addAsManifestResource("META-INF/web-fragment.xml","web-fragment.xml")
		;
		return ShrinkWrap.create(WebArchive.class, "integrationTest.war")
				.addAsWebInfResource("themes/pluto.jsp", "themes/pluto.jsp")
		.addAsWebInfResource("themes/portlet-skin.jsp", "themes/portlet-skin.jsp")
		.addAsWebInfResource("META-INF/pluto.tld", "tld/pluto.tld")
		.addAsWebInfResource("pluto-portal-driver-config.xml")
		.addAsLibrary(javaArchive)
		;
	}
	
	public static WebArchive create(String webInfResource,String portletResource,String facesConfigResource) {
		return create()
		.addAsWebInfResource(webInfResource, "web.xml")
		.addAsWebInfResource(facesConfigResource,"faces-config.xml")
		.addAsWebInfResource(portletResource, "portlet.xml");
	}

}
