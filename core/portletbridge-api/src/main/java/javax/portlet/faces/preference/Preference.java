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
package javax.portlet.faces.preference;

import java.util.List;

import javax.portlet.ReadOnlyException;

/**
 * The <CODE>Preference</CODE> interface allows one to access each <CODE>PortletPreferences</CODE> as a discrete object. This
 * allows one to more easily access a preference via EL. Operations made on a <CODE>Preference</CODE> object are immediately
 * reflected in the underlying <CODE>PortletPreferences</CODE>. As usual, changes aren't committed until
 * <CODE>PortletPreferences.store</CODE> is called.
 */
public interface Preference {

    /**
     * Sets the name of this preference.
     * 
     * @param name the new name for this preference.
     */
    public void setName(String name);

    /**
     * Returns the name of this preference.
     * 
     * @return the name of this preference.
     */
    public String getName();

    /**
     * Associates the specified String value with this preference.
     * <p>
     * <code>null</code> values for the value parameter are allowed.
     * 
     * @param value value to be associated with the specified key.
     * 
     * @exception ReadOnlyException if this preference cannot be modified for this request
     * 
     * @see #setValues(String[])
     */
    public void setValue(String value) throws ReadOnlyException;

    /**
     * Returns the first String value associated with this preference. If there is one or more values associated with this
     * preference it returns the first associated value. If there are no values associated with this preference, or the backing
     * preference database is unavailable, it returns null.
     * 
     * @return the first value associated with this preference, or <code>null</code> if there isn't an associated value or the
     *         backing store is inaccessible.
     * 
     * 
     * @see #getValues()
     */
    public String getValue();

    /**
     * Associates the specified String array value with this preference.
     * <p>
     * <code>null</code> values in the values parameter are allowed.
     * 
     * @param values values to be associated with key
     * 
     * @exception ReadOnlyException if this preference cannot be modified for this request
     * 
     * @see #setValue(String)
     */
    public void setValues(String[] values) throws ReadOnlyException;

    /**
     * Returns a <code>List</code> of values associated with this preference.
     * 
     * <p>
     * Returns the <CODE>null</CODE> if there aren't any values, or if the backing store is inaccessible.
     * 
     * <p>
     * If the implementation supports <i>stored defaults</i> and such a default exists and is accessible, they are returned in a
     * situation where null otherwise would have been returned.
     * 
     * 
     * 
     * @return the List associated with this preference, or <code>null</code> if the associated value does not exist.
     * 
     * @see #getValue()
     */
    public List<String> getValues();

    /**
     * Returns true, if the value of this preference cannot be modified by the user.
     * <p>
     * Modifiable preferences can be changed by the portlet in any standard portlet mode (<code>EDIT, HELP, VIEW</code>). Per
     * default every preference is modifiable.
     * <p>
     * Read-only preferences cannot be changed by the portlet in any standard portlet mode, but inside of custom modes it may be
     * allowed changing them. Preferences are read-only, if they are defined in the deployment descriptor with
     * <code>read-only</code> set to <code>true</code>, or if the portlet container restricts write access.
     * 
     * @return false, if the value of this preference can be changed
     * 
     */
    public boolean isReadOnly();

    /**
     * Resets or removes the value(s) of this preference.
     * <p>
     * If this implementation supports stored defaults, and there is such a default for the specified preference, the preference
     * will be reset to the stored default.
     * <p>
     * If there is no default available the preference will be removed from the underyling system.
     * 
     * @exception ReadOnlyException if this preference cannot be modified for this request
     */
    public void reset() throws ReadOnlyException;

}
