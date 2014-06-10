package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.Catchment;

public interface CatchmentDao extends PagingAndSortingRepository<Catchment, Integer>, CatchmentCustomDao {
}