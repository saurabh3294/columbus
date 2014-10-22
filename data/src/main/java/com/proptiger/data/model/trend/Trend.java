package com.proptiger.data.model.trend;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;
import com.proptiger.data.enums.UnitType;

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
public class Trend extends BaseModel {

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
    
    @Column(name = "construction_status_quarter")
    private String            constructionStatusQuarter;

    @Column(name = "construction_status_year")
    private String            constructionStatusYear;

    @Column(name = "construction_status_financial_year")
    private String            constructionStatusFinancialYear;

    @Column(name = "booking_status")
    private String            bookingStatus;

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

    @Column(name = "builder_headquarter_city")
    private String            builderHeadquarterCity;

    @Column(name = "effective_month")
    private Date              month;

    @Column(name = "quarter")
    private Date              quarter;

    @Column(name = "half_year")
    private Date              halfYear;

    @Column(name = "year")
    private Date              year;

    @Column(name = "financial_year")
    private Date              financialYear;

    @Column(name = "completion_date")
    private Date              completionDate;

    @Column(name = "completion_delay")
    private int               completionDelayInMonth;

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

    @Column(name = "average_price_per_unit_area_quarter")
    private Integer           pricePerUnitAreaQuarter;

    @Column(name = "average_price_per_unit_area_year")
    private Integer           pricePerUnitAreaYear;

    @Column(name = "average_price_per_unit_area_financial_year")
    private Integer           pricePerUnitAreaFinancialYear;

    @Column(name = "average_secondary_price_per_unit_area")
    private Integer           secondaryPricePerUnitArea;

    @Column(name = "average_secondary_price_per_unit_area_quarter")
    private Integer           secondaryPricePerUnitAreaQuarter;

    @Column(name = "average_secondary_price_per_unit_area_year")
    private Integer           secondaryPricePerUnitAreaYear;

    @Column(name = "average_secondary_price_per_unit_area_financial_year")
    private Integer           secondaryPricePerUnitAreaFinancialYear;

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

    @Column(name = "ltd_launched_unit_quarter")
    private Integer           ltdLaunchedUnitQuarter;

    @Column(name = "ltd_launched_unit_year")
    private Integer           ltdLaunchedUnitYear;

    @Column(name = "ltd_launched_unit_financial_year")
    private Integer           ltdLaunchedUnitFinancialYear;

    @Column(name = "supply_area")
    private Integer           supplyArea;

    @Column(name = "ltd_supply_area")
    private Integer           ltdSupplyArea;

    @Column(name = "inventory")
    private Integer           inventory;

    @Column(name = "inventory_quarter")
    private Integer           inventoryQuarter;

    @Column(name = "inventory_year")
    private Integer           inventoryYear;

    @Column(name = "inventory_financial_year")
    private Integer           inventoryFinancialYear;

    @Column(name = "units_sold")
    private Integer           unitsSold;

    @Column(name = "rate_of_sale")
    private Float             rateOfSale;

    @Column(name = "rate_of_sale_quarter")
    private Float             rateOfSaleQuarter;

    @Column(name = "rate_of_sale_year")
    private Float             rateOfSaleYear;

    @Column(name = "rate_of_sale_financial_year")
    private Float             rateOfSaleFinancialYear;

    @Column(name = "inventory_overhang")
    private Integer           inventoryOverhang;

    @Column(name = "inventory_overhang_quarter")
    private Integer           inventoryOverhangQuarter;

    @Column(name = "inventory_overhang_year")
    private Integer           inventoryOverhangYear;

    @Column(name = "inventory_overhang_financial_year")
    private Integer           inventoryOverhangFinancialYear;

    @Column(name = "units_delivered")
    private Integer           unitsDelivered;

    private Float             demand;

    @Column(name = "customer_demand")
    private Float             customerDemand;

    @Column(name = "investor_demand")
    private Float             investorDemand;

    @Transient
    private String            rangeValue;

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
    
    public String getConstructionStatusQuarter() {
        return constructionStatusQuarter;
    }

    public void setConstructionStatusQuarter(String constructionStatusQuarter) {
        this.constructionStatusQuarter = constructionStatusQuarter;
    }

    public String getConstructionStatusYear() {
        return constructionStatusYear;
    }

    public void setConstructionStatusYear(String constructionStatusYear) {
        this.constructionStatusYear = constructionStatusYear;
    }

    public String getConstructionStatusFinancialYear() {
        return constructionStatusFinancialYear;
    }

    public void setConstructionStatusFinancialYear(String constructionStatusFinancialYear) {
        this.constructionStatusFinancialYear = constructionStatusFinancialYear;
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

    public String getBuilderHeadquarterCity() {
        return builderHeadquarterCity;
    }

    public void setBuilderHeadquarterCity(String builderHeadquarterCity) {
        this.builderHeadquarterCity = builderHeadquarterCity;
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

    public Date getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(Date financialYear) {
        this.financialYear = financialYear;
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

    public Integer getPricePerUnitAreaQuarter() {
        return pricePerUnitAreaQuarter;
    }

    public void setPricePerUnitAreaQuarter(Integer pricePerUnitAreaQuarter) {
        this.pricePerUnitAreaQuarter = pricePerUnitAreaQuarter;
    }

    public Integer getPricePerUnitAreaYear() {
        return pricePerUnitAreaYear;
    }

    public void setPricePerUnitAreaYear(Integer pricePerUnitAreaYear) {
        this.pricePerUnitAreaYear = pricePerUnitAreaYear;
    }

    public Integer getPricePerUnitAreaFinancialYear() {
        return pricePerUnitAreaFinancialYear;
    }

    public void setPricePerUnitAreaFinancialYear(Integer pricePerUnitAreaFinancialYear) {
        this.pricePerUnitAreaFinancialYear = pricePerUnitAreaFinancialYear;
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

    public Float getRateOfSale() {
        return rateOfSale;
    }

    public Float getRateOfSaleQuarter() {
        return rateOfSaleQuarter;
    }

    public void setRateOfSaleQuarter(Float rateOfSaleQuarter) {
        this.rateOfSaleQuarter = rateOfSaleQuarter;
    }

    public Float getRateOfSaleYear() {
        return rateOfSaleYear;
    }

    public void setRateOfSaleYear(Float rateOfSaleYear) {
        this.rateOfSaleYear = rateOfSaleYear;
    }

    public Float getRateOfSaleFinancialYear() {
        return rateOfSaleFinancialYear;
    }

    public void setRateOfSaleFinancialYear(Float rateOfSaleFinancialYear) {
        this.rateOfSaleFinancialYear = rateOfSaleFinancialYear;
    }

    public void setRateOfSale(Float rateOfSale) {
        this.rateOfSale = rateOfSale;
    }

    public Integer getInventoryOverhang() {
        return inventoryOverhang;
    }

    public void setInventoryOverhang(Integer inventoryOverhang) {
        this.inventoryOverhang = inventoryOverhang;
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

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public int getCompletionDelayInMonth() {
        return completionDelayInMonth;
    }

    public void setCompletionDelayInMonth(int completionDelayInMonth) {
        this.completionDelayInMonth = completionDelayInMonth;
    }

    public Integer getSupplyArea() {
        return supplyArea;
    }

    public void setSupplyArea(Integer supplyArea) {
        this.supplyArea = supplyArea;
    }

    public Integer getLtdSupplyArea() {
        return ltdSupplyArea;
    }

    public void setLtdSupplyArea(Integer ltdSupplyArea) {
        this.ltdSupplyArea = ltdSupplyArea;
    }

    public String getRangeValue() {
        return rangeValue;
    }

    public void setRangeValue(String rangeValue) {
        this.rangeValue = rangeValue;
    }

    public Integer getSecondaryPricePerUnitAreaQuarter() {
        return secondaryPricePerUnitAreaQuarter;
    }

    public void setSecondaryPricePerUnitAreaQuarter(Integer secondaryPricePerUnitAreaQuarter) {
        this.secondaryPricePerUnitAreaQuarter = secondaryPricePerUnitAreaQuarter;
    }

    public Integer getSecondaryPricePerUnitAreaYear() {
        return secondaryPricePerUnitAreaYear;
    }

    public void setSecondaryPricePerUnitAreaYear(Integer secondaryPricePerUnitAreaYear) {
        this.secondaryPricePerUnitAreaYear = secondaryPricePerUnitAreaYear;
    }

    public Integer getSecondaryPricePerUnitAreaFinancialYear() {
        return secondaryPricePerUnitAreaFinancialYear;
    }

    public void setSecondaryPricePerUnitAreaFinancialYear(Integer secondaryPricePerUnitAreaFinancialYear) {
        this.secondaryPricePerUnitAreaFinancialYear = secondaryPricePerUnitAreaFinancialYear;
    }

    public Integer getLtdLaunchedUnitQuarter() {
        return ltdLaunchedUnitQuarter;
    }

    public void setLtdLaunchedUnitQuarter(Integer ltdLaunchedUnitQuarter) {
        this.ltdLaunchedUnitQuarter = ltdLaunchedUnitQuarter;
    }

    public Integer getLtdLaunchedUnitYear() {
        return ltdLaunchedUnitYear;
    }

    public void setLtdLaunchedUnitYear(Integer ltdLaunchedUnitYear) {
        this.ltdLaunchedUnitYear = ltdLaunchedUnitYear;
    }

    public Integer getLtdLaunchedUnitFinancialYear() {
        return ltdLaunchedUnitFinancialYear;
    }

    public void setLtdLaunchedUnitFinancialYear(Integer ltdLaunchedUnitFinancialYear) {
        this.ltdLaunchedUnitFinancialYear = ltdLaunchedUnitFinancialYear;
    }

    public Integer getInventoryQuarter() {
        return inventoryQuarter;
    }

    public void setInventoryQuarter(Integer inventoryQuarter) {
        this.inventoryQuarter = inventoryQuarter;
    }

    public Integer getInventoryYear() {
        return inventoryYear;
    }

    public void setInventoryYear(Integer inventoryYear) {
        this.inventoryYear = inventoryYear;
    }

    public Integer getInventoryFinancialYear() {
        return inventoryFinancialYear;
    }

    public void setInventoryFinancialYear(Integer inventoryFinancialYear) {
        this.inventoryFinancialYear = inventoryFinancialYear;
    }

    public Integer getInventoryOverhangQuarter() {
        return inventoryOverhangQuarter;
    }

    public void setInventoryOverhangQuarter(Integer inventoryOverhangQuarter) {
        this.inventoryOverhangQuarter = inventoryOverhangQuarter;
    }

    public Integer getInventoryOverhangYear() {
        return inventoryOverhangYear;
    }

    public void setInventoryOverhangYear(Integer inventoryOverhangYear) {
        this.inventoryOverhangYear = inventoryOverhangYear;
    }

    public Integer getInventoryOverhangFinancialYear() {
        return inventoryOverhangFinancialYear;
    }

    public void setInventoryOverhangFinancialYear(Integer inventoryOverhangFinancialYear) {
        this.inventoryOverhangFinancialYear = inventoryOverhangFinancialYear;
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