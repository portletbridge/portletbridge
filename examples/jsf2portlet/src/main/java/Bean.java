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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

/**
 * @author $Autor$
 * 
 */
@ManagedBean(name = "bean")
@SessionScoped

public class Bean {

	private java.lang.String text;

	public java.lang.String getText() {
		return text;
	}

	public void setText(java.lang.String text) {
		this.text = text;
	}

	public String ok() {
		System.out.println("Button pressed");
		return null;
	}

	public void validate(FacesContext context, UIComponent input,
			Object newValue) {
		FacesMessage msg = new FacesMessage("#{bean.validate} called");
		context.addMessage(input.getClientId(context), msg);
		System.out.println("validate");
	}

	public void onChange(ValueChangeEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIComponent input = event.getComponent();
		FacesMessage msg = new FacesMessage("#{bean.onChange} called");
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