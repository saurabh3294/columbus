package com.proptiger.columbus.thandlers;

public class URLGenerationConstants {
    public static String Selector                           = "selector=";

    public static String SelectorGetAllCities               = "{\"fields\":[\"label\", \"id\",\"centerLatitude\",\"centerLongitude\"],\"paging\":{\"start\":0,\"rows\":%d}}";

    public static String SelectorGetLocalityNamesByCityName = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"label\"]}";
    public static String SelectorGetBuilderNamesByCityName  = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"name\",\"url\"]}";

    public static String SelectorGetBuilderIdsAsFacet       = "{\"paging\":{\"rows\":0},\"filters\":{\"and\":[{\"equal\":{\"%s\":%s}}]}}&facets=%s";

    public static String SelectorGetCityIdsByLocalityIds    = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[%s]}}]},\"fields\":[\"cityId\",\"suburb\",\"localityId\"],\"paging\":{\"start\":0,\"rows\":%s}}";

    public static String GenericURLPropertyForSale          = "%s/property-sale";
    public static String GenericURLPropertyForResale        = "%s/resale-property";

    public static String GenericUrlProjectsIn               = "projects-in-%s";
    public static String GenericUrlLuxuryProjectsIn         = "%s/luxury-projects";
    public static String GenericUrlAffordableProjectsIn     = "%s/affordable-flats";
    public static String GenericUrlUpcomingProjectsIn       = "%s/upcoming-flats-for-sale";
    public static String GenericUrlNewProjectsIn            = "projects-in-%s/filters?projectStatus=launch";
    public static String GenericUrlPreLaunchProjectsIn      = "projects-in-%s/filters?projectStatus=not launched,pre launch";
    public static String GenericUrlUnderConstProjectsIn     = "%s/under-construction-property";
    public static String GenericUrlReadyToMoveProjectsIn    = "%s/ready-to-move-property";

    public static String GenericUrlCity                     = "projects-in-%s/";
}