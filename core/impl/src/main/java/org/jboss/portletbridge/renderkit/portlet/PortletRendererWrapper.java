package org.jboss.portletbridge.renderkit.portlet;

import javax.faces.FacesWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * Implemented by Portlet Bridge to enable its use in JSF 2.1, as RendererWrapper is now provided in JSF 2.2.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class PortletRendererWrapper extends Renderer implements FacesWrapper<Renderer> {

    public abstract Renderer getWrapped();

    @Override
    public String convertClientId(FacesContext context, String clientId) {
        return getWrapped().convertClientId(context, clientId);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return getWrapped().getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        getWrapped().decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        getWrapped().encodeBegin(context, component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        getWrapped().encodeChildren(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        getWrapped().encodeEnd(context, component);
    }

    @Override
    public boolean getRendersChildren() {
        return getWrapped().getRendersChildren();
    }

}
