/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.LandMarkService;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping(value = "data/v1/entity/")
public class LandMarkController extends BaseController {
    @Autowired
    private LandMarkService localityAmenityService;

    @RequestMapping(value = "locality/{id}/amenity", method = RequestMethod.GET)
    @ResponseBody
    public Object getAmenitiesByLocalityIdAndAmenity(@PathVariable("id") int localityId, @RequestParam(
            value = "amenity",
            required = false) String amenityName,
            @RequestParam(required = false, value = "selector") String selectorStr) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        return new APIResponse(localityAmenityService.getLocalityAmenitiesWithSelector(localityId, amenityName, selector));

    }
}
