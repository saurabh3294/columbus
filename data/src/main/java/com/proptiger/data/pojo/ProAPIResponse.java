package com.proptiger.data.pojo;

import java.io.Serializable;

/**
 * @author Rajeev Pandey
 * 
 */
public interface ProAPIResponse extends Serializable {
    public String getStatusCode();

    public void setStatusCode(String code);
}
