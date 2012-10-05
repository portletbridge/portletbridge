/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.portletbridge.test.validator;

import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
public class ValidatorEmailTest {

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addClass(LoginRegisterBean.class)
                .addAsWebResource("pages/validator/main.xhtml", "home.xhtml");        
    }

    protected static final By LABEL = By.xpath("//label[contains(@id,'outlabel')]");
    protected static final By INPUT_FIELD = By.xpath("//input[contains(@id,'userEmail')]");
    protected static final By OUTPUT_FIELD = By.xpath("//span[contains(@id,'validator')]");
    protected static final By SUBMIT_BUTTON = By.xpath("//input[@type='submit']");
    
    protected static final String INPUT1 = "userEmail";
    protected static final String INPUT2 = "user@Email";
    protected static final String INPUT3 = "user@Email.";
    protected static final String INPUT4 = "user@Email.sk";
    
    protected static final String outInvalid = "Invalid";
    protected static final String outLabelValue = "userEmail";

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testValidator() throws Exception {
        driver.get(portalURL.toString());
                
        assertNotNull("Check that page contains output element!", driver.findElement(LABEL));
        assertTrue("Portlet should return: " + outLabelValue, ExpectedConditions.textToBePresentInElement(LABEL, outLabelValue).apply(driver));
        
        driver.findElement(INPUT_FIELD).clear();        
        driver.findElement(INPUT_FIELD).sendKeys(INPUT1);
        driver.findElement(SUBMIT_BUTTON).click();
        assertNotNull("Check that page after 1st submit contains output element", driver.findElement(OUTPUT_FIELD));
        assertTrue("Portlet should 1st return: " + outInvalid, ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, outInvalid).apply(driver));
        
        driver.findElement(INPUT_FIELD).clear();
        driver.findElement(INPUT_FIELD).sendKeys(INPUT2);
        driver.findElement(SUBMIT_BUTTON).click();
        assertNotNull("Check that page after 2nd submit contains output element", driver.findElement(OUTPUT_FIELD));
        assertTrue("Portlet should 2nd return: " + outInvalid, ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, outInvalid).apply(driver));
        
        driver.findElement(INPUT_FIELD).clear();
        driver.findElement(INPUT_FIELD).sendKeys(INPUT3);
        driver.findElement(SUBMIT_BUTTON).click();
        assertNotNull("Check that page after 3rd submit contains output element", driver.findElement(OUTPUT_FIELD));
        assertTrue("Portlet should 3rd return: " + outInvalid, ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, outInvalid).apply(driver));
        
        driver.findElement(INPUT_FIELD).clear();
        driver.findElement(INPUT_FIELD).sendKeys(INPUT4);
        driver.findElement(SUBMIT_BUTTON).click();
        assertNotNull("Check that page after 4th submit contains output element", driver.findElement(OUTPUT_FIELD));
        assertTrue("Portlet should 4th return empty string", ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, "").apply(driver));
        
    }


}
