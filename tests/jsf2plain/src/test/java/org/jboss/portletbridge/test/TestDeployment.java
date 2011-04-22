package org.jboss.portletbridge.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.hamcrest.TypeSafeMatcher;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class TestDeployment {

	public static WebArchive createDeployment()

	{

		return PortletArchive.create().addClass(Bean.class)
				.addAsWebInfResource("WEB-INF/web.xml", "web.xml")
				.addAsWebInfResource("WEB-INF/faces-config.xml")
				.addAsWebInfResource("WEB-INF/portlet.xml", "portlet.xml");
	}

	public static WebClient createWebClient() {
		WebClient client = new WebClient();
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		return client;
	}

	public static Matcher<HtmlElement> htmlAttributeMatcher(
			final String string, final Matcher<String> matcher) {
		return new TypeSafeMatcher<HtmlElement>(HtmlElement.class) {

			public void describeTo(Description description) {
				description.appendText("Html element attribute '"+string+"' containing ").appendDescriptionOf(matcher);
			}

			@Override
			public boolean matchesSafely(HtmlElement item) {
				String attribute = item.getAttribute(string);
				return matcher.matches(attribute);
			}

		};
	}

}
