/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.it.navigation.flows;

import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class FlowPage {

    @FindByJQuery("[id$=':title']")
    private WebElement title;

    @FindByJQuery("[id$=':flow']")
    private WebElement flowPresent;

    @FindByJQuery("[id$=':flowValue']")
    private WebElement flowValue;

    @FindByJQuery("[id$=':first']")
    private WebElement firstName;

    @FindByJQuery("[id$=':last']")
    private WebElement lastName;

    @FindByJQuery("[id$=':user']")
    private WebElement userName;

    @FindByJQuery("[id$=':value']")
    private WebElement value;

    @FindByJQuery("[id$=':next']")
    private WebElement nextButton;

    @FindByJQuery("[id$=':back']")
    private WebElement backButton;

    @FindByJQuery("[id$=':return']")
    private WebElement returnButton;

    @FindByJQuery("[id$=':home']")
    private WebElement homeButton;

    public WebElement getTitle() {
        return title;
    }

    public WebElement getBackButton() {
        return backButton;
    }

    public WebElement getFirstName() {
        return firstName;
    }

    public WebElement getFlowPresent() {
        return flowPresent;
    }

    public WebElement getFlowValue() {
        return flowValue;
    }

    public WebElement getHomeButton() {
        return homeButton;
    }

    public WebElement getLastName() {
        return lastName;
    }

    public WebElement getNextButton() {
        return nextButton;
    }

    public WebElement getReturnButton() {
        return returnButton;
    }

    public WebElement getUserName() {
        return userName;
    }

    public WebElement getValue() {
        return value;
    }
}
