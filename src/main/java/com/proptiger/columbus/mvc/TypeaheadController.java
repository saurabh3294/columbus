/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService;
import com.proptiger.core.service.ApiVersionService.ApiVersion;
import com.proptiger.core.service.ConfigService.ConfigGroupName;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.core.util.UtilityClass;

/**
 * 
 * @author mukand
 * @author hemendra
 * @author rahul
 */
@Controller
@DisableCaching
public class TypeaheadController extends BaseController {

    @Autowired
    private TypeaheadService  typeaheadService;

    @Autowired
    private ApiVersionService apiVersionService;

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
        getFilterQueryListFromRequestParams(filterQueries, city, locality, typeAheadType);

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
        getFilterQueryListFromRequestParams(filterQueries, city, locality, typeAheadType);

        List<Typeahead> list = typeaheadService.getTypeaheadsV2(query, rows, filterQueries);
        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @Intercepted.TypeaheadListing
    @RequestMapping(value = { "app/v3/typeahead" })
    @ResponseBody
    public APIResponse getTypeaheadsV3(HttpServletRequest request, @RequestParam String query, @RequestParam(
            defaultValue = "5") int rows, @RequestParam(required = false) String typeAheadType, @RequestParam(
            required = false) String city, @RequestParam(required = false) String locality, @RequestParam(
            required = false) String usercity, @RequestParam(required = false) String enhance) {

        List<String> filterQueries = new ArrayList<String>();
        getFilterQueryListFromRequestParams(filterQueries, city, locality, typeAheadType);
        usercity = getCityContext(usercity, request);
        List<Typeahead> list = typeaheadService.getTypeaheadsV3(query, rows, filterQueries, usercity, enhance);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

    @Intercepted.TypeaheadListing
    @RequestMapping(value = { "app/v4/typeahead" })
    @ResponseBody
    public APIResponse getTypeaheadsV4(HttpServletRequest request, @RequestParam String query, @RequestParam(
            defaultValue = "5") int rows, @RequestParam(required = false) String typeAheadType, @RequestParam(
            required = false) String city, @RequestParam(required = false) String locality, @RequestParam(
            required = false) String usercity, @RequestParam(required = false) String enhance) {

        ApiVersion version = getApiVersion();

        city = (city == null ? null : city.toLowerCase());
        usercity = (usercity == null ? null : usercity.toLowerCase());
        Map<String, String> filterQueries = getFilterQueryMapFromRequestParams(city, locality, typeAheadType);
        usercity = getCityContext(usercity, request);
        List<Typeahead> list = typeaheadService.getTypeaheadsV4(query, rows, filterQueries, usercity, enhance);

        return new APIResponse(super.filterFields(list, null), (long) (list.size()), version);
    }

    private String getCityContext(String usercity, HttpServletRequest request) {
        /* if city was explicitly set in URL use that */
        if (usercity != null && !usercity.isEmpty()) {
            return usercity;
        }

        /*
         * return null here (because now null means no-city-boosting)
         * defaultConfiguredCity is used only by templates and will be set
         * there.
         */
        return null;
    }

    private void getFilterQueryListFromRequestParams(
            List<String> filterQueries,
            String city,
            String locality,
            String typeAheadType) {
        Map<String, String> filterQueryMap = getFilterQueryMapFromRequestParams(city, locality, typeAheadType);
        filterQueries.addAll(UtilityClass.convertMapToDlimSeparatedKeyValueList(filterQueryMap, ":"));
    }

    private Map<String, String> getFilterQueryMapFromRequestParams(String city, String locality, String typeAheadType) {
        Map<String, String> filterQueries = new HashMap<String, String>();
        if (city != null && city.trim() != "") {
            filterQueries.put("TYPEAHEAD_CITY", city);
        }
        if (locality != null && locality.trim() != "") {
            filterQueries.put("TYPEAHEAD_LOCALITY", "(\"" + locality + "\")");
        }
        if (typeAheadType != null && typeAheadType.trim() != "") {
            filterQueries.put("TYPEAHEAD_TYPE", typeAheadType.toUpperCase());
        }
        return filterQueries;
    }

    private ApiVersion getApiVersion() {
        ApiVersion version = apiVersionService.getVersion(
                ConfigGroupName.Search,
                RequestHolderUtil.getMixpanelDistinctIdFromRequestCookie());
        return version;
    }

}
