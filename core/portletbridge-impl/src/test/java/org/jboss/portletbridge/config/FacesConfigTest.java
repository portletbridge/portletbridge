/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author asmirnov
 * 
 */
public class FacesConfigTest extends TestCase {

    /**
     * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
     * 
     * @throws ParsingException
     */
    public void testParseInputStream() throws Exception {
        InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config.xml");
        FacesConfigProcessor config = new FacesConfigProcessor();
        config.parse(facesConfigResource, FacesConfigProcessor.ParsingLocation.DEFAULT);
        assertEquals(2, FacesConfigProcessor.getExcludedAttributes().size());
        assertTrue(FacesConfigProcessor.getExcludedAttributes().contains("foo.bar"));
        assertTrue(FacesConfigProcessor.getExcludedAttributes().contains("foo.baz.*"));
    }

    /**
     * Test method for {@link org.jboss.portletbridge.config.FacesConfig#parse(java.io.InputStream)}.
     * 
     * @throws ParsingException
     */
    public void testPublicParameters() throws Exception {
        InputStream facesConfigResource = this.getClass().getResourceAsStream("/test/WEB-INF/faces-config-params.xml");
        FacesConfigProcessor config = new FacesConfigProcessor();
        config.parse(facesConfigResource, FacesConfigProcessor.ParsingLocation.DEFAULT);
        Map<String, String> mapping = FacesConfigProcessor.getPublicParameterMappings();
        assertEquals(3, mapping.size());
        assertEquals("UserBean.lastName", mapping.get("name"));
        assertEquals("AnotherUser.name", mapping.get("AnotherPortlet:name"));
    }

}
