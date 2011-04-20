/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.portletbridge.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.faces.FacesException;

/**
 * <p>
 * Utility class to provide access to various framework implementation services.
 * Examples of such services are: {@link org.richfaces.skin.SkinFactory}, TBD.
 * </p>
 * 
 * 
 * <p>
 * This class manages application-scoped service factories that are stored in
 * the map with {@link Thread#currentThread()} Context classloader as the key.
 * Therefore, there is only one instance per JEE application in the current JVM.
 * </p>
 * <p>
 * actual call for the service instance delegated to the current factory
 * </p>
 * <img src="services.png" alt="services tracking class diagramm"/>
 * 
 * 
 * <p>
 * <b>Note:</b> in initial state this class is not synchronized and presumes
 * that all modification operations are done in a context of single-thread (in
 * JSF initialization listener).
 * </p>
 * 
 * @author Nick Belaevski
 * @since 4.0
 */
public final class Services {

	/**
	 * <p class="changed_added_4_0">
	 * </p>
	 */
	private static final ConcurrentMap<ClassLoader, ServiceFactoryImpl> INSTANCES = new ConcurrentHashMap<ClassLoader, ServiceFactoryImpl>();

	private static final class ServiceFactoryImpl implements ServiceFactory {

		private final Map<Class<?>, Object> serviceInstances;

		private boolean initialized = false;

		/**
		 * <p class="changed_added_4_0">
		 * This class supposed to use with static methods only and cannot be
		 * instantiated.
		 * </p>
		 */
		private ServiceFactoryImpl() {
			serviceInstances = new HashMap<Class<?>, Object>();
		}

		@SuppressWarnings("unchecked")
		public <T> T get(Class<T> target) {
			if (serviceInstances.containsKey(target)) {
				return (T) serviceInstances.get(target);
			} else {
				throw new ServiceException("Service not registered :"
						+ target.getName());
			}
		}

		public <T> ServiceFactory register(Class<T> service, T instance) {
			if (initialized) {
				throw new ServiceException(
						"Cannot register new service, Services already initialized");
			} else {
				serviceInstances.put(service, instance);
			}
			return this;
		}

		private void release() {
			for (Object instance : serviceInstances.values()) {
				if (instance instanceof Initializable) {
					((Initializable) instance).release();
				}
			}
		}

		private void init() {
			for (Object instance : serviceInstances.values()) {
				if (instance instanceof Initializable) {
					((Initializable) instance).init();
				}
			}
			initialized = true;
		}
	}

	/**
	 * <p>
	 * Get service instance for given type.
	 * </p>
	 * 
	 * @param <T>
	 *            The service type, usually interface.
	 * @param target
	 *            Service type class.
	 * @return service implementation instance.
	 */
	public static <T> T getService(Class<T> target) {
		return getServiceFactory().get(target);
	}

	/**
	 * @return services factory.
	 */
	public static ServiceFactory getFactory() {
		ServiceFactoryImpl factory = new ServiceFactoryImpl();
		ServiceFactory oldValue = INSTANCES.putIfAbsent(getCurrentLoader(),
				factory);
		return null == oldValue ? factory : oldValue;
	}

	/**
	 * Initializes all registered services, makes factory immutable.
	 */
	public static void init() {
		getServiceFactory().init();
	}

	private static ServiceFactoryImpl getServiceFactory() {
		if (!INSTANCES.containsKey(getCurrentLoader())) {
			throw new FacesException("Service Tracker has not been initialized");
		}
		ServiceFactoryImpl service = INSTANCES.get(getCurrentLoader());
		return service;
	}

	private static ClassLoader getCurrentLoader() {
		ClassLoader contextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		if (null == contextClassLoader) {
			contextClassLoader = Services.class.getClassLoader();
		}
		return contextClassLoader;
	}

	/**
	 * <p class="changed_added_4_0">
	 * Release factory service associated with current context.
	 * </p>
	 */
	public static void release() {
		ServiceFactoryImpl servicesFactory = INSTANCES
				.remove(getCurrentLoader());
		if (null != servicesFactory) {
			servicesFactory.release();
		}
	}

}
