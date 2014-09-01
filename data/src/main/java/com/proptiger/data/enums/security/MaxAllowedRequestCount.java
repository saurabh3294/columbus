package com.proptiger.data.enums.security;

public enum MaxAllowedRequestCount {
    LIMIT_PER_MINUTE(60, 200, 25), LIMIT_PER_DAY(86400, 5000, 500);

    private String  label;
    private Integer timeFrame;
    private Integer allowedRequestCount;
    private Integer allowedPostRequestCount;

    private MaxAllowedRequestCount(Integer timeFrame, Integer allowedRequestCount, Integer allowedPostRequestCount) {
        this.timeFrame = timeFrame;
        this.allowedRequestCount = allowedRequestCount;
        this.label = this.name();
        this.allowedPostRequestCount = allowedPostRequestCount;
    }

    public Integer getAllowedPostRequestCount() {
        return allowedPostRequestCount;
    }

    public String getLabel() {
        return label;
    }

    public Integer getTimeFrame() {
        return timeFrame;
    }

    public Integer getAllowedRequestCount() {
        return allowedRequestCount;
    }
}