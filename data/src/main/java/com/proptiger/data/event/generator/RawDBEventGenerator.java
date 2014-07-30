package com.proptiger.data.event.generator;

import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.RawDBEvent;

/**
 * Generates the Raw Events from DB
 * 
 * @author sahil
 * 
 */
public class RawDBEventGenerator {

    public List<RawDBEvent> getRawDBEvents() {
        return null;
    }

    public void populateRawDBEventData(RawDBEvent rawDBEvent) {
        if (DBOperation.INSERT.equals(rawDBEvent.getDbOperation())) {
            populateInsertRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getDbOperation())) {
            populateDeleteRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getDbOperation())) {
            populateUpdateRawDBEventData(rawDBEvent);
        }
    }

    public void populateInsertRawDBEventData(RawDBEvent rawDBEvent) {

    }

    public void populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {

    }

    public void populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {

    }
}
