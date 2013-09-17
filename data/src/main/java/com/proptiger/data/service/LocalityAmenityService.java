/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.repo.LocalityAmenityDao;

/**
 *
 * @author mukand
 */
@Service
public class LocalityAmenityService {
    @Autowired
    private LocalityAmenityDao localityAmenityDao;
    
    public Object getAmenitiesByLocalityIdAndAmenity(int localityId, String amenityName){
        System.out.println("*****************************************************");
        System.out.println("PARAMS "+localityId+" AMENITY "+amenityName);
        Object[] output = null;
        
        if(amenityName == null || amenityName.isEmpty() )
            output = localityAmenityDao.getAmenitiesByLocalityId(localityId);
        else
            output = localityAmenityDao.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName);
        
        Gson gson = new Gson();
        System.out.println(gson.toJson(output));
        System.out.println("***************************************************######################");
        return output;
    }
}
