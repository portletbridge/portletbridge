package org.jboss.portletbridge.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class TestPortlet extends GenericPortlet {
	
	public static final String HELLO_FROM_EMBEDDED_PORTAL = "Hello From Embedded Portal";

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.print(HELLO_FROM_EMBEDDED_PORTAL);
		writer.flush();
		writer.close();
	}

}
