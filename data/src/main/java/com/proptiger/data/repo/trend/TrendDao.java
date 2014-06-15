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
import com.proptiger.data.model.trend.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.util.Constants;

@Repository
public class TrendDao {
    @Autowired
    private EntityManagerFactory emf;

    private TrendDaoFieldSwitcher trendDaoFieldSwitcher;

    public TrendDao() {
        trendDaoFieldSwitcher = new TrendDaoFieldSwitcher();
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    public List<InventoryPriceTrend> getTrend(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<InventoryPriceTrend> builder = new JPAQueryBuilder<>(
                entityManager,
                InventoryPriceTrend.class);
        HashMap<String, String> fieldSwitchMap = trendDaoFieldSwitcher.getFieldSwitchMap(selector);
        builder.buildQuery(modifyWavgFieldsInSelector(selector, fieldSwitchMap));
        List<InventoryPriceTrend> modifyWavgKeysInResultSet = modifyWavgKeysInResultSet(
                builder.retrieveResults(),
                fieldSwitchMap);
        entityManager.close();
        return modifyWavgKeysInResultSet;
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    public long getResultCount(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<InventoryPriceTrend> builder = new JPAQueryBuilder<>(
                entityManager,
                InventoryPriceTrend.class);
        builder.buildQuery(modifyWavgFieldsInSelector(selector, trendDaoFieldSwitcher.getFieldSwitchMap(selector)));
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
        return fiqlSelector;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private List<InventoryPriceTrend> modifyWavgKeysInResultSet(
            List<InventoryPriceTrend> list,
            HashMap<String, String> fieldSwitchMap) {

        for (InventoryPriceTrend inventoryPriceTrend : list) {

            Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
            Map<String, Object> newExtraAttributes = new HashMap<>();

            /* First switch back manually-overridden-columns */

            for (Map.Entry<String, String> mapEntry : fieldSwitchMap.entrySet()) {
                newExtraAttributes.put(mapEntry.getKey(), extraAttributes.get(mapEntry.getValue()));
            }
            inventoryPriceTrend.setExtraAttributes(newExtraAttributes);
        }
        return list;
    }
}