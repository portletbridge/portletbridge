/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

/**
 * @author leo
 * 
 */
public class FacesURI {
	
	public interface Parameter {
		
		public String getName();
		
		public String getValue();
		
	}

	private static final Pattern urlPattern = Pattern
			.compile("^(\\w*:)?(//[\\w\\._-]+[^/:])?((?:\\:)(\\d+))?([^?]*)?((?:\\?)(.*))?$");

	private static final String NULL = "";

	private String protocol;

	private String host;

	private int port = -1;

	/**
	 * The authority part of this URL.
	 * 
	 * @serial
	 */
	private String authority;

	/**
	 * The path part of this URL.
	 */
	private transient String path;

	/**
	 * The userinfo part of this URL.
	 */
	private transient String userInfo;

	private Map<String, String[]> parameters;

	private int _length;

	public FacesURI() {
		// Clear parameters map.
		setParameters(new LinkedHashMap<String, String[]>(30));
	}

	/**
	 * @param url
	 */
	public FacesURI(String url) throws MalformedURLException {
		Matcher urlMatcher = urlPattern.matcher(url);
		if (!urlMatcher.matches()) {
			throw new MalformedURLException(url);
		}
		_length = url.length();
		this.protocol = urlMatcher.group(1);
		this.host = urlMatcher.group(2);
		String portStr = urlMatcher.group(4);
		if (null != portStr && portStr.length() > 0) {
			this.port = Integer.parseInt(portStr);
		}
		this.path = urlMatcher.group(5);
		setQueryString(urlMatcher.group(7));
	}

	/**
	 * Clone constructor
	 * 
	 * @param src
	 */
	public FacesURI(FacesURI src) {
		if (null == src) {
			throw new NullPointerException("Source URL is null");
		}
		this._length = src._length;
		this.protocol = src.protocol;
		this.host = src.host;
		this.port = src.port;
		this.path = src.path;
		this.setParameters(new LinkedHashMap<String, String[]>(src
				.getParameters()));
		this.authority = src.authority;
		this.userInfo = src.userInfo;
	}

	/**
	 * Create relative URI.
	 * @param src
	 * @param file
	 */
	public FacesURI(FacesURI src,String file) {
		this(src);
		if (null == file) {
			throw new NullPointerException("Relative URL is null");
		}
		int i;
		if(file.startsWith("/")){
			this.path = file;
		} else if(this.path.endsWith("/")){
			this.path = this.path+file;
		} else if((i=this.path.lastIndexOf('/'))>=0 ){
			this.path = this.path.substring(0, i)+file;
		} else {
			this.path = file;
		}
	}
	
	// Factory methods
	
	/**
	 * Creates new {@link FacesURI} from path ( viewId ).
	 * @param path
	 * @return
	 */
	public static FacesURI of(String path){
		FacesURI facesURI = new FacesURI();
		facesURI.setPath(path);
		return facesURI;
	}
	
	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the authority
	 */
	public String getAuthority() {
		return authority;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the userInfo
	 */
	public String getUserInfo() {
		return userInfo;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		if (null != getParameters() && getParameters().size() > 0) {
			StringBuilder queryString = new StringBuilder();
			for (Iterator<Entry<String, String[]>> iterator = getParameters()
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String[]> param = iterator.next();
				String[] values = param.getValue();
				for (int i = 0; i < values.length; i++) {
					queryString.append(encode(param.getKey()));
					if (values[i] != NULL) {
						queryString.append('=').append(encode(values[i]));
					}
					if (i < values.length - 1) {
						queryString.append('&');
					}

				}
				if (iterator.hasNext()) {
					queryString.append('&');
				}
			}
			return queryString.toString();

		} else {
			return null;
		}
	}

	public String getParameter(String name) {
		String[] values = getParameters().get(name);
		if (null != values && values.length > 0) {
			return values[0];
		}
		return null;
	}

	public void setParameter(String name, String value) {
		getParameters().put(name, new String[] { value });
	}

	public void setParameter(Parameter par){
		setParameter(par.getName(), par.getValue());
	}

	public void addParameter(String name, String value) {
		String[] values = getParameters().get(name);
		if (null != values && values.length > 0) {
			List<String> valuesList = new ArrayList<String>(Arrays
					.asList(values));
			valuesList.add(value);
			values = valuesList.toArray(new String[valuesList.size()]);
		} else {
			values = new String[] { value };
		}
		getParameters().put(name, values);
	}

	public void addParameter(Parameter par){
		addParameter(par.getName(), par.getValue());
	}
	
	public String removeParameter(String name) {
		String[] values = getParameters().remove(name);
		if (null != values && values.length > 0) {
			return values[0];
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder url = new StringBuilder(_length);
		if (null != protocol) {
			url.append(protocol);
		}
		if (null != host) {
			url.append(host);
		}
		if (port > 0) {
			url.append(':').append(port);
		}
		url.append(path);
		String queryString = getQueryString();
		if (null != queryString) {
			url.append('?').append(queryString);
		}
		return url.toString();
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}

	public void setQueryString(String queryString) {
		// Clear parameters map.
		setParameters(new LinkedHashMap<String, String[]>(30));
		if (null != queryString && queryString.length() > 0) {
			String[] queryParams = queryString.split("&");
			for (int i = 0; i < queryParams.length; i++) {
				String par = queryParams[i];
				int eqIndex = par.indexOf('=');
				if (eqIndex >= 0) {
					String value = par.substring(eqIndex + 1);
					String name = par.substring(0, eqIndex);
					addParameter(decode(name), decode(value));
				} else {
					addParameter(par, NULL);
				}
			}
		}
	}

	/**
	 * This method converts absolute or relative internal uri path ( aka vievId ) to
	 * the form that will be served by Faces Servlet.
	 * @param context
	 * @return
	 */
	public FacesURI asFacesRequest(FacesContext context) {
		FacesURI facesURI = new FacesURI(this);
		StringBuilder facesPath = new StringBuilder(facesURI.getPath());
		// Convert internal path to be served as Faces Servlet
		String pathInfo = context.getExternalContext().getRequestPathInfo();
		String servletPath = context.getExternalContext()
				.getRequestServletPath();
		// TODO - resolve relative URL.
		// If the path returned by HttpServletRequest.getServletPath()
		// returns a zero-length String, then the FacesServlet has
		// been mapped to '/*'.
		if (servletPath != null && servletPath.length() != 0) {
			if (pathInfo != null) {
				// preffix mapping, is it already starts with preffix ?
				if (facesPath.indexOf(servletPath) != 0) {
					facesURI.setPath(facesPath.insert(0, servletPath)
							.toString());
				}
			} else if (servletPath.indexOf('.') >= 0) {
				// suffix mapping.
				String suffix = servletPath.substring(servletPath
						.lastIndexOf('.'));
				// TODO - replace suffix instead of append ?
				if (!facesPath.toString().endsWith(suffix)) {
					facesURI.setPath(facesPath.append(suffix).toString());
				}
			}
		}

		return facesURI;
	}

	@SuppressWarnings("deprecation")
	protected String decode(String par) {
		try {
			return URLDecoder.decode(par, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is part of the standard encoding. Just in case, return
			// string
			// decoded by default encoding
			return URLDecoder.decode(par);
		}
	}

	@SuppressWarnings("deprecation")
	protected String encode(String par) {
		try {
			return URLEncoder.encode(par, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is part of the standard encoding. Just in case, return
			// string
			// decoded by default encoding
			return URLEncoder.encode(par);
		}
	}

}
