package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

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
public class InventoryPriceTrend implements BaseModel  {
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
    private Date effectiveMonth;
    
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
    
    @Transient
    private int minPricePerUnitArea;
    
    @Transient
    private int maxPricePerUnitArea;
    
    @Transient
    private int minSize;
    
    @Transient
    private int maxSize;
    
    @Transient
    private int minPrice;
    
    @Transient
    private int maxPrice;
    
    @Transient
    private int avgPrice;
    
    @Transient
    private int totalSupply;
    
    @Transient
    private int totalLaunchedUnit;
    
    @Transient
    private int totalInventory;

    @Transient
    private int totalUnitsSold;

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

	public Date getEffectiveMonth() {
		return effectiveMonth;
	}

	public void setEffectiveMonth(Date effectiveMonth) {
		this.effectiveMonth = effectiveMonth;
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

	public int getMinPricePerUnitArea() {
		return minPricePerUnitArea;
	}

	public void setMinPricePerUnitArea(int minPricePerUnitArea) {
		this.minPricePerUnitArea = minPricePerUnitArea;
	}

	public int getMaxPricePerUnitArea() {
		return maxPricePerUnitArea;
	}

	public void setMaxPricePerUnitArea(int maxPricePerUnitArea) {
		this.maxPricePerUnitArea = maxPricePerUnitArea;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(int avgPrice) {
		this.avgPrice = avgPrice;
	}

	public int getTotalSupply() {
		return totalSupply;
	}

	public void setTotalSupply(int totalSupply) {
		this.totalSupply = totalSupply;
	}

	public int getTotalLaunchedUnit() {
		return totalLaunchedUnit;
	}

	public void setTotalLaunchedUnit(int totalLaunchedUnit) {
		this.totalLaunchedUnit = totalLaunchedUnit;
	}

	public int getTotalInventory() {
		return totalInventory;
	}

	public void setTotalInventory(int totalInventory) {
		this.totalInventory = totalInventory;
	}

	public int getTotalUnitsSold() {
		return totalUnitsSold;
	}

	public void setTotalUnitsSold(int totalUnitsSold) {
		this.totalUnitsSold = totalUnitsSold;
	}
}