package com.proptiger.columbus.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.service.PropguideService;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

@Controller
@DisableCaching
public class PropguideController extends BaseController {

    @Autowired
    private PropguideService propguideService;

    @RequestMapping(value = "app/v1/propguide")
    @ResponseBody
    public APIResponse getDocumentsV1(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") int rows) {

        String[] categories = StringUtils.split(category, ',');
        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        results = propguideService.getDocumentsV1(query, categories, rows);
        return new APIResponse(super.filterFields(results, null), results.size());
    }

    @RequestMapping(value = "app/v1/propguideListing")
    @ResponseBody
    public APIResponse getListingDocumentsV1(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "5") int rows) {

        String[] categories = StringUtils.split(category, ',');
        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        results = propguideService.getListingDocumentsV1(query, categories, start, rows);
        return new APIResponse(super.filterFields(results, null), results.size());
    }
}
