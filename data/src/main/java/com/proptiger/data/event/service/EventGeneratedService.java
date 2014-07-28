package com.proptiger.data.event.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.repo.EventGeneratedDao;

@Service
public class EventGeneratedService {
    @Autowired
    private EventGeneratedDao eventGeneratedDao;
    
    public void persistEvents(List<EventGenerated> eventGenerateds){
        eventGeneratedDao.save(eventGenerateds);
    }
    
    public List<EventGenerated> getRawEvents(){
        return eventGeneratedDao.findByStatusOrderByCreatedDateAsc(EventGenerated.EventStatus.Raw.name());
    }
    
    public List<EventGenerated> getProcessedEvents(){
        return eventGeneratedDao.findByStatusAndExpiryDateOrderByCreatedDateAsc(EventGenerated.EventStatus.Processed.name(), new Date());
    }
        
}
