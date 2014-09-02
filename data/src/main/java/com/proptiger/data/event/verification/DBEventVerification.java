package com.proptiger.data.event.verification;

import org.springframework.stereotype.Component;

import com.proptiger.data.event.model.EventGenerated;

@Component
public class DBEventVerification implements EventVerification {

    public boolean verifyEvents(EventGenerated eventGenerated) {
        return true;
    }

}
