package com.proptiger.columbus.model;

import java.util.Date;
import java.util.List;


import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.AbstractTypeahead;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class PropguideDocument extends AbstractTypeahead {

    private static final long serialVersionUID = 1L;

    @Field(value = "PGD_TYPE")
    private String            pgdType;

    @Field(value = "PGD_ID")
    private Integer           pgdId;

    @Field(value = "PGD_TITLE")
    private String            pgdTitle;

    @Field(value = "PGD_EXCERPT")
    private String            pgdExcerpt;

    @JsonIgnore
    @Field(value = "PGD_CONTENT")
    private String            pgdContent;

    @Field(value = "PGD_POST_TYPE")
    private String            pgdPostType;

    @Field(value = "PGD_POST_NAME")
    private String            pgdPostName;

    @Field(value = "PGD_GUID")
    private String            pgdGuid;

    @Field(value = "PGD_DATE")
    private Date              pgdDate;

    
    @Field(value = "PGD_TAGS")
    private List<String>      pgdTags;

    
    @Field(value = "PGD_CATEGORY")
    private List<String>      pgdCategory;

    
    @JsonIgnore
    @Field(value = "PGD_ROOT_CATEGORY_ID")
    private List<Integer>     pgdRootCategoryId;

    public String getPgdType() {
        return pgdType;
    }

    public void setPgdType(String pgdType) {
        this.pgdType = pgdType;
    }

    public Integer getPgdId() {
        return pgdId;
    }

    public void setPgdId(Integer pgdId) {
        this.pgdId = pgdId;
    }

    public String getPgdTitle() {
        return pgdTitle;
    }

    public void setPgdTitle(String pgdTitle) {
        this.pgdTitle = pgdTitle;
    }

    public String getPgdExcerpt() {
        return pgdExcerpt;
    }

    public void setPgdExcerpt(String pgdExcerpt) {
        this.pgdExcerpt = pgdExcerpt;
    }

    public String getPgdContent() {
        return pgdContent;
    }

    public void setPgdContent(String pgdContent) {
        this.pgdContent = pgdContent;
    }

    public String getPgdPostType() {
        return pgdPostType;
    }

    public void setPgdPostType(String pgdPostType) {
        this.pgdPostType = pgdPostType;
    }

    public String getPgdPostName() {
        return pgdPostName;
    }

    public void setPgdPostName(String pgdPostName) {
        this.pgdPostName = pgdPostName;
    }

    public String getPgdGuid() {
        return pgdGuid;
    }

    public void setPgdGuid(String pgdGuid) {
        this.pgdGuid = pgdGuid;
    }

    public Date getPgdDate() {
        return pgdDate;
    }

    public void setPgdDate(Date pgdDate) {
        this.pgdDate = pgdDate;
    }

    public List<String> getPgdTags() {
        return pgdTags;
    }

    public void setPgdTags(List<String> pgdTags) {
        this.pgdTags = pgdTags;
    }

    public List<String> getPgdCategory() {
        return pgdCategory;
    }

    public void setPgdCategory(List<String> pgdCategory) {
        this.pgdCategory = pgdCategory;
    }

    public List<Integer> getPgdRootCategoryId() {
        return pgdRootCategoryId;
    }

    public void setPgdRootCategoryId(List<Integer> pgdRootCategoryId) {
        this.pgdRootCategoryId = pgdRootCategoryId;
    }

    @Override
    public String toString() {
        return (super.getId() + ":" + pgdType + ":" + pgdTitle + ":" + super.getScore());
    }
}