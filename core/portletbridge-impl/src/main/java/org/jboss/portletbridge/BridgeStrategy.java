/**
 * 
 */
package org.jboss.portletbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.BridgeException;

import org.jboss.portletbridge.util.BridgeLogger;

/**
 * This class incapsulates all library-dependent behaviors.
 * @author asmirnov
 *
 */
public abstract class BridgeStrategy {

	
	/**
	 * Location of the services descriptor file in a brige installation that defines the class name of the bridge implementation. 
	 */
	private static final String SERVICE_CLASSPATH = "META-INF/services/"+BridgeStrategy.class.getName();

	public static final Logger log = BridgeLogger.BRIDGE.getLogger();

	protected final BridgeConfig config;


	
	/**
	 * @param config
	 */
	public BridgeStrategy(BridgeConfig config) {
		this.config = config;
	}

	/**
	 * @return the config
	 */
	public BridgeConfig getConfig() {
		return config;
	}

	/**
	 * @param context
	 * @param renderKitFactory
	 */
	public abstract void init(FacesContext context, RenderKitFactory renderKitFactory);

	/**
	 * @param facesContext
	 */
	public abstract void beforeActionRequest(FacesContext facesContext);

	public abstract void afterActionRequestExecute(FacesContext facesContext);

	/**
	 * @param facesContext
	 */
	public abstract void afterActionRequest(FacesContext facesContext);
	
	/**
	 * @param facesContext
	 */
	public abstract void beforeEventRequest(FacesContext facesContext);

	/**
	 * @param facesContext
	 */
	public abstract void afterEventRequest(FacesContext facesContext);
	/**
	 * @param facesContext
	 */
	public abstract void beforeRenderRequest(FacesContext facesContext);

	/**
	 * @param facesContext
	 * @param wrappedResponse
	 */
	public abstract void afterRenderRequest(FacesContext facesContext,
			RenderResponse wrappedResponse);

	/**
	 * @param facesContext
	 */
	public abstract void beforeResourceRequest(FacesContext facesContext);

	/**
	 * @param facesContext
	 * @param wrappedResponse
	 */
	public abstract void afterResourceRequest(FacesContext facesContext,
			ResourceResponse wrappedResponse);
	/**
	 * @param response
	 * @return
	 */
	public abstract RenderResponse createResponseWrapper(RenderResponse response);
	
	
	public abstract boolean serveResource(ResourceRequest request,ResourceResponse response) throws BridgeException;

	/**
	 * @param response
	 * @return
	 */
	public abstract ResourceResponse createResponseWrapper(ResourceResponse response);
	
	
    /**
     * @param name
     * @return
     */
    public abstract int getPortletSessionScopeForName(String name);

	/**
	 * Factory method that creates strategy for current application configuration.
	 * @param config
	 * @return
	 */
	public static BridgeStrategy getCurrentStrategy(BridgeConfig config) throws BridgeException {
		BridgeStrategy strategy;
		// use contextClassLoader to load strategies, because when bridge-impl.jar is
		// shared classes visible by application would be different.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(null == loader){
			loader = BridgeStrategy.class.getClassLoader();
		}
		strategy = new Jsf20Strategy(config);
        // Attempt to load additional strategy from services.
		try {
		Enumeration<URL> resources = loader.getResources(SERVICE_CLASSPATH);
		while (resources.hasMoreElements()) {
	        URL resource = (URL) resources.nextElement();
			InputStream stream = null;
			try {
				URLConnection connection = resource.openConnection();
				// Prevent jar locking in the Windows environment.
				connection.setUseCaches(false);
				stream = connection.getInputStream();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(
							stream, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					reader = new BufferedReader(new InputStreamReader(
							stream));
				}
				String strategyClassName;
				// Parse file content. All empty string and comments ignored.
				while (null != (strategyClassName = reader.readLine())) {
						int indexOfComment = strategyClassName.indexOf('#');
						if (indexOfComment >= 0) {
							strategyClassName = strategyClassName.substring(0,
									indexOfComment);
						}
						strategyClassName = strategyClassName.trim();
						if (strategyClassName.length() > 0) {
							strategy = createStrategyInstance(config, strategy,
							        loader, strategyClassName);

						}
					}
			} catch (IOException e) {
				log.log(Level.SEVERE,"Error reading bridge service definition file",e);
			} catch (SecurityException e) {
				log.log(Level.SEVERE,"Error reading bridge service definition file",e);
            } finally {
				if (null != stream) {
					try {
						stream.close();
					} catch (IOException e) {
						log.log(Level.SEVERE,
								"Error to close input stream for a resource "
										+ SERVICE_CLASSPATH,e);
					}

				}
			}
		}
		} catch (IOException e) {
			log.log(Level.SEVERE,"Error geting strategy service definition resources",e);
		}

		return strategy;
	}

	/**
     * @param config
     * @param strategy
     * @param loader
     * @param className
     * @return
     */
	private static BridgeStrategy createStrategyInstance(BridgeConfig config,
	        BridgeStrategy strategy, ClassLoader loader, String className) throws BridgeException {
		try {
			Class<? extends BridgeStrategy> strategyClass = loader.loadClass(
			        className).asSubclass(BridgeStrategy.class);
			try {
				Constructor<? extends BridgeStrategy> constructor = strategyClass
				        .getConstructor(BridgeConfig.class,
				                BridgeStrategy.class);
				strategy = constructor.newInstance(config, strategy);
				if(log.isLoggable(Level.FINE)){
					log.fine("New strategy instance "+className+ " has been created with parent strategy");
				}
			} catch (NoSuchMethodException e) {
				Constructor<? extends BridgeStrategy> constructor = strategyClass
				        .getConstructor(BridgeConfig.class);
				strategy = constructor.newInstance(config);
				if(log.isLoggable(Level.FINE)){
					log.fine("New strategy instance "+className+ " has been created");
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BridgeException("Bridge Strategy class "+className+" not found");
		} catch (NoSuchMethodException e) {
			throw new BridgeException("Bridge Strategy "+className+" has never BridgeStrategy(BridgeConfig) nor BridgeStrategy(BridgeConfig,BridgeStrategy) constructor");
		} catch (IllegalArgumentException e) {
			throw new BridgeException("Illegal argument for Bridge Strategy "+className+" constructor",e);
		} catch (InstantiationException e) {
			throw new BridgeException("Can't instantiate Bridge Strategy class "+className,e);
		} catch (IllegalAccessException e) {
			throw new BridgeException("Illegal access to Bridge Strategy constructor",e);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			if (targetException instanceof NoClassDefFoundError) {
				log.info("Bridge Strategy was not activated due to "
				        + targetException.getMessage());
			} else {
				throw new BridgeException("Can't instantiate Bridge Strategy class "+className,targetException);
			}
		} catch( NoClassDefFoundError e){
			log.info("Bridge Strategy was not activated due to "
			        + e.getMessage());			
		}
		return strategy;
	}
	
}
