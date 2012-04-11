package org.jboss.portletbridge.config;

import org.xml.sax.SAXException;

/**
 * @author asmirnov
 */
public class ParsingException extends SAXException {
    private static final long serialVersionUID = -5290968254626492196L;

	public ParsingException() {
	}

	/**
	 * @param message
	 */
	public ParsingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ParsingException(Exception cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParsingException(String message, Exception cause) {
		super(message, cause);
	}

}
