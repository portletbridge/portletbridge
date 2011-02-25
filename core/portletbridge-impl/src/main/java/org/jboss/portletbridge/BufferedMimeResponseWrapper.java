/**
 *
 */
package org.jboss.portletbridge;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.filter.PortletResponseWrapper;

import org.jboss.portletbridge.io.FastBufferOutputStream;
import org.jboss.portletbridge.io.FastPrintWriter;

/**
 * @author asmirnov
 *
 */
public class BufferedMimeResponseWrapper extends PortletResponseWrapper
		implements MimeResponse {

	private FastBufferOutputStream fastBufferStream = null;

	private FastPrintWriter fastPrintWriter = null;

	public BufferedMimeResponseWrapper(MimeResponse response) {
		super(response);
	}

	@Override
	public MimeResponse getResponse() {
		return (MimeResponse) super.getResponse();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see javax.portlet.RenderResponse#getPortletOutputStream()
	 */
	public FastBufferOutputStream getPortletOutputStream() throws IOException {
		if (fastBufferStream == null) {
			fastBufferStream = new FastBufferOutputStream();
		}

		return fastBufferStream;
	}

	/**
	 * @return
	 * @throws IOException
	 * @see javax.portlet.RenderResponse#getWriter()
	 */
	public FastPrintWriter getWriter() throws IOException {
		if (fastPrintWriter == null) {
			fastPrintWriter = new FastPrintWriter();
		}

		return fastPrintWriter;
	}

	public void resetBuffers() {
		if (fastBufferStream != null) {
			fastBufferStream.reset();
		}

		if (fastPrintWriter != null) {
			fastPrintWriter.reset();
		}
	}

	/**
	 * @see javax.portlet.RenderResponse#reset()
	 */
	public void reset() {
		getResponse().reset();

		resetBuffers();
	}

	/**
	 * @see javax.portlet.RenderResponse#resetBuffer()
	 */
	public void resetBuffer() {
		if(!isCommitted()){
			getResponse().resetBuffer();
		}
		resetBuffers();
	}

	/**
	 * @throws IOException
	 * @see javax.portlet.RenderResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		getResponse().flushBuffer();
	}

	public void writeBufferedData() throws IOException {
		if (fastBufferStream != null) {
			OutputStream outputStream = getResponse().getPortletOutputStream();
			fastBufferStream.writeTo(outputStream);
			outputStream.flush();
		} else if (fastPrintWriter != null) {
			PrintWriter writer = getResponse().getWriter();
			fastPrintWriter.writeTo(writer);
			writer.flush();
		}
	}

	public String toString() {
		if (fastBufferStream != null) {
			return "Stream content: "+fastBufferStream.toString();
		} else if (fastPrintWriter != null) {
			return "Writer content: "+fastPrintWriter.toString();
		} else {
			return super.toString()+", no data written";
		}
	}

	public boolean isUseWriter() {
		return fastBufferStream == null;
	}

	public PortletURL createActionURL() {
		return getResponse().createActionURL();
	}

	public PortletURL createRenderURL() {
		return getResponse().createRenderURL();
	}

	public ResourceURL createResourceURL() {
		return getResponse().createResourceURL();
	}

	public int getBufferSize() {
		return getResponse().getBufferSize();
	}

	public CacheControl getCacheControl() {
		return getResponse().getCacheControl();
	}

	public String getCharacterEncoding() {
		return getResponse().getCharacterEncoding();
	}

	public String getContentType() {
		return getResponse().getCharacterEncoding();
	}

	public Locale getLocale() {
		return getResponse().getLocale();
	}

	public boolean isCommitted() {
		return getResponse().isCommitted();
	}

	public void setBufferSize(int size) {
		getResponse().setBufferSize(size);

	}

	public void setContentType(String type) {
		getResponse().setContentType(type);

	}

	/**
	 * Hook method for ajax rendering.
	 * @param name
	 * @param value
	 */
	public void setHeader(String name, String value) {
	    setProperty(name, value);
    }
}