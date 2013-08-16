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
package org.jboss.portletbridge.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class PortletApplicationFactoryImpl extends ApplicationFactory {

    private ApplicationFactory mHandler;
    private volatile Application applicationImpl;

    public PortletApplicationFactoryImpl(ApplicationFactory handler) {
        mHandler = handler;
        applicationImpl = new PortletApplicationImpl(mHandler.getApplication());
    }

    @Override
    public Application getApplication() {
        if (applicationImpl == null) {
            synchronized (this) {
                if (applicationImpl == null) {
                    applicationImpl = new PortletApplicationImpl(mHandler.getApplication());
                }
            }
        }
        return applicationImpl;
    }

    @Override
    public void setApplication(Application app) {
        applicationImpl = null;
        mHandler.setApplication(app);
    }
}
