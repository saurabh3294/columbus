/**
 * 
 */
package com.proptiger.data.util;

/**
 * 
 *
 */
public enum PageType {
    /*
     * URL should match the specification, then only the regex will work
     * correctly
     */
    CITY_LISTING("^([\\w]+)-real-estate$", new String[] { "cityName" }), SUBURB_LISTING(
            "^(?:[\\w]+)/(?:[\\-\\w]+)-(1\\d{4})(?:/[\\d]bhk)?(?:/\\d+-\\d+-lacs)?$", new String[] { "suburbId" }), LOCALITY_LISTING(
            "^(?:[\\w]+)/(?:[\\-\\w]+)-(5\\d{4})(?:/[\\d]bhk)?(?:/\\d+-\\d+-lacs)?$", new String[] { "localityId" }), LOCALITY_OVERVIEW(
            "^(?:[\\-\\w]+)/(?:[\\-\\w]+)-(5\\d{4})/overview$", new String[] { "localityId" }), PROJECT(
            "^(?:[\\w]+)/(?:[\\-\\w]+)/(?:[\\-\\w]+)-([5-9]\\d{5})$", new String[] { "projectId" }), CITY_OVERVIEW(
            "^([\\w]+)-real-estate/overview$", new String[] { "cityName" }), BUILDER("^(?:[\\-\\w]+)-(1\\d{5})$",
            new String[] { "builderId" }), ALL_BUILDERS_IN_A_CITY("^([\\w]+)/all-builders$",
            new String[] { "cityName" }), CITY_BUILDER_PAGE("^([\\w]+)/(?:[\\-\\w]+)-(1\\d{5})$", new String[] {
            "cityName",
            "builderId" }), CITY_LISTING_BHK(
            "^([\\w]+)/(?:property|apartments-flats|villas|sites-plots)-(sale)/(?:[\\d]bhk)$",
            new String[] { "cityName" }), CITY_LISTING_BHK_BUDGET(
            "^([\\w]+)/(?:property|apartments-flats|villas|sites-plots)-(sale)/(?:[\\d]bhk/)?(?:\\d+-\\d+-lacs)$",
            new String[] { "cityName" }),DEFAULT("(.*)", new String[]{"URL"});

    private String   regex;
    private String[] URLDetailFields;

    private PageType(String regex, String[] URLDetailFields) {
        this.regex = regex;
        this.URLDetailFields = URLDetailFields;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String[] getURLDetailFields() {
        return URLDetailFields;
    }

    public void setURLDetailFields(String[] uRLDetailFields) {
        URLDetailFields = uRLDetailFields;
    }
}