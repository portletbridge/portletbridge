/**
 * 
 */
package org.jboss.portletbridge.application;

import java.util.regex.Matcher;

import javax.faces.render.ResponseStateManager;

import junit.framework.TestCase;

/**
 * @author asmirnov
 *
 */
public class PortletStateManagerTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.application.PortletStateManager#PortletStateManager(javax.faces.application.StateManager)}.
	 */
	public void testPortletStateManager() {
		
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.application.PortletStateManager#writeState(javax.faces.context.FacesContext, java.lang.Object)}.
	 */
	public void testStateStringPattern() {
		String input = "<input name=\""+ResponseStateManager.VIEW_STATE_PARAM+"\" value=\"foo\" >";
		match(input);
	}

	public void testStateStringPattern1() {
		String input = "<input value=\"foo\" name=\""+ResponseStateManager.VIEW_STATE_PARAM+"\" >";
		match(input);
	}

	public void testStateStringPattern2() {
		String input = "<input name='xxx' value='yyy><input name=\""+ResponseStateManager.VIEW_STATE_PARAM+"\" value=\"foo\" >";
		match(input);
	}
	

	public void testGetStateValue() throws Exception {
		String input = "<input name='xxx' value='yyy><input name=\""+ResponseStateManager.VIEW_STATE_PARAM+"\" value=\"foo\" autocomplete=\"false\" >";
	    assertEquals("foo", PortletStateManager.getStateValue(input));
    }
	
	public void testGetStateValueMojarra() throws Exception {
	    String input="<input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"j_id1\" autocomplete=\"off\"  />";
	    assertEquals("j_id1", PortletStateManager.getStateValue(input));
    }
	
	private void match(String input) {
		Matcher matcher = PortletStateManager.PATTERN.matcher(input);
		if(!matcher.matches()){
			matcher = PortletStateManager.PATTERN2.matcher(input);
		}
		assertTrue(matcher.matches());
		for(int i=0;i<=matcher.groupCount();i++ ){
			System.out.println(matcher.group(i));
		}
		assertEquals(1,matcher.groupCount());
	}

}
