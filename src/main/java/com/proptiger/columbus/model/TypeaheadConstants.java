package com.proptiger.columbus.model;

public class TypeaheadConstants {

    public static final String typeaheadIdPattern             = "TYPEAHEAD-%s-%s";

    public static final String defaultCityName                = "Noida";
    public static final int    defaultCityId                  = 20;
    public static final String cityCookieLabel                = "HOME_CITY";
    public static final String cityCookieSeparater            = "%2C";

    public static final float  suggestionScoreThreshold       = 20.0f;

    public static final float  queryTimeBoostStart            = 10f;
    public static final float  queryTimeBoostMultiplier       = 0.3f;

    public static final float  cityBoostMinScore              = 8.0f;
    public static final float  cityBoost                      = 1.25f;
    public static final float  documentFetchMultiplier        = 4.0f;
    public static final float  documentFetchLimit             = 20;

    public static final String externalApiIdentifierGoogle    = "gp";

    public static final String typeaheadFieldNameCity         = "TYPEAHEAD_CITY";

    public static final String typeaheadTypeBuilder           = "BUILDER";

    /** Used while creating suggestions **/

    public static final int    maxSuggestionCount             = 3;
    public static final int    suggestionNewLaunchMultiplier  = 3;
    public static final int    suggestionProjectCountTheshold = 0;

    public static final int    cityRadius                     = 50;

}
