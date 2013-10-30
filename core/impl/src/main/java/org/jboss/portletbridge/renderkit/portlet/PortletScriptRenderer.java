/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.renderkit.portlet;

import org.jboss.portletbridge.bridge.logger.BridgeLogger;
import org.jboss.portletbridge.bridge.logger.JULLoggerImpl;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class PortletScriptRenderer extends PortletRendererWrapper implements ComponentSystemEventListener, StateHolder {

    private static final BridgeLogger logger = new JULLoggerImpl(PortletScriptRenderer.class.getName());

    private boolean transientFlag;
    private Renderer wrappedRenderer;

    public PortletScriptRenderer() {
    }

    public PortletScriptRenderer(Renderer renderer) {
        this.wrappedRenderer = renderer;
    }

    @Override
    public Renderer getWrapped() {
        return wrappedRenderer;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        boolean isPartial = context.getPartialViewContext().isPartialRequest();

        if (isPartial) {
            ResponseWriter original = context.getResponseWriter();

            // Set custom writer and encode it
            context.setResponseWriter(new PortletPartialResponseWriter(original));
            super.encodeEnd(context, component);

            context.setResponseWriter(original);
        } else {
            super.encodeEnd(context, component);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (wrappedRenderer instanceof ComponentSystemEventListener) {
            ComponentSystemEventListener wrappedListener = (ComponentSystemEventListener) wrappedRenderer;
            wrappedListener.processEvent(event);
        }
    }

    public void restoreState(FacesContext facesContext, Object state) {
        if (wrappedRenderer == null) {
            try {
                String wrappedRendererClassName = (String) state;
                Class<?> wrappedRendererClass = Class.forName(wrappedRendererClassName);
                wrappedRenderer = (Renderer) wrappedRendererClass.newInstance();
            } catch (Exception e) {
                logger.log(BridgeLogger.Level.ERROR, "Unable to instantiate wrapped Renderer", e);
            }
        }
    }

    public Object saveState(FacesContext facesContext) {
        return wrappedRenderer.getClass().getName();
    }

    public boolean isTransient() {
        return transientFlag;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientFlag = newTransientValue;
    }
}
