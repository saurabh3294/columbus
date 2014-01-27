package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.pojo.PaginatedResponse;

@Repository
public class TrendDao {
	@Autowired
    private EntityManagerFactory emf;
	
	public PaginatedResponse<List<InventoryPriceTrend>> getTrend(FIQLSelector selector) {
        MySqlQueryBuilder<InventoryPriceTrend> builder = new MySqlQueryBuilder<>(emf.createEntityManager(), InventoryPriceTrend.class);
        builder.buildQuery(selector);
        builder.getTypedQuery().getResultList();
        PaginatedResponse<List<InventoryPriceTrend>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(builder.getTypedQuery().getResultList());
        paginatedResponse.setTotalCount(10);
        return paginatedResponse;
    }
}