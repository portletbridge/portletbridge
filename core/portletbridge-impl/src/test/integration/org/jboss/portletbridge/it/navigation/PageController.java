/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.portletbridge.it.navigation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author vrockai
 */
@ManagedBean
@SessionScoped
public class PageController implements Serializable {

    private static final long serialVersionUID = 1L;

    public String moveToPage2() {
        return "target";
    }

    public String processPage1() {
        return "success";
    }

    public String processPage2() {
        return "success";
    }
}
