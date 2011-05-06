/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import junit.framework.TestCase;

import org.jboss.portletbridge.config.FacesConfig;
import org.jboss.portletbridge.config.ParsingException;
import org.xml.sax.SAXException;

/**
 * @author asmirnov
 *
 */
public class FacesConfigTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
	 * @throws ParsingException 
	 */
	public void testParseInputStream() throws Exception {
		InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config.xml");
		FacesConfig config = new FacesConfig();
		config.parse(facesConfigResource);
		assertEquals(2, config.getExcludedAttributes().size());
		assertTrue(config.getExcludedAttributes().contains("foo.bar"));
		assertTrue(config.getExcludedAttributes().contains("foo.baz.*"));
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
	 * @throws ParsingException 
	 */
	public void testPublicParameters() throws Exception {
		InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config-params.xml");
		FacesConfig config = new FacesConfig();
		config.parse(facesConfigResource);
		Map<String, String> mapping = config.getParameterMapping();
		assertEquals(3, mapping.size());
		assertEquals("UserBean.lastName", mapping.get("name"));
		assertEquals("AnotherUser.name", mapping.get("AnotherPortlet:name"));
	}
	/**
	 * Test method for {@link org.jboss.portletbridge.config.FacesConfig#getParser()}.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public void testGetParser() throws Exception {
		FacesConfig config = new FacesConfig();
		SAXParser parser = config.getParser();
		assertNotSame(parser, config.getParser());
	}

}
