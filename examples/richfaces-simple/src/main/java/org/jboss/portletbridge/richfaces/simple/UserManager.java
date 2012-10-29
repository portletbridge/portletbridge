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
package org.jboss.portletbridge.richfaces.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean
@ApplicationScoped
public class UserManager implements Serializable {

    private static final long serialVersionUID = 5172630160799032072L;

    private transient List<SelectItem> userTypeOptions = null;

    private Set<User> users = new HashSet<User>();

    private boolean usersLoaded = false;

    @PostConstruct
    public void init() {
        userTypeOptions = new ArrayList<SelectItem>();
        for (UserType userType : UserType.values()) {
            userTypeOptions.add(new SelectItem(userType, userType.name()));
        }

        if (!usersLoaded) {
            users.add(new User("Gary", "Ablett", "gary.ablett@gmail.com", UserType.USER));
            users.add(new User("Mickey", "Mouse", "mickey.mouse@gmail.com", UserType.USER));
            users.add(new User("Geoff", "Mornay", "geoff.mornay@gmail.com", UserType.USER));
            users.add(new User("Peter", "Pan", "peter.pan@gmail.com", UserType.ADMIN));
            usersLoaded = true;
        }
    }

    public List<User> getUsers() {
        return new ArrayList<User>(users);
    }

    public boolean addUser(User user) {
        boolean result = users.add(user);
        if (result) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User saved", "You were succesful in saving the new User"));
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "User already saved",
                            "You attempted to save a User that had previously been saved"));
        }
        return result;
    }

    public List<SelectItem> getUserTypeOptions() {
        if (null == userTypeOptions) {
            init();
        }
        return userTypeOptions;
    }
}
