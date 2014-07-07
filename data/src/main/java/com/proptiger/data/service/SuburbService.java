/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.SuburbDao;
import com.proptiger.data.util.Constants;

/**
 * @author mandeep
 * 
 */
@Service
public class SuburbService {
    @Autowired
    private SuburbDao       suburbDao;

    @Autowired
    private ProjectService  projectService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private ImageService    imageService;

    /**
     * This method will return the list of localities based on the selector.
     * 
     * @param selector
     * @return List<Suburb>
     */
    public List<Suburb> getSuburbs(Selector selector) {
        return Lists.newArrayList(suburbDao.getSuburbs(selector));
    }

    /**
     * This method will return the Suburb Object based on suburb Id.
     * 
     * @param suburbId
     * @return Suburb
     */
    public Suburb getSuburb(int suburbId) {
        Suburb suburb = suburbDao.getSuburb(suburbId);
        if (suburb == null)
            return null;

        suburb.setAvgBHKPricePerUnitArea(localityService.getAvgPricePerUnitAreaBHKWise(
                "suburbId",
                suburbId,
                suburb.getDominantUnitType()));
        updateProjectCountAndStatusCount(suburb);
        localityService.updateSuburbRatingAndReviewDetails(suburb);
        suburb.setImages(imageService.getImages(DomainObject.suburb, null, suburbId));
        return suburb;
    }

    private void updateProjectCountAndStatusCount(Suburb suburb) {
        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"suburbId\":" + suburb.getId()
                + "}}]}, \"paging\":{\"start\":0,\"rows\":0}}", Selector.class);
        Map<String, Long> projectStatusCount = projectService.getProjectStatusCount(selector);
        suburb.setProjectStatusCount(projectStatusCount);
    }
    
    public Suburb getSuburbById(int suburbId){
    	String js = "{\"filters\":{\"and\":[{\"equal\":{\"id\":" + suburbId + "}}]}}";
    	Gson gson = new Gson();
        Selector selector = gson.fromJson(js, Selector.class);
        List<Suburb> suburbs = getSuburbs(selector);
        if(suburbs == null || suburbs.isEmpty())
        	return null;
        	
        return suburbs.get(0); 
    }

    @Cacheable(value = Constants.CacheName.SUBURB_INACTIVE)
    public Suburb getActiveOrInactiveSuburbById(Integer id) {
        return suburbDao.findOne(id);
    }
}
