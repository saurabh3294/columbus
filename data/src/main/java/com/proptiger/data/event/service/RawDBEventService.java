package com.proptiger.data.event.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.RawDBEventDao;

@Service
public class RawDBEventService {
    @Autowired
    private RawDBEventDao rawDBEventDao;

    public List<RawDBEvent> getRawDBEvents(String tableName, String dateAttributeName, String dateAttributeValue) {
        return null;
    }
    
}
