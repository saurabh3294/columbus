/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.Topsearch;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TopsearchDao;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.UtilityClass;

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

    private static Logger           logger              = LoggerFactory.getLogger(TypeaheadService.class);

    @Autowired
    private HttpRequestUtil         httpRequestUtil;

    private Map<String, City>       cityNameToIdMap;

    private int                     MAX_CITY_COUNT      = 1000;

    private String                  domainObjectIdRegex = "^[\\d]{5,6}$";

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
