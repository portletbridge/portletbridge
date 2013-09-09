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
package org.jboss.portletbridge.it.component.h.selectCheckbox;

import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class SelectCheckboxPage {

    @FindByJQuery("[id$=':sbcAccepted']")
    private WebElement acceptCheckbox;

    @FindByJQuery("[id$=':smcGender']")
    private WebElement genderCheckbox;

    @FindByJQuery("[id$=':smcGender:0']")
    private WebElement genderOptionMale;

    @FindByJQuery("[id$=':smcGender:1']")
    private WebElement genderOptionFemale;

    @FindByJQuery("[id$=':sorAge:0']")
    private WebElement ageOptionYoung;

    @FindByJQuery("[id$=':sorAge:1']")
    private WebElement ageOptionAdult;

    @FindByJQuery("[id$=':sorAge:2']")
    private WebElement ageOptionSenior;

    @FindBy(xpath = "//option[contains(@value,'no_continent')]")
    private WebElement continentNoSel;

    @FindBy(xpath = "//option[contains(@value,'africa')]")
    private WebElement continentAfrica;

    @FindBy(xpath = "//option[contains(@value,'asia')]")
    private WebElement continentAsia;

    @FindBy(xpath = "//option[contains(@value,'europe')]")
    private WebElement continentEurope;

    @FindByJQuery("[id$=':smlColors']")
    private WebElement colorsCheckbox;

    @FindByJQuery("[id$=':submit']")
    private WebElement submitButton;

    @FindByJQuery("[id$=':ajax']")
    private WebElement ajaxButton;

    @FindByJQuery("[id$=':messages']")
    private WebElement messages;

    @FindByJQuery("[id$=':result']")
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
