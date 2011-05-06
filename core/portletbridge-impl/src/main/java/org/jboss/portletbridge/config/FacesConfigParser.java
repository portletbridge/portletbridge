/**
 * 
 */
package org.jboss.portletbridge.config;

import javax.portlet.PortletContext;

/**
 * @author asmirnov
 *
 */
public class FacesConfigParser {

	/**
	 * This method parses all possible faces-config.xml files, as it required by JSF 2 spec,
	 * and creates bean that describes portlet-related extensions.
	 * @param portletContext
	 * @return
	 */
	public static FacesConfigBean parseConfigs(PortletContext portletContext){
		FacesConfigBean config = new FacesConfigBean();
		return config;
	}
	
	
}
