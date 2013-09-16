package org.jboss.portletbridge.it.component.f.viewAction;

import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class ResultPage {
    @FindByJQuery("[id$='id']")
    private WebElement itemId;

    @FindByJQuery("[id$='name']")
    private WebElement itemName;

    @FindByJQuery("[id$='update']")
    private WebElement updateNameLink;

    public WebElement getItemId() {
        return itemId;
    }

    public WebElement getItemName() {
        return itemName;
    }

    public WebElement getUpdateNameLink() {
        return updateNameLink;
    }
}
