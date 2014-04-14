package com.proptiger.data.service.b2b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.data.b2b.external.dto.BuilderTrend;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.InventoryPriceTrend;
import com.proptiger.data.model.enums.UnitType;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.b2b.TrendDao;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ResourceNotFoundException;

/**
 * Service for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

@Service
public class BuilderTrendService {
    private static final String WAVG_PRICE = "wavgPricePerUnitAreaOnSupply";

    @Autowired
    TrendDao                    trendDao;

    @Value("${b2b.price-inventory.max.month}")
    private String              currentMonth;

    @Value("${b2b.price-appreciation.duration}")
    private Integer             appreciationDuration;

    public BuilderTrend getBuilderTrendForSingleBuilder(Integer builderId, UserInfo userInfo) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("builderId==" + builderId);
        Map<Integer, BuilderTrend> builderTrends = getBuilderTrend(selector, userInfo);
        if (builderTrends.get(builderId) == null) {
            throw new ResourceNotFoundException("BuilderId " + builderId + " doesn't exist");
        }
        return builderTrends.get(builderId);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, BuilderTrend> getBuilderTrend(FIQLSelector selector, UserInfo userInfo) {
        Map<Integer, BuilderTrend> result = new HashMap<>();
        FIQLSelector fiqlSelector = getFIQLFromUserFIQL(selector);
        Date currentDate = DateUtil.parseYYYYmmddStringToDate(currentMonth);
        Date pastDate = DateUtil.shiftMonths(currentDate, -1 * appreciationDuration);
        List<InventoryPriceTrend> inventoryPriceTrends = trendDao.getTrend(fiqlSelector);

        if (inventoryPriceTrends.size() != 0) {
            Map<Integer, Map<String, List<InventoryPriceTrend>>> localityUnitTypePricesMap = (Map<Integer, Map<String, List<InventoryPriceTrend>>>) UtilityClass
                    .groupFieldsAsPerKeys(
                            trendDao.getTrend(getFIQLForLocalityPrice(getLocalityDominantTypeFromList(inventoryPriceTrends))),
                            new ArrayList<String>(Arrays.asList("localityId", "unitType")));

            Map<Integer, Map<Date, Map<Integer, List<InventoryPriceTrend>>>> inventoryPriceTrendMap = (Map<Integer, Map<Date, Map<Integer, List<InventoryPriceTrend>>>>) UtilityClass
                    .groupFieldsAsPerKeys(
                            inventoryPriceTrends,
                            new ArrayList<String>(Arrays.asList("builderId", "month", "projectId")));

            for (Integer builderId : inventoryPriceTrendMap.keySet()) {
                BuilderTrend builderTrend = new BuilderTrend();
                builderTrend.setBuilderId(builderId);

                Map<Integer, List<InventoryPriceTrend>> currentMonthDetails = inventoryPriceTrendMap.get(builderId)
                        .get(currentDate.getTime());

                builderTrend.setProjectCount(currentMonthDetails.size());

                for (Integer projectId : currentMonthDetails.keySet()) {
                    List<InventoryPriceTrend> currentMonthProjectDetails = currentMonthDetails.get(projectId);

                    for (InventoryPriceTrend inventoryPriceTrend : currentMonthProjectDetails) {
                        builderTrend.setBuilderName(inventoryPriceTrend.getBuilderName());
                        builderTrend.setMinPricePerUnitArea(UtilityClass.min(
                                builderTrend.getMinPricePerUnitArea(),
                                (Integer) inventoryPriceTrend.getExtraAttributes().get("minPricePerUnitArea")));
                        builderTrend.setMaxPricePerUnitArea(UtilityClass.max(
                                builderTrend.getMaxPricePerUnitArea(),
                                (Integer) inventoryPriceTrend.getExtraAttributes().get("maxPricePerUnitArea")));
                        builderTrend.setSupply(builderTrend.getSupply() + ((Long) inventoryPriceTrend
                                .getExtraAttributes().get("sumLtdSupply")).intValue());
                        builderTrend.setInventory(builderTrend.getInventory() + ((Long) inventoryPriceTrend
                                .getExtraAttributes().get("sumInventory")).intValue());
                        builderTrend.getUnitTypes().add(inventoryPriceTrend.getUnitType());

                        if (inventoryPriceTrend.getIsDominantProjectUnitType()) {
                            Object currentPriceObject = inventoryPriceTrend.getExtraAttributes().get(WAVG_PRICE);
                            if (currentPriceObject != null) {
                                populateLocalityPriceComparision(
                                        localityUnitTypePricesMap,
                                        builderTrend,
                                        inventoryPriceTrend);

                                populatePastPriceComparision(
                                        inventoryPriceTrend,
                                        pastDate,
                                        inventoryPriceTrendMap,
                                        builderTrend);
                            }
                        }
                    }
                }

                result.put(builderId, builderTrend);
            }
        }

        return result;
    }

    private void populatePastPriceComparision(
            InventoryPriceTrend inventoryPriceTrend,
            Date pastDate,
            Map<Integer, Map<Date, Map<Integer, List<InventoryPriceTrend>>>> inventoryPriceTrendMap,
            BuilderTrend builderTrend) {
        Integer builderId = inventoryPriceTrend.getBuilderId();
        Integer projectId = inventoryPriceTrend.getProjectId();
        UnitType unitType = inventoryPriceTrend.getUnitType();
        Double currentPrice = Double.valueOf(inventoryPriceTrend.getExtraAttributes().get(WAVG_PRICE).toString());

        if (inventoryPriceTrendMap.get(builderId).get(pastDate.getTime()) != null && inventoryPriceTrendMap
                .get(builderId).get(pastDate.getTime()).get(projectId) != null) {
            List<InventoryPriceTrend> pastMonthProjectDetails = inventoryPriceTrendMap.get(builderId)
                    .get(pastDate.getTime()).get(projectId);
            for (InventoryPriceTrend pastIinventoryPriceTrend : pastMonthProjectDetails) {
                if (pastIinventoryPriceTrend.getUnitType().equals(unitType)) {
                    Object pastPriceObject = pastIinventoryPriceTrend.getExtraAttributes().get(WAVG_PRICE);
                    if (pastPriceObject != null) {
                        Double pastPrice = Double.valueOf(pastPriceObject.toString());
                        if (currentPrice > pastPrice) {
                            builderTrend.setProjectCountHavingPriceAppreciation(builderTrend
                                    .getProjectCountHavingPriceAppreciation() + 1);
                        }
                        else if (currentPrice < pastPrice) {
                            builderTrend.setProjectCountHavingPriceDepreciation(builderTrend
                                    .getProjectCountHavingPriceDepreciation() + 1);
                        }
                    }
                }
            }
        }
    }

    private void populateLocalityPriceComparision(
            Map<Integer, Map<String, List<InventoryPriceTrend>>> localityUnitTypePricesMap,
            BuilderTrend builderTrend,
            InventoryPriceTrend inventoryPriceTrend) {
        Double currentPrice = Double.valueOf(inventoryPriceTrend.getExtraAttributes().get(WAVG_PRICE).toString());
        Double currentLocalityPrice = Double.valueOf(localityUnitTypePricesMap.get(inventoryPriceTrend.getLocalityId())
                .get(inventoryPriceTrend.getUnitType()).get(0).getExtraAttributes().get(WAVG_PRICE).toString());

        if (currentPrice > currentLocalityPrice) {
            builderTrend.setProjectCountHavingPriceMoreThanLocAvg(builderTrend
                    .getProjectCountHavingPriceMoreThanLocAvg() + 1);
        }
        else if (currentPrice < currentLocalityPrice) {
            builderTrend.setProjectCountHavingPriceLessThanLocAvg(builderTrend
                    .getProjectCountHavingPriceLessThanLocAvg() + 1);
        }
    }

    /**
     * 
     * @param inventoryPriceTrends
     * @return {@link Map} unique combination of locality and dominant unit type
     *         for all projects in the supplied list
     */
    @SuppressWarnings("unchecked")
    private Map<Integer, Set<UnitType>> getLocalityDominantTypeFromList(List<InventoryPriceTrend> inventoryPriceTrends) {
        Map<Integer, Set<UnitType>> result = new HashMap<>();
        Map<String, List<InventoryPriceTrend>> isDominantSupplyGrouped = (Map<String, List<InventoryPriceTrend>>) UtilityClass
                .groupFieldsAsPerKeys(
                        inventoryPriceTrends,
                        new ArrayList<String>(Arrays.asList("isDominantProjectUnitType")));
        if (isDominantSupplyGrouped.get("true") != null) {
            for (InventoryPriceTrend inventoryPriceTrend : isDominantSupplyGrouped.get("true")) {
                Integer localityId = inventoryPriceTrend.getLocalityId();
                UnitType unitType = inventoryPriceTrend.getUnitType();
                if (result.containsKey(localityId)) {
                    result.get(localityId).add(unitType);
                }
                else {
                    Set<UnitType> unitTypes = new HashSet<>(Arrays.asList(unitType));
                    result.put(localityId, unitTypes);
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param localityUnitTypeMap
     * @return {@link FIQLSelector} for getting locality price for different
     *         unit types
     */
    private FIQLSelector getFIQLForLocalityPrice(Map<Integer, Set<UnitType>> localityUnitTypeMap) {
        FIQLSelector fiqlSelector = new FIQLSelector();
        fiqlSelector.setFields("wavgPricePerUnitAreaOnSupply");
        fiqlSelector.setGroup("localityId,unitType");
        for (Integer localityId : localityUnitTypeMap.keySet()) {
            for (UnitType unitType : localityUnitTypeMap.get(localityId)) {

                fiqlSelector.addOrConditionToFilter("localityId==" + localityId + ";unitType==" + unitType);
            }
        }
        fiqlSelector.addAndConditionToFilter("month==" + currentMonth);
        return fiqlSelector;
    }

    /**
     * 
     * @param userFIQLSelector
     * @return {@link FIQLSelector} for builder trend honouring user provided
     *         filters
     */
    private FIQLSelector getFIQLFromUserFIQL(FIQLSelector userFIQLSelector) {
        FIQLSelector result = new FIQLSelector();
        result.setFilters(userFIQLSelector.getFilters()).addAndConditionToFilter(
                "month==" + currentMonth + ",month==" + DateUtil.shiftMonths(currentMonth, -1 * appreciationDuration));
        result.setGroup("builderId,month,projectId,unitType");
        result.setFields("builderId,builderName,minPricePerUnitArea,maxPricePerUnitArea,sumLtdSupply,sumInventory,wavgPricePerUnitAreaOnSupply,month,localityId,isDominantProjectUnitType");
        return result;
    }
}