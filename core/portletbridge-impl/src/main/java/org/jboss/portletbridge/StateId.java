/**
 * 
 */
package org.jboss.portletbridge;

import java.io.Serializable;

import javax.portlet.PortletMode;
import javax.portlet.faces.BridgeException;

/**
 * @author asmirnov
 *
 */
@SuppressWarnings("serial")
public class StateId implements Serializable {
	
	private PortletMode mode;
	
	private final String uuid ;

	/**
	 * @param scopeId
	 * @param mode
	 * @param uuid
	 */
	public StateId(PortletMode mode, String uuid) {
		this.mode = mode;
		this.uuid = uuid;
	}
	
	public StateId(String stateId) {
		int modeEnd = stateId.indexOf(':');
		if(modeEnd >= 0){
				mode = new PortletMode(stateId.substring(0, modeEnd));
				uuid = stateId.substring(modeEnd+1);
		} else {
			throw new BridgeException("Invalid StateId format");
		}
	}


	/**
	 * @return the mode
	 */
	public PortletMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(PortletMode mode) {
		this.mode = mode;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return mode.toString()+':'+uuid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateId other = (StateId) obj;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
