package com.proptiger.data.enums.lead;

public enum BuyPeriod {
    THIRTY("30"), NINETY("90"), ONEEIGHTY("180"), EMPTY("");
    
    private String buyPeriod;
    
    private BuyPeriod(String type) {
        this.buyPeriod = type;
    }
}
