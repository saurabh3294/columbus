/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.model.cms.Locality;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.LocalityService;

/**
 * @author mandeep
 * @author Rajeev Pandey
 * 
 */
@Controller
public class LocalityController extends BaseController {
    @Autowired
    private LocalityService       localityService;

    @Autowired
    private LocalityReviewService localityReviewService;

    @Autowired
    private ImageService          imageService;

    /**
     * Returns localities given a selector ordered by priority
     * 
     * @param selector
     * @return
     */
    @RequestMapping(value = {"data/v1/entity/locality", "data/v1/entity/locality/top"})
    @ResponseBody
    public APIResponse getLocalities(@RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        PaginatedResponse<List<Locality>> localityList = localityService.getLocalitiesWithRatingsAndReviews(localitySelector);
        localityService.updateLocalitiesLifestyleScoresAndRatings(localityList.getResults());
        return new APIResponse(
                super.filterFields(localityList.getResults(), localitySelector.getFields()),
                localityList.getTotalCount());
    }
    @RequestMapping(value = {"data/v2/entity/locality", "data/v2/entity/locality/top"})
    @ResponseBody
    public APIResponse getLocalitiesV2(@RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        PaginatedResponse<List<Locality>> localityList = localityService.getLocalitiesWithRatingsAndReviews(localitySelector);
        return new APIResponse(
                super.filterFields(localityList.getResults(), localitySelector.getFields()),
                localityList.getTotalCount());
    }

    /**
     * This method find popular localities of either city id or suburb id.
     * Popularity is defined by priority of all localities, if there is a tie
     * then take the localities in which number of leads was maximum in last π
     * weeks
     * 
     * @param selector
     * @return
     */
    @RequestMapping(value = "data/v1/entity/locality/popular")
    @ResponseBody
    public APIResponse getPopularLocalitiesOfCity(
            @RequestParam(required = false, value = "cityId") Integer cityId,
            @RequestParam(required = false, value = "suburbId") Integer suburbId,
            @RequestParam(required = false, value = "enquiryInWeeks", defaultValue = "8") Integer enquiryInWeeks,
            @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> popularLocalities = localityService.getPopularLocalities(
                cityId,
                suburbId,
                enquiryInWeeks,
                localitySelector);
        localityService.updateLocalitiesLifestyleScoresAndRatings(popularLocalities);
        return new APIResponse(
                super.filterFields(popularLocalities, localitySelector.getFields()),
                popularLocalities.size());
    }

    @RequestMapping(value = "data/v2/entity/locality/popular")
    @ResponseBody
    public APIResponse getPopularLocalitiesOfCityV2(
            @RequestParam(required = false, value = "cityId") Integer cityId,
            @RequestParam(required = false, value = "suburbId") Integer suburbId,
            @RequestParam(required = false, value = "enquiryInWeeks", defaultValue = "8") Integer enquiryInWeeks,
            @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> popularLocalities = localityService.getPopularLocalities(
                cityId,
                suburbId,
                enquiryInWeeks,
                localitySelector);
        return new APIResponse(
                super.filterFields(popularLocalities, localitySelector.getFields()),
                popularLocalities.size());
    }
    /**
     * Get top localities for city id or suburb id
     * 
     * @param cityId
     * @param suburbId
     * @param selector
     * @return
     */
    @RequestMapping(value = "data/v1/entity/locality/top-rated")
    @ResponseBody
    public APIResponse getTopLocalitiesOfCityOrSuburb(
            @RequestParam(required = false, value = "cityId") Integer cityId,
            @RequestParam(required = false, value = "suburbId") Integer suburbId,
            @RequestParam(required = false, defaultValue = "4", value = "imageCount") Integer imageCount,
            @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> result = localityService.getTopRatedLocalities(cityId, suburbId, localitySelector, imageCount);
        localityService.updateLocalitiesLifestyleScoresAndRatings(result);
        return new APIResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }
    
    @RequestMapping(value = "data/v2/entity/locality/top-rated")
    @ResponseBody
    public APIResponse getTopLocalitiesOfCityOrSuburbV2(
            @RequestParam(required = false, value = "cityId") Integer cityId,
            @RequestParam(required = false, value = "suburbId") Integer suburbId,
            @RequestParam(required = false, defaultValue = "4", value = "imageCount") Integer imageCount,
            @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> result = localityService.getTopRatedLocalities(cityId, suburbId, localitySelector, imageCount);
        return new APIResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }

    /**
     * Computes center of a given locality as per
     * http://web.nmsu.edu/~xiumin/project/smallest_enclosing_circle/SEC.java
     * 
     * @param localityId
     * @return
     */
    @RequestMapping(value = "data/v1/entity/locality/{localityId}/center")
    @ResponseBody
    public APIResponse getCenter(@PathVariable int localityId) {
        return new APIResponse(localityService.computeCenter(localityId));
    }

    /**
     * Get top localities around X km from provided locality id
     * 
     * @param localityId
     * @param selector
     * @return
     */
    @RequestMapping(value = "data/v1/entity/locality/{localityId}/top-rated")
    @ResponseBody
    public APIResponse getTopLocalitiesAroundLocality(@PathVariable Integer localityId, @RequestParam(
            required = false,
            defaultValue = "4",
            value = "imageCount") Integer imageCount, @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> result = localityService.getTopRatedLocalitiesAroundLocality(
                localityId,
                localitySelector,
                imageCount,
                null);
        localityService.updateLocalitiesLifestyleScoresAndRatings(result);
        return new APIResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }

    @RequestMapping(value = "data/v2/entity/locality/{localityId}/top-rated")
    @ResponseBody
    public APIResponse getTopLocalitiesAroundLocalityV2(@PathVariable Integer localityId, @RequestParam(
            required = false,
            defaultValue = "4",
            value = "imageCount") Integer imageCount, @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> result = localityService.getTopRatedLocalitiesAroundLocality(
                localityId,
                localitySelector,
                imageCount,
                null);
        return new APIResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }

    @RequestMapping("data/v1/entity/locality/{localityId}/radius")
    @ResponseBody
    @Deprecated
    public APIResponse getLocalityRadiusOnProject(@PathVariable int localityId) {
        return new APIResponse(localityService.getMaxRadiusForLocalityOnProject(localityId));
    }

    @RequestMapping(value = "data/v1/entity/locality/top-reviewed")
    @ResponseBody
    public APIResponse getTopReviewedLocality(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "2") int minReviewCount,
            @RequestParam(required = false, defaultValue = "5") int numberOfLocalities,
            @RequestParam(required = false) String selector) {

        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }

        PaginatedResponse<List<Locality>> localities = localityService.getTopReviewedLocalities(
                locationType,
                locationId,
                minReviewCount,
                numberOfLocalities,
                localitySelector);
        localityService.updateLocalitiesLifestyleScoresAndRatings(localities.getResults());
        return new APIResponse(super.filterFields(localities.getResults(), localitySelector.getFields()), localities.getTotalCount());
    }

    @RequestMapping(value = "data/v2/entity/locality/top-reviewed")
    @ResponseBody
    public APIResponse getTopReviewedLocalityV2(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "2") int minReviewCount,
            @RequestParam(required = false, defaultValue = "5") int numberOfLocalities,
            @RequestParam(required = false) String selector) {

        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }

        PaginatedResponse<List<Locality>> localities = localityService.getTopReviewedLocalities(
                locationType,
                locationId,
                minReviewCount,
                numberOfLocalities,
                localitySelector);
        return new APIResponse(super.filterFields(localities.getResults(), localitySelector.getFields()), localities.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/locality/highest-return")
    @ResponseBody
    public APIResponse getHighestReturnLocalities(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "5") int numberOfLocalities,
            @RequestParam(required = false, defaultValue = "5") double minimumPriceRise,
            @RequestParam(required = false) String selector) {

        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }

        PaginatedResponse<List<Locality>> localities = localityService.getHighestReturnLocalities(
                locationType,
                locationId,
                numberOfLocalities, minimumPriceRise, localitySelector);
        localityService.updateLocalitiesLifestyleScoresAndRatings(localities.getResults());
        return new APIResponse(super.filterFields(localities.getResults(), localitySelector.getFields()), localities.getTotalCount());
    }
    
    @RequestMapping(value = "data/v2/entity/locality/highest-return")
    @ResponseBody
    public APIResponse getHighestReturnLocalitiesV2(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "5") int numberOfLocalities,
            @RequestParam(required = false, defaultValue = "5") double minimumPriceRise,
            @RequestParam(required = false) String selector) {

        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }

        PaginatedResponse<List<Locality>> localities = localityService.getHighestReturnLocalities(
                locationType,
                locationId,
                numberOfLocalities, minimumPriceRise, localitySelector);

        return new APIResponse(super.filterFields(localities.getResults(), localitySelector.getFields()), localities.getTotalCount());
    }
}
