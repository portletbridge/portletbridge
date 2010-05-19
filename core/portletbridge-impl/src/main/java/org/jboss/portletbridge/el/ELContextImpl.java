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
package org.jboss.portletbridge.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author asmirnov
 * 
 */
public class ELContextImpl extends ELContext {
    /**
         * @author asmirnov
         * 
         */
    public static final class FunctionMapperImpl extends FunctionMapper {
   /*
         * (non-Javadoc)
         * 
         * @see javax.el.FunctionMapper#resolveFunction(java.lang.String,
         *      java.lang.String)
         */
   public Method resolveFunction(String arg0, String arg1) {
       return null;
   }
    }

    /**
         * @author asmirnov
         * 
         */
    public static final class VariableMapperImpl extends VariableMapper {
   private Map variables = new HashMap();

   /*
         * (non-Javadoc)
         * 
         * @see javax.el.VariableMapper#resolveVariable(java.lang.String)
         */
   public ValueExpression resolveVariable(String name) {
       return (ValueExpression) this.variables.get(name);
   }

   /*
         * (non-Javadoc)
         * 
         * @see javax.el.VariableMapper#setVariable(java.lang.String,
         *      javax.el.ValueExpression)
         */
   public ValueExpression setVariable(String name, ValueExpression variable) {
       return (ValueExpression) this.variables.put(name, variable);
   }
    }

    private ELResolver resolver;

    private VariableMapper variableMapper = new VariableMapperImpl();

    private FunctionMapper functionMapper = new FunctionMapperImpl();

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
         * @param functionMapper
         *                the functionMapper to set
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
         * @param variableMapper
         *                the variableMapper to set
         */
    public void setVariableMapper(VariableMapper variableMapper) {
   this.variableMapper = variableMapper;
    }

    public ELResolver getELResolver() {
   return this.resolver;
    }
}
