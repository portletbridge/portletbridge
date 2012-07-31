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
package org.jboss.portletbridge.example.cdi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@Named
@ApplicationScoped
public class IssueManager implements Serializable {

    private static final long serialVersionUID = -6306424542612684236L;

    transient List<Issue> issues;

    transient int lastUsedId = 0;

    @PostConstruct
    public void loadIssues() {
        issues = new ArrayList<Issue>();

        issues.add(new Issue(++lastUsedId, "New Issue", "Here is a new issue that is super dooper important!", IssueStatus.NEW));
        issues.add(new Issue(++lastUsedId, "Open Issue", "This issue is now open and ready for fixing!", IssueStatus.OPEN));
        issues.add(new Issue(++lastUsedId, "Second Open Issue",
                "Oh dear, this issue has been around for a while.  Need to look at fixing it for the next release!",
                IssueStatus.OPEN));
        issues.add(new Issue(++lastUsedId, "Closed Issue", "This issue was closed.  Woohoo!", IssueStatus.CLOSED));
    }

    public List<Issue> getAll() {
        return issues;
    }

    public void createIssue(Issue issue) {
        issues.add(issue);
    }

    public void createIssue(String title, String description) {
        issues.add(new Issue(++lastUsedId, title, description, IssueStatus.NEW));
    }

    public int nextIssueId() {
        return ++lastUsedId;
    }

    public void deleteIssue(Issue issue) {
        boolean result = issues.remove(issue);
        if (!result) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Delete Failed",
                            "Issue has already been deleted from database"));
        }
    }
}
