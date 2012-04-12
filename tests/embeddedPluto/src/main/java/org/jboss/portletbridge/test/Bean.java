package org.jboss.portletbridge.test;

/**
 * License Agreement.
 *
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

/**
 * @author $Autor$
 * 
 */

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