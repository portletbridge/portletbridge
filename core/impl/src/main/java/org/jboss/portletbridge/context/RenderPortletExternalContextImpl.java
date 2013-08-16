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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;

import org.jboss.portletbridge.bridge.config.BridgeConfig;

/**
 * @author asmirnov
 *
 */
public class RenderPortletExternalContextImpl extends MimeExternalContextImpl {

    public RenderPortletExternalContextImpl(PortletContext context, RenderRequest request, RenderResponse response) {
        super(context, request, response);
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        // Do nothing.
    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void redirect(String url) throws IOException {
        if (null == url) {
            throw new NullPointerException("Path to redirect is null");
        }
        PortalActionURL actionURL = new PortalActionURL(url);
        Map<String, String[]> urlParams = null;
        if (null != encodedActionUrlParameters) {
            urlParams = encodedActionUrlParameters.get(url);
        }

        if (null != urlParams) {
            if (null == urlParams.get(bridgeContext.getBridgeConfig().getViewIdRenderParameterName())) {
                throw new IllegalStateException("Can't redirect to Non Faces target: " + url + " during Render.");
            }

            PortalUrlQueryString queryString = new PortalUrlQueryString(null);
            queryString.setParameters(urlParams);

            Map<String, String[]> publicParamMap = getRenderRequest().getPublicParameterMap();
            if (null != publicParamMap && !publicParamMap.isEmpty()) {
                for (Map.Entry<String, String[]> entry : publicParamMap.entrySet()) {
                    String key = entry.getKey();

                    if (!queryString.hasParameter(key)) {
                        for (String param : entry.getValue()) {
                            queryString.addParameter(key, param);
                        }
                    }
                }
            }

            bridgeContext.setRedirectViewId(urlParams.get(bridgeContext.getBridgeConfig().getViewIdRenderParameterName())[0]);
            bridgeContext.setRenderRedirectQueryString(queryString.toString());
            FacesContext.getCurrentInstance().responseComplete();
        } else if (url.startsWith("#") || (!actionURL.isInContext(getRequestContextPath()))
                || "true".equalsIgnoreCase(actionURL.getParameter(Bridge.DIRECT_LINK))) {
            // Do Nothing
        } else {
            BridgeConfig bridgeConfig = bridgeContext.getBridgeConfig();
            String viewIdRenderParameterName = bridgeConfig.getViewIdRenderParameterName();
            String viewIdRenderParameterValue = actionURL.getParameter(viewIdRenderParameterName);

            if (null != viewIdRenderParameterValue) {
                viewIdRenderParameterValue = URLDecoder.decode(viewIdRenderParameterValue, "UTF-8");
                bridgeContext.setRedirectViewId(viewIdRenderParameterValue);
            } else {
                redirect(encodeActionURL(url));
            }
        }
        getPortletFlash().doLastPhaseActions(FacesContext.getCurrentInstance(), true);
    }

    public RenderRequest getRenderRequest() {
        return (RenderRequest) super.getRequest();
    }

    public RenderResponse getRenderResponse() {
        return (RenderResponse) super.getResponse();
    }

    @Override
    protected String getRequestParameter(String name) {
        String[] retObj = getRequestParameterValues(name);
        if (retObj == null) {
            return super.getRequestParameter(name);
        }
        return retObj[0];
    }

    @Override
    public void addResponseHeader(String name, String value) {
        if ("X-Portlet-Title".equals(name)) {
            getRenderResponse().setTitle(value);
        } else {
            super.addResponseHeader(name, value);
        }
    }

    @Override
    public void setResponseHeader(String name, String value) {
        if ("X-Portlet-Title".equals(name)) {
            getRenderResponse().setTitle(value);
        } else {
            super.setResponseHeader(name, value);
        }
    }
}
