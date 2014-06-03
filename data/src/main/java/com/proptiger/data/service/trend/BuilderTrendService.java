package com.proptiger.data.service.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.UnitType;
import com.proptiger.data.init.comparator.GenericComparator;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.trend.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.trend.external.dto.BuilderTrend;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ResourceNotFoundException;

/**
 * Service for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

// XXX This service will silently fail(provide incorrect data) if the user
// provided FIQL results in large dataset
@Service
public class BuilderTrendService {
    private static final String WAVG_PRICE     = "wavgPricePerUnitAreaOnSupply";

    @Autowired
    TrendDao                    trendDao;

    @Value("${b2b.price-inventory.max.month}")
    private String              currentMonth;

    @Value("${b2b.price-appreciation.duration}")
    private Integer             appreciationDuration;

    private static final int    MAX_ROWS       = 5000;

    private static final String DESC_SPECIFIER = "-";

    public BuilderTrend getBuilderTrendForSingleBuilder(Integer builderId, UserInfo userInfo) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("builderId==" + builderId);
        List<BuilderTrend> builderTrends = getBuilderTrend(selector, userInfo);
        if (builderTrends.get(0) == null) {
            throw new ResourceNotFoundException("BuilderId " + builderId + " doesn't exist");
        }
        return builderTrends.get(0);
    }

    public List<BuilderTrend> getBuilderTrend(FIQLSelector userSelector, UserInfo userInfo) {
        List<BuilderTrend> builderTrends = new ArrayList<>();
        FIQLSelector fiqlSelector = getFIQLFromUserFIQL(userSelector);
        Date currentDate = DateUtil.parseYYYYmmddStringToDate(currentMonth);
        Date pastDate = DateUtil.shiftMonths(currentDate, -1 * appreciationDuration);
        List<InventoryPriceTrend> inventoryPriceTrends = trendDao.getTrend(fiqlSelector);

        if (inventoryPriceTrends.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<Integer, Map<String, List<InventoryPriceTrend>>> localityUnitTypePricesMap = (Map<Integer, Map<String, List<InventoryPriceTrend>>>) UtilityClass
                    .groupFieldsAsPerKeys(
                            trendDao.getTrend(getFIQLForLocalityPrice(getLocalityDominantTypeFromList(inventoryPriceTrends))),
                            new ArrayList<String>(Arrays.asList("localityId", "unitType")));

            @SuppressWarnings("unchecked")
            Map<Integer, Map<Date, Map<Integer, List<InventoryPriceTrend>>>> inventoryPriceTrendMap = (Map<Integer, Map<Date, Map<Integer, List<InventoryPriceTrend>>>>) UtilityClass
                    .groupFieldsAsPerKeys(
                            inventoryPriceTrends,
                            new ArrayList<String>(Arrays.asList("builderId", "month", "projectId")));

            @SuppressWarnings("unchecked")
            Map<Integer, List<InventoryPriceTrend>> mappedDelayedProjects = (Map<Integer, List<InventoryPriceTrend>>) UtilityClass
                    .groupFieldsAsPerKeys(
                            trendDao.getTrend(getDelayedFIQLFromUserFiql(userSelector)),
                            Arrays.asList("builderId"));

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
                        builderTrend.setBuilderHeadquarterCity(inventoryPriceTrend.getBuilderHeadquarterCity());

                        Object minPricePerUnitArea = inventoryPriceTrend.getExtraAttributes()
                                .get("minPricePerUnitArea");
                        if (minPricePerUnitArea != null) {
                            builderTrend.setMinPricePerUnitArea(UtilityClass.min(
                                    builderTrend.getMinPricePerUnitArea(),
                                    (Integer) inventoryPriceTrend.getExtraAttributes().get("minPricePerUnitArea")));
                        }

                        Object maxPricePerUnitArea = inventoryPriceTrend.getExtraAttributes()
                                .get("maxPricePerUnitArea");
                        if (maxPricePerUnitArea != null) {
                            builderTrend.setMaxPricePerUnitArea(UtilityClass.max(
                                    builderTrend.getMaxPricePerUnitArea(),
                                    (Integer) maxPricePerUnitArea));
                        }

                        if (inventoryPriceTrend.getExtraAttributes().get("sumLtdSupply") != null) {
                            builderTrend.setSupply(builderTrend.getSupply() + ((Long) inventoryPriceTrend
                                    .getExtraAttributes().get("sumLtdSupply")).intValue());
                        }
                        if (inventoryPriceTrend.getExtraAttributes().get("sumLtdLaunchedUnit") != null) {
                            builderTrend.setLaunchedUnit(builderTrend.getSupply() + ((Long) inventoryPriceTrend
                                    .getExtraAttributes().get("sumLtdLaunchedUnit")).intValue());
                        }
                        if (inventoryPriceTrend.getExtraAttributes().get("sumInventory") != null) {
                            builderTrend.setInventory(builderTrend.getInventory() + ((Long) inventoryPriceTrend
                                    .getExtraAttributes().get("sumInventory")).intValue());
                        }

                        populateUnitTypeDetails(builderTrend, inventoryPriceTrend);

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

                populateDelayedProjectDetails(builderTrend, mappedDelayedProjects);
                builderTrend.trimUnitTypeDetails();
                builderTrends.add(builderTrend);
            }
        }
        return getPaginatedResults(getSortedResults(builderTrends, userSelector), userSelector);
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

    private void populateUnitTypeDetails(BuilderTrend builderTrend, InventoryPriceTrend inventoryPriceTrend) {
        Map<String, Integer> unitTypeDetails = builderTrend.getUnitTypesDetails()
                .get(inventoryPriceTrend.getUnitType());
        unitTypeDetails.put(BuilderTrend.PROJECT_COUNT_KEY, unitTypeDetails.get(BuilderTrend.PROJECT_COUNT_KEY) + 1);
        Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
        Object sumSupply = extraAttributes.get("sumLtdSupply");
        if (sumSupply != null) {
            unitTypeDetails.put(
                    BuilderTrend.SUPPLY_KEY,
                    unitTypeDetails.get(BuilderTrend.SUPPLY_KEY) + Integer.valueOf(sumSupply.toString()));
        }

        Object sumLtdLaunchedUnit = extraAttributes.get("sumLtdLaunchedUnit");
        if (sumLtdLaunchedUnit != null) {
            unitTypeDetails.put(
                    BuilderTrend.LAUNCHED_KEY,
                    unitTypeDetails.get(BuilderTrend.LAUNCHED_KEY) + Integer.valueOf(sumLtdLaunchedUnit.toString()));
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

    private void populateDelayedProjectDetails(
            BuilderTrend builderTrend,
            Map<Integer, List<InventoryPriceTrend>> mappedDelayedProjects) {
        int builderId = builderTrend.getBuilderId();
        if (mappedDelayedProjects.containsKey(builderId)) {
            Map<String, Object> extraAttributes = mappedDelayedProjects.get(builderId).get(0).getExtraAttributes();
            Map<String, Integer> delayedDetails = builderTrend.getDelayed();

            Object sumSupply = extraAttributes.get("sumLtdSupply");
            if (sumSupply != null) {
                delayedDetails.put(
                        BuilderTrend.SUPPLY_KEY,
                        delayedDetails.get(BuilderTrend.SUPPLY_KEY) + Integer.valueOf(sumSupply.toString()));
            }

            Object sumLaunchedUnit = extraAttributes.get("sumLtdLaunchedUnit");
            if (sumSupply != null) {
                delayedDetails.put(
                        BuilderTrend.LAUNCHED_KEY,
                        delayedDetails.get(BuilderTrend.LAUNCHED_KEY) + Integer.valueOf(sumLaunchedUnit.toString()));
            }

            Object avgSize = extraAttributes.get("wavgSizeOnLtdSupply");
            if (avgSize != null) {
                delayedDetails.put(
                        BuilderTrend.AVG_SIZE,
                        delayedDetails.get(BuilderTrend.AVG_SIZE) + Double.valueOf(avgSize.toString()).intValue());
            }
        }
    }

    /**
     * 
     * @param inventoryPriceTrends
     * @return {@link Map} unique combination of locality and dominant unit type
     *         for all projects in the supplied list
     */

    private Map<Integer, Set<UnitType>> getLocalityDominantTypeFromList(List<InventoryPriceTrend> inventoryPriceTrends) {
        Map<Integer, Set<UnitType>> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<Boolean, List<InventoryPriceTrend>> isDominantSupplyGrouped = (Map<Boolean, List<InventoryPriceTrend>>) UtilityClass
                .groupFieldsAsPerKeys(
                        inventoryPriceTrends,
                        new ArrayList<String>(Arrays.asList("isDominantProjectUnitType")));
        if (isDominantSupplyGrouped.get(true) != null) {
            for (InventoryPriceTrend inventoryPriceTrend : isDominantSupplyGrouped.get(true)) {
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

        int resultCount = 0;
        for (Integer localityId : localityUnitTypeMap.keySet()) {
            for (UnitType unitType : localityUnitTypeMap.get(localityId)) {

                fiqlSelector.addOrConditionToFilter("localityId==" + localityId + ";unitType==" + unitType);
                resultCount++;
            }
        }
        fiqlSelector.setRows(resultCount);
        fiqlSelector.addAndConditionToFilter("month==" + currentMonth);
        fiqlSelector.setRows(MAX_ROWS);
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
        result.setFields("builderId,builderName,builderHeadquarterCity,minPricePerUnitArea,maxPricePerUnitArea,sumLtdSupply,sumLtdLaunchedUnit,sumInventory,wavgPricePerUnitAreaOnSupply,month,localityId,isDominantProjectUnitType");
        result.setRows(MAX_ROWS);
        return result;
    }

    private FIQLSelector getDelayedFIQLFromUserFiql(FIQLSelector userFIQLSelector) {
        FIQLSelector result = new FIQLSelector();
        result.setFilters(userFIQLSelector.getFilters()).addAndConditionToFilter("month==" + currentMonth)
                .addAndConditionToFilter("completionDelayInMonth=gt=0");
        result.setFields("builderId,sumLtdLaunchedUnit,sumLtdSupply,sumInventory,wavgSizeOnLtdSupply");
        result.setGroup("builderId");
        result.setRows(MAX_ROWS);
        return result;
    }

    private List<BuilderTrend> getSortedResults(List<BuilderTrend> builderTrends, FIQLSelector selector) {
        if (selector.getSort() != null && selector.getSort().length() > 0) {
            String[] fields = selector.getSort().split(",");
            ComparatorChain chain = new ComparatorChain();
            for (String field : fields) {
                field = field.trim();
                GenericComparator<BuilderTrend> comparator;
                if (field.contains(DESC_SPECIFIER)) {
                    comparator = new GenericComparator<>(field.replace(DESC_SPECIFIER, ""), false);
                }
                else {
                    comparator = new GenericComparator<>(field);
                }
                chain.addComparator(comparator);
            }
            Collections.sort(builderTrends, chain);
        }
        return builderTrends;
    }

    private List<BuilderTrend> getPaginatedResults(List<BuilderTrend> builderTrends, FIQLSelector selector) {
        int start = selector.getStart();
        Integer rows = selector.getRows();
        int size = builderTrends.size();

        if (start >= size) {
            builderTrends.clear();
        }
        else if (rows != null) {
            builderTrends = builderTrends.subList(start, UtilityClass.min(start + rows, size));
        }
        return builderTrends;
    }
}