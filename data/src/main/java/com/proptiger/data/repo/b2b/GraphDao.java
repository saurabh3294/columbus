package com.proptiger.data.repo.b2b;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.b2b.Graph;

public interface GraphDao extends PagingAndSortingRepository<Graph, Integer>, CustomGraphDao {
}
