/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.portletbridge.context;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asmirnov
 */
public class PortalActionURL {

    private static final Pattern urlPattern = Pattern
        .compile("^(\\w*:)?(//[\\w\\._-]+[^/:?])?((?:\\:)(\\d+))?([^?]*)?((?:\\?)(.*))?$");

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

    private PortalUrlQueryString queryString;

    private int _length;

    /**
     * @param url
     */
    public PortalActionURL(String url) throws MalformedURLException {
        this(url, false);
    }

    /**
     * @param url
     */
    public PortalActionURL(String url, boolean escape) throws MalformedURLException {
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
        this.queryString = new PortalUrlQueryString(urlMatcher.group(7), escape);
    }

    /**
     * Clone constructor
     *
     * @param src
     */
    public PortalActionURL(PortalActionURL src, boolean escape) {
        if (null == src) {
            throw new NullPointerException("Source URL is null");
        }
        this._length = src._length;
        this.protocol = src.protocol;
        this.host = src.host;
        this.port = src.port;
        this.path = src.path;
        this.queryString = new PortalUrlQueryString(src.getQueryString(), escape);
        this.authority = src.authority;
        this.userInfo = src.userInfo;
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
        if (null != queryString) {
            return queryString.toString();
        } else {
            return null;
        }
    }

    public boolean hasParameter(String name){
        return queryString.hasParameter(name);
    }

    public String getParameter(String name) {
        return queryString.getParameter(name);
    }

    public void setParameter(String name, String value) {
        queryString.setParameter(name, value);
    }

    public void addParameter(String name, String value) {
        queryString.addParameter(name, value);
    }

    public String removeParameter(String name) {
        return queryString.removeParameter(name);
    }

    public int parametersSize() {
        return queryString.parametersSize();
    }

    public boolean isInContext(String context) {
        return host == null && protocol == null && port == -1
            && (path.startsWith(context + "/") || (!path.startsWith("/")));
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
        return queryString.getParameters();
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
        this.queryString.setParameters(parameters);
    }

    public void setQueryString(String queryString, boolean escape) {
        this.queryString = new PortalUrlQueryString(queryString, escape);
    }

}
