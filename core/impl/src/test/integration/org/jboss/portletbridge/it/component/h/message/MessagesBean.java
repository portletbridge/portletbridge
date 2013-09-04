package org.jboss.portletbridge.it.component.h.message;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;

@ManagedBean(name = "msgsBean")
@SessionScoped
public class MessagesBean implements Serializable {

    private String textOne = "";
    private String textTwo = "";

    // Message One Control (Defaults)
    private boolean renderOne = true;
    private boolean showDetailOne = true;
    private boolean showSummaryOne = false;

    // Messages Control (Defaults)
    private boolean renderMessages = true;
    private boolean showDetailMessages = false;
    private boolean showSummaryMessages = true;
    private boolean globalOnlyMessages = false;

    public static final String ONE = "One";
    public static final String TWO = "Two";
    public static final String GLOBAL = "Global";

    public static final String ONE_ERROR_SUMMARY = "BAD ONE";
    public static final String ONE_ERROR_DETAIL = "One can be any value but " + TWO;
    public static final String TWO_ERROR_SUMMARY = "BAD TWO";
    public static final String TWO_ERROR_DETAIL = "Two can be any value but " + ONE;

    public static final String ONE_OK_SUMMARY = "GOOD ONE";
    public static final String ONE_OK_DETAIL = "One is One";
    public static final String TWO_OK_SUMMARY = "GOOD TWO";
    public static final String TWO_OK_DETAIL = "Two is Two";

    public static final String GLOBAL_SUMMARY = "Hello, Global World!";
    public static final String GLOBAL_DETAIL = "Global Detail";

    public String getTextOne() {
        return textOne;
    }

    public void setTextOne(String textOne) {
        this.textOne = textOne;
    }

    public String getTextTwo() {
        return textTwo;
    }

    public void setTextTwo(String textTwo) {
        this.textTwo = textTwo;
    }

    // Message Control Getters
    public boolean getRenderOne() {
        return renderOne;
    }

    public void setRenderOne(boolean renderOne) {
        this.renderOne = renderOne;
    }

    public boolean getShowDetailOne() {
        return showDetailOne;
    }

    public void setShowDetailOne(boolean showDetailOne) {
        this.showDetailOne = showDetailOne;
    }

    public boolean getShowSummaryOne() {
        return showSummaryOne;
    }

    public void setShowSummaryOne(boolean showSummaryOne) {
        this.showSummaryOne = showSummaryOne;
    }

    // Messages Control Getters
    public boolean getRenderMessages() {
        return renderMessages;
    }

    public void setRenderMessages(boolean renderMessages) {
        this.renderMessages = renderMessages;
    }

    public boolean getShowDetailMessages() {
        return showDetailMessages;
    }

    public void setShowDetailMessages(boolean showDetailMessages) {
        this.showDetailMessages = showDetailMessages;
    }

    public boolean getShowSummaryMessages() {
        return showSummaryMessages;
    }

    public void setShowSummaryMessages(boolean showSummaryMessages) {
        this.showSummaryMessages = showSummaryMessages;
    }

    public boolean getGlobalOnlyMessages() {
        return globalOnlyMessages;
    }

    public void setGlobalOnlyMessages(boolean globalOnlyMessages) {
        this.globalOnlyMessages = globalOnlyMessages;
    }

    public void validateOne(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        if (TWO.equals(newValue)) {
            FacesMessage msg = new FacesMessage(ONE_ERROR_SUMMARY, ONE_ERROR_DETAIL);
            throw new ValidatorException(msg);
        } else if (ONE.equals(newValue)) {
            FacesMessage msg = new FacesMessage(ONE_OK_SUMMARY, ONE_OK_DETAIL);
            context.addMessage(input.getClientId(context), msg);
        }
        if (GLOBAL.equals(newValue)) {
            FacesMessage msg = new FacesMessage(GLOBAL_SUMMARY, GLOBAL_DETAIL);
            context.addMessage(null, msg);
        }
    }

    public void validateTwo(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        if (ONE.equals(newValue)) {
            FacesMessage msg = new FacesMessage(TWO_ERROR_SUMMARY, TWO_ERROR_DETAIL);
            throw new ValidatorException(msg);
        } else if (TWO.equals(newValue)) {
            FacesMessage msg = new FacesMessage(TWO_OK_SUMMARY, TWO_OK_DETAIL);
            context.addMessage(input.getClientId(context), msg);
        }
        if (GLOBAL.equals(newValue)) {
            FacesMessage msg = new FacesMessage(GLOBAL_SUMMARY, GLOBAL_DETAIL);
            context.addMessage(null, msg);
        }
    }

}
