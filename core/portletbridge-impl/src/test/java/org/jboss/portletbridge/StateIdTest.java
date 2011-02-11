/**
 * 
 */
package org.jboss.portletbridge;

import java.util.UUID;

import javax.portlet.PortletMode;

import junit.framework.TestCase;

/**
 * @author asmirnov
 *
 */
public class StateIdTest extends TestCase {

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
	 * Test method for {@link org.jboss.portletbridge.StateId#StateId(java.lang.String, javax.portlet.PortletMode, java.lang.String)}.
	 */
	public void testStateIdStringPortletModeString() {
		PortletMode mode = PortletMode.HELP;
		String uuid = UUID.randomUUID().toString();
		StateId stateId = new StateId(mode.toString()+':'+uuid);
		assertEquals(mode.toString(), stateId.getMode());
		assertEquals(uuid, stateId.getUuid());
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.StateId#StateId(java.lang.String)}.
	 */
	public void testStateIdString() {
		PortletMode mode = PortletMode.HELP;
		String uuid = UUID.randomUUID().toString();
		StateId stateId = new StateId(mode,uuid);
		assertEquals(mode.toString()+':'+uuid, stateId.toString());
	}

	/**
	 * Test method for {@link org.jboss.portletbridge.StateId#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		PortletMode mode = PortletMode.HELP;
		String uuid = UUID.randomUUID().toString();
		StateId stateId = new StateId(mode.toString()+':'+uuid);
		StateId stateIdFromString = new StateId(stateId.toString());
		assertEquals(stateId, stateIdFromString);
	}

}
