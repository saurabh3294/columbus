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
        builder.buildQuery(modifyWavgFieldsInSelector(selector, fieldSwitchMap));
        List<Trend> modifyWavgKeysInResultSet = modifyWavgKeysInResultSet(
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
        builder.buildQuery(modifyWavgFieldsInSelector(selector, trendDaoFieldSwitcher.getFieldSwitchMapForSelectorFields(selector)));
        long count = builder.retrieveCount();
        entityManager.close();
        return count;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private FIQLSelector modifyWavgFieldsInSelector(FIQLSelector selector, HashMap<String, String> fieldSwitchMap) {

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
    private List<Trend> modifyWavgKeysInResultSet(
            List<Trend> list,
            HashMap<String, String> fieldSwitchMap) {

        for (Trend inventoryPriceTrend : list) {

            Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
            Map<String, Object> newExtraAttributes = new HashMap<>();

            /* First switch back manually-overridden-columns */

            for (Map.Entry<String, String> mapEntry : fieldSwitchMap.entrySet()) {
                if (extraAttributes.containsKey(mapEntry.getValue())) {
                    newExtraAttributes.put(mapEntry.getKey(), extraAttributes.get(mapEntry.getValue()));
                }
            }
            inventoryPriceTrend.setExtraAttributes(newExtraAttributes);
        }
        return list;
    }
}
