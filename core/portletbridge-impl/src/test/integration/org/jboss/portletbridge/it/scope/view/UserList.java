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
package org.jboss.portletbridge.it.scope.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@ManagedBean
@ViewScoped
public class UserList {

    private ArrayList<String> users;
    private String selectedUser;

    @PostConstruct
    public void create() {
        users = new ArrayList<String>();
        users.add("Gary");
        users.add("Matt");
        users.add("Simon");
        users.add("Donna");
        users.add("Lisa");
        users.add("Emma");
    }

    public void delete() {
        users.remove(selectedUser);
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String user) {
        this.selectedUser = user;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
