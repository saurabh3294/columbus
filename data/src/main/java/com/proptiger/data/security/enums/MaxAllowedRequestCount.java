package com.proptiger.data.security.enums;

public enum MaxAllowedRequestCount {
    LIMIT_PER_MINUTE(60, 200), LIMIT_PER_DAY(86400, 5000);

    private String  label;
    private Integer timeFrame;
    private Integer allowedRequestCount;

    private MaxAllowedRequestCount(Integer timeFrame, Integer allowedRequestCount) {
        this.timeFrame = timeFrame;
        this.allowedRequestCount = allowedRequestCount;
        this.label = this.name();
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