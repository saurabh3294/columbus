package com.proptiger.data.util;

/**
 * Constants for the keys defined in application.properties file
 * 
 * @author Rajeev Pandey
 * 
 */
public final class PropertyKeys {
    public static final String POPULAR_LOCALITY_THRESHOLD_COUNT           = "popular.locality.threshold.count";
    public static final String RADIUS_THREE_FOR_TOP_LOCALITY              = "radius.three.for.top.locality";
    public static final String RADIUS_TWO_FOR_TOP_LOCALITY                = "radius.two.for.top.locality";
    public static final String RADIUS_ONE_FOR_TOP_LOCALITY                = "radius.one.for.top.locality";
    public static final String MINIMUM_RATING_FOR_TOP_LOCALITY            = "minimum.rating.for.top.locality";

    public static final String DATABASE_DRIVER                            = "db.driver";
    public static final String DATABASE_PASSWORD                          = "db.password";
    public static final String DATABASE_URL                               = "db.url";
    public static final String DATABASE_USERNAME                          = "db.username";

    public static final String HIBERNATE_DIALECT                          = "hibernate.dialect";
    public static final String HIBERNATE_SHOW_SQL                         = "hibernate.show_sql";
    public static final String ENTITYMANAGER_PACKAGES_TO_SCAN             = "entitymanager.packages.to.scan";

    public static final String CMS_USERNAME                               = "cms_username";
    public static final String CMS_PASSWORD                               = "cms_password";
    public static final String CMS_BASE_URL                               = "cms_base_url";

    public static final String SOLR_SERVER_DEFAULT_URL                    = "solr.server.url";
    public static final String SOLR_SERVER_B2B_URL                        = "solr.server.url.b2b";

    public static final String IMAGE_TEMP_PATH                            = "imageTempPath";
    public static final String BUCKET                                     = "bucket";
    public static final String ENDPOINTS                                  = "endpoints";
    public static final String SECRET_ACCESS_KEY                          = "secretAccessKey";
    public static final String ACCESS_KEY_ID                              = "accessKeyId";
    public static final String METAINFO_PACKAGE_TO_SCAN                   = "metainfo.package.to.scan";
    public static final String LEAD_PAGE_URL                              = "lead.page.url";
    public static final String MAIL_HOME_LOAN_INTERNAL_RECIEPIENT         = "mail.home.loan.internal.reciepient";
    public static final String MAIL_INTERESTED_TO_SELL_RECIEPIENT         = "mail.interested.to.sell.reciepient";
    public static final String MAIL_UNMATCHED_PROJECT_INTERNAL_RECIEPIENT = "mail.unmatched-project.internal.reciepient";
    public static final String MEMCACHE_URL_PORT                          = "memcache.url.port";
    public static final String MAIL_FROM_NOREPLY                          = "mail.from.noreply";
    public static final String WORDPRESS_DATABASE_URL                     = "wordpress.db.url";
    public static final String WORDPRESS_NEWS_DATABASE_URL                = "wordpress_news.db.url";

    public static final String REDIS_HOST                                 = "redis.hostName";
    public static final String REDIS_PORT                                 = "redis.port";
    public static final String REDIS_USE_POOL                             = "redis.usePool";
    public static final String REDIS_DEFAULT_EXPIRATION_TIME              = "redis.defaultExpirationTime";

    public static final String SESSION_MAX_INTERACTIVE_INTERVAL           = "session.max.inactive.interval";

    public static final String AVATAR_IMAGE_URL                           = "avatar.image.url";
    public static final String ACCESS_LOG_INTERNAL_DS_SIZE_THRESHOLD      = "assesslog.internal.ds.size.threshold";
    public static final String ILLEGAL_API_ACCESS_THRESHOLD_COUNT         = "illegal.api.access.threshold.count";
    public static final String ENABLE_BOT_PREVENTAION                     = "enable.bot.prevention";
    public static final String MAIL_FROM_SUPPORT                          = "mail.from.support";
}
