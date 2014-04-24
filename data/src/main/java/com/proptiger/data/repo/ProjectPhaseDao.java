package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectPhase;

public interface ProjectPhaseDao extends PagingAndSortingRepository<ProjectPhase, Integer>, CustomProjectPhaseDao {
}