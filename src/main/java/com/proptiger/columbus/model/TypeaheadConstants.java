package com.proptiger.columbus.model;

public class TypeaheadConstants {

    public static final String defaultCityName          = "Noida";
    public static final String cityCookieLabel          = "HOME_CITY";
    public static final String cityCookieSeparater      = "%2C";

    public static final float  SuggestionScoreThreshold = 20.0f;

    public static final float  QueryTimeBoostStart      = 10f;
    public static final float  QueryTimeBoostMultiplier = 0.3f;

    public static final float  CityBoostMinScore        = 8.0f;
    public static final float  CityBoost                = 1.25f;
    public static final float  DocumentFetchMultiplier  = 2.0f;
    
    public static final float GooglePlaceDelegationTheshold = 10.0f;

}
