/**
 * 
 */
package org.jboss.portletbridge.util;

class ServletMapping {
	private String servletName;
	private String urlPattern;

	public ServletMapping() {
	}

	/**
	 * @param servletName
	 * @param urlPattern
	 */
	public ServletMapping(String servletName, String urlPattern) {
		super();
		this.servletName = servletName;
		this.urlPattern = urlPattern;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
}