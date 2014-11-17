package com.proptiger.columbus.thandlers;

public class URLGenerationConstants {
    public static String Selector                           = "selector=";
    public static String SelectorGetLocalityNamesByCityName = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"label\"]}";
    public static String SelectorGetBuilderNamesByCityName  = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"name\"]}";

    public static String SelectorGetCityIdsByLocalityIds    = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[%s]}}]},\"fields\":[\"cityId\",\"suburb\",\"localityId\"],\"paging\":{\"start\":0,\"rows\":%s}}";

    public static String GenericURLPropertyForSale          = "%s/property-sale";
    public static String GenericURLPropertyForResale        = "%s/resale-property";

    public static String GenericUrlProjectsIn               = "%s-real-estate";
    public static String GenericUrlLuxuryProjectsIn         = "%s/luxury-projects";
    public static String GenericUrlAffordableProjectsIn     = "%s/affordable-flats";
    public static String GenericUrlUpcomingProjectsIn       = "%s/upcoming-flats-for-sale";
    public static String GenericUrlNewProjectsIn            = "%s-real-estate/filters?projectStatus=launch";
    public static String GenericUrlPreLaunchProjectsIn      = "%s-real-estate/filters?projectStatus=not launched,pre launch";
    public static String GenericUrlUnderConstProjectsIn     = "%s/under-construction-property";
    public static String GenericUrlReadyToMoveProjectsIn    = "%s/ready-to-move-property";
}