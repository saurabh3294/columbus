package com.proptiger.data.model.event.payload;

import java.util.Date;

import com.proptiger.data.event.model.EventGenerated.EventStatus;

public class EventTypeUpdateHistory {
    private EventStatus eventStatus;
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
