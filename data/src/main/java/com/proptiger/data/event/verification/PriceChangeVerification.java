package com.proptiger.data.event.verification;

import org.springframework.stereotype.Component;

import com.proptiger.data.event.model.EventGenerated;

@Component
public class PriceChangeVerification extends DBEventVerification {

    @Override
    public boolean verifyEvents(EventGenerated eventGenerated) {
        // TODO Auto-generated method stub
        return super.verifyEvents(eventGenerated);
    }

}
