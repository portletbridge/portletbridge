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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class TestDeployment {

    public static WebArchive createDeployment() {
        return ShrinkWrap
            .create(WebArchive.class)
            .addClass(Bean.class)
            .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
                    .artifacts("org.jboss.portletbridge:portletbridge-api").resolveAsFiles())
            .addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
                    .artifacts("org.jboss.portletbridge:portletbridge-impl").resolveAsFiles())
            .addAsWebInfResource("WEB-INF/web.xml", "web.xml").addAsWebInfResource("WEB-INF/faces-config.xml")
            .addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
    }

    public static WebClient createWebClient() {
        WebClient client = new WebClient();
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        return client;
    }

    public static Matcher<HtmlElement> htmlAttributeMatcher(final String string, final Matcher<String> matcher) {
        return new TypeSafeMatcher<HtmlElement>(HtmlElement.class) {

            public void describeTo(Description description) {
                description.appendText("Html element attribute '" + string + "' containing ").appendDescriptionOf(
                    matcher);
            }

            @Override
            public boolean matchesSafely(HtmlElement item) {
                String attribute = item.getAttribute(string);
                return matcher.matches(attribute);
            }

        };
    }

}
