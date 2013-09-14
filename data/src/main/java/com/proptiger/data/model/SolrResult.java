/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

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

    @Field("TYPE_ID")
    public void setTypeId(int typeId) {
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
    public void setPricePerUnitArea(double pricePerUnitArea) {
        property.setPricePerUnitArea(pricePerUnitArea);
    }

    @Field("SIZE")
    public void setSize(double size) {
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
    public void setProjectId(int projectId) {
        project.setId(projectId);
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
    public void setLatitude(double latitude) {
        project.setLatitude(latitude);
    }

    @Field("LONGITUDE")
    public void setLongitude(double longitude) {
        project.setLongitude(longitude);
    }

    @Field("MIN_PRICE_PER_UNIT_AREA")
    public void setMinPricePerUnitArea(double minPricePerUnitArea) {
        project.setMinPricePerUnitArea(minPricePerUnitArea);
    }

    @Field("MAX_PRICE_PER_UNIT_AREA")
    public void setMaxPricePerUnitArea(double maxPricePerUnitArea) {
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
    public void setNorthEastLatitude(double northEastLatitude) {
        city.setNorthEastLatitude(northEastLatitude);
    }

    @Field("NORTH_EAST_LONGITUDE")
    public void setNorthEastLongitude(double northEastLongitude) {
        city.setNorthEastLongitude(northEastLongitude);
    }

    @Field("SOUTH_WEST_LATITUDE")
    public void setSouthWestLatitude(double southWestLatitude) {
        city.setSouthWestLatitude(southWestLatitude);
    }

    @Field("SOUth_WEST_LONGITUDE")
    public void setSouthWestLongitude(double southWestLongitude) {
        city.setSouthWestLongitude(southWestLongitude);
    }

    @Field("CENTER_LATITUDE")
    public void setCenterLatitude(double centerLatitude) {
        city.setCenterLatitude(centerLatitude);
    }

    @Field("CENTER_LONGITUDE")
    public void setCenterLongitude(double centerLongitude) {
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
