/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.google.gson.Gson;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.repo.LocalityAmenityDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mukand
 */
@Service
public class LocalityAmenityService {
    @Autowired
    private LocalityAmenityDao localityAmenityDao;
    
    public List<LocalityAmenity> getAmenitiesByLocalityIdAndAmenity(int localityId, String amenityName){
        List<LocalityAmenity> output = null;
        
        if(amenityName == null || amenityName.isEmpty() )
            output = localityAmenityDao.getAmenitiesByLocalityId(localityId);
        else
            output = localityAmenityDao.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName);
        
        Gson gson = new Gson();
        
        return output;
    }
}
