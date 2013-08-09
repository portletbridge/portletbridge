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
package org.jboss.portletbridge.it.h.commandButton;

import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class CommandButtonPage {

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitButton;

    @FindBy(jquery = "[id$=':reset']")
    private WebElement resetButton;

    @FindBy(jquery = "[id$=':ajax']")
    private WebElement ajaxButton;

    @FindBy(jquery = "[id$=':alert']")
    private WebElement alertButton;

    @FindBy(jquery = "[id$=':output']")
    private WebElement outputText;

    @FindBy(jquery = "[id$=':input']")
    private WebElement inputText;

    public WebElement getSubmitButton() {
        return submitButton;
    }

    public WebElement getResetButton() {
        return resetButton;
    }

    public WebElement getAjaxButton() {
        return ajaxButton;
    }

    public WebElement getAlertButton() {
        return alertButton;
    }

    public WebElement getOutputText() {
        return outputText;
    }

    public WebElement getInputText() {
        return inputText;
    }
}
