/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.portletbridge.test.navigation;

/**
 *
 * @author vrockai
 */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class PaymentController implements Serializable {

    private static final long serialVersionUID = 1L;
    public boolean registerCompleted = true;
    public int orderQty = 99;

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public boolean isRegisterCompleted() {
        return registerCompleted;
    }

    public void setRegisterCompleted(boolean registerCompleted) {
        this.registerCompleted = registerCompleted;
    }
}