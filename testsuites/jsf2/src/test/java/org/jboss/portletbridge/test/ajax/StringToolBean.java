package org.jboss.portletbridge.test.ajax;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "tool")
@SessionScoped
public class StringToolBean implements Serializable {

    public static final String INITIAL= "FINAL";
    private static final long serialVersionUID = 8301865434469950945L;

    String str = INITIAL;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
    
    public void append(ActionEvent ae) {
        str += "X";
    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public void reset(ActionEvent ae) {
        str = "";
    }

}
