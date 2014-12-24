package com.proptiger.data.internal.dto;


/**
 * @author Rajeev Pandey
 *
 */
public class GenericKeyValue{
    private Integer key;
    private String value;
    public GenericKeyValue(Integer key, String val){
       this.key = key;
       this.value = val;
    }
    public Integer getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    
}
