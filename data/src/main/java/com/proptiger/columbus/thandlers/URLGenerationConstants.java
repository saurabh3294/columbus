package com.proptiger.columbus.thandlers;

import org.apache.commons.lang.StringUtils;

import com.proptiger.core.model.cms.Locality;

public class URLGenerationConstants {
    private URLGenerationConstants() {

    }

    public static final String SELECTOR                               = "selector=";

    public static final String SELECTOR_GET_ALL_CITIES                = "{\"fields\":[\"label\", \"id\",\"centerLatitude\",\"centerLongitude\"],\"paging\":{\"start\":0,\"rows\":%d}}";

    public static final String SELECTOR_GET_LOCALITYNAMES_BY_CITYNAME = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"label\"]}";
    public static final String SELECTOR_GET_BUILDERNAMES_BY_CITYNAME  = "{\"filters\":{\"and\":[{\"equal\":{\"cityLabel\":\"%s\"}}]},\"fields\":[\"id\",\"name\",\"url\",\"projectCount\"]}";

    public static final String SELECTOR_GET_BUILDERIDS_AS_FACET       = "{\"paging\":{\"rows\":0},\"filters\":{\"and\":[{\"equal\":{\"%s\":%s}}]}}&facets=%s";

    public static final String SELECTOR_GET_CITYIDS_BY_LOCALITYIDS    = "{\"filters\":{\"and\":[{\"equal\":{\"localityId\":[%s]}}]},\"fields\":[\"cityId\",\"suburb\",\"localityId\"],\"paging\":{\"start\":0,\"rows\":%s}}";

    public static final String GENERIC_URL_CITY                       = "projects-in-%s/";

    public static final String URL_FILTER_LOCALITY                    = "locality=%s";

    public static final String CITY_ID_FILTER_FORMAT                  = "{\"equal\":{\"cityId\":%s}}";
    public static final String LOCALITY_ID_FILTER_FORMAT              = "{\"equal\":{\"localityId\":%s}}";
    public static final String BUILDER_ID_FILTER_FORMAT               = "{\"equal\":{\"builderId\":%s}}";

    public static String addLocalityFilterToRedirectURL(String redirectUrl, Locality locality) {
        if (locality == null) {
            return redirectUrl;
        }
        String localityLabel = locality.getLabel();
        if (StringUtils.contains(redirectUrl, "?")) {
            redirectUrl += ("&" + String.format(URL_FILTER_LOCALITY, localityLabel));
        }
        else {
            redirectUrl += ("?" + String.format(URL_FILTER_LOCALITY, localityLabel));
        }
        return redirectUrl;
    }

    public static String getCityLocalityFilter(int cityId, Locality locality) {
        if (locality == null) {
            return String.format(CITY_ID_FILTER_FORMAT, cityId);
        }
        return String.format(LOCALITY_ID_FILTER_FORMAT, locality.getLocalityId());
    }
}