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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//@RunWith(Arquillian.class)
public class A4jCommandLinkTest extends PortalTestBase {

    public static final String NEW_VALUE = "New Value";

    @Deployment(testable = false)
    public static WebArchive createDeployment()

    {

        return TestDeployment.createDeployment().addAsWebResource("output.xhtml", "output.xhtml")
                .addAsWebResource("a4jLink.xhtml", "home.xhtml");
    }

    // @Test
    public void renderFormPortlet() throws Exception

    {
        HtmlPage portalPage = getPortalPage();

        verifyOutput(portalPage, Bean.HELLO_JSF_PORTLET);

        verifyInput(portalPage, Bean.HELLO_JSF_PORTLET);

        HtmlElement submit = getFirstChildById(portalPage, "submit");

        assertThat(submit.asText(), containsString("Ok"));

        Iterable<HtmlElement> links = portalPage.getElementsByTagName("script");
        assertThat(links,
                Matchers.<HtmlElement> hasItem(TestDeployment.htmlAttributeMatcher("src", containsString("richfaces"))));
    }

    // @Test
    public void testSubmitAndRemainOnPage() throws Exception {
        HtmlPage portalPage = getPortalPage();
        HtmlPage responsePage = submitForm(portalPage, NEW_VALUE);
        assertSame(portalPage, responsePage);
        verifyInput(responsePage, NEW_VALUE);
        verifyOutput(responsePage, NEW_VALUE);
        // Re-render page
        HtmlPage reRenderPage = portalPage;
        verifyInput(reRenderPage, NEW_VALUE);
        verifyOutput(reRenderPage, NEW_VALUE);
    }

}
