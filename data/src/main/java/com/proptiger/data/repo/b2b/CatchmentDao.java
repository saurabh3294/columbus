package com.proptiger.data.repo.b2b;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.b2b.Catchment;

public interface CatchmentDao extends PagingAndSortingRepository<Catchment, Integer> {
    public List<Catchment> findByName(String name);
}