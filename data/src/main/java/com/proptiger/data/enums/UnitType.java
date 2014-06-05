package com.proptiger.data.enums;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public enum UnitType {

    Apartment, Plot, Villa, @Deprecated
    // Needed due to bug in FIQL query parser
    APARTMENT, @Deprecated
    PLOT, @Deprecated
    VILLA;

}