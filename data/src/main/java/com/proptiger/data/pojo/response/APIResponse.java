package com.proptiger.data.pojo.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.constants.ResponseCodes;

/**
 * This class represents a response that will handle error and success scenario
 * 
 * @author Rajeev Pandey
 * 
 */
@JsonInclude(Include.NON_EMPTY)
public class APIResponse implements Serializable {

    private static final long serialVersionUID = -7809000164180146201L;
    private Long              totalCount;
    private String            statusCode;

    @JsonInclude(Include.ALWAYS)
    private Object            data;

    private APIError          error;

    public APIResponse() {
        super();
        this.statusCode = ResponseCodes.SUCCESS;
    }

    public APIResponse(Object data) {
        super();
        this.statusCode = ResponseCodes.SUCCESS;
        this.data = data;
    }

    public APIResponse(Object data, Long totalCount) {
        super();
        this.statusCode = ResponseCodes.SUCCESS;
        this.totalCount = totalCount;
        this.data = data;
    }

    public APIResponse(Object data, int totalCount) {
        super();
        this.statusCode = ResponseCodes.SUCCESS;
        this.totalCount = new Long(totalCount);
        this.data = data;
    }

    public APIResponse(String statusCode, String errorMessage) {
        super();
        this.statusCode = statusCode;
        this.error = new APIError(errorMessage);
    }

    public APIResponse(PaginatedResponse<?> paginatedResponse) {
        super();
        this.statusCode = ResponseCodes.SUCCESS;
        this.data = paginatedResponse.getResults();
        this.totalCount = paginatedResponse.getTotalCount();
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public APIResponse setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        // return this;
    }

    public Object getData() {
        return data;
    }

    public APIResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public APIError getError() {
        return error;
    }

    public APIResponse setError(APIError error) {
        this.error = error;
        return this;
    }

    public APIResponse setErrorMessage(String msg) {
        this.error = new APIError(msg);
        return this;
    }

    static class APIError {
        private String msg;

        public APIError(String msg) {
            super();
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "{msg=" + msg + "}";
        }
    }
}
