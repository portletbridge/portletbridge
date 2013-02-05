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
package org.jboss.portletbridge.test.component.h.commandLink;

import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class CommandLinkPage {

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitLink;

    @FindBy(jquery = "[id$=':reset_counter']")
    private WebElement resetCounterLink;

    @FindBy(jquery = "[id$=':ajax']")
    private WebElement ajaxLink;

    @FindBy(jquery = "[id$=':alert']")
    private WebElement alertLink;

    @FindBy(jquery = "[id$=':output']")
    private WebElement outputText;

    @FindBy(jquery = "[id$=':input']")
    private WebElement inputText;

    public WebElement getAjaxLink() {
        return ajaxLink;
    }

    public WebElement getAlertLink() {
        return alertLink;
    }

    public WebElement getInputText() {
        return inputText;
    }

    public WebElement getOutputText() {
        return outputText;
    }

    public WebElement getResetCounterLink() {
        return resetCounterLink;
    }

    public WebElement getSubmitLink() {
        return submitLink;
    }
}
