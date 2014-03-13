package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectError;

public interface ProjectErrorDao extends PagingAndSortingRepository<ProjectError, Integer> {

}
