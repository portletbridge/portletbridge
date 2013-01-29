package org.jboss.portletbridge.test.component.h.outputLabel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "outputBean")
@SessionScoped
public class OutputLabelBean {

    public static final String OUTPUT_LABEL_DEFAULT_HTML = "Hello, <i>Portlet</i> World!";
    public static final String OUTPUT_LABEL_DEFAULT_PLAINTEXT = "Hello, Portlet World!";

    private String textOne = OUTPUT_LABEL_DEFAULT_HTML;

    public void setTextOne(String textOne) {
        this.textOne = textOne;
    }

    public String getTextOne() {
        return textOne;
    }

    public int getTextOneLength() {
        return textOne.length();
    }

}
