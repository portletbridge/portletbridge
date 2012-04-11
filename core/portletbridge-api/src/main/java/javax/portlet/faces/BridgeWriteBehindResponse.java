/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package javax.portlet.faces;

import java.io.IOException;


/**
 * The <code>BridgeWriteBehindResponse</code> interface defines the api the bridge relies
 * on to acquire the buffered JSP output from the response(Wrapper) used to handle the Faces
 * implementation dependent writeBehindResponse methodlogy/interface.<p>  
 * 
 * Note: the Portlet 1.0 Bridge relied on Portlet 1.0 which didn't support response wrappers.
 * In that version the writeBehindResponse behavior is provided in a Servlet ResponseWrapper inserted
 * in a Servlet filter set up to be called on included JSPs.  In Portlet 2.0 Bridge this behavior can
 * now be implemented directly in a Portlet ResponseWrapper which can then be registered for use with the bridge.
 * So that the bridge recognizes and use this support, such wrappers must implement this interface.<p>
 * 
 * Implementations must be one of the Portlet 2.0 <code>ResponseWrappers</code> and have a null constructor that utilizes
 * <code>FacesContext.getCurrentInstance().getExternalContext().getResponse()</code>
 * to acquire the response to be wrapped.
 */

public interface BridgeWriteBehindResponse
{
  /**
   * Called by the bridge after dispatching is complete to determine whether the 
   * JSP AfterViewContent was written as chars (written via a <code>PrintWriter</code>
   * 
   * 
   * @return <code>true</code> if the response (buffer) is represented as chars
   * written via the <code>PrintWriter</code>, false otherwise.
   */
  public boolean isChars();
  
  /**
   * Called by the bridge after dispatching is complete to acquire the AfterJSPContent
   * when the response has been written as characters.  The bridge writes this buffer
   * to the (real) response.
   * 
   * @return the response as a char[].
   */
  public char[] getChars();
  
  /**
   * Called by the bridge after dispatching is complete to determine whether the 
   * JSP AfterViewContent was written as bytes (written via an <code>OutputStream</code>
   * 
   * 
   * @return <code>true</code> if the response (buffer) is represented as bytes
   * written via the <code>OutputStream</code>, false otherwise.
   */
  public boolean isBytes();
  
  /**
   * Called by the bridge after dispatching is complete to acquire the AfterJSPContent
   * when the response has been written as bytes.  The bridge writes this buffer
   * to the (real) response.
   * 
   * @return the response as a byte[].
   */ 
  public byte[] getBytes();
  
  /**
   * Called by the bridge after dispatching to flush the current buffered content to the wrapped
   * response (this could be a Servlet or Portlet response).  This is done in a situation where
   * we aren't supporting writeBehind behavior.  We stil use a wrapped/buffered response because we use 
   * dispatch.forward which in many environments closes the writer at the end of the forward.  If not
   * wrapped, not further writing to the output would be feasible.
   * @throws IOException if content cannot be written
   */
  public void flushMarkupToWrappedResponse()
    throws IOException;  
  
  /**
   * Called by the bridge to detect whether this response actively participated
   * in the Faces writeBehind support and hence has data that should be written
   * after the View is rendered.  Typically, this method will return <code>true</code>
   * if the Faces write behind implementation specific flush api has been called
   * on this response, otherwise <code>false</code>
   * 
   * @return an indication of whether the response actually particpated in the writeBehind mechanism.
   */  
  public boolean hasFacesWriteBehindMarkup();
}
