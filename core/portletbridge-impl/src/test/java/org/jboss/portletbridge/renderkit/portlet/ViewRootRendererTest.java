package org.jboss.portletbridge.renderkit.portlet;


import static org.junit.Assert.*;

import javax.portlet.PortalContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ViewRootRendererTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncodeHeadRequest() throws Exception {
		// Given - PortalContext property is set, PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT 
		// Request attribute indicates head rendering.
	}

	@Test
	public void testEncodeBodyRequest() throws Exception {
		
	}

	@Test
	public void testEncodeHeadAndBodyRequest() throws Exception {
		// Given - portlet render request, PortalContext property is not set: PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT 
		// Then - call encodeChildren.
		// When - only HtmlHead and HtmlBody components rendered.
	}

}
