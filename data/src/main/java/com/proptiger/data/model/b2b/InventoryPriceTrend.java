package com.proptiger.data.model.b2b;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.UnitType;

/**
 * Denormalized model for price inventory trends
 * 
 * @author azi
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "cms.d_inventory_prices")
@JsonFilter("fieldFilter")
public class InventoryPriceTrend extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer           id;

    @Column(name = "project_id")
    private Integer           projectId;

    @Column(name = "project_name")
    private String            projectName;

    @Column(name = "country_id")
    private Integer           countryId;

    @Column(name = "country_name")
    private String            countryName;

    @Column(name = "phase_id")
    private Integer           phaseId;

    @Column(name = "phase_name")
    private String            phaseName;

    @Column(name = "phase_type")
    private String            phaseType;

    @Column(name = "construction_status")
    private String            constructionStatus;

    @Column(name = "locality_id")
    private Integer           localityId;

    @Column(name = "locality_name")
    private String            localityName;

    @Column(name = "suburb_id")
    private Integer           suburbId;

    @Column(name = "suburb_name")
    private String            suburbName;

    @Column(name = "city_id")
    private Integer           cityId;

    @Column(name = "city_name")
    private String            cityName;

    @Column(name = "builder_id")
    private Integer           builderId;

    @Column(name = "builder_name")
    private String            builderName;

    @Column(name = "effective_month")
    private Date              month;

    @Column(name = "quarter")
    private Date              quarter;

    @Column(name = "half_year")
    private Date              halfYear;

    @Column(name = "year")
    private Date              year;

    @Column(name = "completion_date")
    private Date              completionDate;

    @Column(name = "is_delayed")
    private Boolean           isDelayed;

    @Column(name = "launch_date")
    private Date              launchDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type")
    private UnitType          unitType;

    @Column(name = "bedrooms")
    private Integer           bedrooms;

    @Column(name = "is_dominant_project_unit_type")
    private Boolean           isDominantProjectUnitType;

    @Column(name = "average_price_per_unit_area")
    private Integer           pricePerUnitArea;

    @Column(name = "average_secondary_price_per_unit_area")
    private Integer           secondaryPricePerUnitArea;

    @Column(name = "average_size")
    private Integer           size;

    @Column(name = "average_total_price")
    private Long              budget;

    @Column(name = "supply")
    private Integer           supply;

    @Column(name = "ltd_supply")
    private Integer           ltdSupply;

    @Column(name = "launched_unit")
    private Integer           launchedUnit;

    @Column(name = "ltd_launched_unit")
    private Integer           ltdLaunchedUnit;

    @Column(name = "inventory")
    private Integer           inventory;

    @Column(name = "units_sold")
    private Integer           unitsSold;
    
    @Column(name = "units_delivered")
    private Integer           unitsDelivered;

    private Float             demand;

    @Column(name = "customer_demand")
    private Float             customerDemand;

    @Column(name = "investor_demand")
    private Float             investorDemand;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
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

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public Integer getSuburbId() {
        return suburbId;
    }

    public void setSuburbId(Integer suburbId) {
        this.suburbId = suburbId;
    }

    public String getSuburbName() {
        return suburbName;
    }

    public void setSuburbName(String suburbName) {
        this.suburbName = suburbName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getBuilderId() {
        return builderId;
    }

    public void setBuilderId(Integer builderId) {
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

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Boolean getIsDominantProjectUnitType() {
        return isDominantProjectUnitType;
    }

    public void setIsDominantProjectUnitType(Boolean isDominantProjectUnitType) {
        this.isDominantProjectUnitType = isDominantProjectUnitType;
    }

    public Integer getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    public void setPricePerUnitArea(Integer pricePerUnitArea) {
        this.pricePerUnitArea = pricePerUnitArea;
    }

    public Integer getSecondaryPricePerUnitArea() {
        return secondaryPricePerUnitArea;
    }

    public void setSecondaryPricePerUnitArea(Integer secondaryPricePerUnitArea) {
        this.secondaryPricePerUnitArea = secondaryPricePerUnitArea;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSupply() {
        return supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

    public Integer getLtdSupply() {
        return ltdSupply;
    }

    public void setLtdSupply(Integer ltdSupply) {
        this.ltdSupply = ltdSupply;
    }

    public Integer getLaunchedUnit() {
        return launchedUnit;
    }

    public void setLaunchedUnit(Integer launchedUnit) {
        this.launchedUnit = launchedUnit;
    }

    public Integer getLtdLaunchedUnit() {
        return ltdLaunchedUnit;
    }

    public void setLtdLaunchedUnit(Integer ltdLaunchedUnit) {
        this.ltdLaunchedUnit = ltdLaunchedUnit;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Integer getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Integer unitsSold) {
        this.unitsSold = unitsSold;
    }
    
    public Integer getUnitsDelivered() {
		return unitsDelivered;
	}

	public void setUnitsDelivered(Integer unitsDelivered) {
		this.unitsDelivered = unitsDelivered;
	}

    public Float getDemand() {
        return demand;
    }

    public void setDemand(Float demand) {
        this.demand = demand;
    }

    public Float getCustomerDemand() {
        return customerDemand;
    }

    public void setCustomerDemand(Float customerDemand) {
        this.customerDemand = customerDemand;
    }

    public Float getInvestorDemand() {
        return investorDemand;
    }

    public void setInvestorDemand(Float investorDemand) {
        this.investorDemand = investorDemand;
    }

    public Boolean getIsDelayed() {
        return isDelayed;
    }

    public void setIsDelayed(Boolean isDelayed) {
        this.isDelayed = isDelayed;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                Object object = field.get(this);
                if (object != null) {
                    map.put(field.getName(), object);
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        map.remove("extraAttributes");
        map.putAll(this.getExtraAttributes());
        return map;
    }
}