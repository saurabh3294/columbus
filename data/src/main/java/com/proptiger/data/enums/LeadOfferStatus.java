package com.proptiger.data.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author azi
 * 
 */

public enum LeadOfferStatus {
    Offered(1), Expired(2), Waitlisted(3), New(4), InProgress(6), SiteVisit(8), Negotiation(9), ClosedWon(10), ClosedLost(
            11), Declined(12);

    private int                                  leadOfferStatusId;
    private static Map<Integer, LeadOfferStatus> idToEnumLookUpMap = new HashMap<>();

    static {
        for (LeadOfferStatus offerStatus : LeadOfferStatus.values()) {
            idToEnumLookUpMap.put(offerStatus.getId(), offerStatus);
        }
    }

    private LeadOfferStatus(int leadOfferStatusId) {
        this.leadOfferStatusId = leadOfferStatusId;
    }

    public int getId() {
        return this.leadOfferStatusId;
    }

    public static LeadOfferStatus fromId(int id) {
        return idToEnumLookUpMap.get(id);
    }
}