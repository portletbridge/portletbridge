/**
 * 
 */
package org.jboss.portletbridge.config;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

final class MockXmlReader implements XMLReader {
	
	ContentHandler contentHandler;
	
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	public DTDHandler getDTDHandler() {

		return null;
	}

	public EntityResolver getEntityResolver() {

		return null;
	}

	public ErrorHandler getErrorHandler() {

		return null;
	}

	public boolean getFeature(String name)
			throws SAXNotRecognizedException, SAXNotSupportedException {

		return false;
	}

	public Object getProperty(String name)
			throws SAXNotRecognizedException, SAXNotSupportedException {

		return null;
	}

	public void parse(InputSource input) throws IOException,
			SAXException {

		
	}

	public void parse(String systemId) throws IOException, SAXException {

		
	}

	public void setContentHandler(ContentHandler handler) {
		contentHandler = handler;
		
	}

	public void setDTDHandler(DTDHandler handler) {

		
	}

	public void setEntityResolver(EntityResolver resolver) {

		
	}

	public void setErrorHandler(ErrorHandler handler) {

		
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {

		
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {

		
	}
}