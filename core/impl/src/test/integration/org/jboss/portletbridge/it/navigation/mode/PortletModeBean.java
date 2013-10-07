package org.jboss.portletbridge.it.navigation.mode;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean(name = "modeBean")
@RequestScoped
public class PortletModeBean {

    public void action() {
        //Do nothing
    }

    public String actionWithRedirect() {
        return "main?javax.portlet.faces.PortletMode=view";
    }
}
