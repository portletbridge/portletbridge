package org.jboss.portletbridge.it.component.f.viewAction;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class HomePage {
    @FindBy(linkText = "One")
    private WebElement basicViewActionLink;

    @FindBy(linkText = "Two")
    private WebElement postbackTrueViewActionLink;

    @FindBy(linkText = "Three")
    private WebElement updateModelPhaseViewActionLink;

    @FindBy(linkText = "Four")
    private WebElement immediateViewActionLink;

    public WebElement getBasicViewActionLink() {
        return basicViewActionLink;
    }

    public WebElement getImmediateViewActionLink() {
        return immediateViewActionLink;
    }

    public WebElement getPostbackTrueViewActionLink() {
        return postbackTrueViewActionLink;
    }

    public WebElement getUpdateModelPhaseViewActionLink() {
        return updateModelPhaseViewActionLink;
    }
}
