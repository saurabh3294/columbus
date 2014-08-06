package com.proptiger.data.event.model.payload;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.model.EventGenerated.EventStatus;

public class EventTypeUpdateHistory {
    private EventStatus eventStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date        updatedDate;
    
    public EventTypeUpdateHistory(){
        
    }
    
    public EventTypeUpdateHistory(EventStatus eventStatus, Date updateDate){
        this.eventStatus = eventStatus;
        this.updatedDate = updateDate;
    }
    
    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
