package com.proptiger.columbus.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.model.GooglePlace;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.service.GooglePlacesAPIService;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

@Controller
public class GooglePlacesAPIController extends BaseController {

    @Autowired
    GooglePlacesAPIService googlePlacesAPIService;

    @RequestMapping(value = "app/v1/gp/place-suggest")
    @ResponseBody
    public APIResponse getPlaceList(@RequestParam String query, @RequestParam(defaultValue = "5") int rows) {
        List<Typeahead> list = new ArrayList<Typeahead>();
        list.addAll(googlePlacesAPIService.getPlacePredictions(query, rows));
        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @RequestMapping(value = "app/v1/gp/place-detail")
    @ResponseBody
    public APIResponse getPlaceDetails(@RequestParam String placeId) {
        // "ChIJN1t_tDeuEmsRUsoyG83frY4"
        GooglePlace googlePlace = googlePlacesAPIService.getPlaceDetails(placeId);
        return new APIResponse(googlePlace);
    }

}
