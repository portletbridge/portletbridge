package org.jboss.portletbridge.it.validator;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;

@ManagedBean(name = "loginRegisterBean")
@SessionScoped
public class LoginRegisterBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email = "test";

    public void emailChanged(AjaxBehaviorEvent event) {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}