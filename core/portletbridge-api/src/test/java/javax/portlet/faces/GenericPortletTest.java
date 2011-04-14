/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package javax.portlet.faces;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.MockHandler;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author asmirnov
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericPortletTest  {

	@Mock
	ServletContext servletContext;

	@Mock
	PortletConfig portletConfig;

	@Mock
	PortletContext portletContext;
	
	private static final String FOO = "foo";

	private final class GenericFacesPortletExtension extends
			GenericFacesPortlet {
		boolean editProcessed = false;
		boolean viewProcessed = false;
		boolean helpProcessed = false;

		@Override
		protected void doEdit(RenderRequest request, RenderResponse response)
				throws PortletException, IOException {
			editProcessed = true;
		}

		@Override
		protected void doView(RenderRequest request, RenderResponse response)
				throws PortletException, IOException {
			viewProcessed = true;
		}

		@Override
		protected void doHelp(RenderRequest request, RenderResponse response)
				throws PortletException, IOException {
			helpProcessed = true;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link javax.portlet.faces.GenericFacesPortlet#destroy()}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testDestroy() throws PortletException, IOException {
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#init(javax.portlet.PortletConfig)}
	 * .
	 * 
	 * @throws PortletException
	 */
	public void testInitPortletConfig() throws PortletException {
//		servletContext.addInitParameter(Bridge.LIFECYCLE_ID, "CUSTOM");
//		servletContext.addInitParameter("javax.portlet.faces.renderPolicy",
//				Bridge.BridgeRenderPolicy.NEVER_DELEGATE.toString());

//		portletConfig.addInitParameter(
//				"javax.portlet.faces.preserveActionParams", "true");
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.excludedRequestAttributes", "bar,baz,boo");
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.extension.my_package.my_attribute", "xxx");
		GenericFacesPortlet portlet = createGenericPortlet();
		portlet.init(portletConfig);
		assertEquals(Boolean.TRUE, portletContext
				.getAttribute("javax.portlet.faces.foo.preserveActionParams"));
		List<String> attrsList = (List<String>) portletContext
				.getAttribute("javax.portlet.faces.foo.excludedRequestAttributes");
		assertEquals(3, attrsList.size());
		assertEquals("bar", attrsList.get(0));
		assertEquals("baz", attrsList.get(1));
		assertEquals("boo", attrsList.get(2));
		assertEquals(
				"xxx",
				portletContext
						.getAttribute("javax.portlet.faces.extension.my_package.foo.my_attribute"));
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testDoDispatchRenderRequestRenderResponse()
			throws PortletException, IOException {
//		setupRenderRequest();
		GenericFacesPortlet portlet = createGenericPortlet();
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
		portlet.init(portletConfig);
//		portlet.doDispatch(renderRequest, renderResponse);
//		assertEquals(1, MockBridge.responseCount);
	}

	private GenericFacesPortlet createGenericPortlet() {
		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		portletContext.setInitParameter("javax.portlet.faces.BridgeImplClass",
//				MockBridge.class.getName());
//		portletConfig.setPortletName(FOO);
		return portlet;
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testDoDispatchRenderRequestRenderResponseEdit()
			throws PortletException, IOException {
//		setupRenderRequest();
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.edit", "index.jsf");
//		renderRequest.mode = PortletMode.EDIT;
//		portlet.init(portletConfig);
//		portlet.doDispatch(renderRequest, renderResponse);
//		assertEquals(1, MockBridge.responseCount);
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testDoDispatchRenderRequestRenderResponseHelp()
			throws PortletException, IOException {
//		setupRenderRequest();
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.help", "index.jsf");
//		renderRequest.mode = PortletMode.HELP;
//		portlet.init(portletConfig);
//		portlet.doDispatch(renderRequest, renderResponse);
//		assertEquals(1, MockBridge.responseCount);
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws IOException
	 * @throws PortletException
	 */
	public void testDoEditRenderRequestRenderResponse()
			throws PortletException, IOException {
//		setupRenderRequest();
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
//		renderRequest.mode = PortletMode.EDIT;
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		portlet.doDispatch(renderRequest, renderResponse);
//		assertEquals(0, MockBridge.responseCount);
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doHelp(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 */
	public void testDoHelpRenderRequestRenderResponse() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testDoViewRenderRequestRenderResponse()
			throws PortletException, IOException {
//		setupRenderRequest();
//		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		portletContext.setInitParameter("javax.portlet.faces.BridgeImplClass",
//				MockBridge.class.getName());
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
//		portlet.init(portletConfig);
//		portlet.doDispatch(renderRequest, renderResponse);
//		assertEquals(1, MockBridge.responseCount);
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	public void testProcessActionActionRequestActionResponse()
			throws PortletException, IOException {
//		setupActionRequest();
//		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		portletContext.setInitParameter("javax.portlet.faces.BridgeImplClass",
//				MockBridge.class.getName());
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
//		portlet.init(portletConfig);
//		portlet.processAction(actionRequest, actionResponse);
//		assertEquals(1, MockBridge.actionCount);
	}

	public void testProcessResource() throws Exception {
//		setupResourceRequest();
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		portlet.serveResource(resourceRequest, resourceResponse);
//		assertEquals(1, MockBridge.resourceCount);
	}
	
	public void testProcessEvent() throws Exception {
//		setupEventRequest();
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		portlet.processEvent(eventRequest, eventResponse);
//		assertEquals(1, MockBridge.eventCount);
	}
	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#getBridgeClassName()}.
	 * 
	 * @throws PortletException
	 */
	public void testGetBridgeClassName() throws PortletException {
//		setupActionRequest();
//		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		portletContext.setInitParameter("javax.portlet.faces.BridgeImplClass",
//				MockBridge.class.getName());
//		portletContext.setInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
//		portlet.init(portletConfig);
//		assertEquals(MockBridge.class.getName(), portlet.getBridgeClassName());
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#getBridgeClassName()}.
	 * 
	 * @throws PortletException
	 */
	public void testGetBridgeClassName1() throws PortletException {
//		setupActionRequest();
//		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		try {
//			portlet.init(portletConfig);
//
//		} catch (PortletException e) {
//			assertEquals("Can't detect bridge implementation class name", e
//					.getMessage());
//			return;
//		}
//		assertTrue("No exception for unknown bridge implementation", false);
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#getBridgeClassName()}.
	 * 
	 * @throws PortletException
	 */
	public void testGetBridgeClassName2() throws PortletException {
//		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		ClassLoader testLoader = new ClassLoader(loader) {
//			@Override
//			public URL getResource(String name) {
//				if ("META-INF/services/javax.portlet.faces.Bridge".equals(name)) {
//					return super.getResource("javax.portlet.faces.Bridge.test");
//				}
//				return super.getResource(name);
//			}
//		};
//		try {
//			Thread.currentThread().setContextClassLoader(testLoader);
//			setupActionRequest();
//			GenericFacesPortlet portlet = new GenericFacesPortlet() {
//				@Override
//				public PortletContext getPortletContext() {
//					return portletContext;
//				}
//			};
//			// portlet.init(portletConfig);
//			assertEquals(MockBridge.class.getName(), portlet
//					.getBridgeClassName());
//
//		} finally {
//			Thread.currentThread().setContextClassLoader(loader);
//		}
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#getDefaultViewIdMap(javax.portlet.PortletRequest, javax.portlet.PortletMode)}
	 * .
	 * 
	 * @throws PortletException
	 */
	public void testGetDefaultViewIdMap() throws PortletException {
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.view", "index.jsf");
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.edit", "edit/index.jsf");
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.defaultViewId.help", "help/index.jsf");
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		Map<String, String> viewIdMap = portlet.getDefaultViewIdMap();
//		assertEquals(3, viewIdMap.size());
//		assertEquals("index.jsf", viewIdMap.get(
//				PortletMode.VIEW.toString()));
//		assertEquals("edit/index.jsf", viewIdMap.get(
//				PortletMode.EDIT.toString()));
//		assertEquals("help/index.jsf", viewIdMap.get(
//				PortletMode.HELP.toString()));
//		assertEquals(viewIdMap,portletContext
//				.getAttribute("javax.portlet.faces.foo.defaultViewIdMap"));
	}

	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#getFacesBridge()}.
	 * 
	 * @throws PortletException
	 */
	public void testGetFacesPortletBridge() throws PortletException {
//		setupActionRequest();
//		GenericFacesPortlet portlet = new GenericFacesPortlet();
//		portletContext.setInitParameter("javax.portlet.faces.BridgeImplClass",
//				MockBridge.class.getName());
//		portlet.init(portletConfig);
//		Bridge facesPortletBridge = portlet.getFacesBridge();
//		assertTrue(facesPortletBridge instanceof MockBridge);
//		assertEquals(true, ((MockBridge) facesPortletBridge).isInitialized());
	}

	public void testGetExcludedRequestAttributes() throws Exception {
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.excludedRequestAttributes",
//				"foo.*,bar.baz.*");
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		List<String> attributes = portlet.getExcludedRequestAttributes();
//		assertEquals(2, attributes.size());
//		assertTrue(attributes.contains("foo.*"));
//		assertTrue(attributes.contains("bar.baz.*"));
//		assertEquals(attributes,portletContext
//				.getAttribute("javax.portlet.faces.foo.excludedRequestAttributes"));
	}
	
	public void testIsPreserveActionParams() throws Exception {
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.preserveActionParams",
//				"true");
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		assertTrue(portlet.isPreserveActionParameters());
//		assertEquals(Boolean.TRUE, portletContext.getAttribute("javax.portlet.faces.foo.preserveActionParams"));
	}

	public void testIsPreserveActionParamsFalse() throws Exception {
		GenericFacesPortlet portlet = createGenericPortlet();
		portlet.init(portletConfig);
		assertFalse(portlet.isPreserveActionParameters());
	}
	
	public void testGetBridgeEventHandler() throws Exception {
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.bridgeEventHandler",
//				MockBridgeEventHandler.class.getName());
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		BridgeEventHandler eventHandler = portlet.getBridgeEventHandler();
//		assertNotNull(eventHandler);
//		assertTrue(eventHandler instanceof MockBridgeEventHandler);
//		Object handler = portletContext.getAttribute("javax.portlet.faces.foo.bridgeEventHandler");
//		assertNotNull(handler);
//		assertTrue(handler instanceof MockBridgeEventHandler);
	}
	public void testGetBridgePublicRenderParameterHandler() throws Exception {
//		portletConfig.addInitParameter(
//				"javax.portlet.faces.bridgePublicRenderParameterHandler",
//				MockBridgePublicRenderParameterHandler.class.getName());
//		GenericFacesPortlet portlet = createGenericPortlet();
//		portlet.init(portletConfig);
//		BridgePublicRenderParameterHandler eventHandler = portlet.getBridgePublicRenderParameterHandler();
//		assertNotNull(eventHandler);
//		assertTrue(eventHandler instanceof MockBridgePublicRenderParameterHandler);
//		Object handler = portletContext.getAttribute("javax.portlet.faces.foo.bridgePublicRenderParameterHandler");
//		assertNotNull(handler);
//		assertTrue(handler instanceof MockBridgePublicRenderParameterHandler);
	}
}
