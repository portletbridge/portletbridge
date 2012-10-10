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
package org.jboss.portletbridge.richfaces.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class ApplicationFactoryImpl extends ApplicationFactory {

    private ApplicationFactory wrapped;

    public ApplicationFactoryImpl(ApplicationFactory factory) {
        wrapped = factory;
    }

    @Override
    public ApplicationFactory getWrapped() {
        return wrapped;
    }

    /**
     * @see javax.faces.application.ApplicationFactory#getApplication()
     */
    @Override
    public Application getApplication() {
        return new ApplicationImpl(wrapped.getApplication());
    }

    /**
     * @see javax.faces.application.ApplicationFactory#setApplication(javax.faces.application.Application)
     */
    @Override
    public void setApplication(Application application) {
        wrapped.setApplication(application);
    }

}
