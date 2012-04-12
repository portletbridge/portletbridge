/**
 * 
 */
package javax.portlet.faces;

/**
 * Thrown when the bridge finds that the request is encoded with its marker indicating its a nonFaces target.
 */
@SuppressWarnings("serial")
public class BridgeNotAFacesRequestException extends BridgeException {

    public BridgeNotAFacesRequestException() {
        super();
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

    /**
     * @param message
     * @param cause
     */
    public BridgeNotAFacesRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
