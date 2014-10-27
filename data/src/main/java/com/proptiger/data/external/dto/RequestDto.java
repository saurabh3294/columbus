/**
 * 
 */
package com.proptiger.data.external.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;

/**
 * @author mandeep
 * 
 */
@JsonInclude(Include.NON_EMPTY)
public class RequestDto extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            URL;
    private Object            params;
    private Object            headers;

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }
}
