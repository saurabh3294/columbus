/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.solr.client.solrj.beans.Field;

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
    
    @FieldMetaInfo( displayName = "Id",  description = "Property Id")
    @Field(value="TYPE_ID")
    private long typeId;

    @FieldMetaInfo( displayName = "Bedrooms",  description = "Number of bedrooms")
    @Field(value="BEDROOMS")
    private int bedrooms;
    
    @FieldMetaInfo( displayName = "Bathrooms",  description = "Number of bathrooms")
    @Field(value="BATHROOMS")
    private int bathrooms;    
    
    @FieldMetaInfo( displayName = "Unit type",  description = "Unit type")
    @Field(value="UNIT_TYPE")
    private String unitType;
    
    @FieldMetaInfo( displayName = "Unit name",  description = "Unit name")
    @Field(value="UNIT_NAME")
    private String unitName;

    @FieldMetaInfo( displayName = "Price per unit area",  description = "Price per unit area")
    @Field(value="PRICE_PER_UNIT_AREA")
    private float pricePerUnitArea;

    @FieldMetaInfo( displayName = "Size",  description = "Size")
    @Field(value="SIZE")
    private float size;

    @FieldMetaInfo( displayName = "Measure",  description = "Measure")
    @Field(value="MEASURE")
    private String measure;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value="PROPERTY_URL")
    private String propertyURL;
    @FieldMetaInfo( displayName = "Id",  description = "Project Id")
    @Field(value = "PROJECT_ID")
    private long projectId;

    @FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    private long localityId;

    @FieldMetaInfo( displayName = "Builder Id",  description = "Builder Id")
    @Field(value = "BUILDER_ID")
    private long builderId;

    @FieldMetaInfo( displayName = "Project Name",  description = "Project Name")
    @Field(value = "PROJECT_NAME")
    private String projectName;

    @FieldMetaInfo( displayName = "Project Types",  description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    private String unitTypes;

    @FieldMetaInfo( displayName = "Launch Date",  description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    private Date launchDate;

    @FieldMetaInfo( displayName = "Address",  description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    private String address;

    @FieldMetaInfo( displayName = "Computed Priority",  description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    private float computedPriority;

    @FieldMetaInfo( displayName = "Assigned Priority",  description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    private float assignedPriority;

    @FieldMetaInfo( displayName = "Assigned Locality Priority",  description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    private float assignedLocalityPriority;

    @FieldMetaInfo( displayName = "Assigned Suburb Priority",  description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    private float assignedSuburbPriority;

    @FieldMetaInfo( displayName = "Completion Date",  description = "Completion Date")
    @Field(value = "COMPLETION_DATE")
    private String completionDate;

    @FieldMetaInfo( displayName = "Submitted Date",  description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    private Date submittedDate;

    @FieldMetaInfo( displayName = "Image URL",  description = "Image URL")
    @Field(value = "PROJECT_SMALL_IMAGE")
    private String projectImageURL;

    @FieldMetaInfo( displayName = "Offer",  description = "Offer")
    @Field(value = "OFFER")
    private String offer;

    @FieldMetaInfo( displayName = "Offer Heading",  description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    private String offerHeading;

    @FieldMetaInfo( displayName = "Offer Description",  description = "Offer Description")
    @Field(value = "OFFER_DESC")
    private String offerDesc;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value = "PROJECT_URL")
    private String projectURL;

    @FieldMetaInfo( displayName = "Latitude",  description = "Latitude")
    @Field(value = "LATITUDE")
    private float latitude;

    @FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
    @Field(value = "LONGITUDE")
    private float longitude;

    @FieldMetaInfo( displayName = "Min Price Per Unit Area",  description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    private float minPricePerUnitArea;

    @FieldMetaInfo( displayName = "Max Price Per Unit Area",  description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    private float maxPricePerUnitArea;

    @FieldMetaInfo( displayName = "Min Size",  description = "Min Size")
    @Field(value = "MINSIZE")
    private float minSize;

    @FieldMetaInfo( displayName = "Max Size",  description = "Max Size")
    @Field(value = "MAXSIZE")
    private float maxSize;

    @FieldMetaInfo( displayName = "Project Status",  description = "Project Status")
    @Field(value = "PROJECT_STATUS")
    private String status;

    @Field(value = "IS_RESALE")
    private boolean isResale;

    @FieldMetaInfo( displayName = "Project Description",  description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    private String projectDescription;

    @FieldMetaInfo( displayName = "Total Units",  description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    private int totalUnits;

    @FieldMetaInfo( displayName = "size in acres",  description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    private float sizeInAcres;

    @Field(value="PROJECT_STATUS_BEDROOM")
    private String projectStatusBedroom;

    @FieldMetaInfo( displayName = "Name",  description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    private String builderName;

    @FieldMetaInfo( displayName = "Image",  description = "Builder Image URL")
    @Column(name = "BUILDER_IMAGE")
    private String builderImageURL;

    @FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    private long suburbId;

    @FieldMetaInfo( displayName = "Label",  description = "Label")
    @Column(name = "LABEL")
    private String localityLabel;
    @FieldMetaInfo( displayName = "Priority",  description = "Priority")
    @Column(name = "PRIORITY")
    private int priority;

    @FieldMetaInfo( displayName = "Label",  description = "Suburb label")
    @Column(name = "LABEL")
    private String suburbLabel;
    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    @Id
    private long cityId;

    @FieldMetaInfo( displayName = "Label",  description = "City label")
    @Column(name = "LABEL")
    private String cityLabel;

    @FieldMetaInfo( displayName = "North east latitude",  description = "North east latitude")
    @Field(value="NORTH_EAST_LATITUDE")
    private float northEastLatitude;
    
    @FieldMetaInfo( displayName = "North east longitude",  description = "North east longitude")
    @Field(value="NORTH_EAST_LONGITUDE")
    private float northEastLongitude;
    
    @FieldMetaInfo( displayName = "South west latitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LATITUDE")
    private float southWestLatitude;
    
    @FieldMetaInfo( displayName = "South west longitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LONGITUDE")
    private float southWestLongitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LATITUDE")
    private float centerLatitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LONGITUDE")
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
        locality.setSuburbId(suburbId);
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
