package com.proptiger.columbus.thandlers;

import org.apache.commons.lang.StringUtils;

import com.proptiger.core.model.cms.Locality;

public class URLGenerationConstants {
    public static String Selector                           = "selector=";

    public static String SelectorGetAllCities               = "{\"fields\":[\"label\", \"id\",\"centerLatitude\",\"centerLongitude\"],\"paging\":{\"start\":0,\"rows\":%d}}";

    public static String SelectorGetLocalityNamesByCityName = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"label\"]}";
    public static String SelectorGetBuilderNamesByCityName  = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"name\",\"url\"]}";

    public static String SelectorGetBuilderIdsAsFacet       = "{\"paging\":{\"rows\":0},\"filters\":{\"and\":[{\"equal\":{\"%s\":%s}}]}}&facets=%s";

    public static String SelectorGetCityIdsByLocalityIds    = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[%s]}}]},\"fields\":[\"cityId\",\"suburb\",\"localityId\"],\"paging\":{\"start\":0,\"rows\":%s}}";

    public static String GenericUrlCity                     = "projects-in-%s/";

    public static String urlFilterLocality                  = "locality=%s";

    public static String cityIdFilterFormat                 = "{\"equal\":{\"cityId\":%s}}";
    public static String localityIdFilterFormat             = "{\"equal\":{\"localityId\":%s}}";
    public static String builderIdFilterFormat              = "{\"equal\":{\"buidlerId\":%s}}";

    public static String addLocalityFilterToRedirectURL(String redirectUrl, Locality locality) {
        if (locality == null) {
            return redirectUrl;
        }
        String localityLabel = locality.getLabel();
        if (StringUtils.contains(redirectUrl, "?")) {
            redirectUrl += ("&" + String.format(urlFilterLocality, localityLabel));
        }
        else {
            redirectUrl += ("?" + String.format(urlFilterLocality, localityLabel));
        }
        return redirectUrl;
    }

    public static String getCityLocalityFilter(int cityId, Locality locality) {
        if (locality == null) {
            return String.format(cityIdFilterFormat, cityId);
        }
        return String.format(localityIdFilterFormat, locality.getLocalityId());
    }
}