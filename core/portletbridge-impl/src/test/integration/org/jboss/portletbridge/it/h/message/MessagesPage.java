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
package org.jboss.portletbridge.it.h.message;

import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class MessagesPage {

    @FindBy(jquery = "[id$=':messages']")
    private WebElement messages;

    @FindBy(jquery = "[id$=':message_one']")
    private WebElement messageOne;

    @FindBy(jquery = "[id$=':message_two']")
    private WebElement messageTwo;

    @FindBy(jquery = "[id$=':input_one']")
    private WebElement inputOne;

    @FindBy(jquery = "[id$=':input_two']")
    private WebElement inputTwo;

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitButton;

    @FindBy(jquery = "[id$=':messageOneRenderFalse']")
    private WebElement disableMessageOneRender;

    @FindBy(jquery = "[id$=':messageOneDetailFalse']")
    private WebElement disableMessageOneDetail;

    @FindBy(jquery = "[id$=':messageOneSummaryTrue']")
    private WebElement enableMessageOneSummary;

    @FindBy(jquery = "[id$=':messagesDisable']")
    private WebElement disableMessages;

    @FindBy(jquery = "[id$=':messagesEnableDetails']")
    private WebElement enableMessagesDetail;

    @FindBy(jquery = "[id$=':messagesDisableSummary']")
    private WebElement disableMessagesSummary;

    @FindBy(jquery = "[id$=':messagesEnableGlobal']")
    private WebElement enableMessagesGlobal;

    public WebElement getInputOne() {
        return inputOne;
    }

    public WebElement getInputTwo() {
        return inputTwo;
    }

    public WebElement getMessageOne() {
        return messageOne;
    }

    public WebElement getMessages() {
        return messages;
    }

    public WebElement getMessageTwo() {
        return messageTwo;
    }

    public WebElement getSubmitButton() {
        return submitButton;
    }

    public WebElement getDisableMessageOneRender() {
        return disableMessageOneRender;
    }

    public WebElement getDisableMessageOneDetail() {
        return disableMessageOneDetail;
    }

    public WebElement getEnableMessageOneSummary() {
        return enableMessageOneSummary;
    }

    public WebElement getDisableMessages() {
        return disableMessages;
    }

    public WebElement getEnableMessagesDetail() {
        return enableMessagesDetail;
    }

    public WebElement getDisableMessagesSummary() {
        return disableMessagesSummary;
    }

    public WebElement getEnableMessagesGlobal() {
        return enableMessagesGlobal;
    }
}
