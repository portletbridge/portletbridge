/**
 * 
 */
package org.jboss.portletbridge.config;

import java.util.ArrayList;
import java.util.List;

class ServletBean {
	private String name;
	private String className;
	private final List<String> mappings = new ArrayList<String>();
	
	public ServletBean() {
	}
	/**
	 * @param name
	 * @param className
	 */
	public ServletBean(String name, String className) {
		super();
		this.name = name;
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<String> getMappings() {
		return mappings;
	}
}