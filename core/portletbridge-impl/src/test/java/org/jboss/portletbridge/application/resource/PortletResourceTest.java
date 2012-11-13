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
package org.jboss.portletbridge.application.resource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.faces.application.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.faces.application.resource.ResourceImpl;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletResourceTest {

    static final String RESOURCE_NAME = "resourceName";
    static final String LIBRARY_NAME = "library";
    static final String CONTENT_TYPE = "image/jpg";

    static final String TMP_FILE = "target/test-classes/tmp";

    @Test
    public void testPortletResourceExternalization() {
        Resource res = new ResourceImpl();
        res.setResourceName(RESOURCE_NAME);
        res.setLibraryName(LIBRARY_NAME);
        res.setContentType(CONTENT_TYPE);
        PortletResource portletRes = new PortletResource(res);
        PortletResource newRes = null;

        //serialize the resource
        try {
            FileOutputStream fo = new FileOutputStream(TMP_FILE);
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(portletRes);
            so.flush();
            so.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // de-serialize the resource
        try {
            FileInputStream fi = new FileInputStream(TMP_FILE);
            ObjectInputStream si = new ObjectInputStream(fi);       
            newRes = (PortletResource) si.readObject();
            si.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull("New PortletResource should not be null", newRes);
        Assert.assertEquals(RESOURCE_NAME, newRes.getResourceName());
        Assert.assertEquals(LIBRARY_NAME, newRes.getLibraryName());
        Assert.assertEquals(CONTENT_TYPE, newRes.getContentType());
        Assert.assertEquals(res.getClass().getName(), newRes.getWrapped().getClass().getName());
        Assert.assertEquals(RESOURCE_NAME, newRes.getWrapped().getResourceName());
        Assert.assertEquals(LIBRARY_NAME, newRes.getWrapped().getLibraryName());
        Assert.assertEquals(CONTENT_TYPE, newRes.getWrapped().getContentType());
    }
}
