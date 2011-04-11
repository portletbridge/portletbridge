/**
 * 
 */
package javax.portlet.faces;

/**
 * @author asmirnov
 *
 */
@SuppressWarnings("serial")
public class BridgeInvalidViewPathException extends BridgeException {

	/**
	 * 
	 */
	public BridgeInvalidViewPathException() {
		super();
	}

	/**
	 * @param message
	 */
	public BridgeInvalidViewPathException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BridgeInvalidViewPathException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BridgeInvalidViewPathException(String message, Throwable cause) {
		super(message, cause);
	}

}
