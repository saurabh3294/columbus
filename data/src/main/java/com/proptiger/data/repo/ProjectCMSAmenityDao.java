package com.proptiger.data.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.ProjectCMSAmenity;

public interface ProjectCMSAmenityDao extends JpaRepository<ProjectCMSAmenity, Long> {
    @Query("SELECT pca FROM ProjectCMSAmenity pca JOIN FETCH pca.amenityMaster am WHERE pca.amenityId=am.amenityId AND pca.projectId=?1 AND pca.verified = 1")
    List<ProjectCMSAmenity> findByProjectId(int projectId);
    
    @Query("SELECT pca FROM ProjectCMSAmenity pca JOIN FETCH pca.amenityMaster am WHERE pca.amenityId=am.amenityId AND pca.projectId=?1 and pca.amenityId IN ?2")
    List<ProjectCMSAmenity> findByProjectIdAndMasterAmenityIds(int projectId, Set<Integer> masterAmenityIds);
}
