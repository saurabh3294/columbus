package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.B2b;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.pojo.PaginatedResponse;

public interface B2bCustomDao {
	B2b getB2bById(int documentId);
	public PaginatedResponse<List<B2b>> getFilteredDocuments(Selector selector);
}