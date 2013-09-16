package org.jboss.portletbridge.it.component.f.viewAction;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean(name = "list")
@ApplicationScoped
public class ListBean implements Serializable {

    private Map<Long, Item> items = new HashMap<>();
    private List<Item> itemList;

    public ListBean() {
        // Build list
        Item item = new Item();
        item.setId(Long.parseLong("1"));
        item.setName("One");
        items.put(item.getId(), item);

        item = new Item();
        item.setId(Long.parseLong("2"));
        item.setName("Two");
        items.put(item.getId(), item);

        item = new Item();
        item.setId(Long.parseLong("3"));
        item.setName("Three");
        items.put(item.getId(), item);

        item = new Item();
        item.setId(Long.parseLong("4"));
        item.setName("Four");
        items.put(item.getId(), item);

        itemList = new ArrayList<>(items.values());
    }

    public List<Item> getItems() {
        return itemList;
    }

    public Item getItem(Long id) {
        return items.get(id);
    }
}
