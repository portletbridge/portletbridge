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
package org.jboss.portletbridge.bridge.context;

import org.jboss.portletbridge.bridge.config.BridgeConfig;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.ResponseStateManager;

/**
 * Handle specific behavior related to JSF 2.2. Currently that is limited to appending a Client Window Id
 * during the process of encoding URLs.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class BridgeContextJsf22Impl extends BridgeContextImpl {
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    public BridgeContextJsf22Impl(BridgeConfig bridgeConfig) {
        super(bridgeConfig);
    }

    @Override
    public String appendClientWindowId(String url) {
        FacesContext context = FacesContext.getCurrentInstance();
        ClientWindow cw = context.getExternalContext().getClientWindow();
        boolean appendClientWindow = false;
        if (null != cw) {
            appendClientWindow = cw.isClientWindowRenderModeEnabled(context);
        }
        if (appendClientWindow && -1 == url.indexOf(ResponseStateManager.CLIENT_WINDOW_URL_PARAM)) {
            if (null != cw) {
                String clientWindowId = cw.getId();
                StringBuilder builder = new StringBuilder(url);
                int q = url.indexOf(QUESTION_MARK);
                if (-1 == q) {
                    builder.append(QUESTION_MARK);
                } else {
                    builder.append(AMPERSAND);
                }
                builder.append(ResponseStateManager.CLIENT_WINDOW_URL_PARAM).append(EQUALS).append(clientWindowId);
                return builder.toString();
            }
        }
        return url;
    }
}
