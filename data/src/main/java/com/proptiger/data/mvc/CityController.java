package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.City;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.CityService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/city")
public class CityController extends BaseController {

    @Autowired
    private CityService cityService;

    /**
     * This methods get city details, If no filter provided in selector then it
     * will fetch all city details Single city can be fetched by using filter of
     * selector object
     * 
     * @param selectorStr
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getCities(@RequestParam(required = false, value = "selector") String selectorStr) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        List<City> list = cityService.getCityList(selector);
        Set<String> fieldsToSerialize = null;
        if (selector != null) {
            fieldsToSerialize = selector.getFields();
        }
        return new ProAPISuccessCountResponse(super.filterFields(list, fieldsToSerialize), list.size());
    }

    @RequestMapping(value = "/{cityId}")
    @ResponseBody
    public ProAPIResponse getCity(@PathVariable int cityId) {

        return new ProAPISuccessResponse(super.filterFields(cityService.getCityInfo(cityId), null));
    }
}
