package com.proptiger.data.repo.b2b;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.b2b.InventoryPriceTrend;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.util.Constants;

@Repository
public class TrendDao {
    @Autowired
    private EntityManagerFactory emf;

    @Cacheable(value = Constants.CacheName.CACHE)
    public List<InventoryPriceTrend> getTrend(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<InventoryPriceTrend> builder = new JPAQueryBuilder<>(
                entityManager,
                InventoryPriceTrend.class);
        builder.buildQuery(modifyWavgFieldsInSelector(selector));
        List<InventoryPriceTrend> modifyWavgKeysInResultSet = modifyWavgKeysInResultSet(builder.retrieveResults());
        entityManager.close();
        return modifyWavgKeysInResultSet;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private FIQLSelector modifyWavgFieldsInSelector(FIQLSelector selector) {
        FIQLSelector fiqlSelector;
        fiqlSelector = selector.clone();
        fiqlSelector.setFields(StringUtils.replace(fiqlSelector.getFields(), "OnSupply", "OnLtdSupply"));
        return fiqlSelector;
    }

    // XXX - Hack to switch column names without clients knowing about it
    private List<InventoryPriceTrend> modifyWavgKeysInResultSet(List<InventoryPriceTrend> list) {
        for (InventoryPriceTrend inventoryPriceTrend : list) {
            Map<String, Object> extraAttributes = inventoryPriceTrend.getExtraAttributes();
            Map<String, Object> newExtraAttributes = new HashMap<>();

            for (String key : extraAttributes.keySet()) {
                String newKey = StringUtils.replace(key, "OnLtdSupply", "OnSupply");
                newExtraAttributes.put(newKey, extraAttributes.get(key));
            }
            inventoryPriceTrend.setExtraAttributes(newExtraAttributes);
        }
        return list;
    }
}