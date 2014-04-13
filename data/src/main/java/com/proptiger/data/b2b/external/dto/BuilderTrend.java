package com.proptiger.data.b2b.external.dto;

import java.util.HashSet;
import java.util.Set;

import com.proptiger.data.model.enums.UnitType;

/**
 * DTO for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

public class BuilderTrend {
    private Integer       builderId;
    private String        builderName;
    private Integer       projectCount;
    private Integer       minPricePerUnitArea                   = 0;
    private Integer       maxPricePerUnitArea                   = 0;
    private Integer       supply                                = 0;
    private Integer       inventory                             = 0;
    private Set<UnitType> unitTypes                             = new HashSet<>();
    private Integer       projectCountHavingPriceAppreciation   = 0;
    private Integer       projectCountHavingPriceDepreciation   = 0;
    private Integer       projectCountHavingPriceMoreThanLocAvg = 0;
    private Integer       projectCountHavingPriceLessThanLocAvg = 0;

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

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    public void setMinPricePerUnitArea(Integer minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public Integer getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    public void setMaxPricePerUnitArea(Integer maxPricePerUnitArea) {
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

    public Set<UnitType> getUnitTypes() {
        return unitTypes;
    }

    public void setUnitTypes(Set<UnitType> unitTypes) {
        this.unitTypes = unitTypes;
    }

    public Integer getProjectCountHavingPriceAppreciation() {
        return projectCountHavingPriceAppreciation;
    }

    public void setProjectCountHavingPriceAppreciation(Integer projectCountHavingPriceAppreciation) {
        this.projectCountHavingPriceAppreciation = projectCountHavingPriceAppreciation;
    }

    public Integer getProjectCountHavingPriceDepreciation() {
        return projectCountHavingPriceDepreciation;
    }

    public void setProjectCountHavingPriceDepreciation(Integer projectCountHavingPriceDepreciation) {
        this.projectCountHavingPriceDepreciation = projectCountHavingPriceDepreciation;
    }

    public Integer getProjectCountHavingPriceMoreThanLocAvg() {
        return projectCountHavingPriceMoreThanLocAvg;
    }

    public void setProjectCountHavingPriceMoreThanLocAvg(Integer projectCountHavingPriceMoreThanLocAvg) {
        this.projectCountHavingPriceMoreThanLocAvg = projectCountHavingPriceMoreThanLocAvg;
    }

    public Integer getProjectCountHavingPriceLessThanLocAvg() {
        return projectCountHavingPriceLessThanLocAvg;
    }

    public void setProjectCountHavingPriceLessThanLocAvg(Integer projectCountHavingPriceLessThanLocAvg) {
        this.projectCountHavingPriceLessThanLocAvg = projectCountHavingPriceLessThanLocAvg;
    }
}