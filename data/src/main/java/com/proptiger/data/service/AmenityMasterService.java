package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.AmenityMaster;
import com.proptiger.data.repo.AmenityMasterDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class AmenityMasterService {

    @Autowired
    private AmenityMasterDao amenityMasterDao;
    
    /**
     * If amenities found then return else return empty list
     * 
     * @param amenityIds
     * @return
     */
    public List<AmenityMaster> getMasterAmenities(List<Integer> amenityIds) {
        if (amenityIds != null && amenityIds.size() > 0) {
            return amenityMasterDao.findByAmenityIdIn(amenityIds);
        }
        return new ArrayList<>();
    }
}
