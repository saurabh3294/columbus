/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.enums.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.image.Image;
import com.google.gson.Gson;

/**
 * @author mandeep
 */
@JsonFilter("fieldFilter")
public class SolrResult extends BaseModel {

    private static final long serialVersionUID = 3536334269824289200L;
    private Property          property         = new Property();
    private Project           project          = new Project();
    private Locality          locality         = new Locality();
    private Suburb            suburb           = new Suburb();
    private City              city             = new City();
    private Builder           builder          = new Builder();

    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Min Price Per Unit Area",
            description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    private Double            minPricePerUnitArea;

    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Max Price Per Unit Area",
            description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    private Double            maxPricePerUnitArea;

    @FieldMetaInfo(displayName = "Min Size", description = "Min Size")
    @Field(value = "MINSIZE")
    private Double            minSize;

    @FieldMetaInfo(displayName = "Max Size", description = "Max Size")
    @Field(value = "MAXSIZE")
    private Double            maxSize;

    @FieldMetaInfo(displayName = "Min Bedrooms", description = "Min Bedrooms")
    @Field(value = "BEDROOMS")
    private int               minBedrooms;

    @FieldMetaInfo(displayName = "Max Bedrooms", description = "Max Bedrooms")
    @Field(value = "BEDROOMS")
    private int               maxBedrooms;

    @FieldMetaInfo(displayName = "Property unit types", description = "Property unit types")
    @Field(value = "UNIT_TYPE")
    private String            propertyUnitTypes;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Field(value = "LATITUDE")
    private Double            latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Field(value = "LONGITUDE")
    private Double            longitude;

    @FieldMetaInfo(displayName = "Min Price", description = "Min Price")
    @Field(value = "MIN_BUDGET")
    private Double            minPrice;

    @FieldMetaInfo(displayName = "Max Price", description = "Max Price")
    @Field(value = "MAX_BUDGET")
    private Double            maxPrice;

    @Field(value = "PRIMARY_OR_RESALE_BUDGET")
    private List<Double>      primaryOrResaleBudget;

    @Field(value = "PRIMARY_OR_RESALE_PRICE_PER_UNIT_AREA")
    private List<Double>      primaryOrResalePricePerUnitArea;

    @FieldMetaInfo(displayName = "Project Name", description = "Project Name")
    @Field(value = "PROJECT_NAME")
    private String            name;

    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Field(value = "PROJECT_ID")
    private int               projectId;

    @FieldMetaInfo(displayName = "Property Id", description = "Property Id")
    @Field(value = "TYPE_ID")
    private int               propertyId;

    @FieldMetaInfo(displayName = "Assigned priority", description = "Priorities assigned manually")
    @Field(value = "DISPLAY_ORDER")
    private int               assignedPriority;

    @FieldMetaInfo(displayName = "Computed priority", description = "Priorities computed automatically")
    @Field(value = "PROJECT_PRIORITY")
    private Double            computedPriority;

    @FieldMetaInfo(displayName = "Project enquiry count", description = "Project enquiry count")
    @Field(value = "PROJECT_ENQUIRY_COUNT")
    private Integer           projectEnquiryCount;

    @FieldMetaInfo(displayName = "Locality enquiry count", description = "Locality enquiry count")
    @Field(value = "LOCALITY_ENQUIRY_COUNT")
    private Integer           localityEnquiryCount;

    @FieldMetaInfo(displayName = "City enquiry count", description = "City enquiry count")
    @Field(value = "CITY_ENQUIRY_COUNT")
    private Integer           cityEnquiryCount;

    @FieldMetaInfo(displayName = "Suburb enquiry count", description = "Suburb enquiry count")
    @Field(value = "SUBURB_ENQUIRY_COUNT")
    private Integer           suburbEnquiryCount;

    @FieldMetaInfo(displayName = "Builder enquiry count", description = "Builder enquiry count")
    @Field(value = "BUILDER_ENQUIRY_COUNT")
    private Integer           builderEnquiryCount;

    @FieldMetaInfo(displayName = "Project View count", description = "Project View count")
    @Field(value = "PROJECT_VIEW_COUNT")
    private Integer           projectViewCount;

    @FieldMetaInfo(displayName = "Locality View count", description = "Locality View count")
    @Field(value = "LOCALITY_VIEW_COUNT")
    private Integer           localityViewCount;

    @FieldMetaInfo(displayName = "City View count", description = "City View count")
    @Field(value = "CITY_VIEW_COUNT")
    private Integer           cityViewCount;

    @FieldMetaInfo(displayName = "Suburb View count", description = "Suburb View count")
    @Field(value = "SUBURB_VIEW_COUNT")
    private Integer           suburbViewCount;

    @FieldMetaInfo(displayName = "Builder View count", description = "Builder View count")
    @Field(value = "BUILDER_VIEW_COUNT")
    private Integer           builderViewCount;

    @FieldMetaInfo(displayName = "Assigned priority of locality", description = "Locality priorities assigned manually")
    @Field(value = "LOCALITY_PRIORITY")
    private Integer           localityPriority;

    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    private int               localityId;

    @FieldMetaInfo(displayName = "City Id", description = "City Id")
    @Field(value = "CITY_ID")
    private int               cityId;

    @FieldMetaInfo(displayName = "Suburb Id", description = "Suburb Id")
    @Field(value = "SUBURB_ID")
    private int               suburbId;

    @FieldMetaInfo(displayName = "Builder Id", description = "Builder Id")
    @Field(value = "BUILDER_ID")
    private int               builderId;

    @FieldMetaInfo(displayName = "Property size measure", description = "Property size measure")
    @Field(value = "MEASURE")
    private String            propertySizeMeasure;

    @FieldMetaInfo(displayName = "Launch Date", description = "Launch Date")
    @Field(value = "LAUNCH_DATE")
    private Date              launchDate;

    @FieldMetaInfo(displayName = "Possession Date", description = "Possession Date")
    @Field(value = "PROMISED_COMPLETION_DATE")
    private Date              possessionDate;

    @FieldMetaInfo(displayName = "Bedrooms", description = "Number of bedrooms")
    @Field(value = "BEDROOMS")
    private int               bedrooms;

    @FieldMetaInfo(displayName = "Bathrooms", description = "Number of bathrooms")
    @Field(value = "BATHROOMS")
    private int               bathrooms;

    @FieldMetaInfo(displayName = "Is resale ?", description = "Is resale ?")
    @Field(value = "IS_RESALE")
    private boolean           isResale;

    @FieldMetaInfo(displayName = "Unit type", description = "Unit type")
    @Field(value = "UNIT_TYPE")
    private String            unitType;

    @FieldMetaInfo(displayName = "Project status", description = "Project status")
    @Field(value = "PROJECT_STATUS")
    private String            projectStatus;

    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Price per unit area",
            description = "Price per unit area")
    @Field(value = "PRICE_PER_UNIT_AREA")
    private Double            pricePerUnitArea;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Price", description = "Price")
    @Field(value = "BUDGET")
    private Double            budget;

    @FieldMetaInfo(displayName = "Size", description = "Size")
    @Field(value = "SIZE")
    private Double            size;

    @FieldMetaInfo(displayName = "Locality Label", description = "Locality Label")
    @Field(value = "LOCALITY")
    private String            localityLabel;

    @FieldMetaInfo(displayName = "Suburb Label", description = "Suburb Label")
    @Field(value = "SUBURB")
    private String            suburbLabel;

    @FieldMetaInfo(displayName = "Builder Label", description = "Builder Label")
    @Field(value = "BUILDER_NAME")
    private String            builderLabel;

    @FieldMetaInfo(displayName = "City Label", description = "City Label")
    @Field(value = "CITY")
    private String            cityLabel;

    @FieldMetaInfo(displayName = "Address", description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    private String            address;

    @Field(value = "LOCALITY_OR_SUBURB")
    private List<String>      localityOrSuburbLabel;

    @FieldMetaInfo(displayName = "Text", description = "Full text for search")
    @Field(value = "text")
    private String            text;

    @Field(value = "PROCESSED_LATITUDE")
    private Double            processedLatitude;

    @Field(value = "PROCESSED_LONGITUDE")
    private Double            processedLongitude;

    @Field(value = "GEO")
    private List<String>      geo;

    @Field("LOCALITY_LABEL_PRIORITY")
    private String            localityLabelPriority;

    @Field("SUBURB_LABEL_PRIORITY")
    private String            suburbLabelPriority;

    @Field("BUILDER_LABEL_PRIORITY")
    private String            builderLabelPriority;

    @Field("RESALE_PRICE")
    private Double            resalePrice;

    @Field("geodist()")
    private Double            geoDistance;

    @Field("LOCALITY_PRICE_RISE")
    private Double            localityAvgPriceRisePercentage;

    @Field("LOCALITY_PRICE_APPRECIATION_RATE")
    private Double            localityPriceAppreciationRate;

    @Field("LOCALITY_PRICE_RISE_TIME")
    private Integer           localityAvgPriceRiseMonths;

    @Field("PROJECT_PRICE_RISE")
    private Double            projectAvgPriceRisePercentage;

    @Field("PROJECT_PRICE_APPRECIATION_RATE")
    private Double            projectPriceAppreciationRate;

    @Field("PROJECT_PRICE_RISE_TIME")
    private String            projectAvgPriceRiseMonths;

    @Field("HAS_GEO")
    private Integer           hasGeo;

    @Field("MIN_RESALE_OR_PRIMARY_PRICE")
    private Double            minResaleOrPrimaryPrice;

    @Field("MAX_RESALE_OR_PRIMARY_PRICE")
    private Double            maxResaleOrPrimaryPrice;

    @Field("LOCALITY_PRICE_RISE_6MONTHS")
    private Double            localityPriceRise6Months;

    @Field("PROJECT_PRICE_RISE_6MONTHS")
    private Double            projectPriceRise6Months;

    @Field("SUBURB_PROJECT_COUNT")
    private Integer           suburbProjectCount;

    @Field("LOCALITY_PROJECT_COUNT")
    private Integer           localityProjectCount;

    @Field("BUILDER_PROJECT_COUNT")
    private Integer           builderProjectCount;

    @Field("TYPEAHEAD_LOCALITY_UNITS_LAUNCHED_6MONTHS")
    private Integer           localityUnitsLaunched6Months;

    @Field("TYPEAHEAD_LOCALITY_UNITS_SOLD_6MONTHS")
    private Integer           localityUnitsSold6Months;

    @Field("TYPEAHEAD_LOCALITY_UNITS_DELIVERED_6MONTHS")
    private Integer           localityUnitsDelivered6Months;

    @Field("PROJECT_SAFETY_SCORE")
    private Double            projectSafetyScore;

    @Field("PROJECT_LIVABILITY_SCORE")
    private Float             projectLivabilityScore;

    @Field("LOCALITY_SAFETY_SCORE")
    private Double            localitySafetyScore;

    @Field("LOCALITY_LIVABILITY_SCORE")
    private Float             localityLivabilityScore;

    @Field("BUILDER_URL")
    private String            builderUrl;
       
    @Field("BUILDER_LOCALITY_COUNT")
    private Integer           builderLocalityCount;
    
    @Field("BUILDER_CITIES")
    private List<String>      builderCities;
    
    @Field("BUILDER_IMAGE_ALTTEXT")
    private String            builderImgAltText;
    
    @Field("BUILDER_IMAGE_TITLE")
    private String            builderImgTitle;
    
    @Field("PROJECT_IMAGE_ALTTEXT")
    private String            projectImgAltText;
    
    @Field("PROJECT_IMAGE_TITLE")
    private String            projectImgTitle;
    
    @Field("PROJECT_LOCALITY_SCORE")
    private Float             projectLocalityScore;
    
    @Field("PROJECT_SOCIETY_SCORE")
    private Float             projectSocietyScore;
    
    @Field(value = "PROJECT_SAFETY_RANK")
    private Integer                 projectSafetyRank;
    
    @Field(value = "PROJECT_LIVABILITY_RANK")
    private Integer                 projectLivabilityRank;
    
    @Field(value = "LOCALITY_SAFETY_RANK")
    private Integer                 localitySafetyRank;
    
    @Field(value = "LOCALITY_LIVABILITY_RANK")
    private Integer                 localityLivabilityRank;
    
    @Field(value = "HAS_3D_IMAGES")
    private boolean                 hasProject3DImages;
    
    @Field(value = "PROJECT_MAX_SAFETY_SCORE")
    private Double                  projectMaxSafetyScore;
    
    @Field(value = "PROJECT_MIN_SAFETY_SCORE")                     
    private Double                  projectMinSafetyScore;
    
    @Field(value = "PROJECT_MAX_LIVABILITY_SCORE")
    private Double                  projectMaxLivabilityScore;
    
    @Field(value = "PROJECT_MIN_LIVABILITY_SCORE")
    private Double                  projectMinLivabilityScore;
    
    @Field(value = "LOCALITY_MAX_SAFETY_SCORE")
    private Double                  localityMaxSafetyScore;
    
    @Field(value = "LOCALITY_MIN_SAFETY_SCORE")                     
    private Double                  localityMinSafetyScore;
    
    @Field(value = "LOCALITY_MAX_LIVABILITY_SCORE")
    private Double                  localityMaxLivabilityScore;
    
    @Field(value = "LOCALITY_MIN_LIVABILITY_SCORE")
    private Double                  localityMinLivabilityScore;
    
    public SolrResult() {
        property.setProject(project);
        project.setBuilder(builder);
        project.setLocality(locality);
        locality.setSuburb(suburb);
        suburb.setCity(city);
        project.setMainImage(new Image());
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

    @Field("PROJECT_TYPES")
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

    @Field("PROJECT_ENQUIRY_COUNT")
    public void setProjectEnquiryCount(int projectEnquiryCount) {
        project.setProjectEnquiryCount(projectEnquiryCount);
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

    @Field("MINSIZE")
    public void setMinSize(Double minSize) {
        project.setMinSize(minSize);
    }

    @Field("MAXSIZE")
    public void setMaxSize(Double maxSize) {
        project.setMaxSize(maxSize);
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
        locality.setCityId(cityId);
        
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

    @Field("CITY_MIN_ZOOM_LEVEL")
    public void setMinZoomLevel(Integer minZoomLevel) {
        city.setMinZoomLevel(minZoomLevel);
    }

    @Field("CITY_MAX_ZOOM_LEVEL")
    public void setMaxZoomLevel(Integer maxZoomLevel) {
        city.setMaxZoomLevel(maxZoomLevel);
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
    public void setProcessedLatitude(Double processedLatitude) {
        property.setProcessedLatitude(processedLatitude);
    }

    @Field("PROCESSED_LONGITUDE")
    public void setProcessedLongitude(Double processedLongitude) {
        property.setProcessedLongitude(processedLongitude);
    }

    @Field("BUDGET")
    public void setBudget(Double budget) {
        property.setBudget(budget);
    }

    @Field("PROJECT_ID_BEDROOM")
    public void setProjectIdBedroom(String projectIdBedroom) {
        property.setProjectIdBedroom(projectIdBedroom);
    }

    @Field("LOCALITY_LABEL_PRIORITY")
    public void setLocalityLabelPriority(String localityLabelPriority) {
        project.setLocalityLabelPriority(localityLabelPriority);
    }

    @Field("SUBURB_LABEL_PRIORITY")
    public void setSuburbLabelPriority(String suburbLabelPriority) {
        project.setSuburbLabelPriority(suburbLabelPriority);
    }

    @Field("BUILDER_LABEL_PRIORITY")
    public void setBuilderLabelPriority(String builderLabelPriority) {
        project.setBuilderLabelPriority(builderLabelPriority);
    }

    @Field("__RADIUS__")
    public void setDerivedMaxRadius(double radius) {
        project.getLocality().setMaxRadius(radius);
    }

    @Field("LOCALITY_URL")
    public void setLocalityURL(String url) {
        locality.setUrl(url);
    }

    @Field("SUBURB_URL")
    public void setSuburbURL(String url) {
        suburb.setUrl(url);
    }

    @Field("CITY_URL")
    public void setCityURL(String url) {
        city.setUrl(url);
    }

    @Field("LOCALITY_LATITUDE")
    public void setLocalityLatitude(Double latitude) {
        locality.setLatitude(latitude);
    }

    @Field("LOCALITY_LONGITUDE")
    public void setLocalityLongitude(Double longitude) {
        locality.setLongitude(longitude);
    }

    @Field("LOCALITY_DESCRIPTION")
    public void setLocalityDescription(String description) {
        locality.setDescription(description);
    }

    @Field("SUBURB_DESCRIPTION")
    public void setSuburbDescription(String description) {
        suburb.setDescription(description);
    }

    @Field("CITY_DESCRIPTION")
    public void setCityDescription(String description) {
        city.setDescription(description);
    }

    @Field("DISPLAY_PRIORITY")
    public void setDisplayPriority(int displayPriority) {
        city.setDisplayPriority(displayPriority);
    }

    @Field("CITY_DISPLAY_ORDER")
    public void setCityDisplayOrder(int cityDisplayOrder) {
        city.setDisplayOrder(cityDisplayOrder);
    }

    @Field("LOCALITY_PRIORITY")
    public void setLocalityPriority(Integer localityPriority) {
        locality.setPriority(localityPriority);
    }

    @Field("SUBURB_PRIORITY")
    public void setSuburbPriority(int suburbPriority) {
        suburb.setPriority(suburbPriority);
    }

    @Field("RESALE_PRICE_PER_UNIT_AREA")
    public void setResalePricePerUnitArea(Double resalePricePerUnitArea) {
        property.setResalePricePerUnitArea(resalePricePerUnitArea);
    }

    @Field("RESALE_PRICE")
    public void setResalePrice(Double resalePrice) {
        property.setResalePrice(resalePrice);
    }

    @Field("CITY_PRICE_PER_UNIT_AREA")
    public void setCityPricePerUnitArea(Double cityPricePerUnitArea) {
        city.setAvgPricePerUnitArea(cityPricePerUnitArea);
    }

    @Field("LOCALITY_PRICE_PER_UNIT_AREA")
    public void setLocalityPricePerUnitArea(Double localityPricePerUnitArea) {
        locality.setAvgPricePerUnitArea(localityPricePerUnitArea);
    }

    @Field("SUBURB_PRICE_PER_UNIT_AREA")
    public void setSuburbPricePerUnitArea(Double suburbPricePerUnitArea) {
        suburb.setAvgPricePerUnitArea(suburbPricePerUnitArea);
    }

    @Field("PROJECT_PRICE_RISE_TIME")
    public void setProjectPriceRiseTime(String projectPriceRiseTime) {
        project.setAvgPriceRiseMonths(Integer.valueOf(projectPriceRiseTime));
    }

    @Field("PROJECT_PRICE_RISE")
    public void setProjectPriceRise(Double projectPriceRise) {
        project.setAvgPriceRisePercentage(projectPriceRise);
    }

    @Field("CITY_PRICE_RISE_TIME")
    public void setCityPriceRiseTime(Integer cityPriceRiseTime) {
        city.setAvgPriceRiseMonths(cityPriceRiseTime);
    }

    @Field("CITY_PRICE_RISE")
    public void setCityPriceRise(Double cityPriceRise) {
        city.setAvgPriceRisePercentage(cityPriceRise);
    }

    @Field("SUBURB_PRICE_RISE_TIME")
    public void setSuburbPriceRiseTime(Integer suburbPriceRiseTime) {
        suburb.setAvgPriceRiseMonths(suburbPriceRiseTime);
    }

    @Field("SUBURB_PRICE_RISE")
    public void setSuburbPriceRise(Double suburbPriceRise) {
        suburb.setAvgPriceRisePercentage(suburbPriceRise);
    }

    @Field("LOCALITY_PRICE_RISE_TIME")
    public void setLocalityPriceRiseTime(Integer localityPriceRiseTime) {
        locality.setAvgPriceRiseMonths(localityPriceRiseTime);
    }

    @Field("LOCALITY_PRICE_RISE")
    public void setLocalityPriceRise(Double localityPriceRise) {
        locality.setAvgPriceRisePercentage(localityPriceRise);
    }

    @Field("LOCALITY_DOMINANT_UNIT_TYPE")
    public void setLocalityDominantUnitType(String dominantUnitType) {
        locality.setDominantUnitType(dominantUnitType);
    }

    @Field("PROJECT_DOMINANT_UNIT_TYPE")
    public void setProjectDominantUnitType(String dominantUnitType) {
        project.setDominantUnitType(dominantUnitType);
    }

    @Field("SUBURB_DOMINANT_UNIT_TYPE")
    public void setSuburbDominantUnitType(String dominantUnitType) {
        suburb.setDominantUnitType(dominantUnitType);
    }

    @Field("CITY_DOMINANT_UNIT_TYPE")
    public void setCityDominantUnitType(String dominantUnitType) {
        city.setDominantUnitType(dominantUnitType);
    }

    @Field("LOCALITY_MAX_RADIUS")
    public void setLocalityMaxRadius(Double dominantUnitType) {
        locality.setMaxRadius(dominantUnitType);
    }

    @Field("PAYMENT_PLAN_URL")
    public void setPaymentPlanUrl(String paymentPlanUrl) {
        project.setPaymentPlanUrl(paymentPlanUrl);
    }

    @Field("SERVANT_ROOM")
    public void setServantRoom(int servantRoom) {
        property.setServantRoom(servantRoom);
    }

    @Field("POOJA_ROOM")
    public void setPoojaRoom(int poojaRoom) {
        property.setPoojaRoom(poojaRoom);
    }

    @Field("AVAILABILITY")
    public void setAvailability(Integer availability) {
        project.setDerivedAvailability(availability);
    }

    @Field("PROJECT_OFFER")
    public void setProjectOffer(String[] projectOffer) {
        project.addOffers(projectOffer);
    }

    @Field("NUMBER_OF_PROJECT_DISCUSSION")
    public void setTotalProjectDiscussion(Integer totalProjectDiscussion) {
        project.setTotalProjectDiscussion(totalProjectDiscussion);
    }

    @Field("PROJECT_MAIN_IMAGE")
    public void setProjectMainImage(String projectMainImage) {
        project.setImageURL(projectMainImage);
        project.getMainImage().setAbsolutePath(projectMainImage);
    }

    @Field("BUILDER_LOGO_IMAGE")
    public void setBuilderLogoImage(String builderLogoImage) {
        builder.setImageURL(builderLogoImage);
        builder.setAbsolutePath(builderLogoImage);
    }

    @Field("PROJECT_LAST_UPDATED_TIME")
    public void setProjectLastUpdatedTime(Date lastUpdatedTime) {
        project.setLastUpdatedDate(lastUpdatedTime);
    }

    @Field("PROJECT_SUPPLY")
    public void setProjectSupply(int supply) {
        project.setSupply(supply);
    }

    @Field("PRE_LAUNCH_DATE")
    public void setPreLaunchDate(Date preLaunchDate) {
        project.setPreLaunchDate(preLaunchDate);
    }

    @Field("PROJECT_SIZE")
    public void setProjectSize(double projectSize) {
        project.setSizeInAcres(projectSize);
    }

    @Field("LOCALITY_OVERVIEW_URL")
    public void setLocalityOverviewUrl(String localityOverviewUrl) {
        locality.setOverviewUrl(localityOverviewUrl);
    }

    @Field("CITY_OVERVIEW_URL")
    public void setCityOverviewUrl(String cityOverviewUrl) {
        city.setOverviewUrl(cityOverviewUrl);
    }

    @Field("SUBURB_OVERVIEW_URL")
    public void setSuburbOverviewUrl(String suburbOverviewUrl) {
        suburb.setOverviewUrl(suburbOverviewUrl);
    }

    @Field("__PROJECT_GEO_DISTANCE__")
    public void setProjectGeoDistance(double geoDistance) {
        project.setGeoDistance(geoDistance);
    }

    @Field("__PROPERTY_GEO_DISTANCE__")
    public void setPropertyGeoDistance(double geoDistance) {
        project.setGeoDistance(geoDistance);
    }

    @Field("__LOCALITY_GEO_DISTANCE__")
    public void setLocalityGeoDistance(double geoDistance) {
        locality.setGeoDistance(geoDistance);
    }

    @Field("PROJECT_VIDEOS_COUNT")
    public void setProjectVideosCount(int projectVideosCount) {
        project.setVideosCount(projectVideosCount);
    }

    @Field("PROJECT_IMAGES_COUNT")
    public void setProjectImagesCount(int projectImagesCount) {
        project.setImagesCount(projectImagesCount);
    }

    @Field("PROJECT_AVG_PRICE_PER_UNIT_AREA")
    public void setProjectAvgPricePerUnitArea(double projectAvgPriceUnitArea) {
        project.setAvgPricePerUnitArea(projectAvgPriceUnitArea);
    }

    @Field("IS_PRIMARY")
    public void setIsPrimary(boolean isPrimary) {
        project.setPrimary(isPrimary);
    }

    @Field("IS_SOLD_OUT")
    public void setIsSoldOut(boolean isSoldOut) {
        project.setSoldOut(isSoldOut);
    }

    @Field("MIN_RESALE_OR_PRIMARY_PRICE")
    public void setMinResaleOrPrimaryPrice(double minResaleOrPrimaryPrice) {
        property.setMinResaleOrPrimaryPrice(minResaleOrPrimaryPrice);
    }

    @Field("MAX_RESALE_OR_PRIMARY_PRICE")
    public void setMaxResaleOrPrimaryPrice(double maxResaleOrPrimaryPrice) {
        property.setMaxResaleOrPrimaryPrice(maxResaleOrPrimaryPrice);
    }

    @Field("LOCALITY_PRICE_RISE_6MONTHS")
    public void setLocalityPriceRise6Months(Double localityPriceRise6Months) {
        locality.setPriceRise6Months(localityPriceRise6Months);
    }

    @Field("PROJECT_PRICE_RISE_6MONTHS")
    public void setProjectPriceRise6Months(Double projectPriceRise6Months) {
        project.setPriceRise6Months(projectPriceRise6Months);
    }

    @Field("SUBURB_PROJECT_COUNT")
    public void setSuburbProjectCount(Integer suburbProjectCount) {
        suburb.setProjectCount(suburbProjectCount);
    }

    @Field("LOCALITY_PROJECT_COUNT")
    public void setLocalityProjectCount(Integer localityProjectCount) {
        locality.setProjectCount(localityProjectCount);
    }

    @Field("BUILDER_PROJECT_COUNT")
    public void setBuilderProjectCount(Integer builderProjectCount) {
        builder.setProjectCount(builderProjectCount);
    }

    @Field("LOCALITY_ENQUIRY_COUNT")
    public void setLocalityEnquiryCount(int localityEnquiryCount) {
        locality.setLocalityEnquiryCount(localityEnquiryCount);
    }

    @Field("CITY_ENQUIRY_COUNT")
    public void setCityEnquiryCount(int cityEnquiryCount) {
        city.setCityEnquiryCount(cityEnquiryCount);
    }

    @Field("SUBURB_ENQUIRY_COUNT")
    public void setSuburbEnquiryCount(int suburbEnquiryCount) {
        suburb.setSuburbEnquiryCount(suburbEnquiryCount);
    }

    @Field("BUILDER_ENQUIRY_COUNT")
    public void setBuilderEnquiryCount(int builderEnquiryCount) {
        builder.setBuilderEnquiryCount(builderEnquiryCount);
    }

    @Field("PROJECT_VIEW_COUNT")
    public void setProjectViewCount(int projectViewCount) {
        project.setProjectViewCount(projectViewCount);
    }

    @Field("LOCALITY_VIEW_COUNT")
    public void setLocalityViewCount(int localityViewCount) {
        locality.setLocalityViewCount(localityViewCount);
    }

    @Field("CITY_VIEW_COUNT")
    public void setCityViewCount(int cityViewCount) {
        city.setCityViewCount(cityViewCount);
    }

    @Field("SUBURB_VIEW_COUNT")
    public void setSuburbViewCount(int suburbViewCount) {
        suburb.setSuburbViewCount(suburbViewCount);
    }

    @Field("BUILDER_VIEW_COUNT")
    public void setBuilderViewCount(int builderViewCount) {
        builder.setBuilderViewCount(builderViewCount);
    }

    @Field("TYPEAHEAD_LOCALITY_UNITS_LAUNCHED_6MONTHS")
    public void setLocalityUnitsLaunched6Months(Integer localityUnitsLaunched6Months) {
        locality.setLocalityUnitsLaunched6Months(localityUnitsLaunched6Months);
    }

    @Field("TYPEAHEAD_LOCALITY_UNITS_SOLD_6MONTHS")
    public void setLocalityUnitsSold6Months(Integer localityUnitsSold6Months) {
        locality.setLocalityUnitsSold6Months(localityUnitsSold6Months);
    }

    @Field("TYPEAHEAD_LOCALITY_UNITS_DELIVERED_6MONTHS")
    public void setLocalityUnitsDelivered6Months(Integer localityUnitsDelivered6Months) {
        locality.setLocalityUnitsDelivered6Months(localityUnitsDelivered6Months);
    }

    @Field("PROJECT_SAFETY_SCORE")
    public void setProjectSafetyScore(Double projectSafetyScore) {
        project.setSafetyScore(projectSafetyScore);
    }

    @Field("PROJECT_LIVABILITY_SCORE")
    public void setProjectLivabilityScore(Float projectLivabilityScore) {
        project.setLivabilityScore(projectLivabilityScore);
    }

    @Field("LOCALITY_SAFETY_SCORE")
    public void setLocalitySafetyScore(Double localitySafetyScore) {
        locality.setSafetyScore(localitySafetyScore);
    }

    @Field("LOCALITY_LIVABILITY_SCORE")
    public void setLocalityLivabilityScore(Float localityLivabilityScore) {
        locality.setLivabilityScore(localityLivabilityScore);
    }

    @Field("BUILDER_URL")
    public void setBuilderUrl(String builderUrl) {
        builder.setUrl(builderUrl);
    }
    
    @Field("BUILDER_LOCALITY_COUNT")
    public void setBuilderLocalityCount(Integer builderLocalityCount) {
        builder.setBuilderLocalityCount(builderLocalityCount);
    } 
    
    @Field("BUILDER_CITIES")
    public void setBuilderCities(List<String> builderCities) {
        builder.setBuilderCities(builderCities);
    }

    @Field("BUILDER_IMAGE_ALTTEXT")
    public void setBuilderImgAltText(String builderImgAltText) {
        builder.setImageAltText(builderImgAltText);
    }

    @Field("BUILDER_IMAGE_TITLE")
    public void setBuilderImgTitle(String builderImgTitle) {
       builder.setImageTitle(builderImgTitle);
    }

    @Field("PROJECT_IMAGE_ALTTEXT")
    public void setProjectImgAltText(String projectImgAltText) {
        project.getMainImage().setAltText(projectImgAltText);
    }
    
    @Field("PROJECT_IMAGE_TITLE")
    public void setProjectImgTitle(String projectImgTitle) {
        project.getMainImage().setTitle(projectImgTitle);
    }

    @Field("PROJECT_LOCALITY_SCORE")
    public void setProjectLocalityScore(Float projectLocalityScore) {
        project.setProjectLocalityScore(projectLocalityScore);
    }

    @Field("PROJECT_STATUS")
    public void setProjectStatus(String projectStatus) {
        project.setProjectStatus(projectStatus);
    }
    
    @Field("PROJECT_SOCIETY_SCORE")
    public void setProjectSocietyScore(Float projectSocietyScore) {
        project.setProjectSocietyScore(projectSocietyScore);
    }

    @Field("PROJECT_SAFETY_RANK")
    public void setProjectSafetyRank(Integer projectSafetyRank) {
        project.setProjectSafetyRank(projectSafetyRank);
    }

    @Field("PROJECT_LIVABILITY_RANK")
    public void setProjectLivabilityRank(Integer projectLivabilityRank) {
        project.setProjectLivabilityRank(projectLivabilityRank);
    }

    @Field("LOCALITY_SAFETY_RANK")
    public void setLocalitySafetyRank(Integer localitySafetyRank) {
       locality.setLocalitySafetyRank(localitySafetyRank);
    }

    @Field("LOCALITY_LIVABILITY_RANK")
    public void setLocalityLivabilityRank(Integer localityLivabilityRank) {
        locality.setLocalityLivabilityRank(localityLivabilityRank);
    }

    @Field("IMAGE_TYPE_COUNT")
    public void setImageTypeCount(String imageJson) {
        Gson gson=new Gson();
        Map<String, Number> object = new HashMap<String, Number>();
        Map<String, Integer> imageTypeCount = new HashMap<String, Integer>();
        object = (Map<String, Number>) gson.fromJson(imageJson, object.getClass());
        for(Map.Entry<String, Number> entry:object.entrySet()){
            imageTypeCount.put(entry.getKey(), entry.getValue().intValue());
        }
        project.setImageTypeCount(imageTypeCount);
        property.setImageTypeCount(imageTypeCount);
    }

    @Field("HAS_3D_IMAGES")
    public void setHasProject3DImages(boolean hasProject3DImages) {
        project.setHas3DImages(hasProject3DImages);
    }

    @Field(value = "PROJECT_MAX_SAFETY_SCORE")
    public void setProjectMaxSafetyScore(Double projectMaxSafetyScore) {
        locality.setProjectMaxSafetyScore(projectMaxSafetyScore);
    }
    
    @Field(value = "PROJECT_MIN_SAFETY_SCORE")     
    public void setProjectMinSafetyScore(Double projectMinSafetyScore) {
        locality.setProjectMinSafetyScore(projectMinSafetyScore);
    }

    @Field(value = "PROJECT_MAX_LIVABILITY_SCORE")
    public void setProjectMaxLivabilityScore(Double projectMaxLivabilityScore) {
        locality.setProjectMaxLivabilityScore(projectMaxLivabilityScore);
    }

    @Field(value = "PROJECT_MIN_LIVABILITY_SCORE")
    public void setProjectMinLivabilityScore(Double projectMinLivabilityScore) {
        locality.setProjectMinLivabilityScore(projectMinLivabilityScore);
    }
    
    @Field(value = "LOCALITY_MAX_SAFETY_SCORE")
    public void setLocalityMaxSafetyScore(Double projectMaxSafetyScore) {
        city.setLocalityMaxSafetyScore(projectMaxSafetyScore);
    }
    
    @Field(value = "LOCALITY_MIN_SAFETY_SCORE")     
    public void setLocalityMinSafetyScore(Double projectMinSafetyScore) {
        city.setLocalityMinSafetyScore(projectMinSafetyScore);
    }

    @Field(value = "LOCALITY_MAX_LIVABILITY_SCORE")
    public void setLocalityMaxLivabilityScore(Double projectMaxLivabilityScore) {
        city.setLocalityMaxLivabilityScore(projectMaxLivabilityScore);
    }

    @Field(value = "LOCALITY_MIN_LIVABILITY_SCORE")
    public void setLocalityMinLivabilityScore(Double projectMinLivabilityScore) {
        city.setLocalityMinLivabilityScore(projectMinLivabilityScore);
    }
}