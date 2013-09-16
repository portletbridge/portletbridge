package org.jboss.portletbridge.it.component.f.viewAction;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean(name = "item")
@RequestScoped
public class DisplayItemBean {
    @ManagedProperty("#{list}")
    private ListBean list;
    private Long id;
    private Item item;

    public void loadItem() {
        item = list.getItem(id);
    }

    public String updateName(String newName) {
        item.setName(newName);
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

}
