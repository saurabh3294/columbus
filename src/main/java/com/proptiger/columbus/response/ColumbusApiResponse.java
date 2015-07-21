package com.proptiger.columbus.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService.ApiVersion;

public class ColumbusApiResponse extends APIResponse {

    private static final long serialVersionUID = -2779854202059782696L;

    @JsonInclude(Include.NON_NULL)
    Boolean                   forcedDirectable;

    public ColumbusApiResponse(Object data, Long totalCount, ApiVersion version, Boolean forcedDirectable) {
        super(data, totalCount, version);
        this.forcedDirectable = forcedDirectable;
    }

    public Boolean getForcedDirectable() {
        return forcedDirectable;
    }

    public void setForcedDirectable(Boolean forcedDirectable) {
        this.forcedDirectable = true;
    }

}
