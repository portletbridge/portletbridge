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
package org.richfaces.demo.common.navigation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class DemoDescriptor extends BaseDescriptor {

    private static final long serialVersionUID = 6822187362271025752L;

    private static final String BASE_SAMPLES_DIR = "/richfaces/";

    private List<SampleDescriptor> samples;

    public SampleDescriptor getSampleById(String id) {
        for (SampleDescriptor sample : getSamples()) {
            if (sample.getId().equals(id)) {
                return sample;
            }
        }
        return samples.get(0);
    }

    @XmlElementWrapper(name = "samples")
    @XmlElement(name = "sample")
    public List<SampleDescriptor> getSamples() {
        return samples;
    }

    public void setSamples(List<SampleDescriptor> samples) {
        this.samples = samples;
    }

}
