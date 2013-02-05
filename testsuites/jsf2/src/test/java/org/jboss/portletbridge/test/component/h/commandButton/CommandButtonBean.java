package org.jboss.portletbridge.test.component.h.commandButton;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "commandButtonBean")
@SessionScoped
public class CommandButtonBean implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
