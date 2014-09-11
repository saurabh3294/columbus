package com.proptiger.data.enums;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public enum UnitType {

    Apartment, Plot, Villa, Commercial, Shop, Office, Other, @Deprecated
    // Needed due to bug in FIQL query parser
    APARTMENT, @Deprecated
    PLOT, @Deprecated
    VILLA, @Deprecated
    COMMERCIAL, @Deprecated
    SHOP, @Deprecated
    OFFICE, @Deprecated
    OTHER 
    }