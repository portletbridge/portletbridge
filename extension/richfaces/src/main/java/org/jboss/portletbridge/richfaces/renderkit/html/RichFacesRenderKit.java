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
package org.jboss.portletbridge.richfaces.renderkit.html;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.Renderer;

import org.richfaces.component.UICalendar;
import org.richfaces.component.UIEditor;
import org.richfaces.component.UIInplaceInput;
import org.richfaces.component.UIInplaceSelect;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class RichFacesRenderKit extends RenderKitWrapper {

    private static final String RENDERER_SUFFIX = "Renderer";

    private static final String EDITOR_FAMILY = UIEditor.COMPONENT_FAMILY;
    private static final String EDITOR_RENDERER_TYPE = UIEditor.COMPONENT_TYPE + RENDERER_SUFFIX;

    private static final String INPLACESELECT_FAMILY = UIInplaceSelect.COMPONENT_FAMILY;
    private static final String INPLACESELECT_RENDERER_TYPE = UIInplaceSelect.COMPONENT_TYPE + RENDERER_SUFFIX;

    private static final String INPLACEINPUT_FAMILY = UIInplaceInput.COMPONENT_FAMILY;
    private static final String INPLACEINPUT_RENDERER_TYPE = UIInplaceInput.COMPONENT_TYPE + RENDERER_SUFFIX;

    private static final String CALENDAR_FAMILY = UICalendar.COMPONENT_FAMILY;
    private static final String CALENDAR_RENDERER_TYPE = UICalendar.COMPONENT_TYPE + RENDERER_SUFFIX;

    private RenderKit wrapped;

    public RichFacesRenderKit(RenderKit renderKit) {
        wrapped = renderKit;
    }

    /**
     * @see javax.faces.render.RenderKitWrapper#getWrapped()
     */
    @Override
    public RenderKit getWrapped() {
        return wrapped;
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        if (EDITOR_FAMILY.equals(family) && EDITOR_RENDERER_TYPE.equals(rendererType)) {
            return new RichFacesEditorRenderer();
        }
        else if(INPLACESELECT_FAMILY.equals(family) && INPLACESELECT_RENDERER_TYPE.equals(rendererType)) {
            return new RichFacesInplaceSelectRenderer();
        }
        else if(INPLACEINPUT_FAMILY.equals(family) && INPLACEINPUT_RENDERER_TYPE.equals(rendererType)) {
            return new RichFacesInplaceInputRenderer();
        }
        else if(CALENDAR_FAMILY.equals(family) && CALENDAR_RENDERER_TYPE.equals(rendererType)) {
            return new RichFacesCalendarRenderer();
        }
        return wrapped.getRenderer(family, rendererType);
    }

}
