package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.LandMarkTypes;
import com.proptiger.data.repo.LocalityAmenityTypeDao;

/**
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityAmenityTypeService {

    @Autowired
    private LocalityAmenityTypeDao             amenityTypeDao;

    private Map<Integer, LandMarkTypes> amenitiesTypesMap;

    /**
     * Get all types of available amenities. Only first call will get data from
     * DB and then subsequent calls will return the cached data from
     * amenitiesTypesMap
     * 
     * @return
     */
    public Map<Integer, LandMarkTypes> getLocalityAmenityTypes() {
        if (amenitiesTypesMap == null) {
            amenitiesTypesMap = new HashMap<>();
            List<LandMarkTypes> list = amenityTypeDao.findAll();
            if (list != null) {
                for (LandMarkTypes amenityType : list) {
                    amenitiesTypesMap.put(amenityType.getId(), amenityType);
                }
            }
        }
        return amenitiesTypesMap;
    }
}
