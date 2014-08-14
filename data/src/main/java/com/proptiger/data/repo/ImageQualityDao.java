package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.image.ImageQuality;

@Repository
public interface ImageQualityDao extends JpaRepository<ImageQuality, Integer> {

    public ImageQuality findByImageTypeIdAndResolutionId(int imageTypeId ,int resolutionId);
}
