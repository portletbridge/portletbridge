package org.jboss.portletbridge.test.component.h.outputText;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "outputBean")
@SessionScoped
public class OutputTextBean {

    public static final String OUTPUT_TEXT_DEFAULT_HTML = "Hello, <i>Portlet</i> World!";
    public static final String OUTPUT_TEXT_DEFAULT_PLAINTEXT = "Hello, Portlet World!";

    public static boolean OUTPUT_TEXT_ESCAPE = true;
    public static boolean OUTPUT_TEXT_RENDER = true;

    private String textOne = OUTPUT_TEXT_DEFAULT_HTML;

    public void setTextOne(String textOne) {
        this.textOne = textOne;
    }

    public String getTextOne() {
        return textOne;
    }

    public int getTextOneLength() {
        return textOne.length();
    }

    public boolean getOutputTextEscape() {
        return OUTPUT_TEXT_ESCAPE;
    }

    public boolean getOutputTextRender() {
        return OUTPUT_TEXT_RENDER;
    }
}
