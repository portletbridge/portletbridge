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

import java.beans.BeanInfo;
import java.io.IOException;

import javax.faces.FacesWrapper;
import javax.faces.application.Resource;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.StateManagementStrategy;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class VdlWrapper extends ViewDeclarationLanguage implements FacesWrapper<ViewDeclarationLanguage> {

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#getComponentMetadata(javax.faces.context.FacesContext, javax.faces.application.Resource)
     */
    @Override
    public BeanInfo getComponentMetadata(FacesContext context, Resource componentResource) {
        return getWrapped().getComponentMetadata(context, componentResource);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#getViewMetadata(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public ViewMetadata getViewMetadata(FacesContext context, String viewId) {
        return getWrapped().getViewMetadata(context, viewId);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#getScriptComponentResource(javax.faces.context.FacesContext, javax.faces.application.Resource)
     */
    @Override
    public Resource getScriptComponentResource(FacesContext context, Resource componentResource) {
        return getWrapped().getScriptComponentResource(context, componentResource);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#createView(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        return getWrapped().createView(context, viewId);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#restoreView(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return getWrapped().restoreView(context, viewId);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#buildView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
     */
    @Override
    public void buildView(FacesContext context, UIViewRoot root) throws IOException {
        getWrapped().buildView(context, root);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
     */
    @Override
    public void renderView(FacesContext context, UIViewRoot view) throws IOException {
        getWrapped().renderView(context, view);
    }

    /**
     * @see javax.faces.view.ViewDeclarationLanguage#getStateManagementStrategy(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public StateManagementStrategy getStateManagementStrategy(FacesContext context, String viewId) {
        return getWrapped().getStateManagementStrategy(context, viewId);
    }

    @Override
    public abstract ViewDeclarationLanguage getWrapped();

}
