package org.jboss.portletbridge.test.component.h.link;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "linkBean")
@SessionScoped
public class LinkBean {

    public static final String LINK_ONE = "exit";
    public static final String LINK_ONE_TEXT = "Exit";

    public static boolean LINK_RENDER = true;

    private String linkOne = LINK_ONE;
    private String linkOneText = LINK_ONE_TEXT;

    public void setLinkOne(String linkOne) {
        this.linkOne = linkOne;
    }

    public String getLinkOne() {
        return linkOne;
    }

    public int getLinkOneLength() {
        return linkOne.length();
    }

    public String getLinkOneText() {
        return linkOneText;
    }

    public void setLinkOneText(String linkOneText) {
        this.linkOneText = linkOneText;
    }

    public boolean getLinkRender() {
        return LINK_RENDER;
    }

}
