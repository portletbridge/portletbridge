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
package org.jboss.portletbridge.it.common;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import java.util.Date;

@ManagedBean
@RequestScoped
public class Bean {

    public static final String ON_CHANGE_CALLED = "#{bean.onChange} called";
    public static final String VALIDATE_CALLED = "#{bean.validate} called";
    public static final String VALIDATOR_ERROR_MESSAGE = "#{bean.validate} wrong value";
    public static final String WRONG = "Wrong";
    public static final String HELLO_JSF_PORTLET = "Hello,JSF Portlet";
    private java.lang.String text = HELLO_JSF_PORTLET;

    public java.lang.String getText() {
        return text;
    }

    public void setText(java.lang.String text) {
        this.text = text;
    }

    public String ok() {
        System.out.println("Button pressed");
        return text;
    }

    public void validate(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        System.out.println("validate");
        if (WRONG.equals(newValue)) {
            FacesMessage msg = new FacesMessage(VALIDATOR_ERROR_MESSAGE);
            throw new ValidatorException(msg);
        } else {
            FacesMessage msg = new FacesMessage(VALIDATE_CALLED);
            context.addMessage(input.getClientId(context), msg);
        }
    }

    public void onChange(ValueChangeEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent input = event.getComponent();
        FacesMessage msg = new FacesMessage(ON_CHANGE_CALLED);
        context.addMessage(input.getClientId(context), msg);
        System.out.println("onChange");

    }

    public String getTime() {
        return (new Date(System.currentTimeMillis())).toString();
    }

    Integer count = 0;

    public Integer getCount() {
        return count++;
    }

    public void reset(ActionEvent ae) {
        count = 0;
    }

}