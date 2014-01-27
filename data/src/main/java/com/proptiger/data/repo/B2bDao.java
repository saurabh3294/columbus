package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.pojo.PaginatedResponse;


public interface B2bDao {
	public PaginatedResponse<List<InventoryPriceTrend>> getFilteredDocuments(Selector selector);
}