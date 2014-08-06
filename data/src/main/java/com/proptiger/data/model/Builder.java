/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.image.Image;

/**
 * @author mandeep
 * 
 */
@Entity
@Table(name = "RESI_BUILDER")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Builder extends BaseModel {
    private static final long serialVersionUID = 1369026324841504903L;

    @FieldMetaInfo(displayName = "Builder Id", description = "Builder Id")
    @Column(name = "BUILDER_ID")
    @Id
    @Field("BUILDER_ID")
    private int               id;

    @FieldMetaInfo(displayName = "Name", description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    @Field("BUILDER_NAME")
    private String            name;

    @FieldMetaInfo(displayName = "Image", description = "Builder Image URL")
    @Transient
    @Field("BUILDER_LOGO_IMAGE")
    private String            imageURL;

    @FieldMetaInfo(displayName = "Description", description = "Description")
    @Column(name = "DESCRIPTION")
    @Field("BUILDER_DESCRIPTION")
    private String            description;

    @Transient
    @Field("BUILDER_ESTABLISHED_DATE")
    private Date              establishedDate;

    @Transient
    private List<Project>     projects;

    @Column(name = "DISPLAY_ORDER")
    @Field("BUILDER_PRIORITY")
    private Integer           priority;

    @Field("BUILDER_URL")
    @Column(name = "URL")
    private String            url;

    @Transient
    private Map<String, Long> projectStatusCount;

    @Transient
    @Field("BUILDER_PROJECT_COUNT")
    private Integer           projectCount;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "Builder enquiry count", description = "Builder enquiry count")
    @Field(value = "BUIULDER_ENQUIRY_COUNT")
    private Integer           builderEnquiryCount;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "Builder view count", description = "Builder view count")
    @Field(value = "BUILDER_VIEW_COUNT")
    private Integer           builderViewCount;
    
    @Transient
    private Integer           avgCompletionTimeMonths;

    @Transient
    private Map<String, Integer> projectCountByCity;
 
    @Transient
    @Field(value = "BUILDER_WEBSITE")
    private String            builderWebsite;
    
    @Transient
    @Field(value = "BUILDER_ADDRESS")
    private String            builderAddress;
    
    @Transient
    @Field(value = "BUILDER_LOCALITY_COUNT")
    private Integer           builderLocalityCount;
    
    @Transient
    private List<Image>       images;
    
    @Transient
    @JsonIgnore
    @Field("BUILDER_CITIES")
    private List<String>      builderCities;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Field("BUILDER_LOGO_IMAGE")
    public void setImageURL(String imageUrl) {
        this.imageURL = Image.addImageHostUrl(imageUrl);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEstabilishedDate() {
        return establishedDate;
    }

    public void setEstabilishedDate(Date estabilishedDate) {
        this.establishedDate = estabilishedDate;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Map<String, Long> getProjectStatusCount() {
        return projectStatusCount;
    }

    public void setProjectStatusCount(Map<String, Long> projectStatusCount) {
        this.projectStatusCount = projectStatusCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getBuilderEnquiryCount() {
        return builderEnquiryCount;
    }

    public void setBuilderEnquiryCount(Integer builderEnquiryCount) {
        this.builderEnquiryCount = builderEnquiryCount;
    }

    public Integer getBuilderViewCount() {
        return builderViewCount;
    }

    public void setBuilderViewCount(Integer builderViewCount) {
        this.builderViewCount = builderViewCount;
    }

    public Integer getAvgCompletionTimeMonths() {
        return avgCompletionTimeMonths;
    }

    public void setAvgCompletionTimeMonths(Integer avgCompletionTimeMonths) {
        this.avgCompletionTimeMonths = avgCompletionTimeMonths;
    }

    public Map<String, Integer> getProjectCountByCity() {
        return projectCountByCity;
    }

    public void setProjectCountByCity(Map<String, Integer> projectCountByCity) {
        this.projectCountByCity = projectCountByCity;
    }

    public String getBuilderWebsite() {
        return builderWebsite;
    }

    public void setBuilderWebsite(String builderWebsite) {
        this.builderWebsite = builderWebsite;
    }

    public String getBuilderAddress() {
        return builderAddress;
    }

    public void setBuilderAddress(String builderAddress) {
        this.builderAddress = builderAddress;
    }

    public Integer getBuilderLocalityCount() {
        return builderLocalityCount;
    }

    public void setBuilderLocalityCount(Integer builderLocalityCount) {
        this.builderLocalityCount = builderLocalityCount;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<String> getBuilderCities() {
        return builderCities;
    }

    public void setBuilderCities(List<String> builderCities) {
        this.builderCities = builderCities;
    }
}
