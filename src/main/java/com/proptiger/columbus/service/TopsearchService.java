/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.repo.TopsearchDao;
import com.proptiger.columbus.util.Topsearch;
import com.proptiger.core.util.Constants;

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
    public List<Topsearch> getTopsearches(int entityId, String requiredEntities) {
        List<Topsearch> topsearches = topsearchDao.getTopsearchess(entityId, requiredEntities);
        return topsearches;
    }

}
