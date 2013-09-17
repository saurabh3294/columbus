/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.repo.LocalityAmenityDao;
import com.proptiger.data.repo.LocalityDao;
import java.util.Collection;
import java.util.LinkedList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


/**
 *
 * @author mukand
 */
@Service
public class LocalityAmenityService {
    @Autowired
    private LocalityAmenityDao localityAmenityDao;
    @Autowired
    private LocalityDao localityDao;
    
    public List<LocalityAmenity> getAmenitiesByLocalityIdAndAmenity(int localityId, String amenityName){
        List<LocalityAmenity> output = null;
        
        if(amenityName == null || amenityName.isEmpty() )
            output = localityAmenityDao.getAmenitiesByLocalityId(localityId);
        else
            output = localityAmenityDao.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName);
        
        Gson gson = new Gson();
        
        return output;
    }
    
    public List<LocalityAmenity> getAmenitiesByHighPriorityLocalityId(Integer cityId, List<Integer> localityIds){
        Pageable pageable = new PageRequest(0, 1);
        Page<Locality> localityInfo = null;
        if(localityIds != null)
            localityInfo = localityDao.findByLocalityIdInAndIsActiveAndDeletedFlagOrderByPriorityDescLabelAsc(localityIds, true, true, pageable);
        else
            localityInfo = localityDao.findByCityIdAndIsActiveAndDeletedFlagOrderByPriorityDesc(cityId, true, true, pageable);
                
        Integer localityId = localityInfo.getContent().get(0).getLocalityId();
        
        return localityAmenityDao.getAmenitiesByLocalityId(localityId);
    }
}
