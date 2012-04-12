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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

@RunWith(Arquillian.class)
public class AjaxSubmitTest extends PortalTestBase {

    public static final String NEW_VALUE = "New Value";

    @Deployment(testable = false)
    public static WebArchive createDeployment()

    {

        return TestDeployment.createDeployment().addAsWebResource("ajax.xhtml", "home.xhtml");
    }

    @Test
    public void renderFormPortlet() throws Exception

    {
        HtmlPage portalPage = getPortalPage();

        verifyOutput(portalPage, Bean.HELLO_JSF_PORTLET);

        verifyInput(portalPage, Bean.HELLO_JSF_PORTLET);

        HtmlSubmitInput submit = getSubmit(portalPage);

        assertThat(submit.getOnClickAttribute(), not(equalTo("")));
    }

    // @Test
    public void testSubmitAndRemainOnPage() throws Exception {
        HtmlPage portalPage = getPortalPage();
        HtmlPage responsePage = submitForm(portalPage, NEW_VALUE);
        assertSame(portalPage, responsePage);
        verifyInput(responsePage, NEW_VALUE);
        verifyOutput(responsePage, NEW_VALUE);
        // Re-render page
        // Re-render page
        HtmlPage reRenderPage = getPortalPage();
        verifyInput(reRenderPage, NEW_VALUE);
        verifyOutput(reRenderPage, NEW_VALUE);
    }

}
