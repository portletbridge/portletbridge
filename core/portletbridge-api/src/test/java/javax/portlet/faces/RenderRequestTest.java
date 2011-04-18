package javax.portlet.faces;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RenderRequestTest extends GenericPortletTestBase {
	
	@Mock
	private RenderRequest request;
	
	@Mock
	private RenderResponse response;
	
	@Mock
	private Bridge bridge;

	@Before
	public void setupRequest(){
		
	}
	/**
	 * Test method for
	 * {@link javax.portlet.faces.GenericFacesPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)}
	 * .
	 * 
	 * @throws PortletException
	 * @throws IOException
	 */
	@Test
	public void testDoDispatchRenderRequestRenderResponse()
			throws PortletException, IOException {
		GenericFacesPortlet portlet = createGenericPortlet();
		doReturn(portletContext).when(portlet).getPortletContext();
		doReturn(portletConfig).when(portlet).getPortletConfig();
		when(request.getPortletMode()).thenReturn(PortletMode.VIEW);
		when(request.getWindowState()).thenReturn(WindowState.NORMAL);
		portlet.doDispatch(request, response);
		verify(portlet).doFacesDispatch(request, response);
	}

}
