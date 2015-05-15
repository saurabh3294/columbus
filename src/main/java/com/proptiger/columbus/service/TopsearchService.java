/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.proptiger.columbus.model.Topsearch;
import com.proptiger.columbus.repo.TopsearchDao;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;


/**
 * @author Manmohan
 */

@Service
public class TopsearchService {

    @Autowired
    private TopsearchDao            topsearchDao;

    @Autowired
    private EntitySuggestionHandler entitySuggestionHandler;

    @Autowired
    private NLPSuggestionHandler    nlpSuggestionHandler;

    @Autowired
    private GooglePlacesAPIService  googlePlacesAPIService;

    @Autowired
    private HttpRequestUtil         httpRequestUtil;

    @Value("${google.place.threshold.score}")
    private int                     googlePlaceThresholdScore;

    @Value("${google.place.top.threshold.score}")
    private int                     googlePlaceTopThresholdScore;

    @Value("${own.results.privileged.slots}")
    private int                     ownResultsPrivilegedSlots;

    /**
     * This method will return the list of topsearch results based on the
     * params.
     * 
     * @param query
     * @param rows
     * @param filterQueries
     * @return List<Typeahead>
     */
    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Topsearch> getTopsearches(String requiredEntities, int rows, List<String> filterQueries) {
        List<Topsearch> topsearches = topsearchDao.getTopsearches(requiredEntities, rows, filterQueries);
        
        return topsearches;
    }



}
