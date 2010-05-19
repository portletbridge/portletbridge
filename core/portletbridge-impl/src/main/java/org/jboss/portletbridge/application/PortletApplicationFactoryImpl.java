/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.jboss.portletbridge.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class PortletApplicationFactoryImpl extends ApplicationFactory {

	private ApplicationFactory mHandler;
	private volatile PortletApplicationImpl applicationImpl;

	public PortletApplicationFactoryImpl(ApplicationFactory handler) {
		mHandler = handler;
		applicationImpl = new PortletApplicationImpl(mHandler.getApplication());
	}

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

	public void setApplication(Application app) {
		applicationImpl = null;
		mHandler.setApplication(app);
	}
}
