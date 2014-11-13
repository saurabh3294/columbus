package com.proptiger.data.service.trend;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.UnitType;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.FIQLSelector.FIQLOperator;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.UtilityClass;
import com.proptiger.data.dto.internal.trend.HithertoDurationSelector;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.user.CatchmentService;

/**
 * @author Azitabh Ajit
 */

@Service
public class TrendService {

    @Autowired
    private B2BAttributeService b2bAttributeService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    private String              currentMonthDbLabel;

    private String              currentMonth;

    @Autowired
    private TrendDao            trendDao;

    @Autowired
    private CatchmentService    catchmentService;

    public static final String  RANGE_KEY             = "rangeValue";

    private static final String RANGE_VALUE_SEPARATOR = "-";
    private static final String DATA_KEY              = "data";
    private static final String COUNT_KEY             = "count";
    private static final int    MAX_THREAD_POOL_SIZE  = 5;

    @PostConstruct
    private void initialize() {
        currentMonth = b2bAttributeService.getAttributeByName(currentMonthDbLabel);
        HithertoDurationSelector.currentMonth = currentMonth;
    }

    public List<Trend> getTrend(FIQLSelector selector) {
        return trendDao.getTrend(selector);
    }

    public List<Trend> getTrend(FIQLSelector selector, String rangeField, String rangeValue) {
        if (rangeField == null || rangeValue == null) {
            return getTrend(selector);
        }
        else {
            return getRangeSpecificTrend(selector, rangeField, rangeValue);
        }
    }

    @SuppressWarnings("unchecked")
    public PaginatedResponse<List<Trend>> getPaginatedTrend(
            final FIQLSelector selector,
            final String rangeField,
            final String rangeValue) {
        PaginatedResponse<List<Trend>> response = new PaginatedResponse<>();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Set<Callable<Map<String, Object>>> callables = new LinkedHashSet<>();

        callables.add(new Callable<Map<String, Object>>() {
            public Map<String, Object> call() throws Exception {
                Map<String, Object> map = new HashMap<>();
                map.put(DATA_KEY, getTrend(selector, rangeField, rangeValue));
                return map;
            }
        });

        callables.add(new Callable<Map<String, Object>>() {
            public Map<String, Object> call() throws Exception {
                Map<String, Object> map = new HashMap<>();
                FIQLSelector sel = selector.clone();
                map.put(COUNT_KEY, trendDao.getResultCount(sel));
                return map;
            }
        });

        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        try {
            futures = executor.invokeAll(callables);

            for (Future<Map<String, Object>> future : futures) {
                Map<String, Object> mapResult = future.get();
                String key = mapResult.keySet().iterator().next();
                if (key.equals(DATA_KEY)) {
                    response.setResults((List<Trend>) mapResult.get(DATA_KEY));
                }
                else if (key.equals(COUNT_KEY)) {
                    response.setTotalCount((long) mapResult.get(COUNT_KEY));
                }
            }
        }
        catch (InterruptedException | ExecutionException e) {
            throw new ProAPIException(e);
        }
        executor.shutdownNow();

        return response;
    }

    public PaginatedResponse<List<Trend>> getCatchmentPaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(
                selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCatchmentTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(
                selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCurrentTrend(FIQLSelector selector, String rangeField, String rangeValue) {
        return getTrend(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCurrentPaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue) {
        return getPaginatedTrend(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCatchmentCurrentPaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(
                getCurrentDateAppendedSelector(selector).addAndConditionToFilter(
                        catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCatchmentCurrentTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(
                getCurrentDateAppendedSelector(selector).addAndConditionToFilter(
                        catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getHithertoTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector) {
        return getTrend(getHithertoDateAppendedSelector(selector, hithertoDurationSelector), rangeField, rangeValue);
    }

    public PaginatedResponse<List<Trend>> getHithertoPaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector) {
        return getPaginatedTrend(
                getHithertoDateAppendedSelector(selector, hithertoDurationSelector),
                rangeField,
                rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCatchmentHithertoPaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(
                getHithertoDateAppendedSelector(selector, hithertoDurationSelector).addAndConditionToFilter(
                        catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCatchmentHithertoTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(
                getHithertoDateAppendedSelector(selector, hithertoDurationSelector).addAndConditionToFilter(
                        catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getPriceTrend(FIQLSelector selector, String rangeField, String rangeValue) {
        return getTrend(getDominantSupplyAppendedSelector(selector), rangeField, rangeValue);
    }

    public PaginatedResponse<List<Trend>> getPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue) {
        return getPaginatedTrend(getDominantSupplyAppendedSelector(selector), rangeField, rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCatchmentPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(getDominantSupplyAppendedSelector(selector.addAndConditionToFilter(catchmentService
                .getCatchmentFIQLFilter(catchmentId, userInfo))), rangeField, rangeValue);
    }

    public List<Trend> getCatchmentPriceTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(getDominantSupplyAppendedSelector(selector.addAndConditionToFilter(catchmentService
                .getCatchmentFIQLFilter(catchmentId, userInfo))), rangeField, rangeValue);
    }

    public List<Trend> getCurrentPriceTrend(FIQLSelector selector, String rangeField, String rangeValue) {
        return getTrend(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector)),
                rangeField,
                rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCurrentPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue) {
        return getPaginatedTrend(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector)),
                rangeField,
                rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCatchmentCurrentPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector.addAndConditionToFilter(catchmentService
                        .getCatchmentFIQLFilter(catchmentId, userInfo)))),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCatchmentCurrentPriceTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector.addAndConditionToFilter(catchmentService
                        .getCatchmentFIQLFilter(catchmentId, userInfo)))),
                rangeField,
                rangeValue);
    }

    public List<Trend> getHithertoPriceTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector) {
        return getTrend(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(selector, hithertoDurationSelector)),
                rangeField,
                rangeValue);
    }

    public PaginatedResponse<List<Trend>> getHithertoPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector) {
        return getPaginatedTrend(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(selector, hithertoDurationSelector)),
                rangeField,
                rangeValue);
    }

    public PaginatedResponse<List<Trend>> getCatchmentHithertoPricePaginatedTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getPaginatedTrend(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(
                        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                        hithertoDurationSelector)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getCatchmentHithertoPriceTrend(
            FIQLSelector selector,
            String rangeField,
            String rangeValue,
            HithertoDurationSelector hithertoDurationSelector,
            Integer catchmentId,
            ActiveUser userInfo) {
        return getTrend(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(
                        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo)),
                        hithertoDurationSelector)),
                rangeField,
                rangeValue);
    }

    public List<Trend> getRangeSpecificTrend(final FIQLSelector selector, final String rangeField, String rangeValue) {
        List<Trend> result = new ArrayList<>();

        final List<Integer> rangeValueList = new ArrayList<>(getRangeValueListFromUserInput(rangeValue));
        final int rangeValueLength = rangeValueList.size();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(rangeValueLength + 1, MAX_THREAD_POOL_SIZE));

        Set<Callable<List<Trend>>> callables = new LinkedHashSet<>();

        callables.add(new Callable<List<Trend>>() {
            public List<Trend> call() throws Exception {
                FIQLSelector sel = selector.clone();
                sel.addAndConditionToFilter(rangeField + FIQLOperator.LessThan.getValue() + rangeValueList.get(0));
                return getTrend(sel);
            }
        });

        for (int i = 1; i < rangeValueLength; i++) {
            final int j = i;
            callables.add(new Callable<List<Trend>>() {
                public List<Trend> call() throws Exception {
                    FIQLSelector sel = selector.clone();
                    sel.addAndConditionToFilter(rangeField + FIQLOperator.LessThan.getValue() + rangeValueList.get(j))
                            .addAndConditionToFilter(
                                    rangeField + FIQLOperator.GreaterThanEqual.getValue() + rangeValueList.get(j - 1));
                    return getTrend(sel);
                }
            });
        }

        callables.add(new Callable<List<Trend>>() {
            public List<Trend> call() throws Exception {
                FIQLSelector sel = selector.clone();
                sel.addAndConditionToFilter(rangeField + FIQLOperator.GreaterThanEqual.getValue()
                        + rangeValueList.get(rangeValueLength - 1));
                return getTrend(sel);
            }
        });

        List<Future<List<Trend>>> futures = new ArrayList<>();

        try {
            futures = executor.invokeAll(callables);

            int i = 0;
            for (Future<List<Trend>> future : futures) {
                String key = RANGE_VALUE_SEPARATOR;
                if (i != 0) {
                    key = rangeValueList.get(i - 1) + key;
                }
                if (i != (rangeValueLength)) {
                    key = key + rangeValueList.get(i);
                }

                List<Trend> inventoryPriceTrends = future.get();
                for (Trend inventoryPriceTrend : inventoryPriceTrends) {
                    inventoryPriceTrend.setRangeValue(key);
                    Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
                    extraAttributes.put(RANGE_KEY, key);
                    inventoryPriceTrend.setExtraAttributes(extraAttributes);
                }
                result.addAll(future.get());
                i++;
            }
        }
        catch (InterruptedException | ExecutionException e) {
            throw new ProAPIException(e);
        }

        executor.shutdownNow();
        return result;
    }

    private UnitType getDominantSupply(FIQLSelector sel) {
        FIQLSelector selector;
        UnitType unitType = UnitType.Apartment;
        selector = sel.clone();
        selector.removeMonthFilter();
        selector.setFields("sumSupply");
        selector.setGroup("unitType");
        selector.setSort("-sumSupply");
        selector.setStart(0);
        selector.setRows(1);

        try {
            unitType = trendDao.getTrend(selector).get(0).getUnitType();
        }
        catch (IndexOutOfBoundsException e) {
        }
        return unitType;
    }

    public List<Map<String, Object>> getFlattenedList(List<Trend> inventoryPriceTrends) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Trend inventoryPriceTrend : inventoryPriceTrends) {
            maps.add(inventoryPriceTrend.convertToMap());
        }
        return maps;
    }

    private FIQLSelector getHithertoDateAppendedSelector(
            FIQLSelector selector,
            HithertoDurationSelector hithertoDurationSelector) {
        if (!hithertoDurationSelector.isValid()) {
            selector.addAndConditionToFilter("month" + FIQLOperator.LessThanEqual.getValue() + currentMonth);
            return selector;
        }

        String startMonth = hithertoDurationSelector.getStartMonth();
        String endMonth = hithertoDurationSelector.getEndMonth();

        selector.addAndConditionToFilter("month" + FIQLOperator.GreaterThanEqual.getValue() + startMonth);
        selector.addAndConditionToFilter("month" + FIQLOperator.LessThanEqual.getValue() + endMonth);
        return selector;
    }

    private FIQLSelector getCurrentDateAppendedSelector(FIQLSelector selector) {
        return selector.addAndConditionToFilter("month" + FIQLOperator.Equal.getValue() + currentMonth);
    }

    private FIQLSelector getDominantSupplyAppendedSelector(FIQLSelector selector) {
        selector.addAndConditionToFilter("unitType" + FIQLOperator.Equal.getValue() + getDominantSupply(selector));
        selector.addField("unitType");
        return selector;
    }

    private Set<Integer> getRangeValueListFromUserInput(String rangeValue) {
        List<Integer> allValues = new ArrayList<>();
        if (rangeValue != null && rangeValue.length() != 0) {
            allValues = Arrays.asList(UtilityClass.getIntArrFromStringArr(rangeValue.split(",")));
            Collections.sort(allValues);
        }

        return new LinkedHashSet<>(allValues);
    }

    public Set<String> getRangeValueKeySetFromUserInput(String rangeValue) {
        Set<String> result = new LinkedHashSet<>();
        List<Integer> allValues = new ArrayList<>(getRangeValueListFromUserInput(rangeValue));
        int size = allValues.size();

        if (size > 0) {
            result.add(RANGE_VALUE_SEPARATOR + allValues.get(0));
            for (int i = 0; i < size - 1; i++) {
                result.add(allValues.get(i) + RANGE_VALUE_SEPARATOR + allValues.get(i + 1));
            }
            result.add(allValues.get(size - 1) + RANGE_VALUE_SEPARATOR);
        }
        return result;
    }

    /**
     * gets master list of all group values that's there in the trend list
     * 
     * @param trends
     * @param selector
     * @return {@link Map} of {@link String} and {@link Set} of {@link Object}
     */
    public Map<String, Set<Object>> getAllGroupValues(List<Trend> trends, FIQLSelector selector) {
        Map<String, Set<Object>> allValues = new HashMap<>();
        Set<String> groupFields = selector.getGroupSet();

        for (Trend trend : trends) {
            for (String groupField : groupFields) {
                try {
                    Field field = trend.getClass().getDeclaredField(groupField);
                    field.setAccessible(true);
                    if (!allValues.containsKey(groupField)) {
                        allValues.put(groupField, new HashSet<>());
                    }
                    allValues.get(groupField).add(field.get(trend));
                }
                catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    throw new ProAPIException("Error in reading field value: ", e);
                }
            }
        }
        return allValues;
    }

    /**
     * 
     * @param allGroupValues
     * @param selector
     * @param valuesForDummyObject
     * @return
     */
    public Object getDummyObject(
            Map<String, Set<Object>> allGroupValues,
            FIQLSelector selector,
            LinkedHashMap<String, Object> valuesForDummyObject) {
        Set<String> selectorGroups = selector.getGroupSet();
        Object result = null;

        List<String> allKeys = new ArrayList<>(valuesForDummyObject.keySet());
        String setGroupField = allKeys.get(allKeys.size() - 1);

        Iterator<String> iterator = selectorGroups.iterator();
        while (iterator.hasNext()) {
            String currentGroupField = iterator.next();
            if (currentGroupField.equals(setGroupField)) {
                if (iterator.hasNext()) {
                    String nextGroupField = iterator.next();
                    Map<Object, Object> nextCallResult = new HashMap<>();

                    for (Object groupValue : allGroupValues.get(nextGroupField)) {
                        @SuppressWarnings("unchecked")
                        LinkedHashMap<String, Object> valuesForNextDummyObject = (LinkedHashMap<String, Object>) valuesForDummyObject
                                .clone();
                        valuesForNextDummyObject.put(nextGroupField, groupValue);
                        nextCallResult.put(
                                UtilityClass.getResponseGroupKey(groupValue),
                                getDummyObject(allGroupValues, selector, valuesForNextDummyObject));
                    }
                    result = nextCallResult;
                }
                else {
                    result = populateGroupFieldsInDummyTrend(getDummyTrendObject(selector), valuesForDummyObject);
                }
            }
        }
        return result;
    }

    /**
     * populates group values in dummy {@link Trend} object
     * 
     * @param trend
     * @param groupValueMap
     *            key value map of group values that needs to be populated
     * @return
     */
    private Trend populateGroupFieldsInDummyTrend(Trend trend, Map<String, Object> groupValueMap) {
        for (String key : groupValueMap.keySet()) {
            try {
                Field field = trend.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(trend, groupValueMap.get(key));
            }
            catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new ProAPIException("Exception in generating group values", e);
            }
        }
        return trend;
    }

    /**
     * gets dummy {@link Trend} object populating extra attributes as required
     * by group fields
     * 
     * @param selector
     * @return
     */
    private Trend getDummyTrendObject(FIQLSelector selector) {
        Trend trend = new Trend();
        Set<String> selectorFields = selector.getFieldSet();
        for (String selectorField : selectorFields) {
            try {
                Trend.class.getField(selectorField);
            }
            catch (NoSuchFieldException | SecurityException e) {
                trend.getExtraAttributes().put(selectorField, null);
            }
        }
        return trend;
    }
}