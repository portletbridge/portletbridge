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
package org.jboss.portletbridge.application;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import org.jboss.portletbridge.context.PortalActionURL;
import org.jboss.portletbridge.context.PortletBridgeContext;
import org.jboss.portletbridge.util.BridgeLogger;

/**
 * @author asmirnov
 * 
 */
public class PortletViewHandler extends ViewHandlerWrapper {


	private static final Logger _log = BridgeLogger.FACES.getLogger();

	private static final String SAVESTATE_FIELD_MARKER = "~org.jboss.portletbridge.saveStateFieldMarker~";

	ViewHandler parent;


	/**
	 * @param parent
	 */
	public PortletViewHandler(ViewHandler parent) {
		super();
		this.parent = parent;
	}

	@Override
	public Locale calculateLocale(FacesContext context) {
		Locale locale;
		if (BridgeUtil.isPortletRequest()) {
			locale = context.getExternalContext().getRequestLocale();
			if (null == locale) {
				locale = super.calculateLocale(context);
			}
		} else {
			locale = super.calculateLocale(context);
		}
		return locale;
	}

	public UIViewRoot createView(FacesContext facesContext, String viewId) {
		boolean portletRequest = BridgeUtil.isPortletRequest();
		if (portletRequest) {
			viewId = evaluateUrl(facesContext, viewId);
			try {
				PortalActionURL viewIdUrl = new PortalActionURL(viewId);
				viewId = viewIdUrl.getPath();
				Map<String, String[]> viewIdParameters = viewIdUrl
				        .getParameters();
				PortletBridgeContext.getCurrentInstance(facesContext)
				        .setViewIdParameters(viewIdParameters);
			} catch (MalformedURLException e) {
				// Do nothing, it is ordinary view Id
				_log.log(Level.WARNING, "Mailformed ViewId url", e);
			}
		}
		UIViewRoot root = super.createView(facesContext, viewId);
		Class<? extends UIViewRoot> rootClass = root.getClass();

		if (portletRequest) {
			if (rootClass.getAnnotation(PortletNamingContainer.class) == null) {
				UIViewRoot portletRoot = new PortletNamingContainerUIViewRoot();
				portletRoot.setViewId(root.getViewId());
				portletRoot.setLocale(root.getLocale());
				portletRoot.setRenderKitId(root.getRenderKitId());
				portletRoot.setId(root.getId());
				root = portletRoot;
			}
			Object response = facesContext.getExternalContext().getResponse();
			if (response instanceof PortletResponse) {
				PortletResponse portletResponse = (PortletResponse) response;
				portletResponse.setProperty("X-JAVAX-PORTLET-IS-NAMESPACED",
				        "true");
			}
		}
		return root;
	}

	@Override
	public void writeState(FacesContext context) throws IOException {
		StringBuilderWriter stringBuilderWriter = StringBuilderWriter
		        .getInstance();
		if (null != stringBuilderWriter) {
			stringBuilderWriter.stateWrited();
			context.getResponseWriter().write(SAVESTATE_FIELD_MARKER);
		} else {
			super.writeState(context);
		}
	}

	public String getActionURL(FacesContext context, String url) {
		// action URLs are processed by the bridge in encodeActionURL
		// however the bridge extends Faces navigation rule support in that it
		// allows a to-view-id element to contain an EL expression.
		// We recognize this EL expresion here and evaluate to a viewid
		// before delegating. Only executed during portlet request or AJAX
		// request
		// from portlet page.

		url = evaluateUrl(context, url);

		return super.getActionURL(context, url);

	}

	protected String evaluateUrl(FacesContext context, String url) {
		if (url.startsWith("#")) {
			// evaluate this as an EL expression
			url = (String) context.getApplication().evaluateExpressionGet(
			        context, url, String.class);
			if (url == null) {
				throw new FacesException("Evaluated view ID is null " + url);
			}
		}
		return url;
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender)
	        throws IOException, FacesException {
		// Get the renderPolicy from the init parameters
		ExternalContext externalContext = context.getExternalContext();
		String renderPolicyParam = externalContext
		        .getInitParameter(Bridge.RENDER_POLICY);

		Bridge.BridgeRenderPolicy renderPolicy;
		if (renderPolicyParam == null) {
			renderPolicy = Bridge.BridgeRenderPolicy.DEFAULT;
		} else {
			renderPolicy = Bridge.BridgeRenderPolicy.valueOf(renderPolicyParam);
		}

		if (null == externalContext.getRequestMap().get(
		        Bridge.PORTLET_LIFECYCLE_PHASE)
		        || renderPolicy == Bridge.BridgeRenderPolicy.ALWAYS_DELEGATE) {
			super.renderView(context, viewToRender);
		} else if (renderPolicy == Bridge.BridgeRenderPolicy.DEFAULT) {
			// https://jira.jboss.org/jira/browse/PBR-121 - save original
			// request/response objects.
			Object portletRequest = externalContext.getRequest();
			Object portletResponse = externalContext.getResponse();
			try {
				// IDEA - set ServletRequest/ServletResponse wrappers to
				// ExternalContext
				// to use original view handler functionality.
				super.renderView(context, viewToRender);
			} catch (Throwable t) {
				if (_log.isLoggable(Level.INFO)) {
					_log
					        .log(
					                Level.INFO,
					                "Error rendering view by parent ViewHandler, try to render as portletbridge JSP page",
					                t);
				}
				// Restore request/response objects if parent renderer change
				// them.
				if (portletRequest != externalContext.getRequest()) {
					externalContext.setRequest(portletRequest);
				}
				if (portletResponse != externalContext.getResponse()) {
					externalContext.setResponse(portletResponse);
				}
				// catch all throws and swallow -- falling through to our own
				// render
				// suppress rendering if "rendered" property on the component is
				// false
				if (viewToRender.isRendered()) {
					doRenderView(context, viewToRender);
				}

			}
		} else if (viewToRender.isRendered()) {
			// NEVER_DELEGATE
			doRenderView(context, viewToRender);
		}

	}

	private void doRenderView(FacesContext context, UIViewRoot viewToRender)
	        throws IOException {
		ExternalContext externalContext = context.getExternalContext();
		MimeResponse renderResponse = (MimeResponse) externalContext
		        .getResponse();

		try {

			// set request attribute indicating we can deal with content
			// that is supposed to be delayed until after JSF tree is ouput.
			externalContext.getRequestMap().put(
			        Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);
			// executePageToBuildView() creates
			// ViewHandlerResponseWrapper
			// to handle error page and text that exists after the <f:view> tag
			// among other things which have lots of servlet dependencies -
			// we're skipping this for now for portletbridge
			// extContext.dispatch(viewToRender.getViewId());

			if (executePageToBuildView(context, viewToRender)) {
				renderResponse.flushBuffer();
				return;
			}

		} catch (IOException e) {
			throw new FacesException(e);
		}

		// set up the ResponseWriter
		RenderKitFactory renderFactory = (RenderKitFactory) FactoryFinder
		        .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
		RenderKit renderKit = renderFactory.getRenderKit(context, viewToRender
		        .getRenderKitId());

		ResponseWriter oldWriter = context.getResponseWriter();
		StringBuilderWriter strWriter = new StringBuilderWriter(context,
		        renderResponse.getWriter(), 4096);
		try {
			ResponseWriter newWriter;
			if (null != oldWriter) {
				newWriter = oldWriter.cloneWithWriter(strWriter);
			} else {
				newWriter = renderKit.createResponseWriter(strWriter, null,
				        renderResponse.getCharacterEncoding());
			}
			context.setResponseWriter(newWriter);

			newWriter.startDocument();
			viewToRender.encodeAll(context);
			newWriter.endDocument();

			// replace markers in the body content and write it to response.

			strWriter.flushToWriter();

		} finally {
			strWriter.release();
		}
		if (null != oldWriter) {
			context.setResponseWriter(oldWriter);
		}

		renderResponse.flushBuffer();
	}

	private static final class StringBuilderWriter extends Writer {

		private static final ThreadLocal<StringBuilderWriter> instance = new ThreadLocal<StringBuilderWriter>();
		private final StringBuilder mBuilder;
		private final FacesContext context;
		private final Writer responseWriter;
		private boolean stateWrited = false;
		private static final int SAVESTATE_MARK_LEN = SAVESTATE_FIELD_MARKER
		        .length();

		public StringBuilderWriter(FacesContext context, Writer responseWriter,
		        int initialCapacity) {
			if (initialCapacity < 0) {
				throw new IllegalArgumentException();
			}
			mBuilder = new StringBuilder(initialCapacity);
			this.context = context;
			this.responseWriter = responseWriter;
			instance.set(this);
		}

		public void release() {
			instance.remove();
		}

		public void stateWrited() {
			this.stateWrited = true;

		}

		public static StringBuilderWriter getInstance() {
			return instance.get();
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			if (off < 0 || off > cbuf.length || len < 0
			        || off + len > cbuf.length || off + len < 0) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			if (stateWrited) {
				mBuilder.append(cbuf, off, len);
			} else {
				responseWriter.write(cbuf, off, len);
			}
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void close() throws IOException {
		}

		/**
		 * Write a string.
		 * 
		 * @param str
		 *            String to be written
		 */
		@Override
		public void write(String str) throws IOException {
			if (stateWrited) {
				mBuilder.append(str);
			} else {
				responseWriter.write(str);
			}
		}

		@Override
		public void write(String str, int off, int len) throws IOException {
			if (stateWrited) {
				mBuilder.append(str, off, off + len);
			} else {
				responseWriter.write(str, off, len);
			}
		}

		public StringBuilder getBuffer() {
			return mBuilder;
		}

		@Override
		public String toString() {
			return mBuilder.toString();
		}

		public void flushToWriter() throws IOException {
			// TODO: Buffer?
			if (stateWrited) {
				StateManager stateManager = context.getApplication()
				        .getStateManager();
				ResponseWriter oldResponseWriter = context.getResponseWriter();
				context.setResponseWriter(oldResponseWriter
				        .cloneWithWriter(responseWriter));
				Object stateToWrite = stateManager.saveView(context);
				int pos = 0;
				int tildeIdx = mBuilder.indexOf(SAVESTATE_FIELD_MARKER);
				while (tildeIdx >= 0) {
					responseWriter.write(mBuilder.substring(pos, tildeIdx));
					stateManager.writeState(context, stateToWrite);
					pos = tildeIdx + SAVESTATE_MARK_LEN;
					tildeIdx = mBuilder.indexOf(SAVESTATE_FIELD_MARKER, pos);
				}
				responseWriter.write(mBuilder.substring(pos));
				context.setResponseWriter(oldResponseWriter);
			}
		}
	}

	/**
	 * Execute the target view. If the HTTP status code range is not 2xx, then
	 * return true to indicate the response should be immediately flushed by the
	 * caller so that conditions such as 404 are properly handled.
	 * 
	 * @param context
	 *            the <code>FacesContext</code> for the current request
	 * @param viewToExecute
	 *            the view to build
	 * @return <code>true</code> if the response should be immediately flushed
	 *         to the client, otherwise <code>false</code>
	 * @throws IOException
	 *             if an error occurs executing the page
	 */
	private boolean executePageToBuildView(FacesContext context,
	        UIViewRoot viewToExecute) throws IOException {
		String requestURI = viewToExecute.getViewId();

		ExternalContext extContext = context.getExternalContext();

		extContext.dispatch(requestURI);
		return false;
	}

	@Override
	public ViewHandler getWrapped() {
		return parent;
	}

}
