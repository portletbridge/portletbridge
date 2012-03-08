/**
 * 
 */
package javax.portlet.faces;

/**
 * Thrown when the bridge's <code>doFacesRequest</code> method is called
 * and the bridge is in an uninitialized state.
 */
public class BridgeUninitializedException extends BridgeException {

	public BridgeUninitializedException() {
		super();
	}

	/**
	 * @param message
	 */
	public BridgeUninitializedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BridgeUninitializedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BridgeUninitializedException(String message, Throwable cause) {
		super(message, cause);
	}

}
