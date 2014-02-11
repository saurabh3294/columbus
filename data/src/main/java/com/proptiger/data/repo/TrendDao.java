package com.proptiger.data.repo;

import java.util.List;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;

@Repository
public class TrendDao {
	@Autowired
    private EntityManagerFactory emf;
	
	public List<InventoryPriceTrend> getTrend(FIQLSelector selector) {
		AbstractQueryBuilder<InventoryPriceTrend> builder = new JPAQueryBuilder<>(emf.createEntityManager(), InventoryPriceTrend.class);
        builder.buildQuery(selector);
        return builder.retrieveResults();
    }
}