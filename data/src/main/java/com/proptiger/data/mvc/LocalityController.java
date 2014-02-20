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

import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author mandeep
 * @author Rajeev Pandey
 * 
 */
@RequestMapping("data/v1/entity/locality")
@Controller
public class LocalityController extends BaseController {
    @Autowired
    private LocalityService       localityService;

    @Autowired
    private LocalityReviewService localityReviewService;

    @Autowired
    private ImageService          imageService;

    /**
     * Returns localities given a selector
     * 
     * @param selector
     * @return
     */
    @RequestMapping
    @ResponseBody
    public ProAPIResponse getLocalities(@RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> localityList = localityService.getLocalities(localitySelector);
        return new ProAPISuccessCountResponse(
                super.filterFields(localityList, localitySelector.getFields()),
                localityList.size());
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
    @RequestMapping(value = "/popular")
    @ResponseBody
    public ProAPIResponse getPopularLocalitiesOfCity(
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
        return new ProAPISuccessCountResponse(
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
    @RequestMapping(value = "/top")
    @ResponseBody
    public ProAPIResponse getTopLocalitiesOfCityOrSuburb(
            @RequestParam(required = false, value = "cityId") Integer cityId,
            @RequestParam(required = false, value = "suburbId") Integer suburbId,
            @RequestParam(required = false, defaultValue = "4", value = "imageCount") Integer imageCount,
            @RequestParam(required = false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        List<Locality> result = localityService.getTopLocalities(cityId, suburbId, localitySelector, imageCount);
        return new ProAPISuccessCountResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }

    /**
     * Computes center of a given locality as per
     * http://web.nmsu.edu/~xiumin/project/smallest_enclosing_circle/SEC.java
     * 
     * @param localityId
     * @return
     */
    @RequestMapping(value = "{localityId}/center")
    @ResponseBody
    public ProAPIResponse getCenter(@PathVariable int localityId) {
        return new ProAPISuccessResponse(localityService.computeCenter(localityId));
    }

    /**
     * Get top localities around X km from provided locality id
     * 
     * @param localityId
     * @param selector
     * @return
     */
    @RequestMapping(value = "{localityId}/top")
    @ResponseBody
    public ProAPIResponse getTopLocalitiesAroundLocality(@PathVariable Integer localityId, @RequestParam(
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
        return new ProAPISuccessCountResponse(super.filterFields(result, localitySelector.getFields()), result.size());
    }

    @RequestMapping("/{localityId}/radius")
    @ResponseBody
    @Deprecated
    public ProAPIResponse getLocalityRadiusOnProject(@PathVariable int localityId) {
        return new ProAPISuccessResponse(localityService.getMaxRadiusForLocalityOnProject(localityId));
    }

    @RequestMapping(value = "top-reviewed")
    @ResponseBody
    public ProAPIResponse getTopReviewedLocality(
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
                numberOfLocalities);
        return new ProAPISuccessResponse(super.filterFields(localities, localitySelector.getFields()));
    }
}
