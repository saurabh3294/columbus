package com.proptiger.app.mvc;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.annotations.Intercepted;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Locality;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.LocalityService;

/**
 * Locality related data for specific need. Means many form of data will be
 * combined with these APIs
 * 
 * @author Mukand
 * @author Rajeev Pandey
 * 
 */
@Controller
public class AppLocalityController extends BaseController {

    @Autowired
    private LocalityService localityService;

    /**
     * @param selector
     * @return
     */
    @Intercepted.LocalityListing
    @RequestMapping(value = "app/v1/locality")
    @ResponseBody
    public APIResponse getLocalityListingData(@RequestParam(required = false) String selector) {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        PaginatedResponse<List<Locality>> solrRes = localityService.getLocalityListing(propRequestParam);
        localityService.updateLocalitiesLifestyleScoresAndRatings(solrRes.getResults());
        return new APIResponse(
                super.filterFields(solrRes.getResults(), propRequestParam.getFields()),
                solrRes.getTotalCount());
    }

    /**
     * @param selector
     * @return
     */
    @Intercepted.LocalityListing
    @RequestMapping(value = "app/v2/locality")
    @ResponseBody
    public APIResponse getLocalityListingDataV2(@RequestParam(required = false) String selector) {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        PaginatedResponse<List<Locality>> solrRes = localityService.getLocalityListing(propRequestParam);
        return new APIResponse(
                super.filterFields(solrRes.getResults(), propRequestParam.getFields()),
                solrRes.getTotalCount());
    }
    
    /**
     * @param localityId
     * @param selectorStr
     * @return
     */
    @RequestMapping(value = "app/v1/locality/{localityId}", method = RequestMethod.GET)
    @ResponseBody
    @DisableCaching
    public APIResponse getLocalityDetails(@PathVariable int localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @RequestParam(required = false) Integer imageCount) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        if (selector == null) {
            selector = new Selector();
        }
        Locality locality = localityService.getLocalityInfo(localityId, imageCount, selector);
        if (locality != null ) {
            localityService.updateLocalitiesLifestyleScoresAndRatings(Collections.singletonList(locality));
        }
        return new APIResponse(super.filterFields(locality, selector.getFields()));
    }

    /**
     * @param localityId
     * @param selectorStr
     * @return
     */
    @RequestMapping(value = "app/v2/locality/{localityId}", method = RequestMethod.GET)
    @ResponseBody
    @DisableCaching
    public APIResponse getLocalityDetailsV2(@PathVariable int localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @RequestParam(required = false) Integer imageCount) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        if (selector == null) {
            selector = new Selector();
        }
        Locality locality = localityService.getLocalityInfo(localityId, imageCount, selector);
        return new APIResponse(super.filterFields(locality, selector.getFields()));
    }
}
