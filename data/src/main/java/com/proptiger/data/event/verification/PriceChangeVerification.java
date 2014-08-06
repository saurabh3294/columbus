package com.proptiger.data.event.verification;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;

@Service
public class PriceChangeVerification extends DBEventVerification{

    @Override
    public boolean verifyEvents(EventGenerated eventGenerated) {
        // TODO Auto-generated method stub
        return super.verifyEvents(eventGenerated);
    }

}
