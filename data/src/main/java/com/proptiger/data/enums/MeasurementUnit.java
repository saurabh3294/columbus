package com.proptiger.data.enums;

/**
 * @author Rajeev Pandey
 * 
 */
public enum MeasurementUnit {
    SQ_FT("sq ft"), SQ_YD("sq yd"), SQ_M("sq m");

    String value;

    MeasurementUnit(String str) {
        this.value = str;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.value;
    }

    public static void main(String args[]) {
        // MeasurementUnit me = MeasurementUnit.valueOf("sq ft");
    }
}
