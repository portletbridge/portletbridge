package org.jboss.portletbridge.it.component.f.viewParam;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean(name = "bean2")
@ViewScoped
public class ViewParamBean {

    private String param1;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }
}
