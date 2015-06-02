/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.repo.TopsearchDao;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.UtilityClass;

/**
 * @author Manmohan
 */

@Service
public class TopsearchService {

    @Autowired
    private TopsearchDao topsearchDao;

    /**
     * This method will return the list of topsearch results based on the
     * params.
     */
    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTopsearches(int entityId, String entityType, String requiredEntities, int rows) {
        List<Typeahead> topsearches = topsearchDao.getTopsearchess(entityId, entityType, requiredEntities);
        if (!topsearches.isEmpty()) {
            Collections.shuffle(topsearches);
        }

        topsearches = UtilityClass.getFirstNElementsOfList(topsearches, rows);
        return topsearches;
    }

}
