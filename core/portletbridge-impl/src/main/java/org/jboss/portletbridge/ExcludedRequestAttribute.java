/**
 * 
 */
package org.jboss.portletbridge;

/**
 * @author asmirnov
 *
 */
public class ExcludedRequestAttribute {
	private final String name;
	private final boolean wildcard;
	/**
	 * @param name
	 */
	public ExcludedRequestAttribute(String name) {
		if(null == name){
			throw new NullPointerException("Excluded request attribute name is null");
		}
		if(name.endsWith("*")){
			wildcard = true;
			name = name.substring(0, name.length()-1);
		} else {
			wildcard = false;
		}
		this.name = name;
	}
	
	public boolean match(String attributeName) {
		if(wildcard){
			return attributeName.startsWith(name);
		} else {
			return attributeName.equals(name);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (wildcard ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExcludedRequestAttribute other = (ExcludedRequestAttribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (wildcard != other.wildcard)
			return false;
		return true;
	}
	
}
