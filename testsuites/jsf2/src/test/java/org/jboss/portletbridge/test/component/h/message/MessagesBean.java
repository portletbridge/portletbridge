package org.jboss.portletbridge.test.component.h.message;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

@ManagedBean(name = "msgsBean")
@SessionScoped
public class MessagesBean {

    private String textOne = "";
    private String textTwo = "";

    // Message One Control (Defaults)
    public static boolean RENDER_ONE = true;
    public static boolean SHOW_DETAIL_ONE = true;
    public static boolean SHOW_SUMMARY_ONE = false;

    // Messages Control (Defaults)
    public static boolean RENDER_MESSAGES = true;
    public static boolean SHOW_DETAIL_MESSAGES = false;
    public static boolean SHOW_SUMMARY_MESSAGES = true;
    public static boolean GLOBAL_ONLY_MESSAGES = false;

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

    public static final String HIDE = "HIDE ME";

    public static final String NAMESPACE = FacesContext.getCurrentInstance().getExternalContext().encodeNamespace("");

    public String getTextOne() {
        System.out.println("getTextOne() = '" + textOne + "'");
        return textOne;
    }

    public void setTextOne(String textOne) {
        System.out.println("setTextOne('" + textOne + "')");
        this.textOne = textOne;
    }

    public String getTextTwo() {
        System.out.println("getTextTwo() = '" + textTwo + "'");
        return textTwo;
    }

    public void setTextTwo(String textTwo) {
        System.out.println("setTextTwo('" + textTwo + "')");
        this.textTwo = textTwo;
    }

    // Message Control Getters
    public boolean getRenderOne() {
        System.out.println("getRenderOne() = " + RENDER_ONE);
        return RENDER_ONE;
    }

    public boolean getShowDetailOne() {
        System.out.println("getShowDetailOne() = " + SHOW_DETAIL_ONE);
        return SHOW_DETAIL_ONE;
    }

    public boolean getShowSummaryOne() {
        System.out.println("getShowSummaryOne() = " + SHOW_SUMMARY_ONE);
        return SHOW_SUMMARY_ONE;
    }

    // Messages Control Getters
    public boolean getRenderMessages() {
        System.out.println("getRenderedMessages() = " + RENDER_MESSAGES);
        return RENDER_MESSAGES;
    }

    public boolean getShowDetailMessages() {
        System.out.println("getShowDetailMessages() = " + SHOW_DETAIL_MESSAGES);
        return SHOW_DETAIL_MESSAGES;
    }

    public boolean getShowSummaryMessages() {
        System.out.println("getShowSummaryMessages() = " + SHOW_SUMMARY_MESSAGES);
        return SHOW_SUMMARY_MESSAGES;
    }

    public boolean getGlobalOnlyMessages() {
        System.out.println("getGlobalOnlyMessages() = " + GLOBAL_ONLY_MESSAGES);
        return GLOBAL_ONLY_MESSAGES;
    }

    public void validateOne(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        System.out.println("validateOne(..., ..., '" + newValue + "')");
        if (TWO.equals(newValue)) {
            FacesMessage msg = new FacesMessage(ONE_ERROR_SUMMARY, ONE_ERROR_DETAIL);
            throw new ValidatorException(msg);
        }
        else if (ONE.equals(newValue)) {
            FacesMessage msg = new FacesMessage(ONE_OK_SUMMARY, ONE_OK_DETAIL);
            context.addMessage(input.getClientId(context), msg);
        }
        if (GLOBAL.equals(newValue)) {
            FacesMessage msg = new FacesMessage(GLOBAL_SUMMARY, GLOBAL_DETAIL);
            context.addMessage(null, msg);
        }
    }

    public void validateTwo(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        System.out.println("validateTwo(..., ..., '" + newValue + "')");
        if (ONE.equals(newValue)) {
            FacesMessage msg = new FacesMessage(TWO_ERROR_SUMMARY, TWO_ERROR_DETAIL);
            throw new ValidatorException(msg);
        }
        else if (TWO.equals(newValue)) {
            FacesMessage msg = new FacesMessage(TWO_OK_SUMMARY, TWO_OK_DETAIL);
            context.addMessage(input.getClientId(context), msg);
        }
        if (GLOBAL.equals(newValue)) {
            FacesMessage msg = new FacesMessage(GLOBAL_SUMMARY, GLOBAL_DETAIL);
            context.addMessage(null, msg);
        }
    }

    public static void setDefaults() {
        RENDER_ONE = true;
        SHOW_DETAIL_ONE = true;
        SHOW_SUMMARY_ONE = false;

        RENDER_MESSAGES = true;
        SHOW_DETAIL_MESSAGES = false;
        SHOW_SUMMARY_MESSAGES = true;
        GLOBAL_ONLY_MESSAGES = false;
    }

}
