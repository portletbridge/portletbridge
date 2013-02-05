package org.jboss.portletbridge.test.component.h.inputText;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

@ManagedBean(name = "inputBean")
@SessionScoped
public class InputTextBean {

    public static final String REQUIRED_MESSAGE = "Please fill INPUT ONE field.";
    public static final String MIN_LENGTH_MESSAGE = "The INPUT ONE field has to have at least 3 chars.";

    private String textOne = "";
    private String textTwo = "";

    private int textOneCount = 0;
    private int textTwoCount = 0;

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

    // For valueChangeListener attribute

    public void onChange(ValueChangeEvent e) {
        textOneCount = textOne.length();
        textTwoCount = textTwo.length();
    }

    public void ajaxListener(AjaxBehaviorEvent event) {
        textTwoCount = textTwo.length();
    }

    public int getTextOneCount() {
        return textOneCount;
    }

    public int getTextTwoCount() {
        return textTwoCount;
    }

    // For required attribute

    public String getRequiredMessage() {
        return REQUIRED_MESSAGE;
    }

    public String getMinLengthMessage() {
        return MIN_LENGTH_MESSAGE;
    }
}
