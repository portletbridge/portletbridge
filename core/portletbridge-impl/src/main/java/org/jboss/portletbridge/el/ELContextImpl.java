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
package org.jboss.portletbridge.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.portlet.PortletConfig;

/**
 * @author asmirnov
 * 
 */
public class ELContextImpl extends ELContext {

    public static final class FunctionMapperImpl extends FunctionMapper {
        /**
         * @see javax.el.FunctionMapper#resolveFunction(java.lang.String, java.lang.String)
         */
        public Method resolveFunction(String arg0, String arg1) {
            return null;
        }
    }

    public static final class VariableMapperImpl extends VariableMapper {
        private Map<String, ValueExpression> variables = new HashMap<String, ValueExpression>();

        /**
         * @see javax.el.VariableMapper#resolveVariable(java.lang.String)
         */
        public ValueExpression resolveVariable(String name) {
            return (ValueExpression) this.variables.get(name);
        }

        /**
         * @see javax.el.VariableMapper#setVariable(java.lang.String, javax.el.ValueExpression)
         */
        public ValueExpression setVariable(String name, ValueExpression variable) {
            return (ValueExpression) this.variables.put(name, variable);
        }
    }

    private ELResolver resolver;

    private VariableMapper variableMapper = new VariableMapperImpl();

    private FunctionMapper functionMapper = new FunctionMapperImpl();

    private PortletConfig portletConfig;
    private boolean facesResolved = false;

    public ELContextImpl(ELResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * @return the functionMapper
     */
    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }

    /**
     * @param functionMapper the functionMapper to set
     */
    public void setFunctionMapper(FunctionMapper functionMapper) {
        this.functionMapper = functionMapper;
    }

    /**
     * @return the variableMapper
     */
    public VariableMapper getVariableMapper() {
        return this.variableMapper;
    }

    /**
     * @param variableMapper the variableMapper to set
     */
    public void setVariableMapper(VariableMapper variableMapper) {
        this.variableMapper = variableMapper;
    }

    public ELResolver getELResolver() {
        return this.resolver;
    }

    public PortletConfig getPortletConfig() {
        return portletConfig;
    }

    public void setPortletConfig(PortletConfig config) {
        portletConfig = config;
    }

    public boolean isFacesResolved() {
        return facesResolved;
    }

    public void setFacesResolved(boolean facesResolved) {
        this.facesResolved = facesResolved;
    }
}
