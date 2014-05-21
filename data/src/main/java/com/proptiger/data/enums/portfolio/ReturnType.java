package com.proptiger.data.enums.portfolio;

/**
 * @author Rajeev Pandey
 * 
 */
public enum ReturnType {

    APPRECIATION("Appreciation"), DECLINE("Decline"), NOCHANGE("No Change");

    private String type;

    private ReturnType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
