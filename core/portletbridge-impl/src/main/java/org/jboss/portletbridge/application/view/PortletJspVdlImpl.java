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
    private static final String SAVESTATE_FIELD_MARKER = "~org.jboss.portletbridge.saveStateFieldMarker~";

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
                }
            } catch (Exception e) {
                externalContext.log("Instantiation of BridgeWriteBehindResponse failed", e);
            }
        }

        externalContext.dispatch(viewToRender.getViewId());

        if (null != wrapped) {
            externalContext.setResponse(response);
            wrapped.flushMarkupToWrappedResponse();
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

        externalContext.getRequestMap().put(Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);

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

        if (null != oldWriter) {
            facesContext.setResponseWriter(oldWriter);
        }

        renderResponse.flushBuffer();

        externalContext.getRequestMap().remove(Bridge.RENDER_CONTENT_AFTER_VIEW);
        Object afterViewContent = facesContext.getExternalContext().getRequestMap().get(Bridge.AFTER_VIEW_CONTENT);
        if (null != afterViewContent) {
            if (afterViewContent instanceof char[]) {
                facesContext.getResponseWriter().write((char[]) afterViewContent);
            } else if (afterViewContent instanceof byte[]) {
                facesContext.getResponseWriter().write(new String((byte[]) afterViewContent));
            } else {
                externalContext.log("Invalid type for " + Bridge.AFTER_VIEW_CONTENT + " : " + afterViewContent.getClass());
            }
        }
    }

    private static final class StringBuilderWriter extends Writer {

        private static final ThreadLocal<StringBuilderWriter> instance = new ThreadLocal<StringBuilderWriter>();
        private final StringBuilder mBuilder;
        private final FacesContext facesContext;
        private final Writer responseWriter;
        private boolean stateWrited = false;
        private static final int SAVESTATE_MARK_LEN = SAVESTATE_FIELD_MARKER.length();

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

        public void stateWrited() {
            this.stateWrited = true;

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
         * @param str String to be written
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
                StateManager stateManager = facesContext.getApplication().getStateManager();
                ResponseWriter oldResponseWriter = facesContext.getResponseWriter();
                facesContext.setResponseWriter(oldResponseWriter.cloneWithWriter(responseWriter));
                Object stateToWrite = stateManager.saveView(facesContext);
                int pos = 0;
                int tildeIdx = mBuilder.indexOf(SAVESTATE_FIELD_MARKER);
                while (tildeIdx >= 0) {
                    responseWriter.write(mBuilder.substring(pos, tildeIdx));
                    stateManager.writeState(facesContext, stateToWrite);
                    pos = tildeIdx + SAVESTATE_MARK_LEN;
                    tildeIdx = mBuilder.indexOf(SAVESTATE_FIELD_MARKER, pos);
                }
                responseWriter.write(mBuilder.substring(pos));
                facesContext.setResponseWriter(oldResponseWriter);
            }
        }
    }
}
