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

import java.util.regex.Pattern;

import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageFactory;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletVdlFactoryImpl extends ViewDeclarationLanguageFactory {
    private static final String DISABLE_JSF_FACELET_INIT_PARAM = "javax.faces.DISABLE_FACELET_JSF_VIEWHANDLER";

    private ViewDeclarationLanguageFactory wrappedFactory;
    private ViewDeclarationLanguage nonFaceletVDL;
    private String viewExtension;
    private Pattern viewMappingPattern;

    public PortletVdlFactoryImpl(ViewDeclarationLanguageFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;

        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        if (null != extContext) {
            viewExtension = loadFaceletExtension(extContext);
            viewMappingPattern = loadFaceletMappingPattern(extContext);
        }
    }

    @Override
    public ViewDeclarationLanguageFactory getWrapped() {
        return wrappedFactory;
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguageFactory#getViewDeclarationLanguage(java.lang.String)
     */
    @Override
    public ViewDeclarationLanguage getViewDeclarationLanguage(String viewId) {
        ViewDeclarationLanguage vdl = getWrapped().getViewDeclarationLanguage(viewId);
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

        if (BridgeUtil.isPortletRequest() && !isFacelet(extContext, viewId)) {
            if (null == nonFaceletVDL) {
                String renderPolicy = extContext.getInitParameter(Bridge.RENDER_POLICY);
                if (null == renderPolicy) {
                    renderPolicy = Bridge.BridgeRenderPolicy.DEFAULT.toString();
                }

                switch (Bridge.BridgeRenderPolicy.valueOf(renderPolicy)) {
                    case ALWAYS_DELEGATE:
                        nonFaceletVDL = vdl;
                        break;
                    default:
                        // Improve performance by not calling Facelet VDL first as we know it will fail.
                        nonFaceletVDL = new PortletJspVdlImpl(vdl);
                        break;
                }
            }
            return nonFaceletVDL;
        }
        return vdl;
    }

    protected boolean isFacelet(ExternalContext extContext, String viewId) {
        String faceletsDisabled = extContext.getInitParameter(DISABLE_JSF_FACELET_INIT_PARAM);
        if (null != faceletsDisabled && Boolean.parseBoolean(faceletsDisabled.toLowerCase())) {
            // Facelets is disabled
            return false;
        }

        if (viewId.endsWith(viewExtension)) {
            // Extension matches, we have a Facelet viewId
            return true;
        }
        return viewMappingPattern != null && viewMappingPattern.matcher(viewId).matches();
    }

    protected String loadFaceletExtension(ExternalContext extContext) {
        String faceletSuffix = extContext.getInitParameter(ViewHandler.FACELETS_SUFFIX_PARAM_NAME);
        if (null != faceletSuffix) {
            faceletSuffix = faceletSuffix.trim();
        }

        if (null == faceletSuffix || faceletSuffix.length() == 0) {
            faceletSuffix = ViewHandler.DEFAULT_FACELETS_SUFFIX;
        }

        return faceletSuffix;
    }

    protected Pattern loadFaceletMappingPattern(ExternalContext extContext) {
        String viewMappings = extContext.getInitParameter(ViewHandler.FACELETS_VIEW_MAPPINGS_PARAM_NAME);
        if (null == viewMappings) {
            return null;
        }

        viewMappings.trim();
        if (viewMappings.length() == 0) {
            return null;
        }

        return Pattern.compile(convertToRegex(viewMappings));
    }

    protected String convertToRegex(String viewMappings) {
        // Remove spaces
        viewMappings = viewMappings.replaceAll("\\s", "");

        // Escape '.'
        viewMappings = viewMappings.replaceAll("\\.", "\\\\.");

        // Change '*' to represent any match
        viewMappings = viewMappings.replaceAll("\\*", ".*");

        // Separate the mappings
        viewMappings = viewMappings.replaceAll(";", "|");

        return viewMappings;
    }

}
