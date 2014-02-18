package com.proptiger.data.model.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information regarding a data model object.
 * 
 * @author Rajeev Pandey
 * 
 */
public class ResourceModelMeta {
    private String              name;

    private List<FieldMetaData> fieldMeta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldMetaData> getFieldMeta() {
        return fieldMeta;
    }

    public void setFieldMeta(List<FieldMetaData> fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    public void addFieldMeta(FieldMetaData fieldMeta) {
        if (this.fieldMeta == null) {
            this.fieldMeta = new ArrayList<FieldMetaData>();
        }
        this.fieldMeta.add(fieldMeta);
    }
}
