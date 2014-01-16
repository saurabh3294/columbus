/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * 
 * @author mukand
 */
@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name="RESI_PROJECT")
@JsonFilter("fieldFilter")
public class Project implements BaseModel {
    public static enum NestedProperties {
        builderLabel(new String[]{"builder", "name"}),
        cityLabel(new String[]{"locality", "suburb", "city", "label"}),
        suburbLabel(new String[]{"locality", "suburb", "label"}),
        cityId(new String[]{"locality", "suburb", "city", "id"}),
        suburbId(new String[]{"locality", "suburb", "id"}),
        localityLabel(new String[]{"locality", "label"}),
        builderImageURL(new String[]{"builder", "imageURL"}),
        bedrooms(new String[]{"properties", "bedrooms"}),
        bathrooms(new String[]{"properties", "bathrooms"}),
        pricePerUnitArea(new String[]{"properties", "pricePerUnitArea"}),
        size(new String[]{"properties", "size"}),
        unitName(new String[]{"properties", "unitName"}),
        unitType(new String[]{"properties", "unitType"});

        private String[] fields;

        private NestedProperties(String[] fields) {
            this.fields = fields;
        }

        public String[] getFields() {
            return fields;
        }
    };
    
   /* @Id
    @FieldMetaInfo( displayName = "DB Project Id",  description = "DB Project Id")
    @Column(name="PROJECT_ID", insertable=false, updatable=false)
    private Integer id;*/
    
    @Id	
    @FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
    @Field(value = "PROJECT_ID")
    @Column(name="PROJECT_ID", insertable=false, updatable=false)
    private int projectId;
	
    @Deprecated
	@FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
    @Field(value = "LOCALITY_ID")
	@Column(name="LOCALITY_ID")
    private int localityId;
	
    @ManyToOne
    @JoinColumn(name="LOCALITY_ID", insertable=false, updatable=false)
    private Locality locality;

    @FieldMetaInfo( displayName = "Builder Id",  description = "Builder Id")
    @Field(value = "BUILDER_ID")
	@Column(name="BUILDER_ID")
    private int builderId;

    @ManyToOne
    @JoinColumn(name="BUILDER_ID", insertable=false, updatable=false)
    private Builder builder;

    @Transient
    @OneToMany(mappedBy="project")
    private List<Property> properties;
    
    @FieldMetaInfo( displayName = "Project Name",  description = "Project Name")
    @Field(value = "PROJECT_NAME")
    @Column(name="PROJECT_NAME")
    private String name;

    @Transient
    @FieldMetaInfo( displayName = "Project Types",  description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    private String unitTypes;

    @FieldMetaInfo( displayName = "Launch Date",  description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    @Column(name="LAUNCH_DATE")
    private Date launchDate;

    @FieldMetaInfo( displayName = "Address",  description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    @Column(name="PROJECT_ADDRESS")
    private String address;

    @Transient
    @FieldMetaInfo( displayName = "Computed Priority",  description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    private double computedPriority;
    
    @Transient
    @FieldMetaInfo( displayName = "Project enquiry count",  description = "Project enquiry count")
    @Field(value = "PROJECT_ENQUIRY_COUNT")
    private int projectEnquiryCount;

    @FieldMetaInfo( displayName = "Assigned Priority",  description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    @Column(name="DISPLAY_ORDER")
    private int assignedPriority;

    @FieldMetaInfo( displayName = "Assigned Locality Priority",  description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    @Column(name="DISPLAY_ORDER_LOCALITY")
    private int assignedLocalityPriority;

    @FieldMetaInfo( displayName = "Assigned Suburb Priority",  description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    @Column(name="DISPLAY_ORDER_SUBURB")
    private int assignedSuburbPriority;
    
    @FieldMetaInfo( displayName = "Possession Date",  description = "Possession Date")
    @Field(value = "PROMISED_COMPLETION_DATE")
    @Column(name="PROMISED_COMPLETION_DATE")
    private Date possessionDate;

    @FieldMetaInfo( displayName = "Submitted Date",  description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    @Column(name="SUBMITTED_DATE")
    private Date submittedDate;

    @FieldMetaInfo( displayName = "Image URL",  description = "Image URL")
    @Transient
    private String imageURL;

    @Transient
    @FieldMetaInfo( displayName = "Offer",  description = "Offer")
    @Field(value = "OFFER")
    private String offer;

    @FieldMetaInfo( displayName = "Offer Heading",  description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    @Column(name="OFFER_HEADING")
    private String offerHeading;

    @FieldMetaInfo( displayName = "Offer Description",  description = "Offer Description")
    @Field(value = "OFFER_DESC")
    @Column(name="OFFER_DESC")
    private String offerDesc;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value = "PROJECT_URL")
    @Column(name="PROJECT_URL")
    private String URL;

    @FieldMetaInfo( displayName = "Latitude",  description = "Latitude")
    @Field(value = "LATITUDE")
    @Column(name="LATITUDE")
    private Double latitude;

    @FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
    @Field(value = "LONGITUDE")
    @Column(name="LONGITUDE")
    private Double longitude;

    @Transient
    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Min Price Per Unit Area",  description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minPricePerUnitArea;

    @Transient
    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Max Price Per Unit Area",  description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxPricePerUnitArea;

    @Transient
    @FieldMetaInfo( displayName = "Min Size",  description = "Min Size")
    @Field(value = "MINSIZE")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minSize;

    @Transient
    @FieldMetaInfo( displayName = "Max Size",  description = "Max Size")
    @Field(value = "MAXSIZE")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxSize;

    @Transient
    @FieldMetaInfo( displayName = "Min Price",  description = "Min Price")
    @Field(value = "MIN_BUDGET")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minPrice;

    @Transient
    @FieldMetaInfo( displayName = "Max Price",  description = "Max Price")
    @Field(value = "MAX_BUDGET")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxPrice;

    @Transient
    @FieldMetaInfo( displayName = "Min Bedroooms",  description = "Min Bedroooms")
    private int minBedrooms;

    @Transient
    @FieldMetaInfo( displayName = "Max Bedroooms",  description = "Max Bedroooms")
    private int maxBedrooms;

    @FieldMetaInfo( displayName = "Project Status",  description = "Project Status")
    @Field(value = "PROJECT_STATUS")
    @Column(name="PROJECT_STATUS")
    private String projectStatus;

    @Field(value = "IS_RESALE")
    @Column(name="FORCE_RESALE")
    private boolean isResale;

    @FieldMetaInfo( displayName = "Project Description",  description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    @Column(name="PROJECT_DESCRIPTION")
    private String description;

    @Transient
    @FieldMetaInfo( displayName = "Total Units",  description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    private Integer totalUnits;

    @FieldMetaInfo( displayName = "size in acres",  description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    @Column(name="PROJECT_SIZE")
    private Double sizeInAcres;

    @Transient
    @Field(value = "GEO")
    private List<String> geo;

    @Transient
    @Field(value="PROJECT_STATUS_BEDROOM")
    @JsonIgnore
    private String projectStatusBedroom;

    @Transient
    @Field(value="MEASURE")
    private String propertySizeMeasure;

    @Transient
    private Set<String> propertyUnitTypes = new HashSet<>();

    @Transient
    private List<Image> images;
    
    @Transient
    @Field(value="LOCALITY_LABEL_PRIORITY")
    private String localityLabelPriority;
    
    @Transient
    @Field(value="SUBURB_LABEL_PRIORITY")
    private String suburbLabelPriority;

    @Transient
    @Field(value="BUILDER_LABEL_PRIORITY")
    private String builderLabelPriority;
    
    @Transient
    private Set<Integer> distinctBedrooms = new HashSet<>();
    
    @Transient
    private Double minResalePrice;
    
    @Transient
    private Double maxResalePrice;
    
    @Transient
    private Double avgPriceRisePercentage;
    
    @Transient
    private Integer avgPriceRiseMonths;

    @FieldMetaInfo(displayName="AVAILABILITY", description="AVAILABILITY")
    @Column(name="AVAILABILITY")
    private Integer derivedAvailability ;

	@FieldMetaInfo(displayName="PRE LAUNCH Date", description="PRE LAUNCH Date")
    @Column(name="PRE_LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date preLaunchDate ;
	
	 @FieldMetaInfo(displayName="YOUTUBE VEDIO", description="YOUTUBE VEDIO")
	 @Column(name="YOUTUBE_VEDIO")
	 private String youtubeVideo ;
	 
	 @FieldMetaInfo(displayName="NO OF FLATS", description="NO OF FLATS")
	 @Column(name="NO_OF_FLATES")
	 private Integer supply ;
	 
	 @OneToMany(fetch=FetchType.LAZY)
	 @Fetch(FetchMode.JOIN)
	 @JoinColumn(insertable=false, updatable=false, name="PROJECT_ID")
	 @Transient
	 @Deprecated
	 private List<ProjectAmenity> projectAmenity;
	 
	 @Transient
	 private Integer totalProjectDiscussion;
	 
	 @Transient
	 private List<LocalityAmenity> neighborhood;
	 
	 @JsonUnwrapped
	 @Transient
	 private ProjectSpecification projectSpecification;
	 
	 @Transient
	 private List<ProjectCMSAmenity> projectAmenities;
    
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
    	this.locality = locality;
    }

    public int getBuilderId() {
        return builderId;
    }

    public void setBuilderId(int builderId) {
        this.builderId = builderId;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitTypes() {
        return unitTypes;
    }

    public void setUnitTypes(String unitTypes) {
        this.unitTypes = unitTypes;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getComputedPriority() {
        return computedPriority;
    }

    public void setComputedPriority(double computedPriority) {
        this.computedPriority = computedPriority;
    }

    
    public int getProjectEnquiryCount() {
		return projectEnquiryCount;
	}

	public void setProjectEnquiryCount(int projectEnquiryCount) {
		this.projectEnquiryCount = projectEnquiryCount;
	}

	public int getAssignedPriority() {
        return assignedPriority;
    }

    public void setAssignedPriority(int assignedPriority) {
        this.assignedPriority = assignedPriority;
    }

    public int getAssignedLocalityPriority() {
        return assignedLocalityPriority;
    }

    public void setAssignedLocalityPriority(int assignedLocalityPriority) {
        this.assignedLocalityPriority = assignedLocalityPriority;
    }

    public int getAssignedSuburbPriority() {
        return assignedSuburbPriority;
    }

    public void setAssignedSuburbPriority(int assignedSuburbPriority) {
        this.assignedSuburbPriority = assignedSuburbPriority;
    }

    public Date getPossessionDate() {
        return possessionDate;
    }

    public void setPossessionDate(Date possessionDate) {
        this.possessionDate = possessionDate;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getOfferHeading() {
        return offerHeading;
    }

    public void setOfferHeading(String offerHeading) {
        this.offerHeading = offerHeading;
    }

    public String getOfferDesc() {
        return offerDesc;
    }

    public void setOfferDesc(String offerDesc) {
        this.offerDesc = offerDesc;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    public void setMinPricePerUnitArea(Double minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public Double getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    public void setMaxPricePerUnitArea(Double maxPricePerUnitArea) {
        this.maxPricePerUnitArea = maxPricePerUnitArea;
    }

    public Double getMinSize() {
        return minSize;
    }

    public void setMinSize(Double minSize) {
        this.minSize = minSize;
    }

    public Double getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Double maxSize) {
        this.maxSize = maxSize;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public boolean isIsResale() {
        return isResale;
    }

    public void setResale(boolean isResale) {
        this.isResale = isResale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Double getSizeInAcres() {
        return sizeInAcres;
    }

    public void setSizeInAcres(Double sizeInAcres) {
        this.sizeInAcres = sizeInAcres;
    }

    public String getProjectStatusBedroom() {
        return projectStatusBedroom;
    }

    public void setProjectStatusBedroom(String projectStatusBedroom) {
        this.projectStatusBedroom = projectStatusBedroom;
    }

    public String getPropertySizeMeasure() {
        return propertySizeMeasure;
    }

    public void setPropertySizeMeasure(String propertySizeMeasure) {
        this.propertySizeMeasure = propertySizeMeasure;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getGeo() {
        return geo;
    }

    public void setGeo(List<String> geo) {
        this.geo = geo;
    }

    public Set<String> getPropertyUnitTypes() {
        return propertyUnitTypes;
    }

    public void setPropertyUnitTypes(Set<String> propertyUnitTypes) {
        this.propertyUnitTypes = propertyUnitTypes;
    }
    
    public void addPropertyUnitType(String propertyUnitType){
    	this.propertyUnitTypes.add(propertyUnitType);
    }

    public int getMinBedrooms() {
        return minBedrooms;
    }

    public void setMinBedrooms(int minBedrooms) {
        this.minBedrooms = minBedrooms;
    }

    public int getMaxBedrooms() {
        return maxBedrooms;
    }

    public void setMaxBedrooms(int maxBedrooms) {
        this.maxBedrooms = maxBedrooms;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

	public String getLocalityLabelPriority() {
		return localityLabelPriority;
	}

	public void setLocalityLabelPriority(String localityLabelPriority) {
		this.localityLabelPriority = localityLabelPriority;
	}

	public String getSuburbLabelPriority() {
		return suburbLabelPriority;
	}

	public void setSuburbLabelPriority(String suburbLabelPriority) {
		this.suburbLabelPriority = suburbLabelPriority;
	}

	public String getBuilderLabelPriority() {
		return builderLabelPriority;
	}

	public void setBuilderLabelPriority(String builderLabelPriority) {
		this.builderLabelPriority = builderLabelPriority;
	}

	public Set<Integer> getDistinctBedrooms() {
		return distinctBedrooms;
	}

	public void setDistinctBedrooms(Set<Integer> bedrooms) {
		this.distinctBedrooms = bedrooms;
	}
	
	public void addBedrooms(int bedroom){
		this.distinctBedrooms.add(bedroom);
	}

	public Double getMinResalePrice() {
		return minResalePrice;
	}

	public void setMinResalePrice(Double minResalePrice) {
		this.minResalePrice = minResalePrice;
	}

	public Double getMaxResalePrice() {
		return maxResalePrice;
	}

	public void setMaxResalePrice(Double maxResalePrice) {
		this.maxResalePrice = maxResalePrice;
	}

    public Double getAvgPriceRisePercentage() {
        return avgPriceRisePercentage;
    }

    public void setAvgPriceRisePercentage(Double avgPriceRisePercentage) {
        this.avgPriceRisePercentage = avgPriceRisePercentage;
    }

    public Integer getAvgPriceRiseMonths() {
        return avgPriceRiseMonths;
    }

    public void setAvgPriceRiseMonths(Integer avgPriceRiseMonths) {
        this.avgPriceRiseMonths = avgPriceRiseMonths;
    }

    public boolean isResale() {
        return isResale;
    }

	/*public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
*/
	public Integer getAvailability() {
		return derivedAvailability;
	}

	public void setAvailability(Integer availability) {
		this.derivedAvailability = availability;
	}

	public Date getPreLaunchDate() {
		return preLaunchDate;
	}

	public void setPreLaunchDate(Date preLaunchDate) {
		this.preLaunchDate = preLaunchDate;
	}

	public Integer getDerivedAvailability() {
		return derivedAvailability;
	}

	public void setDerivedAvailability(Integer derivedAvailability) {
		this.derivedAvailability = derivedAvailability;
	}

	public String getYoutubeVideo() {
		return youtubeVideo;
	}

	public void setYoutubeVideo(String youtubeVideo) {
		this.youtubeVideo = youtubeVideo;
	}

	public Integer getSupply() {
		return supply;
	}

	public void setSupply(Integer supply) {
		this.supply = supply;
	}

	public List<ProjectAmenity> getProjectAmenity() {
		return projectAmenity;
	}

	public void setProjectAmenity(List<ProjectAmenity> projectAmenity) {
		this.projectAmenity = projectAmenity;
	}

	public Integer getTotalProjectDiscussion() {
		return totalProjectDiscussion;
	}

	public void setTotalProjectDiscussion(Integer totalProjectDiscussion) {
		this.totalProjectDiscussion = totalProjectDiscussion;
	}

	public List<LocalityAmenity> getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(List<LocalityAmenity> neighborhood) {
		this.neighborhood = neighborhood;
	}

	public ProjectSpecification getProjectSpecification() {
		return projectSpecification;
	}

	public void setProjectSpecification(ProjectSpecification projectSpecification) {
		this.projectSpecification = projectSpecification;
	}

	public List<ProjectCMSAmenity> getProjectAmenities() {
		return projectAmenities;
	}

	public void setProjectAmenities(List<ProjectCMSAmenity> projectAmenities) {
		this.projectAmenities = projectAmenities;
	}

}
