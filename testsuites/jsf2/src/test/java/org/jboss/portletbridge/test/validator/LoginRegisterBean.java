package org.jboss.portletbridge.test.validator;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean(name = "loginRegisterBean")
@SessionScoped
public class LoginRegisterBean implements Serializable {
 
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