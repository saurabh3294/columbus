/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.pojo.ProAPISuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.service.LocalityAmenityService;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="v1/entity/locality/")
public class LocalityAmenityController extends BaseController{
    @Autowired
    private LocalityAmenityService localityAmenityService;
    
    @RequestMapping(value="{id}/amenity/", method = RequestMethod.GET)
    @ResponseBody
    public Object getAmenitiesByLocalityIdAndAmenity(@PathVariable("id")int localityId, @RequestParam(value = "amenity", required = false) String amenityName){
        return new ProAPISuccessResponse(localityAmenityService.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName));
        
    }
}
