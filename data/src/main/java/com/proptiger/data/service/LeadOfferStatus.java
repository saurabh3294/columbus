package com.proptiger.data.service;

public enum LeadOfferStatus
{
    Dead(7),
    ClosedLost(8),
    ClosedWon(9);
    private int id;

    LeadOfferStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}