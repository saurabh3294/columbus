/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.app.mvc;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityAmenityService;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping(value = "app/v1/amenity")
public class AppLocalityAmenityController extends BaseController {
    @Autowired
    private LocalityAmenityService localityAmenityService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ProAPIResponse getAllAmenitiesOnLocality(
            @RequestParam(value = "city-id", required = false) Integer cityId,
            @RequestParam(value = "locality-ids", required = false) String localityIds) {
        if (cityId == null && localityIds == null) {
            return new ProAPIErrorResponse("Error", "city-ids and locality-ids both should not be false");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {}.getType();
        List<Integer> localityIdsList = gson.fromJson(localityIds, type);

        List<LocalityAmenity> data = localityAmenityService.getAmenitiesByHighPriorityLocalityId(
                cityId,
                localityIdsList);

        return new ProAPISuccessResponse(data);
    }
}
