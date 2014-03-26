/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.SuburbDao;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author mandeep
 * 
 */
@Service
public class SuburbService {
    @Autowired
    private SuburbDao       suburbDao;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private LocalityService localityService;

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

        suburb.setAvgBHKPrice(localityService.getAvgPricePerUnitAreaBHKWise(
                "suburbId",
                suburbId,
                suburb.getDominantUnitType()));
        updateProjectCountAndStatusCount(suburb);
        return suburb;
    }
    
    private void updateProjectCountAndStatusCount(Suburb suburb) {
        Selector selector = new Gson().fromJson(
                "{\"filters\":{\"and\":[{\"equal\":{\"suburbId\":" + suburb.getId() + "}}]}, \"paging\":{\"start\":0,\"rows\":0}}",
                Selector.class);
        PaginatedResponse<List<Project>> response = projectService.getProjects(selector);
        if(response != null){
            suburb.setProjectCount(response.getTotalCount());
        }
        Map<String, Long> projectStatusCount = projectService.getProjectStatusCount(selector);
        suburb.setProjectStatusCount(projectStatusCount);
    }
}
