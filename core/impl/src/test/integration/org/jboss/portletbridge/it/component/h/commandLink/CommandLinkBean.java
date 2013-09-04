package org.jboss.portletbridge.it.component.h.commandLink;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;

@ManagedBean(name = "commandLinkBean")
@SessionScoped
public class CommandLinkBean {

    public static final String SUBMIT_LABEL = "Increment";

    private Integer counter = 0;
    private Integer step = 1;

    public Integer getCounter() {
        return counter;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public void incrementCounter() {
        counter += step;
    }

    public String getSubmitLabel() {
        return SUBMIT_LABEL;
    }

    public void setCounterTo() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ext_context = context.getExternalContext();
        ActionRequest req = (ActionRequest) ext_context.getRequest();

        this.counter = Integer.valueOf(req.getParameter("nc"));
    }
}
