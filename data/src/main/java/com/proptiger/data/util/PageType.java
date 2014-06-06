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
    CITY_LISTING("^([\\w]+)(?:(/(apartments-flats-sale|property-sale|house-sale|villas-sale|sites-plots-sale)(/[\\d]bhk)?(/\\d+-\\d+-lacs)?)|(-real-estate(/overview)?))$", new String[] { "cityName" }),
    LOCALITY_SUBURB_LISTING("^(?:[\\w]+)/(apartments-flats-sale|property-sale|house-sale|villas-sale|sites-plots-sale)-(?:[\\w][\\-\\w]+[\\w])-(\\d{5,5})(/[\\d]bhk)?(/\\d+-\\d+-lacs)?$", new String[] { "propertyType", "localityId", "beds", "price" }), 
    LOCALITY_SUBURB_OVERVIEW("^(?:[\\w][\\-\\w]+[\\w])/(?:[\\w][\\-\\w]+[\\w])-(\\d{5,5})/overview$", new String[] { "localityId" }), 
    PROJECT("^(?:.*)([5-9]\\d{5})(?:.*)$", new String[] { "projectId" }),
    PROPERTY("^(?:.*)([5-9]\\d{6,6})(?:.*)$", new String[] {"propertyId"}),
    BUILDER("^(?:[\\-\\w]+)-(1\\d{5})$", new String[] { "builderId" }), 
    ALL_BUILDERS_IN_A_CITY("^([\\w]+)/all-builders$", new String[] { "cityName" }), 
    CITY_BUILDER_PAGE("^([\\w]+)/(?:[\\-\\w]+)-(1\\d{5})$", new String[] {"cityName", "builderId" }), 
    DEFAULT("(.*)", new String[]{"URL"});

    private String   regex;
    private String[] URLDetailFields;

    private PageType(String regex, String[] URLDetailFields) {
        this.regex = regex;
        this.URLDetailFields = URLDetailFields;
        //String var = "^(?:[\\w]+)/(apartments-flats-sale|property-sale|house-sale|villas-sale|sites-plots-sale)-(?:[\\w][\\-\\w]+[\\w])-(\\d{5,5})(/[\\d]bhk)?(/\\d+-\\d+-lacs)?$";
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
