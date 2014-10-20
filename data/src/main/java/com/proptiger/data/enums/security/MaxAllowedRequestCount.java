package com.proptiger.data.enums.security;

import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * Enum that hold value for allowable request count per request method in a time
 * frame from a user. Values will be picked from application.properties file, if
 * values not defined in property file then default values will be used as
 * defined in enum definition.
 * 
 * This enum should be used only after context load other wise
 * default values will be returned.
 *
 * @author Rajeev Pandey
 */

public enum MaxAllowedRequestCount {
    REQUEST_PER_MIN(60, 200, 5), REQUEST_PER_DAY(86400, 5000, 50);

    private Integer timeFrame;
    private Integer allowedAllRequestCount;
    private Integer allowedPostRequestCount;

    private MaxAllowedRequestCount(Integer timeFrame, Integer allowedRequestCount, Integer allowedPostRequestCount) {
        this.timeFrame = timeFrame;
        this.allowedAllRequestCount = allowedRequestCount;
        this.allowedPostRequestCount = allowedPostRequestCount;
    }

    public Integer getAllowedPostRequestCount() {
        Integer val = getValue(PropertyKeys.REQ_POST_COUNT);
        if (val != null) {
            return val;
        }
        return allowedPostRequestCount;
    }

    public Integer getTimeFrame() {
        return timeFrame;
    }

    public Integer getAllowedAllRequestCount() {
        Integer val = getValue(PropertyKeys.REQ_ALL_COUNT);
        if (val != null) {
            return val;
        }
        return allowedAllRequestCount;
    }

    private Integer getValue(String key) {
        return PropertyReader.getRequiredPropertyAsType(this.name() + "." + key, Integer.class);
    }
}