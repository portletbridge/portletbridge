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
package org.jboss.portletbridge.context;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.portlet.PortletContext;

import org.jboss.portletbridge.el.ELContextImpl;

/**
 * @author asmirnov
 *
 */
public class InitFacesContext extends FacesContext {

    private final Application application;

    private final FacesContext originalContext;

    private ExternalContext externalContext;

    private UIViewRoot viewRoot;

    private ELContext elContext;

    /**
     * @param application
     * @param portletContext
     */
    public InitFacesContext(Application application, PortletContext portletContext) {
        originalContext = FacesContext.getCurrentInstance();
        setCurrentInstance(this);
        this.application = application;
        viewRoot = new UIViewRoot();
        viewRoot.setLocale(Locale.getDefault());
        if (null != originalContext) {
            externalContext = originalContext.getExternalContext();
        } else {
            externalContext = new InitExternalContext(portletContext);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#addMessage(java.lang.String, javax.faces.application.FacesMessage)
     */
    @Override
    public void addMessage(String clientId, FacesMessage message) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getApplication()
     */
    @Override
    public Application getApplication() {
        return application;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getClientIdsWithMessages()
     */
    @Override
    public Iterator<String> getClientIdsWithMessages() {
        List<String> list = Collections.emptyList();
        return list.iterator();
    }

    @Override
    public ELContext getELContext() {
        if (this.elContext == null) {
            Application application = getApplication();
            this.elContext = new ELContextImpl(application.getELResolver());
            this.elContext.putContext(FacesContext.class, this);
        }
        return this.elContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getExternalContext()
     */
    @Override
    public ExternalContext getExternalContext() {
        return externalContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getMaximumSeverity()
     */
    @Override
    public Severity getMaximumSeverity() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getMessages()
     */
    @Override
    public Iterator<FacesMessage> getMessages() {
        List<FacesMessage> list = Collections.emptyList();
        return list.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
     */
    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        List<FacesMessage> list = Collections.emptyList();
        return list.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getRenderKit()
     */
    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getRenderResponse()
     */
    @Override
    public boolean getRenderResponse() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getResponseComplete()
     */
    @Override
    public boolean getResponseComplete() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getResponseStream()
     */
    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getResponseWriter()
     */
    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#getViewRoot()
     */
    @Override
    public UIViewRoot getViewRoot() {
        return viewRoot;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#release()
     */
    @Override
    public void release() {
        setCurrentInstance(originalContext);

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#renderResponse()
     */
    @Override
    public void renderResponse() {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#responseComplete()
     */
    @Override
    public void responseComplete() {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
     */
    @Override
    public void setResponseStream(ResponseStream responseStream) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
     */
    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.context.FacesContext#setViewRoot(javax.faces.component.UIViewRoot)
     */
    @Override
    public void setViewRoot(UIViewRoot root) {
        viewRoot = root;
    }

}
