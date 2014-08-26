package com.proptiger.data.event.verification;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;

@Service
public class DBEventVerification implements EventVerification {
    
    public boolean verifyEvents(EventGenerated eventGenerated) {
        return true;
    }

}
