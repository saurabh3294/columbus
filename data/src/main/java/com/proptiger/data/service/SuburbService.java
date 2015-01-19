/**
 * 
 */
package com.proptiger.data.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.model.proptiger.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.util.Constants;
import com.proptiger.data.repo.SuburbDao;

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
    public Suburb getSuburb(int suburbId, boolean useFieldSelector, Selector selector) {
        Suburb suburb = suburbDao.getSuburb(suburbId);
        if (suburb == null)
            return null;
        Set<String> fields = selector.getFields() == null ? new HashSet<String>(): selector.getFields();
        
        /*
         * setting avgPricePerUnitAreaBHKWise based on fieldSelector or no selector.
         */
        if(useFieldSelector == false || fields.contains("avgPricePerUnitAreaBHKWise")){
            suburb.setAvgBHKPricePerUnitArea(localityService.getAvgPricePerUnitAreaBHKWise(
                "suburbId",
                suburbId,
                suburb.getDominantUnitType()));
        }
        /*
         * setting project count and project status count based on field selector.
         */
        if(useFieldSelector == false || fields.contains("projectCount") || fields.contains("projectStatusCount")){
            updateProjectCountAndStatusCount(suburb);
        }
        /*
         * Setting the average rating of the suburb.
         */
        if(useFieldSelector == false || fields.contains("avgRatingsByCategory")){
            LocalityAverageRatingByCategory avgRatingsOfLocalityCategory = localityService.getSuburbRatingAndReviewDetails(suburb);
            suburb.setAvgRatingsByCategory(avgRatingsOfLocalityCategory);
        }
        /*
         * setting images.
         */
        if(useFieldSelector == false || fields.contains("images")){
            suburb.setImages(imageService.getImages(DomainObject.suburb, null, suburbId));
        }
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
    /*
     * updates description of the suburb
     */
    @Transactional
    public Suburb updateSuburb(Suburb suburb) {
        if(suburb.getDescription() != null && !suburb.getDescription().isEmpty()){
            Suburb suburbActual=suburbDao.findOne(suburb.getId());
            suburbActual.setDescription(suburb.getDescription());
            suburbActual = suburbDao.save(suburbActual);
            return suburbActual;
        }else{
        throw new BadRequestException("Invalid suburb description");
        }
    }

   
}