/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.pojo.Selector;
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

        suburb.setAvgBHKPricePerUnitArea(localityService.getAvgPricePerUnitAreaBHKWise(
                "suburbId",
                suburbId,
                suburb.getDominantUnitType()));
        return suburb;
    }
}
