/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.TypeaheadService;

/**
 * 
 * @author mukand
 * @author hemendra
 * @author rahul
 */
@Controller
public class TypeaheadController extends BaseController {
    @Autowired
    private TypeaheadService typeaheadService;

    private final String     defaultCityName = "Noida";

    @RequestMapping(value = "app/v1/typeahead")
    @ResponseBody
    public APIResponse getTypeaheads(@RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city) {

        List<String> filterQueries = new ArrayList<String>();
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());
        }

        if (city != null && city.trim() != "") {
            filterQueries.add("TYPEAHEAD_CITY:" + city);
        }
        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        List<Typeahead> list = typeaheadService.getTypeaheads(query, rows, filterQueries);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @RequestMapping(value = "app/v2/typeahead")
    @ResponseBody
    public APIResponse getTypeaheadsV2(@RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city) {

        List<String> filterQueries = new ArrayList<String>();
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());
        }
        if (city != null && city.trim() != "") {
            filterQueries.add("TYPEAHEAD_CITY:" + city);
        }

        /* If users city is not given then we populate it with a default city */
        if (city == null || city.isEmpty()) {
            city = defaultCityName;
        }
        List<Typeahead> list = typeaheadService.getTypeaheadsV2(query, rows, filterQueries, city);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @RequestMapping(value = "app/v3/typeahead")
    @ResponseBody
    public APIResponse getTypeaheadsV3(@RequestParam String query,
            @RequestParam(defaultValue = "15") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false, defaultValue = defaultCityName) String city) {

        List<String> filterQueries = new ArrayList<String>();
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());
        }
        // if (city != null && city.trim() != "") {
        // filterQueries.add("TYPEAHEAD_CITY:" + city);
        // }

        /* If users city is not given then we populate it with a default city */
        if (city == null || city.isEmpty()) {
            city = defaultCityName;
        }
        List<Typeahead> list = typeaheadService.getTypeaheadsV3(query, rows, filterQueries, city);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @RequestMapping("app/v1/typeahead/exact")
    @ResponseBody
    public APIResponse getExactTypeaheads(@RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city) {

        List<String> filterQueries = new ArrayList<String>();
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());
        }
        if (city != null && city.trim() != "") {
            filterQueries.add("TYPEAHEAD_CITY:" + city);
        }

        List<Typeahead> list = typeaheadService.getExactTypeaheads(query, rows, filterQueries);
        return new APIResponse(super.filterFields(list, null), list.size());
    }
}
