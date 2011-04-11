/**
 * 
 */
package javax.portlet.faces.preference;

import java.util.List;

import javax.portlet.ReadOnlyException;

/**
 * @author asmirnov
 *
 */
public interface Preference {
	
	public void setName(String name) ;
	
	public String getName();

	public void setValue(String value) throws ReadOnlyException;
	
	public String getValue();
	
	public void setValues(String[] values) throws ReadOnlyException;
	
	public List<String> getValues();
	
	public boolean isReadOnly();
	
	public void reset() throws ReadOnlyException;
	
}
