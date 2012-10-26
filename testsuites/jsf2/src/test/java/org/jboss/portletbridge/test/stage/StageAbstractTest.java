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
package org.jboss.portletbridge.test.stage;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.portletbridge.test.TestDeployment;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class StageAbstractTest {

    protected static String getWebXml(String value) {
        WebAppDescriptor webConfig = TestDeployment.createWebXmlDescriptor();
        webConfig.createContextParam()
                 .paramName("javax.faces.PROJECT_STAGE")
                 .paramValue(value);

        return webConfig.exportAsString();
    }

    protected static WebArchive createDeployment(String value) {
         WebArchive wa = TestDeployment.createDeployment()
                .addAsWebResource("pages/stage/main.xhtml", "home.xhtml")
                .addAsWebInfResource(new StringAsset(getWebXml(value)), "web.xml");
        TestDeployment.addFacesConfig(wa);
        TestDeployment.addPortletXml(wa);
        return wa;
    }

    @FindBy(xpath = "//span[contains(@id,'outStage')]")
    protected WebElement label;

    @Drone
    WebDriver driver;

}
