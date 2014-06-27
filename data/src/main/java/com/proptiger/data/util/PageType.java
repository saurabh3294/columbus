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
    HOME_PAGE("^$", new String[]{}),
    CITY_URLS("^([\\w]+)(?:(/(apartments-flats-sale|property-sale|house-sale|villas-sale|sites-plots-sale)(/[\\d]{1,2}bhk)?(/\\d+-\\d+-lacs)?)|(-real-estate(/overview)?)|/(resale-property|ready-to-move-property|under-construction-properties|upcoming-properties|upcoming-projects|luxury-projects|low-budget-flats|affordable-flats|new-projects-for-sale|upcoming-flats-for-sale|ready-to-move-flats|resale-apartments|new-apartments-for-sale|all-builders))$", new String[] { "cityName" }),
    LOCALITY_SUBURB_LISTING_SEO("^(?:[\\w]+)/(society-flats-in|resale-property-in|ready-to-move-property-in|under-construction-properties-in|upcoming-properties-in|upcoming-Projects-in|luxury-projects-in|low-budget-flats-in|affordable-flats-in|new-projects-for-sale-in|residential-property-for-sale-in|upcoming-flats-for-sale-in|ready-to-move-flats-in|resale-apartments-in|new-apartments-for-sale-in)-(?:[\\-\\w]+)-(\\d{5,5})$", new String[] { "propertyType", "localityId"}),
    LOCALITY_SUBURB_LISTING("^(?:[\\w]+)/(apartments-flats-sale|property-sale|house-sale|villas-sale|sites-plots-sale)-(?:[\\-\\w]+)-(\\d{5,5})(/[\\d]{1,2}bhk)?(/\\d+-\\d+-lacs)?$", new String[] { "propertyType", "localityId", "bedroomString", "priceString" }), 
    LOCALITY_SUBURB_OVERVIEW("^(?:[\\-\\w]+)/(?:[\\-\\w]+)-(\\d{5,5})/overview$", new String[] { "localityId" }), 
    PROJECT_URLS("^(?:.*)([5-9]\\d{5})$", new String[] { "projectId" }),
    PROPERTY_URLS("^(?:.*)([5-9]\\d{6,6})/(?:[\\d]{0,2}bhk)$", new String[] {"propertyId"}),
    BUILDER_URLS_SEO("^(apartments-by-|villas-by-|plots-by-|resale-property-by-|ready-to-move-property-by-|ready-to-move-apartments-by-|resale-Flats-by-|under-construction-Property-by-|upcoming-Property-by-|new-launch-project-by-|completed-Property-by-|upcoming-project-by-|new-flats-by|new-project-by-|luxury-project-by-|low-budget-flats-by-|affordable-flats-by-)(?:[\\-\\w]+)-(1\\d{5})$", new String[] { "propertyType", "builderId"}),
    BUILDER_URLS("^([\\w]+/)?(?:[\\-\\w]+)-(1\\d{5})(/[\\d]{1,2}bhk)?$", new String[] { "cityName", "builderId", "bedroomString" }), 
    STATIC_URLS("^(?:all-builders|all-projects|news|contactus|aboutus|nri|vaastu|homeloan|documents|faqs|builderpartner|ourservices|management-team|proptiger-media|privacy-policy|user-agreement|careers|sitemap.php|testimonials|emi|disclaimer|404|server-error|sites-plots-sale|projects-in-india|apartments-in-india|flats-in-india|properties-in-india|new-launch-projects-in-india|ongoing-projects-in-india|completed-projects-in-india|new-projects-for-sale-in-india|upcoming-flats-for-sale-in-india|ready-to-move-flats-in-india|resale-apartments-in-india|new-apartments-for-sale-in-india|((apartments-flats-sale|property-sale|villas-sale)(/[\\d]{1,2}bhk)?))$", new String[]{}),
    PORTFOLIO_URLS("^portfolio/(?:index|dashboard|enquiredproperty|savedsearches|recentlyviewed|myfavorites|property/([\\d]+))$", new String[]{"portfolioId"}),
    InvalidUrl("(.*)", new String[]{"URL"});

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
