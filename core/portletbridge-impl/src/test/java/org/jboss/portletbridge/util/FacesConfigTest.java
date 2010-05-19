/**
 * 
 */
package org.jboss.portletbridge.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import junit.framework.TestCase;

import org.easymock.EasyMock;
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
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parse(javax.portlet.PortletContext)}.
	 */
	public void testParsePortletContext() {
		FacesConfig config = new FacesConfig();
		PortletContext context = EasyMock.createMock(PortletContext.class);
		EasyMock.replay(context);
//		config.parse(context);
		EasyMock.verify(context);
//		assertEquals(6, config.getExcludedAttributes().size());
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parseDefault(javax.portlet.PortletContext)}.
	 */
	public void testParseDefault() throws Exception {
		FacesConfig config = new FacesConfig();
//		MockPortletContext context = new MockPortletContext(new MockServletContext(){
//			@Override
//			public InputStream getResourceAsStream(String path) {
//				return this.getClass().getResourceAsStream("/test"+path);
//			}
//			
//			@Override
//			public String getInitParameter(String name) {
//				if(FacesServlet.CONFIG_FILES_ATTR.equals(name)){
//					return "/WEB-INF/a-faces-config.xml ,/WEB-INF/b-faces-config.xml";
//				}
//				return super.getInitParameter(name);
//			}
//		});
		PortletContext context = EasyMock.createMock(PortletContext.class);
		EasyMock.replay(context);
//		config.parseDefault(context);
		EasyMock.verify(context);
//		assertEquals(2, config.getExcludedAttributes().size());
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parseOptional(javax.portlet.PortletContext)}.
	 */
	public void testParseOptional() throws Exception {
		FacesConfig config = new FacesConfig();
//		MockPortletContext context = new MockPortletContext(new MockServletContext(){
//			@Override
//			public InputStream getResourceAsStream(String path) {
//				return this.getClass().getResourceAsStream("/test"+path);
//			}
//			
//			@Override
//			public String getInitParameter(String name) {
//				if(FacesServlet.CONFIG_FILES_ATTR.equals(name)){
//					return "/WEB-INF/a-faces-config.xml ,/WEB-INF/b-faces-config.xml";
//				}
//				return super.getInitParameter(name);
//			}
//		});
		PortletContext context = EasyMock.createMock(PortletContext.class);
		EasyMock.replay(context);
//		config.parseOptional(context);
		EasyMock.verify(context);
//		assertEquals(4, config.getExcludedAttributes().size());
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parseClasspath(javax.portlet.PortletContext)}.
	 * @throws Exception 
	 */
	public void testParseClasspath() throws Exception {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try{
		ClassLoader classLoader = this.getClass().getClassLoader();
		ClassLoader classLoaderWrapper = new ClassLoader(classLoader){
			
			@Override
			public Enumeration<URL> getResources(String name)
					throws IOException {
				if("META-INF/faces-config.xml".equals(name)){
					Vector<URL> configs = new Vector<URL>(2);
					configs.add(getResource("test/WEB-INF/a-faces-config.xml"));
					configs.add(getResource("test/WEB-INF/b-faces-config.xml"));
					return configs.elements();
				}
				return super.getResources(name);
			}
		};
		Thread.currentThread().setContextClassLoader(classLoaderWrapper);
		FacesConfig config = new FacesConfig();
		PortletContext context = EasyMock.createMock(PortletContext.class);
		EasyMock.replay(context);
		config.parseClasspath(context);
		EasyMock.verify(context);
		assertEquals(4, config.getExcludedAttributes().size());
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
		
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parse(java.io.InputStream)}.
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
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#parse(java.io.InputStream)}.
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
	 * Test method for {@link org.jboss.portletbridge.util.FacesConfig#getParser()}.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public void testGetParser() throws Exception {
		FacesConfig config = new FacesConfig();
		SAXParser parser = config.getParser();
		assertNotSame(parser, config.getParser());
	}

}
