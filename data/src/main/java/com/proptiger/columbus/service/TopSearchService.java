/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.repo.TopSearchDao;
import com.proptiger.core.enums.Domain;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.Constants;

/**
 * @author Manmohan
 */

@Service
public class TopSearchService {

    @Autowired
    private TopSearchDao topsearchDao;

    /**
     * 
     * This method will return the list of topsearch results based on the
     * params.
     * 
     * @param entityId
     *            (cityId, suburbId, localityId, builderId)
     * @param entityType
     *            (type of entity whose id is given in entityId,
     *            city|suburb|locality|builder)
     * @param requiredEntities
     *            (geographically lower order entity types compare to entity
     *            given in entityType param for which top searches are required,
     *            in comma separated string format, eg. if entity type is city
     *            than requiredEntities can be 'locality,project')
     * @param isGroup
     *            (true if topsearch results required in entity groups)
     * @param rows
     *            (no of rows, if isGroup is true than its the result count of
     *            individual entity types, if false than its the total count of
     *            results)
     * @return
     */
    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTopsearches(
            int entityId,
            String entityType,
            String requiredEntities,
            Boolean isGroup,
            int rows,
            Domain domain) {

        List<Typeahead> topsearches = topsearchDao.getTopSearches(
                entityId,
                entityType,
                requiredEntities,
                isGroup,
                rows,
                domain);
        return topsearches;
    }
}
