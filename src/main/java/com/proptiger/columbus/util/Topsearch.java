package com.proptiger.columbus.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.Typeahead;

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

    private List<Typeahead> builder;

    private List<Typeahead> project;

    private List<Typeahead> locality;

    private List<Typeahead> suburb;

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

    public List<Typeahead> getSuburb() {
        return suburb;
    }

    public void setSuburb(List<Typeahead> suburb) {
        this.suburb = suburb;
    }

    public List<Typeahead> getLocality() {
        return locality;
    }

    public void setLocality(List<Typeahead> locality) {
        this.locality = locality;
    }

    public List<Typeahead> getBuilder() {
        return builder;
    }

    public void setBuilder(List<Typeahead> builder) {
        this.builder = builder;
    }

    public List<Typeahead> getProject() {
        return project;
    }

    public void setProject(List<Typeahead> project) {
        this.project = project;
    }

    @Override
    public String toString() {
        String str = entityId + ":";
        for (Typeahead tmp : suburb) {
            str += tmp.toString();
        }
        return str;
    }
}
