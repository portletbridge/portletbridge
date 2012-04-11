package org.jboss.portletbridge.bridge.scope;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * BridgeRequestScope is merely the holder of the bridge's request scope state. As such its a <code>Map</code>. Its main
 * abstraction is to provide an id which a scope manager can use to identify this scope and as the gatekeeper of specific
 * attributes that are excluded from the scope.<br>
 * 
 * 
 * The BridgeRequestScope's main function is to prevent excluded attributes from being added to itself. The excluded attributes
 * are comprised of those defined by the specification and those set by its client (typically its manager).
 */

public interface BridgeRequestScope extends ConcurrentMap<String, Object> {

    /**
     * 
     * @return unique string id representing this scope.
     */
    public String getId();

    /**
     * 
     * @return unique string name representing the portlet to which this scope pertains.
     */
    public String getPortletName();

    /**
     * 
     * @return unique string id representing the session to which this scope pertains.
     */
    public String getSessionId();

    /**
     * 
     * @return unique string id representing the Faces viewId to which this scope pertains.
     */
    public String getViewId();

    /**
     * 
     * @return unique string id representing the Portlet mode to which this scope pertains.
     */
    public String getPortletMode();

    /**
     * Sets a new List of excluded attribute names. The names follow the syntax described in the specification. Any currently
     * set attribute names are lost.
     * 
     * @param excludedNames
     */
    public void setExcludedEntries(List<String> excludedNames);

    /**
     * Adds a new List of excluded attribute names to the existing set. Duplicates are ignored. The names follow the syntax
     * described in the specification.
     * 
     * @param excludedNames
     */
    public void addExcludedEntries(List<String> excludedNames);

    /**
     * 
     * @param excludedNames returns the current list of excluded attribute names
     */
    public List<String> getExcludedEntries(List<String> excludedNames);

    /**
     * Allows the caller to (pre-determine) if a given attribute name/value pair will be excluded or not.
     * 
     * @param key name of the attribute
     * @param value of the attribute
     * @return true if the attribute will be excluded, false otherwise.
     */
    public boolean isExcluded(String key, Object value);

}