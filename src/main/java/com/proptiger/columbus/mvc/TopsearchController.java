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

import com.proptiger.columbus.model.Topsearch;
import com.proptiger.columbus.service.TopsearchService;
import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService;
import com.proptiger.core.service.ApiVersionService.ApiVersion;
import com.proptiger.core.service.ConfigService.ConfigGroupName;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.core.util.UtilityClass;

/**
 * 
 * @author Manmohan

 */
@Controller
@DisableCaching
public class TopsearchController extends BaseController {

    @Autowired
    private TopsearchService  topsearchService;

    @Autowired
    private ApiVersionService apiVersionService;

    @Intercepted.TypeaheadListing
    @RequestMapping(value = "app/v1/topsearch")
    @ResponseBody
    public APIResponse getTopsearches(
            @RequestParam String entityType,
            @RequestParam(defaultValue = "5") int entityId,
            @RequestParam(required = false) String requiredEntities
         ) {
    	int rows = 6;
        List<String> filterQueries = new ArrayList<String>();
        getFilterQueryListFromRequestParams(filterQueries, entityType, entityId);

        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        List<Topsearch> list = topsearchService.getTopsearches(requiredEntities, rows, filterQueries);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

   


   

    private void getFilterQueryListFromRequestParams(
            List<String> filterQueries,
            String entityType,
            int entityId) {
        Map<String, String> filterQueryMap = getFilterQueryMapFromRequestParams(entityType, entityId);
        filterQueries.addAll(UtilityClass.convertMapToDlimSeparatedKeyValueList(filterQueryMap, ":"));
    }

    private Map<String, String> getFilterQueryMapFromRequestParams(String entityType, int entityId) {
        Map<String, String> filterQueries = new HashMap<String, String>();
        String str = "";
        String str1 = entityType.toUpperCase();
        if (str1 != null && entityType.trim() != "") {
	        if(str1.equals("CITY"))
	        	str = "TYPEAHEAD-CITY-";
	        else if(str1.equals("SUBURB"))
	        	str = "TYPEAHEAD-SUBURB-";
	        else if(str1.equals("LOCALITY"))
	        	str = "TYPEAHEAD-LOCALITY-";
	        else if(str1.equals("BUILDER"))
	        	str = "TYPEAHEAD-BUILDER-";
	        
	        if (entityId != 0) {
	        	//filterQueries.put("id", "TYPEAHEAD-CITY-1");
	            filterQueries.put("id", str+entityId);
	        }
        }
	        
        if (entityType != null && entityType.trim() != "") {
            filterQueries.put("TYPEAHEAD_TYPE", entityType.toUpperCase());
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
