package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class Property {
    private String id;
    
    @Field(value="UNIT_NAME")
    @JsonProperty(value="unit_name")
    private String unitName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
