/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

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

    private final String     defaultCityName     = "Noida";

    private String           cityCookieLabel     = "HOME_CITY";
    private String           cityCookieSeparater = "%2C";

    @Intercepted.TypeaheadListing
    @RequestMapping(value = "app/v1/typeahead")
    @ResponseBody
    public APIResponse getTypeaheads(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality) {

        List<String> filterQueries = new ArrayList<String>();
        addReqParamBasedFilterToQuery(filterQueries, city, locality, typeAheadType);

        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        List<Typeahead> list = typeaheadService.getTypeaheads(query, rows, filterQueries);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @Intercepted.TypeaheadListing
    @RequestMapping(value = "app/v2/typeahead")
    @ResponseBody
    public APIResponse getTypeaheadsV2(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality) {

        List<String> filterQueries = new ArrayList<String>();
        addReqParamBasedFilterToQuery(filterQueries, city, locality, typeAheadType);

        List<Typeahead> list = typeaheadService.getTypeaheadsV2(query, rows, filterQueries);
        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @Intercepted.TypeaheadListing
    @RequestMapping(value = "app/v3/typeahead")
    @ResponseBody
    public APIResponse getTypeaheadsV3(HttpServletRequest request, @RequestParam String query, @RequestParam(
            defaultValue = "5") int rows, @RequestParam(required = false) String typeAheadType, @RequestParam(
            required = false) String city, @RequestParam(required = false) String locality) {

        List<String> filterQueries = new ArrayList<String>();
        addReqParamBasedFilterToQuery(filterQueries, city, locality, typeAheadType);

        city = getCityContext(city, request);
        List<Typeahead> list = typeaheadService.getTypeaheadsV3(query, rows, filterQueries, city);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @RequestMapping(value = "app/v4/typeahead")
    @ResponseBody
    public APIResponse getTypeaheadsV4(HttpServletRequest request, @RequestParam String query, @RequestParam(
            defaultValue = "5") int rows, @RequestParam(required = false) String typeAheadType, @RequestParam(
            required = false) String city, @RequestParam(required = false) String locality) {

        List<String> filterQueries = new ArrayList<String>();
        addReqParamBasedFilterToQuery(filterQueries, city, locality, typeAheadType);

        city = getCityContext(city, request);
        List<Typeahead> list = typeaheadService.getTypeaheadsV4(query, rows, filterQueries, city);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    private String getCityContext(String city, HttpServletRequest request) {
        /* if city was explicitly set in URL use that */
        if (city != null && !city.isEmpty()) {
            return city;
        }

        /* fall back to city extraction form cookie */
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null) {
            return defaultCityName;
        }

        for (Cookie c : cookies) {
            if (c.getName().equals(cityCookieLabel)) {
                city = StringUtils.substringAfter(c.getValue(), cityCookieSeparater);
                break;
            }
        }

        /* fall back to default configured city */
        return ((city == null || city.isEmpty()) ? defaultCityName : city);
    }

    @Intercepted.TypeaheadListing
    @RequestMapping("app/v1/typeahead/exact")
    @ResponseBody
    public APIResponse getExactTypeaheads(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int rows,
            @RequestParam(required = false) String typeAheadType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality) {

        List<String> filterQueries = new ArrayList<String>();
        addReqParamBasedFilterToQuery(filterQueries, city, locality, typeAheadType);

        List<Typeahead> list = typeaheadService.getExactTypeaheads(query, rows, filterQueries);
        return new APIResponse(super.filterFields(list, null), list.size());
    }

    private void addReqParamBasedFilterToQuery(
            List<String> filterQueries,
            String city,
            String locality,
            String typeAheadType) {
        if (city != null && city.trim() != "") {
            filterQueries.add("TYPEAHEAD_CITY:" + city);
        }
        if (locality != null && locality.trim() != "") {
            filterQueries.add("TYPEAHEAD_LOCALITY:(\"" + locality + "\")");
        }
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());
        }
    }
}
