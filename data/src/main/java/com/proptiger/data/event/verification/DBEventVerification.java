package com.proptiger.data.event.verification;

import com.proptiger.data.event.model.EventGenerated;

public class DBEventVerification implements EventVerification {
    
    public boolean verifyEvents(EventGenerated eventGenerated) {
        return true;
    }

}
