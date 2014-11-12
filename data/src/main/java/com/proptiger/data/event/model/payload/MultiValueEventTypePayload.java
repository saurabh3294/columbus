package com.proptiger.data.event.model.payload;

import java.util.ArrayList;
import java.util.List;

import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.payload.dto.EventTypePayloadDataDto;

public class MultiValueEventTypePayload extends EventTypePayload {

    /**
     * 
     */
    private static final long serialVersionUID = 3823967413934605524L;
    
    List<EventTypePayloadDataDto> payloadDataDtos = new ArrayList<EventTypePayloadDataDto>();

    public List<EventTypePayloadDataDto> getPayloadDataDtos() {
        return payloadDataDtos;
    }

    public void setPayloadDataDtos(List<EventTypePayloadDataDto> payloadDataDtos) {
        this.payloadDataDtos = payloadDataDtos;
    }

    @Override
    public void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName) {
        // TODO Auto-generated method stub
        super.populatePayloadValues(rawDBEvent, attributeName);
        Object oldValue = rawDBEvent.getOldDBValueMap().get(attributeName);
        Object newValue = rawDBEvent.getNewDBValueMap().get(attributeName);
        payloadDataDtos.add(new EventTypePayloadDataDto(attributeName, oldValue, newValue));
    }

    @Override
    public Object getPayloadValues() {
        // TODO Auto-generated method stub
        return payloadDataDtos;
    }

}
