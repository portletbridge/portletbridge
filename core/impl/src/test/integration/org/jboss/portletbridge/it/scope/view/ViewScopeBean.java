package org.jboss.portletbridge.it.scope.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean(name = "viewBean")
@ViewScoped
public class ViewScopeBean implements Serializable {

    public static String VALUE = "Bean Value";
    public static String NEW_VALUE = "New Val";

    private String text;

    @PostConstruct
    public void init() {
        text = VALUE + " " + this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void changeText() {
        text = NEW_VALUE + " " + this;
    }
}
