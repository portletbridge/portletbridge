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
package org.jboss.portletbridge.it.h.outputLink;

import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class OutputLinkPage {

    @FindBy(jquery = "[id$=':link0']")
    private WebElement linkZero;

    @FindBy(jquery = "[id$=':link1']")
    private WebElement linkOne;

    @FindBy(jquery = "[id$=':link2']")
    private WebElement linkTwo;

    @FindBy(jquery = "[id$=':link3']")
    private WebElement linkThree;

    @FindBy(jquery = "[id$=':link3img']")
    private WebElement linkThreeImage;

    @FindBy(jquery = "[id$=':viewLink1']")
    private WebElement viewLinkOne;

    @FindBy(jquery = "[id$=':viewLink2']")
    private WebElement viewLinkTwo;

    public WebElement getLinkZero() {
        return linkZero;
    }

    public WebElement getLinkOne() {
        return linkOne;
    }

    public WebElement getLinkThree() {
        return linkThree;
    }

    public WebElement getLinkTwo() {
        return linkTwo;
    }

    public WebElement getLinkThreeImage() {
        return linkThreeImage;
    }

    public WebElement getViewLinkOne() {
        return viewLinkOne;
    }

    public WebElement getViewLinkTwo() {
        return viewLinkTwo;
    }
}
