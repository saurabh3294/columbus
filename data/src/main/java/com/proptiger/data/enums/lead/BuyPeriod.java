package com.proptiger.data.enums.lead;

public enum BuyPeriod {
    THIRTY(30), NINETY(90), ONEEIGHTY(180);
    
    private Integer buyPeriod;
    
    private BuyPeriod(Integer type) {
        this.buyPeriod = type;
    }
}
