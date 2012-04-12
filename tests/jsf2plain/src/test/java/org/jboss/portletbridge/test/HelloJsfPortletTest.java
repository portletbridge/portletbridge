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
package org.jboss.portletbridge.test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class HelloJsfPortletTest extends PortalTestBase {

    @Deployment(testable = false)
    public static WebArchive createDeployment()

    {

        return TestDeployment.createDeployment().addAsWebResource("output.xhtml", "home.xhtml")
                .addAsWebResource("resources/stylesheet.css", "resources/stylesheet.css");
    }

    @Test
    public void renderFacesPortlet() throws Exception

    {

        HtmlPage body = getPortalPage();
        HtmlElement element = body.getElementById("output");
        assertNotNull("Check what page contains output element", element);
        Assert.assertThat(

        "Verify that the portlet was deployed and returns the expected result",

        element.asText(), containsString(Bean.HELLO_JSF_PORTLET));
        List<HtmlElement> links = body.getElementsByTagName("link");
        assertThat(links, contains(TestDeployment.htmlAttributeMatcher("href", containsString("stylesheet.css"))));
    }

}
