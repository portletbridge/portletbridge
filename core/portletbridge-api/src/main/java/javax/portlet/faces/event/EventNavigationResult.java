package javax.portlet.faces.event;

/**
 * An <code>EventNavigationResult</code> is the type of object that can
 * be returned from a <code>BrdigeEventHandler.handleEvent</code> call.
 * When it is returned (non-null) it conveys the Faces navigation information
 * to the bridge that it needs to utilize the Faces <code>NavigationHandler</code>
 * to evaluate the navigation according to the configured rules.  The
 * <code>fromAction</code> corresponds to the <code>fromAction</code> string
 * in the faces-config.xml navigation rule.  The <code>outcome</code> 
 * corresponds to the <code>outcome</code> string in the navigation rule.
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
   * Gets the fromAction stored in this object. The <code>fromAction</code>
   * corresponds to the <code>fromAction</code> string in the faces-config.xml
   * navigation rule.
   * 
   * @return <code>String</code> containing the fromAction
   */
	public String getFromAction() {
		return fromAction;
	}
  /**
   * Sets the fromAction for this object. The <code>fromAction</code>
   * corresponds to the <code>fromAction</code> string in the faces-config.xml
   * navigation rule.
   * 
   * @param action
   *       new fromAction
   */    
	public void setFromAction(String fromAction) {
		this.fromAction = fromAction;
	}
  /**
   * Gets the outcome stored in this object. The <code>outcome</code>
   * corresponds to the <code>outcome</code> string in the faces-config.xml
   * navigation rule.
   * 
   * @return <code>String</code> containing the fromAction
   */    
	public String getOutcome() {
		return outcome;
	}
  /**
   * Sets the fromAction for this object. The <code>fromAction</code>
   * corresponds to the <code>fromAction</code> string in the faces-config.xml
   * navigation rule.
   * 
   * @param outcome
   *       new outcome
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
