package com.proptiger.data.repo;

import java.util.List;

import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile.FetchOverride;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectCMSAmenity;

public interface ProjectCMSAmenityDao extends PagingAndSortingRepository<ProjectCMSAmenity, Long> {
    @Query("SELECT pca FROM ProjectCMSAmenity pca JOIN FETCH pca.amenityMaster am WHERE pca.amenityId=am.amenityId AND pca.projectId=?1")
    List<ProjectCMSAmenity> findByProjectId(int projectId);
}
