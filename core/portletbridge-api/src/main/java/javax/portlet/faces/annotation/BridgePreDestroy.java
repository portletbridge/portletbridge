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
package javax.portlet.faces.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The PreDestroy annotation is used on methods as a callback notification to signal that the instance is in the process of
 * being removed by the bridge from the bridge request scope. This method complements one using
 * <code>javax.annotation.PreDestroy</code> to allow the object to release resources that it has been holding. It exists because
 * the existing <code>javax.annotation.PreDestroy</code> method must be ignored as it is called by the container even though the
 * bridge continues to manage the object in its request scope. The method on which the PreDestroy annotation is applied MUST
 * fulfill all of the following criteria - The method MUST NOT have any parameters - The return type of the method MUST be void.
 * - The method MUST NOT throw a checked exception. - The method on which PreDestroy is applied MUST be public. - The method
 * MUST NOT be static. - The method MAY be final. - If the method throws an unchecked exception it is ignored.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface BridgePreDestroy {
}
