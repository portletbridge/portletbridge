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
package org.jboss.portletbridge.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.pluto.container.driver.DriverPortletContext;
import org.apache.pluto.container.driver.PlutoServices;
import org.apache.pluto.container.driver.PortletContextService;
import org.apache.pluto.container.om.portlet.PortletDefinition;
import org.apache.pluto.driver.config.DriverConfigurationException;
import org.apache.pluto.driver.services.impl.resource.RenderConfigServiceImpl;
import org.apache.pluto.driver.services.impl.resource.ResourceConfig;
import org.apache.pluto.driver.services.portal.PageConfig;

public class CustomRenderServiceImpl extends RenderConfigServiceImpl {

    private List<PageConfig> pages;

    private boolean initialized = false;

    public CustomRenderServiceImpl(ResourceConfig config) {
        super(config);
        pages = new ArrayList<PageConfig>();
        init(org.apache.pluto.driver.PortalStartupListener.getServletContext());
    }

    public void addPage(PageConfig arg0) {
        checkInit();
        pages.add(arg0);
    }

    public PageConfig getDefaultPage() {
        checkInit();
        return pages.get(0);
    }

    public PageConfig getPage(String arg0) {
        checkInit();
        Iterator<PageConfig> it = pages.iterator();
        while (it.hasNext()) {
            PageConfig page = it.next();
            if (page.getName().equals(arg0)) {
                return page;
            }
        }
        return pages.get(0);
    }

    public List getPages() {
        checkInit();
        return pages;
    }

    public void removePage(PageConfig arg0) {
        checkInit();
        pages.remove(arg0);
    }

    public void init(ServletContext context) throws DriverConfigurationException {
        if (PlutoServices.getServices() != null) {
            PageConfig page = new PageConfig();
            page.setName("Embedded Portlets");
            page.setUri("/WEB-INF/themes/pluto.jsp");
            PortletContextService contextService = PlutoServices.getServices().getPortletContextService();
            Iterator<DriverPortletContext> portletContexts = contextService.getPortletContexts();
            String contextPath = context.getContextPath();
            while (portletContexts.hasNext()) {
                DriverPortletContext driverPortletContext = portletContexts.next();
                List<? extends PortletDefinition> portlets = driverPortletContext.getPortletApplicationDefinition()
                        .getPortlets();
                for (PortletDefinition portletDefinition : portlets) {
                    String portletName = portletDefinition.getPortletName();
                    page.addPortlet(contextPath, portletName);
                }
            }
            pages.add(page);
            initialized = true;
        }
    }

    private void checkInit() {
        if (!initialized) {
            init(org.apache.pluto.driver.PortalStartupListener.getServletContext());
        }
    }
}
