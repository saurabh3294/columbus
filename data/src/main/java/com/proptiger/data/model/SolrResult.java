/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;

/**
 * @author mandeep
 * 
 */
@JsonFilter("fieldFilter")
public class SolrResult implements BaseModel{
    private Property property = new Property();
    private Project project = new Project();
    private Locality locality = new Locality();
    private Suburb suburb = new Suburb();
    private City city = new City();
    private Builder builder = new Builder();

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Min Price Per Unit Area",  description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    private Double minPricePerUnitArea;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Max Price Per Unit Area",  description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    private Double maxPricePerUnitArea;

    @FieldMetaInfo( displayName = "Min Size",  description = "Min Size")
    @Field(value = "MINSIZE")
    private Double minSize;

    @FieldMetaInfo( displayName = "Max Size",  description = "Max Size")
    @Field(value = "MAXSIZE")
    private Double maxSize;

    @FieldMetaInfo( displayName = "Min Bedrooms",  description = "Min Bedrooms")
    @Field(value = "BEDROOMS")
    private int minBedrooms;

    @FieldMetaInfo( displayName = "Max Bedrooms",  description = "Max Bedrooms")
    @Field(value = "BEDROOMS")
    private int maxBedrooms;

    @FieldMetaInfo( displayName = "Property unit types",  description = "Property unit types")
    @Field(value = "UNIT_TYPE")
    private String propertyUnitTypes;

    @FieldMetaInfo( displayName = "Latitude",  description = "Latitude")
    @Field(value = "LATITUDE")
    private Double latitude;

    @FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
    @Field(value = "LONGITUDE")
    private Double longitude;

    @FieldMetaInfo( displayName = "Min Price",  description = "Min Price")
    @Field(value = "MIN_BUDGET")
    private Double minPrice;

    @FieldMetaInfo( displayName = "Max Price",  description = "Max Price")
    @Field(value = "MAX_BUDGET")
    private Double maxPrice;

    @FieldMetaInfo( displayName = "Project Name",  description = "Project Name")
    @Field(value = "PROJECT_NAME")
    private String name;

    @FieldMetaInfo( displayName = "Project Image URL",  description = "Project Image URL")
    @Field(value = "PROJECT_SMALL_IMAGE")
    private String imageURL;

    @FieldMetaInfo( displayName = "Builder Image URL",  description = "Builder Image URL")
    @Field(value = "BUILDER_IMAGE")
    private String builderImageURL;

    @FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
    @Field(value = "PROJECT_ID")
    private int projectId;

    @FieldMetaInfo( displayName = "Property Id",  description = "Property Id")
    @Field(value = "TYPE_ID")
    private int propertyId;

    @FieldMetaInfo( displayName = "Assigned priority",  description = "Priorities assigned manually")
    @Field(value = "DISPLAY_ORDER")
    private int assignedPriority;

    @FieldMetaInfo( displayName = "Computed priority",  description = "Priorities computed automatically")
    @Field(value = "PROJECT_PRIORITY")
    private Double computedPriority;

    @FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    private int localityId;

    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Field(value = "CITY_ID")
    private int cityId;

    @FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")
    @Field(value = "SUBURB_ID")
    private int suburbId;

    @FieldMetaInfo( displayName = "Builder Id",  description = "Builder Id")
    @Field(value = "BUILDER_ID")
    private int builderId;

    @FieldMetaInfo( displayName = "Property size measure",  description = "Property size measure")
    @Field(value="MEASURE")
    private String propertySizeMeasure;

    @FieldMetaInfo( displayName = "Launch Date",  description = "Launch Date")
    @Field(value = "LAUNCH_DATE")
    private Date launchDate;

    @FieldMetaInfo( displayName = "Possession Date",  description = "Possession Date")
    @Field(value = "PROMISED_COMPLETION_DATE")
    private Date possessionDate;
    
    @FieldMetaInfo( displayName = "Bedrooms",  description = "Number of bedrooms")
    @Field(value="BEDROOMS")
    private int bedrooms;
    
    @FieldMetaInfo( displayName = "Bathrooms",  description = "Number of bathrooms")
    @Field(value="BATHROOMS")
    private int bathrooms;

    @FieldMetaInfo( displayName = "Is resale ?",  description = "Is resale ?")
    @Field(value="IS_RESALE")
    private boolean isResale;

    @FieldMetaInfo( displayName = "Unit type",  description = "Unit type")
    @Field(value="UNIT_TYPE")
    private String unitType;

    @FieldMetaInfo( displayName = "Project status",  description = "Project status")
    @Field(value="PROJECT_STATUS")
    private String projectStatus;
    
    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Price per unit area",  description = "Price per unit area")
    @Field(value="PRICE_PER_UNIT_AREA")
    private Double pricePerUnitArea;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Price",  description = "Price")
    @Field(value="BUDGET")
    private Double budget;

    @FieldMetaInfo( displayName = "Size",  description = "Size")
    @Field(value="SIZE")
    private Double size;

    @FieldMetaInfo(displayName = "Locality Label", description = "Locality Label")
    @Field(value = "LOCALITY")
    private String localityLabel;

    @FieldMetaInfo(displayName = "Suburb Label", description = "Suburb Label")
    @Field(value = "SUBURB")
    private String suburbLabel;
    
    @FieldMetaInfo(displayName = "Builder Label", description = "Builder Label")
    @Field(value = "BUILDER_NAME")
    private String builderLabel;
    
    @FieldMetaInfo(displayName = "City Label", description = "City Label")
    @Field(value = "CITY")
    private String cityLabel;

    @FieldMetaInfo( displayName = "Address",  description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    private String address;

    @Field(value = "LOCALITY_OR_SUBURB")
    private List<String> localityOrSuburbLabel;

    @FieldMetaInfo( displayName = "Text",  description = "Full text for search")
    @Field(value = "text")
    private String text;
    
    @Field(value="PROCESSED_LATITUDE")
    private Double processedLatitude;
    
    @Field(value="PROCESSED_LONGITUDE")
    private Double processedLongitude;

    @Field(value = "GEO")
    private List<String> geo;
    
    @Field("LOCALITY_LABEL_PRIORITY")
    private String localityLabelPriority;
    
    @Field("SUBURB_LABEL_PRIORITY")
    private String suburbLabelPriority;
    
    @Field("BUILDER_LABEL_PRIORITY")
    private String builderLabelPriority;
    
    public SolrResult() {
        property.setProject(project);
        project.setBuilder(builder);
        project.setLocality(locality);
        locality.setSuburb(suburb);
        suburb.setCity(city);
    }

    @Field("TYPE_ID")
    public void setTypeId(int typeId) {
        property.setPropertyId(typeId);
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
    public void setPricePerUnitArea(Double pricePerUnitArea) {
        property.setPricePerUnitArea(pricePerUnitArea);
    }

    @Field("SIZE")
    public void setSize(Double size) {
        property.setSize(size);
    }

    @Field("MEASURE")
    public void setMeasure(String measure) {
        property.setMeasure(measure);
        project.setPropertySizeMeasure(measure);
    }

    @Field("PROPERTY_URL")
    public void setPropertyURL(String propertyURL) {
        property.setURL(propertyURL);
    }

    @Field("PROJECT_ID")
    public void setProjectId(int projectId) {
        project.setProjectId(projectId);
        property.setProjectId(projectId);
    }

    @Field("LOCALITY_ID")
    public void setLocalityId(int localityId) {
        project.setLocalityId(localityId);
        locality.setLocalityId(localityId);
    }

    @Field("BUILDER_ID")
    public void setBuilderId(int builderId) {
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
    public void setComputedPriority(double computedPriority) {
        project.setComputedPriority(computedPriority);
    }

    @Field("DISPLAY_ORDER")
    public void setAssignedPriority(int assignedPriority) {
        project.setAssignedPriority(assignedPriority);
    }

    @Field("DISPLAY_ORDER_LOCALITY")
    public void setAssignedLocalityPriority(int assignedLocalityPriority) {
        project.setAssignedLocalityPriority(assignedLocalityPriority);
    }

    @Field("DISPLAY_ORDER_SUBURB")
    public void setAssignedSuburbPriority(int assignedSuburbPriority) {
        project.setAssignedSuburbPriority(assignedSuburbPriority);
    }

    @Field("PROMISED_COMPLETION_DATE")
    public void setPossessionDate(Date possessionDate) {
        project.setPossessionDate(possessionDate);
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
    public void setLatitude(Double latitude) {
        project.setLatitude(latitude);
    }

    @Field("LONGITUDE")
    public void setLongitude(Double longitude) {
        project.setLongitude(longitude);
    }

    @Field("MIN_PRICE_PER_UNIT_AREA")
    public void setMinPricePerUnitArea(Double minPricePerUnitArea) {
        project.setMinPricePerUnitArea(minPricePerUnitArea);
    }

    @Field("MAX_PRICE_PER_UNIT_AREA")
    public void setMaxPricePerUnitArea(Double maxPricePerUnitArea) {
        project.setMaxPricePerUnitArea(maxPricePerUnitArea);
    }

    @Field("MIN_SIZE")
    public void setMinSize(double minSize) {
        project.setMinSize(minSize);
    }

    @Field("MAX_SIZE")
    public void setMaxSize(double maxSize) {
        project.setMaxSize(maxSize);
    }

    @Field("PROJECT_STATUS")
    public void setStatus(String status) {
        project.setProjectStatus(status);
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
        builder.setImageURL(builderImageURL);
    }

    @Field("SUBURB_ID")
    public void setSuburbId(int suburbId) {
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
    public void setCityId(int cityId) {
        suburb.setCityId(cityId);
        city.setId(cityId);
    }

    @Field("CITY")
    public void setCityLabel(String cityLabel) {
        city.setLabel(cityLabel);
    }

    @Field("NORTH_EAST_LATITUDE")
    public void setNorthEastLatitude(Double northEastLatitude) {
        city.setNorthEastLatitude(northEastLatitude);
    }

    @Field("NORTH_EAST_LONGITUDE")
    public void setNorthEastLongitude(Double northEastLongitude) {
        city.setNorthEastLongitude(northEastLongitude);
    }

    @Field("SOUTH_WEST_LATITUDE")
    public void setSouthWestLatitude(Double southWestLatitude) {
        city.setSouthWestLatitude(southWestLatitude);
    }

    @Field("SOUTH_WEST_LONGITUDE")
    public void setSouthWestLongitude(Double southWestLongitude) {
        city.setSouthWestLongitude(southWestLongitude);
    }

    @Field("CENTER_LATITUDE")
    public void setCenterLatitude(Double centerLatitude) {
        city.setCenterLatitude(centerLatitude);
    }

    @Field("CENTER_LONGITUDE")
    public void setCenterLongitude(Double centerLongitude) {
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

    @Field("MIN_BUDGET")
    public void setMinPrice(Double minPrice) {
        project.setMinPrice(minPrice);
    }

    @Field("MAX_BUDGET")
    public void setMaxPrice(Double maxPrice) {
        project.setMaxPrice(maxPrice);
    }
    
    @Field("PROCESSED_LATITUDE")
    public void setProcessedLatitude(Double processedLatitude){
    	property.setProcessedLatitude(processedLatitude);
    }
    
    @Field("PROCESSED_LONGITUDE")
    public void setProcessedLongitude(Double processedLongitude){
    	property.setProcessedLongitude(processedLongitude);
    }
    
    @Field("BUDGET")
	public void setBudget(Double budget) {
		property.setBudget(budget);
	}
    
    @Field("PROJECT_ID_BEDROOM")
    public void setProjectIdBedroom(String projectIdBedroom){
    	property.setProjectIdBedroom(projectIdBedroom);
    }
    
    @Field("LOCALITY_LABEL_PRIORITY")
    public void setLocalityLabelPriority(String localityLabelPriority){
    	project.setLocalityLabelPriority(localityLabelPriority);
    }
    
    @Field("SUBURB_LABEL_PRIORITY")
    public void setSuburbLabelPriority(String suburbLabelPriority){
    	project.setSuburbLabelPriority(suburbLabelPriority);
    }
    
    @Field("BUILDER_LABEL_PRIORITY")
    public void setBuilderLabelPriority(String builderLabelPriority){
    	project.setBuilderLabelPriority(builderLabelPriority);
    }
}
