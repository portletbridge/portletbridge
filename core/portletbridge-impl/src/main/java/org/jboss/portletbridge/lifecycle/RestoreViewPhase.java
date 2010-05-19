/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portletbridge.lifecycle;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PostRestoreStateEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;

import org.jboss.portletbridge.util.BridgeLogger;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;

/**
 * @author asmirnov
 * New changes include VDL
 *
 */
public class RestoreViewPhase extends LifecyclePhase {

    private static final Logger _log = BridgeLogger.FACES.getLogger();
    private final LifecyclePhase nextPhase;
    private static final String WEBAPP_ERROR_PAGE_MARKER =
            "javax.servlet.error.message";


    private WebConfiguration webConfig;

    /**
     * @param lifecycle
     */
    public RestoreViewPhase(Lifecycle lifecycle) {
        super(lifecycle);
        nextPhase = new ApplyValuesPhase(lifecycle);
    }

    public void execute(FacesContext context) {
        context.getApplication().getViewHandler().initView(context);
        super.execute(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executeNextPhase(javax.faces.context.FacesContext,
     *      javax.faces.event.PhaseListener[])
     */
    protected void executeNextPhase(FacesContext context) {
        nextPhase.execute(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#executePhase(javax.faces.context.FacesContext)
     */
    public void executePhase(FacesContext facesContext) {

        if (_log.isLoggable(Level.FINE)) {
            _log.fine("Entering RestoreViewPhase");
        }
        if (null == facesContext) {
            throw new FacesException(MessageUtils.getExceptionMessageString(
                    MessageUtils.NULL_CONTEXT_ERROR_MESSAGE_ID));
        }

        // If an app had explicitely set the tree in the context, use that;
        //
        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot != null) {
            if (_log.isLoggable(Level.FINE)) {
                _log.fine("Found a pre created view in FacesContext");
            }
            facesContext.getViewRoot().setLocale(
                    facesContext.getExternalContext().getRequestLocale());

            // do per-component actions
            UIViewRoot root = facesContext.getViewRoot();
            final PostRestoreStateEvent event = new PostRestoreStateEvent(root);
            try {
                root.visitTree(VisitContext.createVisitContext(facesContext),
                        new VisitCallback() {

                            public VisitResult visit(VisitContext context, UIComponent target) {
                                event.setComponent(target);
                                target.processEvent(event);
                                return VisitResult.ACCEPT;
                            }
                        });
            } catch (AbortProcessingException e) {
                facesContext.getApplication().publishEvent(facesContext,
                        ExceptionQueuedEvent.class,
                        new ExceptionQueuedEventContext(facesContext, e));
            }


            if (!facesContext.isPostback()) {
                facesContext.renderResponse();
            }
            return;
        }

        // Reconstitute or create the request tree
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        String viewId = (String) requestMap.get("javax.servlet.include.path_info");
        if (viewId == null) {
            viewId = facesContext.getExternalContext().getRequestPathInfo();
        }

        // It could be that this request was mapped using
        // a prefix mapping in which case there would be no
        // path_info.  Query the servlet path.
        if (viewId == null) {
            viewId = (String) requestMap.get("javax.servlet.include.servlet_path");
        }

        if (viewId == null) {
            viewId = facesContext.getExternalContext().getRequestServletPath();
        }

        if (viewId == null) {
            throw new FacesException(MessageUtils.getExceptionMessageString(
                    MessageUtils.NULL_REQUEST_VIEW_ERROR_MESSAGE_ID));
        }

        ViewHandler viewHandler = Util.getViewHandler(facesContext);

        boolean isPostBack = (facesContext.isPostback() && !isErrorPage(facesContext));
        if (isPostBack) {
            facesContext.setProcessingEvents(false);
            // try to restore the view
            viewRoot = viewHandler.restoreView(facesContext, viewId);
            if (viewRoot == null) {
                if (is11CompatEnabled(facesContext)) {
                    // 1.1 -> create a new view and flag that the response should
                    //        be immediately rendered
                    viewRoot = viewHandler.createView(facesContext, viewId);
                    facesContext.renderResponse();

                } else {
                    Object[] params = {viewId};
                    throw new ViewExpiredException(
                            MessageUtils.getExceptionMessageString(
                            MessageUtils.RESTORE_VIEW_ERROR_MESSAGE_ID,
                            params),
                            viewId);
                }
            }

            facesContext.setViewRoot(viewRoot);
            facesContext.setProcessingEvents(true);
        } else {

            ViewDeclarationLanguage vdl = facesContext.getApplication().getViewHandler().getViewDeclarationLanguage(facesContext, viewId);

            if (vdl != null) {
                // If we have one, get the ViewMetadata...
                ViewMetadata metadata = vdl.getViewMetadata(facesContext, viewId);

                if (metadata != null) { // perhaps it's not supported
                    // and use it to create the ViewRoot.  This will have, at most
                    // the UIViewRoot and its metadata facet.
                    viewRoot = metadata.createMetadataView(facesContext);

                    // Only skip to render response if there are no view parameters
                    Collection<UIViewParameter> params =
                            ViewMetadata.getViewParameters(viewRoot);
                    if (params.isEmpty()) {
                        facesContext.renderResponse();
                    }
                }
            } else {
                facesContext.renderResponse();
            }

            if (null == viewRoot) {
                viewRoot = (Util.getViewHandler(facesContext)).createView(facesContext, viewId);
            }
            facesContext.setViewRoot(viewRoot);
            assert (null != viewRoot);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.portletbridge.lifecycle.LifecyclePhase#getPhaseId()
     */
    protected PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    private static boolean isErrorPage(FacesContext context) {

        return (context.getExternalContext().
                getRequestMap().get(WEBAPP_ERROR_PAGE_MARKER) != null);

    }

    private WebConfiguration getWebConfig(FacesContext context) {

        if (webConfig == null) {
            webConfig = WebConfiguration.getInstance(context.getExternalContext());
        }
        return webConfig;

    }

    private boolean is11CompatEnabled(FacesContext context) {

        return (getWebConfig(context).isOptionEnabled(
                BooleanWebContextInitParameter.EnableRestoreView11Compatibility));

    }
}
