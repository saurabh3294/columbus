package com.proptiger.data.b2b.external.dto;

import java.util.List;

/**
 * DTO for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

public class BuilderTrend {
    private Integer      builderId;
    private String       builderName;
    private Integer      projectCount;
    private Integer      minPricePerUnitArea;
    private Integer      maxPricePerUnitArea;
    private Integer      supply;
    private Integer      inventory;
    private List<String> unitTypes;
    private Integer      projectCountHavingPriceAppreciation;
    private Integer      projectCountHavingPriceDepreciation;
    private Integer      projectCountHavingPriceMoreThanLocAvg;
    private Integer      projectCountHavingPriceLessThanLocAvg;

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

    public List<String> getUnitTypes() {
        return unitTypes;
    }

    public void setUnitTypes(List<String> unitTypes) {
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