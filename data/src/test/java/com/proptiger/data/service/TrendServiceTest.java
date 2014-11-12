package com.proptiger.data.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.service.trend.TrendService;

public class TrendServiceTest extends AbstractTest {
    @Autowired
    private TrendService trendService;

    @Test
    private void testGetDummyObject() {
        Map<String, Set<Object>> groupValues = new HashMap<>();
        Set<Object> cityIds = new HashSet<>();
        cityIds.add(1);
        cityIds.add(2);
        groupValues.put("cityId", cityIds);

        Set<Object> localityIds = new HashSet<>();
        localityIds.add(100);
        localityIds.add(200);
        groupValues.put("localityId", localityIds);

        FIQLSelector selector = new FIQLSelector();
        selector.setGroup("cityId,localityId");

        LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("cityId", 1);

        Map<String, Trend> map = (Map<String, Trend>) trendService.getDummyObject(groupValues, selector, valueMap);
        Assert.assertTrue(((Trend) map.get(100)).getLocalityId().equals(100));
        Assert.assertTrue(((Trend) map.get(100)).getCityId().equals(1));
        Assert.assertTrue(((Trend) map.get(200)).getLocalityId().equals(200));
        Assert.assertTrue(((Trend) map.get(200)).getCityId().equals(1));
    }
}
