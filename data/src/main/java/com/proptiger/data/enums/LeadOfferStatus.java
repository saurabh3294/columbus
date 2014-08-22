package com.proptiger.data.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author azi
 * 
 */

public enum LeadOfferStatus {
    Offered(1), Expired(2), Waitlisted(3), New(4), Declined(12);

    private int                                  leadOfferStatusId;
    private static Map<Integer, LeadOfferStatus> idToEnumLookUpMap = new HashMap<>();

    static {
        for (LeadOfferStatus offerStatus : LeadOfferStatus.values()) {
            idToEnumLookUpMap.put(offerStatus.getLeadOfferStatusId(), offerStatus);
        }
    }

    private LeadOfferStatus(int leadOfferStatusId) {
        this.leadOfferStatusId = leadOfferStatusId;
    }

    public int getLeadOfferStatusId() {
        return this.leadOfferStatusId;
    }

    public LeadOfferStatus fromLeadOfferStatusId(int id) {
        return idToEnumLookUpMap.get(id);
    }
}