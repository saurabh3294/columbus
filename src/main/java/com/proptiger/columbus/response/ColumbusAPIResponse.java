package com.proptiger.columbus.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService.ApiVersion;

public class ColumbusAPIResponse extends APIResponse {

    private static final long serialVersionUID = -2779854202059782696L;

    @JsonInclude(Include.NON_NULL)
    Boolean                   redirectable;

    public ColumbusAPIResponse(Object data, Long totalCount, ApiVersion version, Boolean forcedDirectable) {
        super(data, totalCount, version);
        this.redirectable = forcedDirectable;
    }

    public Boolean getRedirectable() {
        return redirectable;
    }

    public void setRedirectable(Boolean redirectable) {
        this.redirectable = redirectable;
    }
}
