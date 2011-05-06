/**
 * 
 */
package org.jboss.portletbridge.config;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author asmirnov
 * 
 */
public class StringContentHandler extends StateHandler {

	private StringBuilder result;

	public StringContentHandler(XMLReader reader, ContentHandler parentHandler,
			StringBuilder result) {
		super(reader, parentHandler);
		this.result = result;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		result.append(ch, start, length);
	}

	protected StringBuilder getResult() {
		return result;
	}
}
