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
package org.jboss.portletbridge.test.ajax;

import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
public class BasicTest {

    public static final String NEW_VALUE = "New Value";

    @Deployment()
    public static WebArchive createDeployment() {
        return TestDeployment.createDeploymentWithAll()
                .addClass(StringToolBean.class)
                .addAsWebResource("pages/ajax/main.xhtml", "home.xhtml");
    }
    
    protected static final By OUTPUT = By.xpath("//span[contains(@id,'out1')]");
    protected static final By INPUT = By.xpath("//input[contains(@id,'in1')]");
    protected static final By SUBMIT = By.xpath("//input[contains(@id,'button1')]");
    protected static final By RELOAD = By.xpath("//input[contains(@id,'reload')]");
    protected static final By RESET = By.xpath("//input[contains(@id,'reset')]");
    
    protected static final String INPUT1 = "st4ing";
    protected static final String INPUT2 = "str1ng";
    protected static final String INPUT3 = "5tring";
    protected static final String INPUT4 = "5t41n6";
    
    @ArquillianResource
    @PortalURL
    URL portalURL;
    
    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testBasic() throws Exception {
        driver.get(portalURL.toString());

        assertNotNull("Check that page contains output element.", driver.findElement(OUTPUT));
        assertNotNull("Check that page contains input element.", driver.findElement(INPUT));
        assertNotNull("Check that page contains submit element.", driver.findElement(SUBMIT));
        assertNotNull("Check that page contains reload element.", driver.findElement(RELOAD));
        assertNotNull("Check that page contains reset element.", driver.findElement(RESET));

        assertTrue("Portlet should return: " + StringToolBean.INITIAL, ExpectedConditions.textToBePresentInElement(OUTPUT, StringToolBean.INITIAL).apply(driver));

        driver.findElement(INPUT).sendKeys(INPUT1);
        driver.findElement(SUBMIT).click();

        assertTrue("Portlet should return: " + StringToolBean.INITIAL, ExpectedConditions.textToBePresentInElement(OUTPUT, StringToolBean.INITIAL).apply(driver));

        driver.findElement(INPUT).sendKeys(INPUT2);
        driver.findElement(RELOAD).click();

        assertTrue("Portlet should return: " + INPUT2, ExpectedConditions.textToBePresentInElement(OUTPUT, INPUT2).apply(driver));

        driver.findElement(INPUT).sendKeys(INPUT1);
        driver.findElement(RESET).click();

        assertFalse("Portlet should not return: " + INPUT1, ExpectedConditions.textToBePresentInElement(OUTPUT, INPUT1).apply(driver));
        assertFalse("Portlet should not return: " + INPUT2, ExpectedConditions.textToBePresentInElement(OUTPUT, INPUT2).apply(driver));
        assertTrue("Portlet should return: " + "", ExpectedConditions.textToBePresentInElement(OUTPUT, "").apply(driver));


        driver.findElement(INPUT).sendKeys(INPUT3);
        driver.findElement(RELOAD).click();

        assertTrue("Portlet should return: " + INPUT3, ExpectedConditions.textToBePresentInElement(OUTPUT, INPUT3).apply(driver));

        driver.findElement(RELOAD).click();

        assertTrue("Portlet should return: " + INPUT3, ExpectedConditions.textToBePresentInElement(OUTPUT, INPUT3).apply(driver));
    }
}
