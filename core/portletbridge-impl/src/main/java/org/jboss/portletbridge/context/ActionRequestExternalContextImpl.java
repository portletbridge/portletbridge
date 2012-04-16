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
package org.jboss.portletbridge.context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.faces.Bridge;

/**
 * @author asmirnov
 *
 */
public class ActionRequestExternalContextImpl extends PortletExternalContextImpl {

    /**
     * @param context
     * @param request
     * @param response
     */
    public ActionRequestExternalContextImpl(PortletContext context, ActionRequest request, ActionResponse response) {
        super(context, request, response);
    }

    // ============================================================
    // public methods
    @Override
    public PortletContext getContext() {
        return (PortletContext) super.getContext();
    }

    @Override
    public ActionRequest getRequest() {
        return (ActionRequest) super.getRequest();
    }

    @Override
    public ActionResponse getResponse() {
        return (ActionResponse) super.getResponse();
    }

    /**
     * @param url
     * @return
     */
    @Override
    protected String createActionUrl(PortalActionURL actionUrl) {
        String viewIdFromUrl = bridgeContext.getFacesViewIdFromPath(actionUrl.getPath());
        actionUrl.setParameter(bridgeContext.getBridgeConfig().getViewIdRenderParameterName(), viewIdFromUrl);

        StateAwareResponse stateResponse = (StateAwareResponse) getResponse();

        for (Entry<String, String[]> parameter : actionUrl.getParameters().entrySet()) {
            String key = parameter.getKey();
            String[] value = parameter.getValue();

            if (key.equals(Bridge.PORTLET_MODE_PARAMETER)) {
                if (null != value) {
                    PortletMode mode = new PortletMode(value[0]);
                    try {
                        stateResponse.setPortletMode(mode);
                    } catch (PortletModeException e) {
                        // only valid modes supported.
                    }
                }
            } else if (key.equals(Bridge.PORTLET_WINDOWSTATE_PARAMETER)) {
                if (null != value) {
                    WindowState state = new WindowState(value[0]);
                    try {
                        stateResponse.setWindowState(state);
                    } catch (WindowStateException e) {
                        // only valid window states supported.
                    }
                }
            } else if (key.equals(Bridge.PORTLET_SECURE_PARAMETER)) {
                // ignore
            } else {
                stateResponse.setRenderParameter(key, value);
            }
        }
        return actionUrl.toString();
    }

    @Override
    protected String createPartialActionUrl(PortalActionURL portalUrl) {
        return createActionUrl(portalUrl);
    }

    @Override
    protected String createRenderUrl(PortalActionURL portalUrl, Map<String, List<String>> parameters) {
        return ACTION_URL_DO_NOTHITG;
    }

    @Override
    protected String createResourceUrl(PortalActionURL portalUrl) {
        return RESOURCE_URL_DO_NOTHITG;
    }

    public void redirect(String url) throws IOException {
        if (null == url || url.length() < 0) {
            throw new NullPointerException("Path to redirect is null");
        }
        PortalActionURL actionURL = new PortalActionURL(url);
        if (url.startsWith("#") || (!actionURL.isInContext(getRequestContextPath()))
            || "true".equalsIgnoreCase(actionURL.getParameter(Bridge.DIRECT_LINK))) {
            getResponse().sendRedirect(url);
        } else {
            internalRedirect(actionURL);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }

    // ============================================================
    // non-public methods

    @Override
    public String getRequestContentType() {
        return getRequest().getContentType();
    }

    @Override
    public int getRequestContentLength() {
        return getRequest().getContentLength();
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        try {
            getRequest().setCharacterEncoding(encoding);
        } catch (IllegalStateException e) {
            // TODO: handle exception
        }
    }

    @Override
    public String getRequestCharacterEncoding() {
        return getRequest().getCharacterEncoding();
    }

    @Override
    public void addResponseHeader(String name, String value) {
        if ("X-Portlet-Mode".equals(name)) {
            try {
                this.getResponse().setPortletMode(new PortletMode(value));
            } catch (PortletModeException e) {
                throw new RuntimeException("Cant set portlet mode '" + value + "'", e);
            }
        } else if ("X-Window-State".equals(name)) {
            try {
                this.getResponse().setWindowState(new WindowState(value));
            } catch (WindowStateException e) {
                throw new RuntimeException("Cant set window state '" + value + "'", e);
            }
        } else {
            super.addResponseHeader(name, value);
        }
    }

    @Override
    public void setResponseHeader(String name, String value) {
        if ("X-Portlet-Mode".equals(name)) {
            try {
                this.getResponse().setPortletMode(new PortletMode(value));
            } catch (PortletModeException e) {
                throw new RuntimeException("Cant set portlet mode '" + value + "'", e);
            }
        } else if ("X-Window-State".equals(name)) {
            try {
                this.getResponse().setWindowState(new WindowState(value));
            } catch (WindowStateException e) {
                throw new RuntimeException("Cant set window state '" + value + "'", e);
            }
        } else {
            super.setResponseHeader(name, value);
        }
    }

    @Override
    public boolean isResponseCommitted() {
        return true;
    }

    @Override
    public void responseSendError(int statusCode, String message) throws IOException {
        // TODO - set error code for render phase.
    }

    @Override
    public void setResponseStatus(int statusCode) {
        // TODO - set error code for render phase.
    }

    @Override
    public int getResponseBufferSize() {
        return 0;
    }

    @Override
    public OutputStream getResponseOutputStream() throws IOException {
        return null;
    }

    @Override
    public Writer getResponseOutputWriter() throws IOException {
        return null;
    }

    @Override
    public void responseFlushBuffer() throws IOException {

    }

    @Override
    public void responseReset() {

    }

    @Override
    public void setResponseBufferSize(int size) {

    }

    @Override
    public void setResponseContentLength(int length) {

    }

    @Override
    public void setResponseContentType(String contentType) {

    }

}
