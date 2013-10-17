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
package org.jboss.portletbridge.renderkit.portlet.jsf2_0;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;

/**
 * Fixes various issues in Partial Rendering of JSF 2.0.
 *
 * 1) Render the ID of the portlet fragment, instead of the <code><body></code> element, when replacing the entire
 * JSF content in a partial response.
 *
 * 2) If the ViewState hidden form element is not present, add it ourselves to ensure it remains present in the form.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class Jsf20PortletPartialResponseWriter extends ResponseWriterWrapper {

    private static final String ID_ATTRIBUTE = "id";
    private static final String CHANGES_ELEMENT = "changes";
    private static final String UPDATE_ELEMENT = "update";
    private static final String FORM_ELEMENT = "form";
    private static final String INPUT_ELEMENT = "input";

    private ResponseWriter wrapped;
    private boolean inChanges = false;
    private boolean inUpdate = false;
    private boolean inInput = false;
    private boolean viewStateWritten = false;

    public Jsf20PortletPartialResponseWriter(ResponseWriter writer) {
        wrapped = writer;
    }

    @Override
    public ResponseWriter getWrapped() {
        return wrapped;
    }

    @Override
    public void endElement(String name) throws IOException {
        if (inChanges && FORM_ELEMENT.equals(name) && !viewStateWritten) {
            // Write the view state into a hidden field on the form, if it wasn't already written
            super.startElement("input", null);

            super.writeAttribute("type", "hidden", null);
            super.writeAttribute("name", PartialResponseWriter.VIEW_STATE_MARKER, null);
            super.writeAttribute("id", PartialResponseWriter.VIEW_STATE_MARKER, null);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewState = facesContext.getApplication().getStateManager().getViewState(facesContext);
            super.writeAttribute("value", viewState, null);
            super.writeAttribute("autocomplete", "off", null);

            super.endElement("input");

            viewStateWritten = true;
        }

        super.endElement(name);

        if (inChanges && CHANGES_ELEMENT.equals(name)) {
            inChanges = false;
        } else if (inUpdate && UPDATE_ELEMENT.equals(name)) {
            inUpdate = false;
        } else if (inInput && INPUT_ELEMENT.equals(name)) {
            inInput = false;
        }
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if (CHANGES_ELEMENT.equals(name)) {
            inChanges = true;
        } else if (inChanges && UPDATE_ELEMENT.equals(name)) {
            inUpdate = true;
        } else if (inUpdate && INPUT_ELEMENT.equals(name)) {
            inInput = true;
        } else if (FORM_ELEMENT.equals(name) && viewStateWritten) {
            // Encountering another form, reset flag
            viewStateWritten = false;
        }

        super.startElement(name, component);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (ID_ATTRIBUTE.equals(name)) {
            if (inUpdate && PartialResponseWriter.RENDER_ALL_MARKER.equals(value)) {
                // Instead of writing out the ViewRoot marker, set it to the ID of the root component of portlet
                FacesContext facesContext = FacesContext.getCurrentInstance();
                value = facesContext.getViewRoot().getContainerClientId(facesContext);
            } else if (inInput && PartialResponseWriter.VIEW_STATE_MARKER.equals(value)) {
                // We're writing the ViewState
                viewStateWritten = true;
            }
        }

        super.writeAttribute(name, value, property);
    }
}
