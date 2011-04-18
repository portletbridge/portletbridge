package javax.portlet.faces;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import javax.portlet.PortletException;

import org.junit.Test;

public class PortletInitTest extends GenericPortletTestBase {


	@Test
	public void preseveActionParams() throws Exception {
		whenPortletInitParameter("javax.portlet.faces.preserveActionParams", "true");
		initPortlet();
		verifyContextAttribute("javax.portlet.faces.foo.preserveActionParams", Boolean.TRUE);
	}

	@Test
	public void excludedRequestAttreibutes() throws Exception {
		whenPortletInitParameter("javax.portlet.faces.excludedRequestAttributes", "bar,baz,boo");
		initPortlet();
		verifyContextAttribute("javax.portlet.faces.foo.excludedRequestAttributes", Arrays.asList("bar","baz","boo"));
	}

	@Test
	public void customAttribute() throws Exception {
		whenPortletInitParameter("javax.portlet.faces.extension.my_package.my_attribute","xxx");
		initPortlet();
		verifyContextAttribute("javax.portlet.faces.extension.my_package.foo.my_attribute", "xxx");
	}

	private void initPortlet() throws PortletException {
		GenericFacesPortlet portlet = createGenericPortlet();
		portlet.init(portletConfig);
	}

	private void whenPortletInitParameter(String name,String value){
		when(portletConfig.getInitParameter(
		name)).thenReturn(value);
		portletInitParameters.add(name);
	}

	private void verifyContextAttribute(String name,Object value){
		verify(portletContext,atLeastOnce()).setAttribute(name, value);
	}
}
