/**
 * 
 */
package org.jboss.portletbridge.util;


import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author asmirnov
 *
 */
public class StateHandlerTest extends TestCase {
	
	private static final String PREFIX = "foo:";
	private static final String BAR = "bar";
	private static final String NS = "http://foo.com/";

	private XMLReader reader;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		reader = new MockXmlReader();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testReturnBack() throws Exception {
		ContentHandler parentHandler = new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName, String name,
					Attributes attributes) throws SAXException {
				throw new SAXException();
			}
			@Override
			public void endElement(String uri, String localName, String name)
					throws SAXException {
				throw new SAXException();
			}
		};		
		StateHandler handler = new StateHandler(reader,parentHandler){
			
		};
		reader.setContentHandler(handler);
		handler.startElement(NS, BAR, PREFIX+BAR, null);
		handler.startElement(NS, BAR, PREFIX+BAR, null);
		handler.endElement(NS, BAR, PREFIX+BAR);
		assertSame(handler, reader.getContentHandler());
		handler.endElement(NS, BAR, PREFIX+BAR);
		assertSame(handler, reader.getContentHandler());
		handler.endElement(NS, BAR, PREFIX+BAR);
		assertSame(parentHandler, reader.getContentHandler());
	}

}
