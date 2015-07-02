package com.proptiger.columbus.thandlers;

import org.apache.commons.lang.StringUtils;

import com.proptiger.core.model.cms.Locality;

public class URLGenerationConstants {
    public static final String Selector                               = "selector=";

    public static String       SelectorGetAllCities                   = "{\"fields\":[\"label\", \"id\",\"centerLatitude\",\"centerLongitude\"],\"paging\":{\"start\":0,\"rows\":%d}}";

    public static final String SELECTOR_GET_LOCALITYNAMES_BY_CITYNAME = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"label\"]}";
    public static final String SELECTOR_GETBUILDERNAMES_BY_CITYNAME   = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"id\",\"name\",\"url\",\"projectCount\"]}";

    public static final String SELECTOR_GETBUILDERIDSASFACET          = "{\"paging\":{\"rows\":0},\"filters\":{\"and\":[{\"equal\":{\"%s\":%s}}]}}&facets=%s";

    public static final String SELECTOR_GETCITYIDS_BY_LOCALITYIDS     = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[%s]}}]},\"fields\":[\"cityId\",\"suburb\",\"localityId\"],\"paging\":{\"start\":0,\"rows\":%s}}";

    public static final String GENERIC_URL_CITY                       = "projects-in-%s/";

    public static final String urlFilterLocality                      = "locality=%s";

    public static String       cityIdFilterFormat                     = "{\"equal\":{\"cityId\":%s}}";
    public static String       localityIdFilterFormat                 = "{\"equal\":{\"localityId\":%s}}";
    public static String       builderIdFilterFormat                  = "{\"equal\":{\"builderId\":%s}}";

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