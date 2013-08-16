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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortalUrlQueryString {

    private static final String NULL = "";

    private Map<String, String[]> parameters;

    private boolean escape = false;

    public PortalUrlQueryString(String params) {
        this(params, false);
    }

    public PortalUrlQueryString(String params, boolean escape) {
        setQueryString(params);
        this.escape = escape;
    }

    @SuppressWarnings("deprecation")
    protected String decodeURL(String par) {
        try {
            return URLDecoder.decode(par, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is part of the standard encoding. Just in case, return string
            // decoded by default encoding
            return URLDecoder.decode(par);
        }
    }

    @SuppressWarnings("deprecation")
    protected String encodeURL(String par) {
        try {
            return URLEncoder.encode(par, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is part of the standard encoding. Just in case, return string
            // decoded by default encoding
            return URLEncoder.encode(par);
        }
    }

    public boolean hasParameter(String name){
        return parameters.containsKey(name);
    }

    public String getParameter(String name) {
        String[] values = parameters.get(name);
        if (null != values && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public void setParameter(String name, String value) {
        parameters.put(name, new String[] { value });
    }

    public void addParameter(String name, String value) {
        String[] values = parameters.get(name);
        if (null != values && values.length > 0) {
            List<String> valuesList = new ArrayList<String>(Arrays.asList(values));
            valuesList.add(value);
            values = valuesList.toArray(new String[valuesList.size()]);
        } else {
            values = new String[] { value };
        }
        parameters.put(name, values);
    }

    public String removeParameter(String name) {
        String[] values = parameters.remove(name);
        if (null != values && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public int parametersSize() {
        return parameters.size();
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public void setQueryString(String queryString) {
        // Clear parameters map.
        parameters = new LinkedHashMap<String, String[]>(30);
        if (null != queryString && queryString.length() > 0) {
            // PBR-290 - Added conversion to & for all encoded ampersands
            queryString = queryString.replace("&amp;", "&");
            String[] queryParams = queryString.split("&");
            for (int i = 0; i < queryParams.length; i++) {
                String par = queryParams[i];
                int eqIndex = par.indexOf('=');
                if (eqIndex >= 0) {
                    String value = par.substring(eqIndex + 1);
                    String name = par.substring(0, eqIndex);
                    addParameter(decodeURL(name), decodeURL(value));
                } else {
                    addParameter(par, NULL);
                }
            }
        }
    }

    /**
     * @return the queryString
     */
    public String toString() {
        // TODO - cache ?
        if (null != parameters && parameters.size() > 0) {
            StringBuilder queryString = new StringBuilder();
            for (Iterator<Entry<String, String[]>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, String[]> param = iterator.next();
                String[] values = param.getValue();
                for (int i = 0; i < values.length; i++) {
                    queryString.append(escape ? encodeURL(param.getKey()) : param.getKey());
                    if (values[i] != NULL) {
                        queryString.append('=').append(escape ? encodeURL(values[i]) : values[i]);
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

}
