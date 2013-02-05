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
package org.jboss.portletbridge.test.component.h.selectCheckbox;

import org.jboss.arquillian.graphene.enricher.findby.FindBy;
import org.openqa.selenium.WebElement;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class SelectCheckboxPage {

    @FindBy(jquery = "[id$=':sbcAccepted']")
    private WebElement acceptCheckbox;

    @FindBy(jquery = "[id$=':smcGender']")
    private WebElement genderCheckbox;

    @FindBy(jquery = "[id$=':smcGender:0']")
    private WebElement genderOptionMale;

    @FindBy(jquery = "[id$=':smcGender:1']")
    private WebElement genderOptionFemale;

    @FindBy(jquery = "[id$=':sorAge:0']")
    private WebElement ageOptionYoung;

    @FindBy(jquery = "[id$=':sorAge:1']")
    private WebElement ageOptionAdult;

    @FindBy(jquery = "[id$=':sorAge:2']")
    private WebElement ageOptionSenior;

    @FindBy(xpath = "//option[contains(@value,'no_continent')]")
    private WebElement continentNoSel;

    @FindBy(xpath = "//option[contains(@value,'africa')]")
    private WebElement continentAfrica;

    @FindBy(xpath = "//option[contains(@value,'asia')]")
    private WebElement continentAsia;

    @FindBy(xpath = "//option[contains(@value,'europe')]")
    private WebElement continentEurope;

    @FindBy(jquery = "[id$=':smlColors']")
    private WebElement colorsCheckbox;

    @FindBy(jquery = "[id$=':submit']")
    private WebElement submitButton;

    @FindBy(jquery = "[id$=':ajax']")
    private WebElement ajaxButton;

    @FindBy(jquery = "[id$=':messages']")
    private WebElement messages;

    @FindBy(jquery = "[id$=':result']")
    private WebElement result;

    public WebElement getAcceptCheckbox() {
        return acceptCheckbox;
    }

    public WebElement getAgeOptionAdult() {
        return ageOptionAdult;
    }

    public WebElement getAgeOptionSenior() {
        return ageOptionSenior;
    }

    public WebElement getAgeOptionYoung() {
        return ageOptionYoung;
    }

    public WebElement getAjaxButton() {
        return ajaxButton;
    }

    public WebElement getColorsCheckbox() {
        return colorsCheckbox;
    }

    public WebElement getContinentAfrica() {
        return continentAfrica;
    }

    public WebElement getContinentAsia() {
        return continentAsia;
    }

    public WebElement getContinentEurope() {
        return continentEurope;
    }

    public WebElement getContinentNoSel() {
        return continentNoSel;
    }

    public WebElement getGenderCheckbox() {
        return genderCheckbox;
    }

    public WebElement getGenderOptionFemale() {
        return genderOptionFemale;
    }

    public WebElement getGenderOptionMale() {
        return genderOptionMale;
    }

    public WebElement getMessages() {
        return messages;
    }

    public WebElement getResult() {
        return result;
    }

    public WebElement getSubmitButton() {
        return submitButton;
    }
}
