package com.proptiger.data.event.service;

import java.util.List;

import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.RawDBEventDao;

public class RawDBEventService {

    private RawDBEventDao rawDBEventDao;

    public List<RawDBEvent> getRawDBEvents(String tableName, String dateAttributeName, String dateAttributeValue) {
        return null;
    }
}
