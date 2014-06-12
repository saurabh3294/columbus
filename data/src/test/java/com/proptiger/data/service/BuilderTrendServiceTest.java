package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.dto.external.trend.BuilderTrend;
import com.proptiger.data.enums.UnitType;
import com.proptiger.data.model.trend.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.trend.BuilderTrendService;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */
public class BuilderTrendServiceTest extends AbstractTest {

    @Autowired
    BuilderTrendService builderTrendService;

    @Test
    public void getLocalityDominantTypeFromListTest() {
        List<InventoryPriceTrend> inventoryPriceTrends = new ArrayList<>();

        InventoryPriceTrend inventoryPriceTrend1 = new InventoryPriceTrend();
        inventoryPriceTrend1.setIsDominantProjectUnitType(true);
        inventoryPriceTrend1.setLocalityId(1);
        inventoryPriceTrend1.setUnitType(UnitType.Apartment);
        inventoryPriceTrends.add(inventoryPriceTrend1);

        InventoryPriceTrend inventoryPriceTrend2 = new InventoryPriceTrend();
        inventoryPriceTrend2.setIsDominantProjectUnitType(false);
        inventoryPriceTrend2.setLocalityId(1);
        inventoryPriceTrend2.setUnitType(UnitType.Villa);
        inventoryPriceTrends.add(inventoryPriceTrend1);

        Map<Integer, Set<UnitType>> result = new HashMap<>();
        try {
            Method method = BuilderTrendService.class.getDeclaredMethod("getLocalityDominantTypeFromList", List.class);
            method.setAccessible(true);
            result = (Map<Integer, Set<UnitType>>) method.invoke(builderTrendService, inventoryPriceTrends);
            Assert.assertEquals(result.size(), 1);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ProAPIException(e);
        }
    }

    @Test
    public void getBuilderTrendTest() {
        FIQLSelector selector = new FIQLSelector();
        selector.setFilters("builderId==100002");
        BuilderTrend builderTrend = builderTrendService.getBuilderTrend(selector, null).get(0);
        Assert.assertEquals(builderTrend.getSupply() >= builderTrend.getLaunchedUnit(), true);
    }
}