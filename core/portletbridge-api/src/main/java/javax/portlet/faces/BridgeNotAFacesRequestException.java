/**
 * 
 */
package javax.portlet.faces;

/**
 * @author asmirnov
 *
 */
@SuppressWarnings("serial")
public class BridgeNotAFacesRequestException extends BridgeException {

	/**
	 * 
	 */
	public BridgeNotAFacesRequestException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BridgeNotAFacesRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BridgeNotAFacesRequestException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BridgeNotAFacesRequestException(Throwable cause) {
		super(cause);
	}

}
