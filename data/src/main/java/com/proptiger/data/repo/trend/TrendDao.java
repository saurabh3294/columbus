package com.proptiger.data.repo.trend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.trend.Trend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.ReflectionUtils;
import com.proptiger.exception.ProAPIException;

@Repository
public class TrendDao {
    @Autowired
    private EntityManagerFactory  emf;

    private TrendDaoFieldSwitcher trendDaoFieldSwitcher;

    public TrendDao() {
        trendDaoFieldSwitcher = new TrendDaoFieldSwitcher();
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    public List<Trend> getTrend(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<Trend> builder = new JPAQueryBuilder<>(
                entityManager,
                Trend.class);
        HashMap<String, String> fieldSwitchMap = trendDaoFieldSwitcher.getFieldSwitchMapForSelectorFields(selector);
        builder.buildQuery(modifyFieldsInSelector(selector, fieldSwitchMap));
        List<Trend> modifyWavgKeysInResultSet = modifyFieldsInResultSet(
                builder.retrieveResults(),
                fieldSwitchMap);
        entityManager.close();
        return modifyWavgKeysInResultSet;
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    public long getResultCount(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<Trend> builder = new JPAQueryBuilder<>(
                entityManager,
                Trend.class);
        builder.buildQuery(modifyFieldsInSelector(selector, trendDaoFieldSwitcher.getFieldSwitchMapForSelectorFields(selector)));
        long count = builder.retrieveCount();
        entityManager.close();
        return count;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private FIQLSelector modifyFieldsInSelector(FIQLSelector selector, HashMap<String, String> fieldSwitchMap) {

        FIQLSelector fiqlSelector = selector.clone();
        fiqlSelector.setFields("");
        for (Map.Entry<String, String> mapEntry : fieldSwitchMap.entrySet()) {
            fiqlSelector.addField(mapEntry.getValue());
        }
        
        String newSorter = trendDaoFieldSwitcher.getFieldSwitchedSorter(fiqlSelector);
        fiqlSelector.setSort(newSorter);

        return fiqlSelector;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private List<Trend> modifyFieldsInResultSet(List<Trend> list, HashMap<String, String> fieldSwitchMap) {

        String mapKey, mapValue;
        for (Trend inventoryPriceTrend : list) {

            Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
            Map<String, Object> newExtraAttributes = new HashMap<>();

            for (Map.Entry<String, String> mapEntry : fieldSwitchMap.entrySet()) {
                
                mapKey = mapEntry.getKey();
                mapValue = mapEntry.getValue();
                
                /* 1. Modify "Extra-Attribute" Fields : key names are modified. */
                if (extraAttributes.containsKey(mapValue)) {
                    newExtraAttributes.put(mapKey, extraAttributes.get(mapValue));
                }
                /* 2. Modify model class (Trend) fields : values are copied */
                else if(!(mapValue.equals(mapKey))){
                    try {
                        ReflectionUtils.copyFieldsInSameObject(
                                inventoryPriceTrend,
                                mapValue,
                                mapKey);
                    }
                    catch (Exception ex) {
                        throw new ProAPIException(ex);
                    }
                }
            }
            inventoryPriceTrend.setExtraAttributes(newExtraAttributes);
        }

        return list;
    }
}
