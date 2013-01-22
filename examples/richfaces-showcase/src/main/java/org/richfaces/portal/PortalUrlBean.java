/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.richfaces.portal;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author pmensik
 *
 * This bean is designed for generating dynamic link to richfaces components which will be
 * clickable by selenium tests in Portal environment.
 */

@ManagedBean
@RequestScoped
public class PortalUrlBean {

    private static final String AMP = "&amp;";

    private String demo;
    private String sample;
    
    @ManagedProperty("#{skinBean.skin}")
    private String skin;

    public void redirect() throws IOException {
        ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
        String url = ex.getRequestContextPath() + "/richfaces/component-sample.jsf";
        String params = "javax.portlet.faces.ViewLink=true" + AMP + "demo=" + demo + AMP + "skin=" + skin;
        if(sample != null) {
            params += AMP +"sample=" + sample;
        }
        ex.redirect(url + "?" + params);
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
