/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov
 * 
 */
public abstract class StateHandler extends DefaultHandler {

	private final ContentHandler parentHandler;

	private final XMLReader reader;

	private int depth = 0;

	public StateHandler(XMLReader reader, ContentHandler parentHandler) {
		super();
		this.reader = reader;
		this.parentHandler = parentHandler;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		ContentHandler nextHandler = getNextHandler(uri, localName, attributes);
		if (null == nextHandler) {
			depth++;
		} else {
			reader.setContentHandler(nextHandler);
		}
	}

	protected ContentHandler getNextHandler(String uri, String localName,
			Attributes attributes) throws SAXException {
		return null;
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if(depth-- == 0){
			if(null != parentHandler){
				reader.setContentHandler(parentHandler);
			}
			endLastElement();
		}
	}

	protected void endLastElement() throws SAXException {
		// Do nothing		
	}
	
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException
    {
      // Do nothing, to avoid network requests to external DTD/Schema
      return new InputSource(new StringReader(""));
    }

	protected XMLReader getReader() {
		return reader;
	} 
}
