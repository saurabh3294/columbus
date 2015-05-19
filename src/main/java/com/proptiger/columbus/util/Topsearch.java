package com.proptiger.columbus.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author Manmohan
 */
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Topsearch {

    private String                     entityId;

    private String                     entityType;

    private List<TopsearchObjectField> builder;

    private List<TopsearchObjectField> project;

    private List<TopsearchObjectField> locality;

    private List<TopsearchObjectField> suburb;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType.toLowerCase();
    }

    public List<TopsearchObjectField> getSuburb() {
        return suburb;
    }

    public void setSuburb(List<TopsearchObjectField> suburb) {
        this.suburb = suburb;
    }

    public List<TopsearchObjectField> getLocality() {
        return locality;
    }

    public void setLocality(List<TopsearchObjectField> locality) {
        this.locality = locality;
    }

    public List<TopsearchObjectField> getBuilder() {
        return builder;
    }

    public void setBuilder(List<TopsearchObjectField> builder) {
        this.builder = builder;
    }

    public List<TopsearchObjectField> getProject() {
        return project;
    }

    public void setProject(List<TopsearchObjectField> project) {
        this.project = project;
    }

    @Override
    public String toString() {
        String str = entityId + ":";
        for (TopsearchObjectField tmp : suburb) {
            str += tmp.toString();
        }
        return str;
    }
}
