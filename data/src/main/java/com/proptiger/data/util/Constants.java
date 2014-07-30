package com.proptiger.data.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.enums.ConstructionStatus;
import com.proptiger.data.model.user.portfolio.PortfolioListing.Source;

/**
 * Define all constants in this class, either make a group of constants related
 * to particular entity or put that on global scope.
 * 
 * @author Rajeev Pandey
 * 
 */
public class Constants {

    // Global constants start
    public static final String               LOGIN_INFO_OBJECT_NAME    = "_const_user_object_";
    public static final Integer              ADMIN_USER_ID             = 57594;
    public static final String               REQ_PARAMETER_FOR_USER_ID = "_user_id";
    public static final String               JSESSIONID                = "JSESSIONID";
    public static final String               PHPSESSID_KEY             = "PHPSESSID";
    public static final int                  DEFAULT_NO_OF_ROWS        = 10;
    public static final String               USERNAME                  = "username";
    public static final List<Source>         SOURCETYPE_LIST           = Arrays.asList(Source.portfolio, Source.backend);
    public static final int                  LIMIT_OF_COMPOSITE_APIs   = 15;

    public static final String               APPLICATION_NAME_HEADER   = "applicationType";
    /**
     * Put all solr Dynamic Field generated in this class.
     */
    public static final Map<String, Integer> solrDynamicFields;
    static {
        solrDynamicFields = new HashMap<String, Integer>();
        solrDynamicFields.put("geoDistance", 1);
    }

    // Global constants end

    public static class AmenityName {
        public static final String AIRPORT     = "airport";
        public static final String SCHOOL      = "school";
        public static final String BANK        = "bank";
        public static final String ATM         = "atm";
        public static final String RESTAURANT  = "restaurant";
        public static final String GAS_STATION = "gas_station";
        public static final String HOSPITAL    = "hospital";
    }

    public static class SubscriptionType {
        public static final String FORUM                    = "forum";
        public static final String REVIEW                   = "review";
        public static final String PROJECT_UPDATES          = "projectupdates";
        public static final String DISCUSSIONS_REVIEWS_NEWS = "discussionsreviewsnews";
    }

    public static class ForumUserComments {
        public static final boolean FalseReply = false;
        public static final boolean TrueReply  = true;
    }

    /**
     * Locality review related constants
     * 
     * @author Rajeev Pandey
     * 
     */
    public static class LocalityReview {
        public static final String COMMENT_TIME          = "commentTime";
        public static final String REVIEW_LABEL          = "reviewLabel";
        public static final String REVIEW                = "review";
        public static final String REVIEWS               = "reviews";
        public static final String TOTAL_REVIEWS         = "totalReviews";
        public static final String TOTAL_USERS_BY_RATING = "totalUsersByRating";
    }

    /**
     * Locality rating related constants
     * 
     * @author Rajeev Pandey
     * 
     */
    public static class LocalityRating {
        public static final String TOTAL_RATINGS   = "totalRatings";
        public static final String AVERAGE_RATINGS = "averageRatings";
    }

    /**
     * Put all cache related constants in this class
     * 
     * @author Rajeev Pandey
     * 
     */
    public static class CacheName {
        public static final String CACHE                                 = "cache";
        public static final String LOCALITY_REVIEW                       = "loc-review";
        public static final String LOCALITY_REVIEW_CUSTOM_FIELDS         = "loc-review-custom";
        public static final String LOCALITY_REVIEW_RATING                = "loc-review-rating";
        public static final String LOCALITY_REVIEW_COUNT                 = "loc-review-count";

        public static final String LOCALITY                              = "loc";

        public static final String LOCALITY_RATING                       = "loc-rating";
        public static final String LOCALITY_RATING_AVG_BY_CATEGORY       = "loc-rating-avg-ctg";
        public static final String LOCALITY_RATING_USERS_COUNT_BY_RATING = "loc-rating-user-cnt";
        public static final String LOCALITY_RATING_USERS                 = "loc-rating-user";

        public static final String AGENT                                 = "agent";
        public static final String AGENTS_FOR_PROJECT                    = "agents-for-project";

        public static final String LOCALITY_AMENITY                      = "loc-amenity";

        public static final String PROPERTY                              = "property";

        public static final String PROJECT                               = "project";
        public static final String PROJECT_SPECIFICATION                 = "project-specification";
        public static final String PROJECT_CMS_AMENITY                   = "project-cms-amenity";
        public static final String PROJECT_VIDEOS                        = "project-videos";
        public static final String PROJECT_BANKS                         = "project-banks";
        public static final String PROJECT_STATUS_COUNT_ON_LOCALITY      = "project-status-count-on-locality";
        public static final String PROJECT_DISCUSSION                    = "project-discussion";
        public static final String PROJECT_DETAILS                       = "project-details";

        public static final String BUILDER                               = "builder";

        public static final String SIMILAR_PROPERTY                      = "similar-property";

        public static final String SEO_FOOTER                            = "seo-footer";
        public static final String SEO_TEMPLATE                          = "seo-template";

        public static final String REDIRECT_URL_MAP                      = "redirect-url-map";

        public static final String PORTFOLIO_LISTING                     = "portfolio-listing";
        public static final String PROPERTY_INACTIVE                     = "property-inactive";
        public static final String PROJECT_INACTIVE                      = "project-inactive";
        public static final String LOCALITY_INACTIVE                     = "locality-inactive";
        public static final String SUBURB_INACTIVE                       = "suburb-inactive";

    }

    public static final List<ConstructionStatus> CONSTRUCTION_STATUS_FOR_PRIMARY = Arrays.asList(
                                                                                         ConstructionStatus.NotLaunched,
                                                                                         ConstructionStatus.PreLaunch,
                                                                                         ConstructionStatus.Launch,
                                                                                         ConstructionStatus.UnderConstruction);

    public static final List<ConstructionStatus> CONSTRUCTION_STATUS_FOR_RESALE  = Arrays.asList(
                                                                                         ConstructionStatus.ReadyForPossession,
                                                                                         ConstructionStatus.Occupied);
    public static final String                   USER_AGENT                      = "user-agent";

    public static class SeoPageTemplate {
        /* public static final String CITY_OVERVIEW_PAGE = "\"title\":"; */
    }

    /**
     * Put security related constants in this inner class
     * 
     * @author Rajeev Pandey
     * 
     */
    public static final class Security {
        public static final String COOKIE_NAME_JSESSIONID      = "JSESSIONID";

        public static final String LOGOUT_URL                  = "/app/v1/logout";

        public static final String PASSWORD_PARAMETER_NAME     = "password";

        public static final String USERNAME_PARAMETER_NAME     = "username";

        public static final String LOGIN_URL                   = "/app/v1/login";

        public static final String API_SECRET_KEY              = "api.sec.key";

        public static final String USER_API_REGEX              = ".*/user/.*";

        public static final String AUTH_API_REGEX              = ".*/auth/.*";

        public static final String REMEMBER_ME_PARAMETER       = "rememberme";

        public static final String REMEMBER_ME_COOKIE          = "api.rememberme.cookie";

        public static final int    REMEMBER_ME_COOKIE_VALIDITY = 60 * 60 * 24 * 365;

        // cookie valid for 7 days, make sure to set session max inactive
        // interval to same value.
        public static final int    JSESSION_COOKIE_MAX_AGE     = 60 * 60 * 24 * 7;

        public static final String SERVER_CURR_TIME            = "server-time";
        public static final String SECRET_HASH_HEADER_KEY      = "_shhkey";
        public static final String API_SECRET_KEYWORD          = "_askey";
        public static final int    ACCESS_TOKEN_VALIDITY_DAYS    = 7;
        public static final String REGISTER_URL                = "app/v1/register";
        public static final String ACCESS_TOKEN                = "access_token";
    }
}
