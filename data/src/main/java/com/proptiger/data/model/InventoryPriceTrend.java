package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name="cms.d_inventory_prices")
@SuppressWarnings("serial")
@JsonFilter("fieldFilter")
public class InventoryPriceTrend extends BaseModel  {
	@Id	
    private String id;
	
    @Column(name = "project_id")
    private int projectId;
    
    @Column(name = "project_name")
    private String projectName;
    
    @Column(name = "country_id")
    private int countryId;
    
    @Column(name = "country_name")
    private String countryName;
    
    @Column(name = "phase_id")
    private int phaseId;
    
    @Column(name = "phase_name")
    private String phaseName;
    
    @Column(name = "phase_type")
    private String phaseType;
    
    @Column(name = "construction_status")
    private String constructionStatus;

	@Column(name = "locality_id")
    private int localityId;
    
    @Column(name = "locality_name")
    private String localityName;
    
    @Column(name = "city_id")
    private int cityId;
    
    @Column(name = "city_name")
    private String cityName;

    @Column(name = "builder_id")
    private int builderId;
    
    @Column(name = "builder_name")
    private String builderName;

    @Column(name = "effective_month")
    private Date month;
    
    @Column(name = "quarter")
    private Date quarter;
    
    @Column(name = "half_year")
    private Date halfYear;
    
    @Column(name = "year")
    private Date year;
    
    @Column(name = "completion_date")
    private Date completionDate;
    
    @Column(name = "launch_date")
    private Date launchDate;
    
    @Column(name = "unit_type")
    private String unitType;
    
    @Column(name = "bedrooms")
    private int bedrooms;
    
    public int getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(int bedrooms) {
		this.bedrooms = bedrooms;
	}

	@Column(name = "average_price_per_unit_area")
    private int pricePerUnitArea;
    
    @Column(name = "average_size")
    private int size;
    
    @Column(name = "all_size")
    private String allSize;
    
    @Column(name = "average_total_price")
    private long price;
    
    @Column(name = "supply")
    private int supply;
    
    @Column(name = "launched_unit")
    private int launchedUnit;
    
    @Column(name = "inventory")
    private int inventory;

    @Column(name = "units_sold")
    private int unitsSold;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public int getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(int phaseId) {
		this.phaseId = phaseId;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public String getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
	}
	
    public String getConstructionStatus() {
		return constructionStatus;
	}

	public void setConstructionStatus(String constructionStatus) {
		this.constructionStatus = constructionStatus;
	}

	public int getLocalityId() {
		return localityId;
	}

	public void setLocalityId(int localityId) {
		this.localityId = localityId;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getBuilderId() {
		return builderId;
	}

	public void setBuilderId(int builderId) {
		this.builderId = builderId;
	}

	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public Date getQuarter() {
		return quarter;
	}

	public void setQuarter(Date quarter) {
		this.quarter = quarter;
	}

	public Date getHalfYear() {
		return halfYear;
	}

	public void setHalfYear(Date halfYear) {
		this.halfYear = halfYear;
	}

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public Date getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public int getPricePerUnitArea() {
		return pricePerUnitArea;
	}

	public void setPricePerUnitArea(int pricePerUnitArea) {
		this.pricePerUnitArea = pricePerUnitArea;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getAllSize() {
		return allSize;
	}

	public void setAllSize(String allSize) {
		this.allSize = allSize;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
	}

	public int getLaunchedUnit() {
		return launchedUnit;
	}

	public void setLaunchedUnit(int launchedUnit) {
		this.launchedUnit = launchedUnit;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public int getUnitsSold() {
		return unitsSold;
	}

	public void setUnitsSold(int unitsSold) {
		this.unitsSold = unitsSold;
	}
    
	
}