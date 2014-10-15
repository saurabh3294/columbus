package com.proptiger.app.typeahead.thandlers;

public class URLGenerationConstants {

    public static String ServiceSelectorGetLocalityByCity = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":%s}}]}}";

    public static String GenericURLPropertyForSale        = "%s/property-sale";
    public static String GenericURLPropertyForResale      = "%s/resale-property";

    public static String GenericUrlProjectsIn             = "%s-real-estate";
    public static String GenericUrlLuxuryProjectsIn       = "%s/luxury-projects";
    public static String GenericUrlAffordableProjectsIn   = "%s/affordable-flats";
    public static String GenericUrlUpcomingProjectsIn     = "%s/upcoming-flats-for-sale";
    public static String GenericUrlNewProjectsIn          = "%s-real-estate/filters?projectStatus=launch";
    public static String GenericUrlPreLaunchProjectsIn    = "%s-real-estate/filters?projectStatus=not launched,pre launch";
    public static String GenericUrlUnderConstProjectsIn   = "%s/under-construction-property";
    public static String GenericUrlReadyToMoveProjectsIn  = "%s/ready-to-move-property";

}
