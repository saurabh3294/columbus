/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;

/**
 * @author mandeep
 *
 */
public class SolrResult {
    private Property property = new Property();
    private Project project = new Project();
    private Locality locality = new Locality();
    private Suburb suburb = new Suburb();
    private City city = new City();
    private Builder builder = new Builder();

    public SolrResult() {
        property.setProject(project);
        project.setBuilder(builder);
        project.setLocality(locality);
        locality.setSuburb(suburb);
        suburb.setCity(city);
    }
    
    @FieldMetaInfo(name = "id", displayName = "Id", dataType = DataType.LONG, description = "Property Id")
    @Field(value="TYPE_ID")
    @JsonProperty
    private long typeId;

    @FieldMetaInfo(name = "bedrooms", displayName = "Bedrooms", dataType = DataType.INTEGER, description = "Number of bedrooms")
    @Field(value="BEDROOMS")
    @JsonProperty
    private int bedrooms;
    
    @FieldMetaInfo(name = "bathrooms", displayName = "Bathrooms", dataType = DataType.INTEGER, description = "Number of bathrooms")
    @Field(value="BATHROOMS")
    @JsonProperty
    private int bathrooms;    
    
    @FieldMetaInfo(name = "unitType", displayName = "Unit type", dataType = DataType.STRING, description = "Unit type")
    @Field(value="UNIT_TYPE")
    @JsonProperty
    private String unitType;
    
    @FieldMetaInfo(name = "unitName", displayName = "Unit name", dataType = DataType.STRING, description = "Unit name")
    @Field(value="UNIT_NAME")
    @JsonProperty
    private String unitName;

    @FieldMetaInfo(name = "pricePerUnitArea", displayName = "Price per unit area", dataType = DataType.CURRENCY, description = "Price per unit area")
    @JsonProperty
    @Field(value="PRICE_PER_UNIT_AREA")
    private float pricePerUnitArea;

    @FieldMetaInfo(name = "size", displayName = "Size", dataType = DataType.FLOAT, description = "Size")
    @Field(value="SIZE")
    @JsonProperty
    private float size;

    @FieldMetaInfo(name = "measure", displayName = "Measure", dataType = DataType.STRING, description = "Measure")
    @Field(value="MEASURE")
    @JsonProperty
    private String measure;

    @FieldMetaInfo(name = "URL", displayName = "URL", dataType = DataType.STRING, description = "URL")
    @Field(value="PROPERTY_URL")
    @JsonProperty
    private String propertyURL;
    @FieldMetaInfo(name = "id", displayName = "Id", dataType = DataType.LONG, description = "Project Id")
    @Field(value = "PROJECT_ID")
    @JsonProperty
    private long projectId;

    @FieldMetaInfo(name = "localityId", displayName = "Locality Id", dataType = DataType.LONG, description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    @JsonProperty
    private long localityId;

    @FieldMetaInfo(name = "builderId", displayName = "Builder Id", dataType = DataType.LONG, description = "Builder Id")
    @Field(value = "BUILDER_ID")
    @JsonProperty
    private long builderId;

    @FieldMetaInfo(name = "name", displayName = "Project Name", dataType = DataType.STRING, description = "Project Name")
    @Field(value = "PROJECT_NAME")
    @JsonProperty
    private String projectName;

    @FieldMetaInfo(name = "unitTypes", displayName = "Project Types", dataType = DataType.STRING, description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    @JsonProperty
    private String unitTypes;

    @FieldMetaInfo(name = "launchDate", displayName = "Launch Date", dataType = DataType.DATE, description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    @JsonProperty
    private Date launchDate;

    @FieldMetaInfo(name = "address", displayName = "Address", dataType = DataType.STRING, description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    @JsonProperty
    private String address;

    @FieldMetaInfo(name = "computedPriority", displayName = "Computed Priority", dataType = DataType.FLOAT, description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    @JsonProperty
    private float computedPriority;

    @FieldMetaInfo(name = "assignedPriority", displayName = "Assigned Priority", dataType = DataType.INTEGER, description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    @JsonProperty
    private float assignedPriority;

    @FieldMetaInfo(name = "assignedLocalityPriority", displayName = "Assigned Locality Priority", dataType = DataType.INTEGER, description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    @JsonProperty
    private float assignedLocalityPriority;

    @FieldMetaInfo(name = "assignedSuburbPriority", displayName = "Assigned Suburb Priority", dataType = DataType.INTEGER, description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    @JsonProperty
    private float assignedSuburbPriority;

    @FieldMetaInfo(name = "completionDate", displayName = "Completion Date", dataType = DataType.DATE, description = "Completion Date")
    @Field(value = "COMPLETION_DATE")
    @JsonProperty
    private String completionDate;

    @FieldMetaInfo(name = "submittedDate", displayName = "Submitted Date", dataType = DataType.DATE, description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    @JsonProperty
    private Date submittedDate;

    @FieldMetaInfo(name = "projectImageURL", displayName = "Image URL", dataType = DataType.STRING, description = "Image URL")
    @Field(value = "PROJECT_SMALL_IMAGE")
    @JsonProperty
    private String projectImageURL;

    @FieldMetaInfo(name = "offer", displayName = "Offer", dataType = DataType.STRING, description = "Offer")
    @Field(value = "OFFER")
    @JsonProperty(value = "offer")
    private String offer;

    @FieldMetaInfo(name = "offer_heading", displayName = "Offer Heading", dataType = DataType.STRING, description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    @JsonProperty(value = "offer_heading")
    private String offerHeading;

    @FieldMetaInfo(name = "offer_desc", displayName = "Offer Description", dataType = DataType.STRING, description = "Offer Description")
    @Field(value = "OFFER_DESC")
    @JsonProperty(value = "offer_desc")
    private String offerDesc;

    @FieldMetaInfo(name = "URL", displayName = "URL", dataType = DataType.STRING, description = "URL")
    @Field(value = "PROJECT_URL")
    @JsonProperty
    private String projectURL;

    @FieldMetaInfo(name = "latitude", displayName = "Latitude", dataType = DataType.FLOAT, description = "Latitude")
    @Field(value = "LATITUDE")
    @JsonProperty(value = "latitude")
    private float latitude;

    @FieldMetaInfo(name = "longitude", displayName = "Longitude", dataType = DataType.FLOAT, description = "Longitude")
    @Field(value = "LONGITUDE")
    @JsonProperty(value = "longitude")
    private float longitude;

    @FieldMetaInfo(name = "minPricePerUnitArea", displayName = "Min Price Per Unit Area", dataType = DataType.FLOAT, description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    @JsonProperty
    private float minPricePerUnitArea;

    @FieldMetaInfo(name = "maxPricePerUnitArea", displayName = "Max Price Per Unit Area", dataType = DataType.FLOAT, description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    @JsonProperty
    private float maxPricePerUnitArea;

    @FieldMetaInfo(name = "minSize", displayName = "Min Size", dataType = DataType.FLOAT, description = "Min Size")
    @Field(value = "MINSIZE")
    @JsonProperty
    private float minSize;

    @FieldMetaInfo(name = "maxSize", displayName = "Max Size", dataType = DataType.FLOAT, description = "Max Size")
    @Field(value = "MAXSIZE")
    @JsonProperty
    private float maxSize;

    @FieldMetaInfo(name = "status", displayName = "Project Status", dataType = DataType.STRING, description = "Project Status")
    @Field(value = "PROJECT_STATUS")
    @JsonProperty
    private String status;

    @Field(value = "IS_RESALE")
    private boolean isResale;

    @FieldMetaInfo(name = "description", displayName = "Project Description", dataType = DataType.STRING, description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    @JsonProperty
    private String projectDescription;

    @FieldMetaInfo(name = "totalUnits", displayName = "Total Units", dataType = DataType.INTEGER, description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    @JsonProperty
    private int totalUnits;

    @FieldMetaInfo(name = "sizeInAcres", displayName = "size in acres", dataType = DataType.FLOAT, description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    @JsonProperty
    private float sizeInAcres;

    @Field(value="PROJECT_STATUS_BEDROOM")
    private String projectStatusBedroom;

    @FieldMetaInfo(name = "name", displayName = "Name", dataType = DataType.STRING, description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    @JsonProperty
    private String builderName;

    @FieldMetaInfo(name = "image", displayName = "Image", dataType = DataType.STRING, description = "Builder Image URL")
    @Column(name = "BUILDER_IMAGE")
    @JsonProperty
    private String builderImageURL;

    @FieldMetaInfo(name = "suburbId", displayName = "Suburb Id", dataType = DataType.INTEGER, description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    @JsonProperty
    private long suburbId;

    @FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "Label")
    @Column(name = "LABEL")
    private String localityLabel;
    @FieldMetaInfo(name = "priority", displayName = "Priority", dataType = DataType.INTEGER, description = "Priority")
    @Column(name = "PRIORITY")
    private int priority;

    @FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "Suburb label")
    @Column(name = "LABEL")
    @JsonProperty
    private String suburbLabel;
    @FieldMetaInfo(name = "cityId", displayName = "City Id", dataType = DataType.LONG, description = "City Id")
    @Column(name = "CITY_ID")
    @Id
    private long cityId;

    @FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "City label")
    @Column(name = "LABEL")
    private String cityLabel;

    @FieldMetaInfo(name = "northEastLatitude", displayName = "North east latitude", dataType = DataType.FLOAT, description = "North east latitude")
    @Field(value="NORTH_EAST_LATITUDE")
    @JsonProperty
    private float northEastLatitude;
    
    @FieldMetaInfo(name = "northEastLongitude", displayName = "North east longitude", dataType = DataType.FLOAT, description = "North east longitude")
    @Field(value="NORTH_EAST_LONGITUDE")
    @JsonProperty
    private float northEastLongitude;
    
    @FieldMetaInfo(name = "southWestLatitude", displayName = "South west latitude", dataType = DataType.FLOAT, description = "South west latitude")
    @Field(value="SOUTH_WEST_LATITUDE")
    @JsonProperty
    private float southWestLatitude;
    
    @FieldMetaInfo(name = "southWestLongitude", displayName = "South west longitude", dataType = DataType.FLOAT, description = "South west latitude")
    @Field(value="SOUTH_WEST_LONGITUDE")
    @JsonProperty
    private float southWestLongitude;
    
    @FieldMetaInfo(name = "centerLatitude", displayName = "Center latitude", dataType = DataType.FLOAT, description = "Center latitude")
    @Field(value="CENTER_LATITUDE")
    @JsonProperty
    private float centerLatitude;
    
    @FieldMetaInfo(name = "centerLatitude", displayName = "Center latitude", dataType = DataType.FLOAT, description = "Center latitude")
    @Field(value="CENTER_LONGITUDE")
    @JsonProperty
    private float centerLongitude;

    @Field("TYPE_ID")
    public void setTypeId(long typeId) {
        property.setId(typeId);
    }

    @Field("BEDROOMS")
    public void setBedrooms(int bedrooms) {
        property.setBedrooms(bedrooms);
    }

    @Field("BATHROOMS")
    public void setBathrooms(int bathrooms) {
        property.setBathrooms(bathrooms);
    }

    @Field("UNIT_TYPE")
    public void setUnitType(String unitType) {
        property.setUnitType(unitType);
    }

    @Field("UNIT_NAME")
    public void setUnitName(String unitName) {
        property.setUnitName(unitName);
    }

    @Field("PRICE_PER_UNIT_AREA")
    public void setPricePerUnitArea(float pricePerUnitArea) {
        property.setPricePerUnitArea(pricePerUnitArea);
    }

    @Field("SIZE")
    public void setSize(float size) {
        property.setSize(size);
    }

    @Field("MEASURE")
    public void setMeasure(String measure) {
        property.setMeasure(measure);
    }

    @Field("PROPERTY_URL")
    public void setPropertyURL(String propertyURL) {
        property.setURL(propertyURL);
    }

    @Field("PROJECT_ID")
    public void setProjectId(long projectId) {
        project.setId(projectId);
        property.setProjectId(projectId);
    }

    @Field("LOCALITY_ID")
    public void setLocalityId(long localityId) {
        project.setLocalityId(localityId);
        locality.setLocalityId(localityId);
    }

    @Field("BUILDER_ID")
    public void setBuilderId(long builderId) {
        project.setBuilderId(builderId);
        builder.setId(builderId);
    }

    @Field("PROJECT_NAME")
    public void setProjectName(String projectName) {
        project.setName(projectName);
    }

    @Field("UNIT_TYPES")
    public void setUnitTypes(String unitTypes) {
        project.setUnitTypes(unitTypes);
    }

    @Field("LAUNCH_DATE")
    public void setLaunchDate(Date launchDate) {
        project.setLaunchDate(launchDate);
    }

    @Field("PROJECT_ADDRESS")
    public void setAddress(String address) {
        project.setAddress(address);
    }

    @Field("PROJECT_PRIORITY")
    public void setComputedPriority(float computedPriority) {
        project.setComputedPriority(computedPriority);
    }

    @Field("DISPLAY_ORDER")
    public void setAssignedPriority(float assignedPriority) {
        project.setAssignedPriority(assignedPriority);
    }

    @Field("DISPLAY_ORDER_LOCALITY")
    public void setAssignedLocalityPriority(float assignedLocalityPriority) {
        project.setAssignedLocalityPriority(assignedLocalityPriority);
    }

    @Field("DISPLAY_ORDER_SUBURB")
    public void setAssignedSuburbPriority(float assignedSuburbPriority) {
        project.setAssignedSuburbPriority(assignedSuburbPriority);
    }

    @Field("COMPLETION_DATE")
    public void setCompletionDate(String completionDate) {
        project.setCompletionDate(completionDate);
    }

    @Field("SUBMITTED_DATE")
    public void setSubmittedDate(Date submittedDate) {
        project.setSubmittedDate(submittedDate);
    }

    @Field("PROJECT_SMALL_IMAGE")
    public void setProjectImageURL(String projectImageURL) {
        project.setImageURL(projectImageURL);
    }

    @Field("OFFER")
    public void setOffer(String offer) {
        project.setOffer(offer);
    }

    @Field("OFFER_HEADING")
    public void setOfferHeading(String offerHeading) {
        project.setOfferHeading(offerHeading);
    }

    @Field("OFFER_DESC")
    public void setOfferDesc(String offerDesc) {
        project.setOfferDesc(offerDesc);
    }

    @Field("PROJECT_URL")
    public void setProjectURL(String projectURL) {
        project.setURL(projectURL);
    }

    @Field("LATITUDE")
    public void setLatitude(float latitude) {
        project.setLatitude(latitude);
    }

    @Field("LONGITUDE")
    public void setLongitude(float longitude) {
        project.setLongitude(longitude);
    }

    @Field("MIN_PRICE_PER_UNIT_AREA")
    public void setMinPricePerUnitArea(float minPricePerUnitArea) {
        project.setMinPricePerUnitArea(minPricePerUnitArea);
    }

    @Field("MAX_PRICE_PER_UNIT_AREA")
    public void setMaxPricePerUnitArea(float maxPricePerUnitArea) {
        project.setMaxPricePerUnitArea(maxPricePerUnitArea);
    }

    @Field("MIN_SIZE")
    public void setMinSize(float minSize) {
        project.setMinSize(minSize);
    }

    @Field("MAX_SIZE")
    public void setMaxSize(float maxSize) {
        project.setMaxSize(maxSize);
    }

    @Field("PROJECT_STATUS")
    public void setStatus(String status) {
        project.setStatus(status);
    }

    @Field("IS_RESALE")
    public void setResale(boolean isResale) {
        project.setResale(isResale);
    }

    @Field("PROJECT_DESCRIPTION")
    public void setProjectDescription(String projectDescription) {
        project.setDescription(projectDescription);
    }

    @Field("TOTAL_UNITS")
    public void setTotalUnits(int totalUnits) {
        project.setTotalUnits(totalUnits);
    }

    @Field("BUILDER_NAME")
    public void setBuilderName(String builderName) {
        builder.setName(builderName);
    }

    @Field("BUILDER_IMAGE")
    public void setBuilderImageURL(String builderImageURL) {
        builder.setImageUrl(builderImageURL);
    }

    @Field("SUBURB_ID")
    public void setSuburbId(long suburbId) {
        locality.setSuburbID(suburbId);
        suburb.setId(suburbId);
    }

    @Field("LOCALITY")
    public void setLocalityLabel(String localityLabel) {
        locality.setLabel(localityLabel);
    }

    @Field("SUBURB")
    public void setSuburbLabel(String suburbLabel) {
        suburb.setLabel(suburbLabel);
    }

    @Field("CITY_ID")
    public void setCityId(long cityId) {
        suburb.setCityId(cityId);
        city.setId(cityId);
    }

    @Field("CITY")
    public void setCityLabel(String cityLabel) {
        city.setLabel(cityLabel);
    }

    @Field("NORTH_EAST_LATITUDE")
    public void setNorthEastLatitude(float northEastLatitude) {
        city.setNorthEastLatitude(northEastLatitude);
    }

    @Field("NORTH_EAST_LONGITUDE")
    public void setNorthEastLongitude(float northEastLongitude) {
        city.setNorthEastLongitude(northEastLongitude);
    }

    @Field("SOUTH_WEST_LATITUDE")
    public void setSouthWestLatitude(float southWestLatitude) {
        city.setSouthWestLatitude(southWestLatitude);
    }

    @Field("SOUth_WEST_LONGITUDE")
    public void setSouthWestLongitude(float southWestLongitude) {
        city.setSouthWestLongitude(southWestLongitude);
    }

    @Field("CENTER_LATITUDE")
    public void setCenterLatitude(float centerLatitude) {
        city.setCenterLatitude(centerLatitude);
    }

    @Field("CENTER_LONGITUDE")
    public void setCenterLongitude(float centerLongitude) {
        city.setCenterLongitude(centerLongitude);
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
