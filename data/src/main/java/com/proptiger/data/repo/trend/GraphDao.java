package com.proptiger.data.repo.trend;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.trend.Graph;

public interface GraphDao extends PagingAndSortingRepository<Graph, Integer>, CustomGraphDao {
}
