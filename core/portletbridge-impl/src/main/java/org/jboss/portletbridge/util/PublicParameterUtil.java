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
package org.jboss.portletbridge.util;

import java.util.Enumeration;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

/**
 * @author kenfinnigan
 */
public class PublicParameterUtil {

    public static boolean processPublicParameters(FacesContext facesContext, PortletRequest request,
            Map<String, String> publicParameterMapping, Enumeration<String> parameterNames, ParameterFunction function,
            String portletName) {

        boolean valueChanged = false;
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        StringBuilder prefixedName = new StringBuilder(portletName).append(':');
        int preffixLength = prefixedName.length();
        Map<String, String[]> publicParameters = request.getPublicParameterMap();

        // Iterate over configured parameter names.
        while (parameterNames.hasMoreElements()) {
            // First, check for a common name mapping.
            String name = parameterNames.nextElement();
            // Lookup for parameter name in the mappings table.
            String mappingEl = publicParameterMapping.get(name);
            if (null == mappingEl) {
                // if no common mapping found, check for prefixed name.
                prefixedName.setLength(preffixLength);
                mappingEl = publicParameterMapping.get(prefixedName.append(name).toString());
            }
            if (null != mappingEl) {
                // Found a mapping for the parameter, process it.
                ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, mappingEl, Object.class);
                valueChanged = function.processParameter(elContext, publicParameters, name, valueExpression) ? true
                        : valueChanged;
            }
        }
        return valueChanged;
    }
}
