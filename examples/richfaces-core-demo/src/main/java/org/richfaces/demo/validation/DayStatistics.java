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
package org.richfaces.demo.validation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Ilya Shaikovsky
 * 
 */
@ManagedBean
@RequestScoped
public class DayStatistics {

    private List<PassTime> dayPasstimes = new ArrayList<PassTime>();

    public DayStatistics() {
        dayPasstimes.add(new PassTime("Sport", 0));
        dayPasstimes.add(new PassTime("Entertainment", 0));
        dayPasstimes.add(new PassTime("Sleeping", 0));
        dayPasstimes.add(new PassTime("Games", 0));
    }

    public List<PassTime> getDayPasstimes() {
        return dayPasstimes;
    }

    public void setDayPasstimes(List<PassTime> dayPasstimes) {
        this.dayPasstimes = dayPasstimes;
    }

    @NotNull
    @Min(value = 1, message = "Please feel at list one entry")
    @Max(value = 24, message = "Only 24h in a day!")
    public Integer getTotalTime() {
        Integer result = new Integer(0);
        for (PassTime passtime : dayPasstimes) {
            result += passtime.getTime();
        }
        return result;
    }

    public void store(ActionEvent event) {
        FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(FacesContext.getCurrentInstance()),
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Changes Stored Successfully", "Changes Stored Successfully"));
    }
}
