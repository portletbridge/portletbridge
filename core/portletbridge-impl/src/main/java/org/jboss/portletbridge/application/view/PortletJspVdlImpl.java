/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.application.view;

import java.io.IOException;
import java.io.Writer;

import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.view.ViewDeclarationLanguage;
import javax.portlet.MimeResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.BridgeWriteBehindResponse;

import org.jboss.portletbridge.bridge.context.BridgeContext;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletJspVdlImpl extends VdlWrapper {
    private static final String RI_SAVE_STATE_MARKER = "~com.sun.faces.saveStateFieldMarker~";
    private static final String AFTER_VIEW_CONTENT = PortletJspVdlImpl.class + ".AFTER_VIEW_CONTENT";

    private ViewDeclarationLanguage wrappedVDL;

    public PortletJspVdlImpl(ViewDeclarationLanguage wrapped) {
        wrappedVDL = wrapped;
    }

    /**
     * @see org.jboss.portletbridge.application.view.VdlWrapper#getWrapped()
     */
    @Override
    public ViewDeclarationLanguage getWrapped() {
        return wrappedVDL;
    }

    /**
     * @see org.jboss.portletbridge.application.view.VdlWrapper#buildView(javax.faces.context.FacesContext,
     *      javax.faces.component.UIViewRoot)
     */
    @Override
    public void buildView(FacesContext context, UIViewRoot viewToRender) throws IOException {
        if (null == context) {
            throw new IllegalArgumentException("FacesContext was null");
        }
        if (null == viewToRender) {
            throw new IllegalArgumentException("UIViewRoot was null");
        }

        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        MimeResponse response = (MimeResponse) externalContext.getResponse();
        Class<? extends BridgeWriteBehindResponse> writeBehindResponseWrapperClass = null;
        BridgeWriteBehindResponse wrapped = null;
        boolean inRender = false;
        boolean inResource = false;

        if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE) {
            writeBehindResponseWrapperClass = bridgeContext.getBridgeConfig().getWriteBehindResourceResponseWrapper();
            inResource = true;
        } else if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RENDER_PHASE) {
            writeBehindResponseWrapperClass = bridgeContext.getBridgeConfig().getWriteBehindRenderResponseWrapper();
            inRender = true;
        }

        if (null != writeBehindResponseWrapperClass) {
            try {
                wrapped = writeBehindResponseWrapperClass.newInstance();
                if (inRender && wrapped instanceof RenderResponse || inResource && wrapped instanceof ResourceResponse) {
                    externalContext.setResponse(wrapped);
                } else {
                    wrapped = null;
                }
            } catch (Exception e) {
                externalContext.log("Instantiation of BridgeWriteBehindResponse failed", e);
            }
        }

        externalContext.getRequestMap().put(Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);

        externalContext.dispatch(viewToRender.getViewId());

        if (null != wrapped) {
            externalContext.setResponse(response);

            Object obj = externalContext.getRequestMap().get(Bridge.AFTER_VIEW_CONTENT);

            if (null == obj && wrapped.hasFacesWriteBehindMarkup()) {
                obj = wrapped.isChars() ? wrapped.getChars() : wrapped.getBytes();
            }

            if (null != obj) {
                externalContext.getRequestMap().put(AFTER_VIEW_CONTENT, obj);
            } else {
                wrapped.flushMarkupToWrappedResponse();
            }
        }
    }

    @Override
    public void renderView(FacesContext facesContext, UIViewRoot viewToRender) throws IOException {
        // Don't render the view if the component and its children should not be rendered
        if (!viewToRender.isRendered()) {
            return;
        }

        ExternalContext externalContext = facesContext.getExternalContext();
        MimeResponse renderResponse = (MimeResponse) externalContext.getResponse();

        RenderKitFactory renderFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderFactory.getRenderKit(facesContext, viewToRender.getRenderKitId());

        ResponseWriter oldWriter = facesContext.getResponseWriter();
        StringBuilderWriter strWriter = new StringBuilderWriter(facesContext, renderResponse.getWriter(), 4096);
        try {
            ResponseWriter newWriter;
            if (null != oldWriter) {
                newWriter = oldWriter.cloneWithWriter(strWriter);
            } else {
                newWriter = renderKit.createResponseWriter(strWriter, null, renderResponse.getCharacterEncoding());
            }
            facesContext.setResponseWriter(newWriter);

            newWriter.startDocument();
            viewToRender.encodeAll(facesContext);
            newWriter.endDocument();

            strWriter.flushToWriter();

        } finally {
            strWriter.release();
        }

        BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
        if (bridgeContext.hasRenderRedirect()) {
            bridgeContext.setRenderRedirectAfterDispatch(true);
            return;
        }

        if (null != oldWriter) {
            facesContext.setResponseWriter(oldWriter);
        }

        renderResponse.flushBuffer();

        Object afterViewContent = externalContext.getRequestMap().get(Bridge.AFTER_VIEW_CONTENT);
        if (null != afterViewContent) {
            if (afterViewContent instanceof char[]) {
                facesContext.getResponseWriter().write((char[]) afterViewContent);
            } else if (afterViewContent instanceof byte[]) {
                facesContext.getResponseWriter().write(new String((byte[]) afterViewContent));
            } else {
                externalContext.log("Invalid type for " + Bridge.AFTER_VIEW_CONTENT + " : " + afterViewContent.getClass());
            }
        } else {
            Object storedAfterViewContent = externalContext.getRequestMap().remove(AFTER_VIEW_CONTENT);
            if (null != storedAfterViewContent) {
                if (storedAfterViewContent instanceof char[]) {
                    facesContext.getResponseWriter().write((char[]) storedAfterViewContent);
                } else if (storedAfterViewContent instanceof byte[]) {
                    facesContext.getResponseWriter().write(new String((byte[]) storedAfterViewContent));
                } else {
                    externalContext.log("Invalid type for " + Bridge.AFTER_VIEW_CONTENT + " : "
                            + storedAfterViewContent.getClass());
                }
            }
        }
    }

    private static final class StringBuilderWriter extends Writer {

        private static final ThreadLocal<StringBuilderWriter> instance = new ThreadLocal<StringBuilderWriter>();
        private final StringBuilder mBuilder;
        private final FacesContext facesContext;
        private final Writer responseWriter;
        private static final int SAVESTATE_MARK_LEN = RI_SAVE_STATE_MARKER.length();

        public StringBuilderWriter(FacesContext context, Writer responseWriter, int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException();
            }
            mBuilder = new StringBuilder(initialCapacity);
            this.facesContext = context;
            this.responseWriter = responseWriter;
            instance.set(this);
        }

        public void release() {
            instance.remove();
        }

        public static StringBuilderWriter getInstance() {
            return instance.get();
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            mBuilder.append(cbuf, off, len);
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
         * @param str String to be written
         */
        @Override
        public void write(String str) throws IOException {
            mBuilder.append(str);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            mBuilder.append(str, off, off + len);
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
            StateManager stateManager = facesContext.getApplication().getStateManager();
            ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
            facesContext.setResponseWriter(oldResponseWriter.cloneWithWriter(responseWriter));
            Object stateToWrite = stateManager.saveView(facesContext);

            int stateStart = findStateMarker();
            if (stateStart != -1) {
                int pos = 0;
                while (stateStart >= 0) {
                    responseWriter.write(mBuilder.substring(pos, stateStart));
                    stateManager.writeState(facesContext, stateToWrite);
                    pos = stateStart + SAVESTATE_MARK_LEN;
                    stateStart = mBuilder.indexOf(RI_SAVE_STATE_MARKER, pos);
                }
                responseWriter.write(mBuilder.substring(pos));
            } else {
                responseWriter.write(mBuilder.toString());
            }

            facesContext.setResponseWriter(oldResponseWriter);
        }

        private int findStateMarker() {
            return mBuilder.indexOf(RI_SAVE_STATE_MARKER);
        }
    }
}
