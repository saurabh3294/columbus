package com.proptiger.columbus.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService.ApiVersion;

public class ColumbusAPIResponse extends APIResponse {

    private static final long serialVersionUID = -2779854202059782696L;

    @JsonInclude(Include.NON_NULL)
    Boolean                   redirectable;

    @JsonInclude(Include.NON_NULL)
    private Map<String, List<Map<Object, Long>>> facets;

    public ColumbusAPIResponse(Object data, Long totalCount, ApiVersion version, Boolean forcedDirectable) {
        super(data, totalCount, version);
        this.redirectable = forcedDirectable;
    }

    public ColumbusAPIResponse(Object data, Long totalCount){
        super(data, totalCount);
    }
    
    public Boolean getRedirectable() {
        return redirectable;
    }

    public void setRedirectable(Boolean redirectable) {
        this.redirectable = redirectable;
    }

    public Map<String, List<Map<Object, Long>>> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, List<Map<Object, Long>>> facets) {
        this.facets = facets;
    }
}
