package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.exception.ResourceNotFoundException;
import com.proptiger.core.model.external.GooglePlace;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.GooglePlacesAPIService;

@Controller
public class GooglePlacesAPIController extends BaseController {

    @Autowired
    GooglePlacesAPIService googlePlacesAPIService;

    @RequestMapping(value = "app/v1/gp/place-detail/{placeId}")
    @ResponseBody
    public APIResponse getPlaceDetails(@PathVariable String placeId) {

        GooglePlace googlePlace = googlePlacesAPIService.getPlaceDetails(placeId);

        if (googlePlace == null) {
            throw new ResourceNotFoundException("Invalid place id : " + placeId);
        }

        return new APIResponse(googlePlace);
    }
}
