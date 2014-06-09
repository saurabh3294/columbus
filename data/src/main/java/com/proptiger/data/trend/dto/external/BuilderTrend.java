package com.proptiger.data.trend.dto.external;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.enums.UnitType;
import com.proptiger.exception.ProAPIException;

/**
 * DTO for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

public class BuilderTrend {
    @JsonIgnore
    public static final String                  PROJECT_COUNT_KEY                     = "projectCount";
    @JsonIgnore
    public static final String                  SUPPLY_KEY                            = "supply";
    @JsonIgnore
    public static final String                  LAUNCHED_KEY                          = "launchedUnit";
    @JsonIgnore
    public static final String                  AVG_SIZE                              = "wavgSizeOnLtdSupply";

    private int                                 builderId;
    private String                              builderName;
    private String                              builderHeadquarterCity;
    private int                                 projectCount;
    private Integer                             minPricePerUnitArea                   = 0;
    private Integer                             maxPricePerUnitArea                   = 0;
    private Integer                             supply                                = 0;
    private Integer                             launchedUnit;
    private Integer                             inventory                             = 0;
    private Map<UnitType, Map<String, Integer>> unitTypesDetails                      = new HashMap<>();
    private int                                 projectCountHavingPriceAppreciation   = 0;
    private int                                 projectCountHavingPriceDepreciation   = 0;
    private int                                 projectCountHavingPriceMoreThanLocAvg = 0;
    private int                                 projectCountHavingPriceLessThanLocAvg = 0;
    private Map<String, Integer>                delayed                               = new HashMap<>();

    public BuilderTrend() {
        for (UnitType unitType : UnitType.values()) {
            String field = unitType.name();
            try {
                if (UnitType.class.getField(field).getAnnotation(Deprecated.class) == null) {
                    Map<String, Integer> countMap = new HashMap<>();
                    countMap.put(PROJECT_COUNT_KEY, 0);
                    countMap.put(SUPPLY_KEY, 0);
                    countMap.put(LAUNCHED_KEY, 0);

                    this.unitTypesDetails.put(unitType, countMap);
                }
            }
            catch (NoSuchFieldException | SecurityException e) {
                throw new ProAPIException(e);
            }
        }

        this.delayed.put(PROJECT_COUNT_KEY, 0);
        this.delayed.put(SUPPLY_KEY, 0);
        this.delayed.put(LAUNCHED_KEY, 0);
        this.delayed.put(AVG_SIZE, 0);
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

    public String getBuilderHeadquarterCity() {
        return builderHeadquarterCity;
    }

    public void setBuilderHeadquarterCity(String builderHeadquarterCity) {
        this.builderHeadquarterCity = builderHeadquarterCity;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(int projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    public void setMinPricePerUnitArea(Integer minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public int getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    public void setMaxPricePerUnitArea(int maxPricePerUnitArea) {
        this.maxPricePerUnitArea = maxPricePerUnitArea;
    }

    public Integer getSupply() {
        return supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public int getProjectCountHavingPriceAppreciation() {
        return projectCountHavingPriceAppreciation;
    }

    public void setProjectCountHavingPriceAppreciation(int projectCountHavingPriceAppreciation) {
        this.projectCountHavingPriceAppreciation = projectCountHavingPriceAppreciation;
    }

    public int getProjectCountHavingPriceDepreciation() {
        return projectCountHavingPriceDepreciation;
    }

    public void setProjectCountHavingPriceDepreciation(int projectCountHavingPriceDepreciation) {
        this.projectCountHavingPriceDepreciation = projectCountHavingPriceDepreciation;
    }

    public int getProjectCountHavingPriceMoreThanLocAvg() {
        return projectCountHavingPriceMoreThanLocAvg;
    }

    public void setProjectCountHavingPriceMoreThanLocAvg(int projectCountHavingPriceMoreThanLocAvg) {
        this.projectCountHavingPriceMoreThanLocAvg = projectCountHavingPriceMoreThanLocAvg;
    }

    public int getProjectCountHavingPriceLessThanLocAvg() {
        return projectCountHavingPriceLessThanLocAvg;
    }

    public void setProjectCountHavingPriceLessThanLocAvg(int projectCountHavingPriceLessThanLocAvg) {
        this.projectCountHavingPriceLessThanLocAvg = projectCountHavingPriceLessThanLocAvg;
    }

    public Map<UnitType, Map<String, Integer>> getUnitTypesDetails() {
        return unitTypesDetails;
    }

    public void setUnitTypesDetails(Map<UnitType, Map<String, Integer>> unitTypesDetails) {
        this.unitTypesDetails = unitTypesDetails;
    }

    public Map<String, Integer> getDelayed() {
        return delayed;
    }

    public void setDelayed(Map<String, Integer> delayed) {
        this.delayed = delayed;
    }

    public Integer getLaunchedUnit() {
        return launchedUnit;
    }

    public BuilderTrend setLaunchedUnit(Integer launchedUnit) {
        this.launchedUnit = launchedUnit;
        return this;
    }

    public BuilderTrend trimUnitTypeDetails() {
        Iterator<Entry<UnitType, Map<String, Integer>>> iterator = unitTypesDetails.entrySet().iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getValue().get(PROJECT_COUNT_KEY).equals(0)) {
                iterator.remove();
            }
        }
        return this;
    }
}