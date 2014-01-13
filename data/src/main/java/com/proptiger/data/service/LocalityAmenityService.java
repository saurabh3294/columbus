/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.LocalityAmenityDao;
import com.proptiger.data.repo.LocalityDao;


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
    
    /**
     * Get the locality amenities based on locality id and amenity name.
     * If amenity name not provided the it will return all amenities present in locality
     * @param localityId
     * @param amenityName
     * @return
     */
    public List<LocalityAmenity> getLocalityAmenities(int localityId, String amenityName){
        List<LocalityAmenity> output = null;
        System.out.println("localityId"+localityId);
        if(amenityName == null || amenityName.isEmpty() )
            output = localityAmenityDao.getAmenitiesByLocalityId(localityId);
        else
            output = localityAmenityDao.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName);
        
        return output;
    }
    
    /**
     * This method will take the cityId or list of localities and select the locality with highest 
     * priority. Then return the amenites of that locality.
     * @param cityId
     * @param localityIds
     * @return List<LocalityAmenity>
     */
    public List<LocalityAmenity> getAmenitiesByHighPriorityLocalityId(Integer cityId, List<Integer> localityIds){
        Paging paging = new Paging(0, 1);
        List<Locality> localityInfo = null;
        if(localityIds != null)
            localityInfo = localityDao.findByLocationOrderByPriority(localityIds, "locality", paging, SortOrder.ASC);//findByLocalityIdInOrderByPriorityDescLabelAsc(localityIds, paging);
        else
            localityInfo = localityDao.findByLocationOrderByPriority(cityId, "city", paging, SortOrder.ASC);//findByCityIdOrderByPriority(cityId, paging, SortOrder.DESC);
                
        Integer localityId = localityInfo.get(0).getLocalityId();
        
        List<LocalityAmenity> data = localityAmenityDao.getAmenitiesByLocalityId(localityId);
        
        return data;
    }
}
