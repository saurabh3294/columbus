package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.CatchmentProject;

public interface CatchmentProjectDao extends PagingAndSortingRepository<CatchmentProject, Integer> {
}