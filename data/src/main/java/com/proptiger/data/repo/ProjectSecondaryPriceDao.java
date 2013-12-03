package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectSecondaryPrice;

@Repository
public interface ProjectSecondaryPriceDao extends PagingAndSortingRepository<ProjectSecondaryPrice, Integer>{

}
