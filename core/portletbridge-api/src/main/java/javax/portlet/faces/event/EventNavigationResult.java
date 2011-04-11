package javax.portlet.faces.event;

/**
 * @author asmirnov
 *
 */
public class EventNavigationResult {
    private String fromAction;
    private String outcome;
    
    
    public EventNavigationResult() {
	}
	/**
	 * @param fromAction
	 * @param outcome
	 */
	public EventNavigationResult(String fromAction, String outcome) {
		this.fromAction = fromAction;
		this.outcome = outcome;
	}
	/**
	 * @return the fromAction
	 */
	public String getFromAction() {
		return fromAction;
	}
	/**
	 * @param fromAction the fromAction to set
	 */
	public void setFromAction(String fromAction) {
		this.fromAction = fromAction;
	}
	/**
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}
	/**
	 * @param outcome the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromAction == null) ? 0 : fromAction.hashCode());
		result = prime * result + ((outcome == null) ? 0 : outcome.hashCode());
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
		EventNavigationResult other = (EventNavigationResult) obj;
		if (fromAction == null) {
			if (other.fromAction != null)
				return false;
		} else if (!fromAction.equals(other.fromAction))
			return false;
		if (outcome == null) {
			if (other.outcome != null)
				return false;
		} else if (!outcome.equals(other.outcome))
			return false;
		return true;
	}

    
}
