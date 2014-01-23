package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectCMSAmenity;

public interface ProjectCMSAmenityDao extends PagingAndSortingRepository<ProjectCMSAmenity, Long> {
	List<ProjectCMSAmenity> findByProjectId(int projectId);
}
