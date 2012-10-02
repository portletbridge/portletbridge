package org.jboss.portletbridge.test.component.h.outputLink;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "outputLinkBean")
@SessionScoped
public class OutputLinkBean {

	public static final String OUTPUT_LINK_ONE = "exit.xhtml";
	public static final String OUTPUT_LINK_ONE_TEXT = "Exit";

	public static boolean OUTPUT_LINK_RENDER = true;

	private String linkOne = OUTPUT_LINK_ONE;
	private String linkOneText = OUTPUT_LINK_ONE_TEXT;

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

	public boolean getOutputLinkRender() {
		return OUTPUT_LINK_RENDER;
	}

}
