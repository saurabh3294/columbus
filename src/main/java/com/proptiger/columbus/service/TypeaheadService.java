/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.UtilityClass;

/**
 * @author mukand
 * @author Hemendra
 */

@Service
public class TypeaheadService {

    @Autowired
    private TypeaheadDao            typeaheadDao;

    @Autowired
    private EntitySuggestionHandler entitySuggestionHandler;

    @Autowired
    private NLPSuggestionHandler    nlpSuggestionHandler;

    @Autowired
    private GooglePlacesAPIService  googlePlacesAPIService;

    private static Logger           logger         = LoggerFactory.getLogger(TypeaheadService.class);

    @Autowired
    private HttpRequestUtil         httpRequestUtil;

    private List<String>            cityNames;

    private int                     MAX_CITY_COUNT = 1000;

    /**
     * This method will return the list of typeahead results based on the
     * params.
     * 
     * @param query
     * @param rows
     * @param filterQueries
     * @return List<Typeahead>
     */
    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheads(String query, int rows, List<String> filterQueries) {
        List<Typeahead> typeaheads = typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);
        if (typeaheads != null) {
            for (Typeahead typeahead : typeaheads) {
                typeahead.setScore(null);
            }
        }
        return typeaheads;
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getExactTypeaheads(String query, int rows, List<String> filterQueries) {
        return typeaheadDao.getExactTypeaheads(query, rows, filterQueries);
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheadsV2(String query, int rows, List<String> filterQueries) {
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");
        return typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheadsV3(
            String query,
            int rows,
            List<String> filterQueries,
            String usercity,
            String enhance) {

        if (query == null || query.isEmpty()) {
            return new ArrayList<Typeahead>();
        }

        String qcity = null;
        try {
            qcity = parseQueryForCity(query);
        }
        catch (Exception e) {
            logger.error("Error matching city names in query", e);
        }

        String oldQuery = query;
        if (qcity != null) {
            query = StringUtils.substringBeforeLast(query, qcity);
        }

        /* If any filters were passed in URL, return only normal results */
        if (!filterQueries.isEmpty()) {
            if (qcity != null) {
                filterQueries.add("TYPEAHEAD_CITY:" + qcity);
            }
            return (typeaheadDao.getTypeaheadsV3(query, rows, filterQueries, usercity));
        }

        if (qcity != null) {
            filterQueries.add("TYPEAHEAD_CITY:" + qcity);
        }

        /* Get NLP based results */
        List<Typeahead> nlpResults = new ArrayList<Typeahead>();
        try {
            nlpResults = nlpSuggestionHandler.getNlpTemplateBasedResults(query, usercity, rows);
        }
        catch (Exception ex) {
            logger.error("Error while fetching templates. Query = " + query, ex);
        }

        /*
         * Get Normal Results matching the query String. filterQueries if we
         * reach here.
         */
        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");

        List<Typeahead> results = typeaheadDao.getTypeaheadsV3(query, rows, filterQueries, usercity);

        /*
         * Remove not-so-good results and replace them with google place
         * landmarks.
         */
        if (enhance != null && enhance.equalsIgnoreCase(TypeaheadConstants.ExternalApiIdentifierGoogle)) {
            results = incorporateGooglePlaceResults(oldQuery, results, rows);
        }

        /* Get recommendations type results */
        List<Typeahead> suggestions = new ArrayList<Typeahead>();
        try {
            suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, rows);
        }
        catch (Exception ex) {
            logger.error("Error while fetching suggestions.", ex);
        }

        /* Consolidate results */
        List<Typeahead> consolidatedResults = consolidateResults(rows, nlpResults, results, suggestions);

        return consolidatedResults;
    }

    /**
     * Check for city name in query
     * 
     * @param query
     * @return the city name in query
     */
    private String parseQueryForCity(String query) {
        if (cityNames == null || cityNames.isEmpty()) {
            populateCityNames();
        }
        String[] qterms = query.split("\\s+");
        String city = null;
        if (qterms.length <= 1) {
            city = null;
        }
        if (cityNames.contains(qterms[qterms.length - 1].toUpperCase())) {
            city = qterms[qterms.length - 1];
        }
        return city;
    }

    /**
     * Populate all the city names in cityNames, this should be done only once
     */
    private void populateCityNames() {
        String buildParams = "?" + URLGenerationConstants.Selector + URLGenerationConstants.SelectorGetAllCities;
        buildParams = String.format(buildParams, MAX_CITY_COUNT);
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(PropertyKeys.CITY_API_URL) + buildParams).build().encode()
                .toString());
        cityNames = new ArrayList<String>();
        List<City> cities = null;
        try {
            cities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, City.class);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("Error while getting cities", e);
            return;
        }
        for (City c : cities) {
            cityNames.add(c.getLabel().toUpperCase());
        }

    }

    /**
     * @param query
     * @param results
     * @param totalRows
     *            total elements needed in final-list
     * @return A new List<Typeahead> having sub-par elements of results replaced
     *         with google-place results
     */
    private List<Typeahead> incorporateGooglePlaceResults(String query, List<Typeahead> results, int totalRows) {

        List<Typeahead> finalResults = new ArrayList<Typeahead>();
        for (Typeahead t : results) {
            if (t.getScore() > TypeaheadConstants.GooglePlaceDelegationTheshold) {
                finalResults.add(t);
            }
        }

        /* If all results are good then google results are not needed */
        if (finalResults.size() >= totalRows) {
            return finalResults;
        }

        int gpRows = totalRows - finalResults.size();
        List<Typeahead> gpResults = googlePlacesAPIService.getPlacePredictions(query, gpRows);
        finalResults.addAll(gpResults);
        return finalResults;
    }

    /* Consolidate results fetched using different methods. */
    private List<Typeahead> consolidateResults(
            int rows,
            List<Typeahead> nlpResults,
            List<Typeahead> results,
            List<Typeahead> suggestions) {

        List<Typeahead> consolidatedResults = new ArrayList<Typeahead>();
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(results, rows));

        if (suggestions.isEmpty()) {
            consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(nlpResults, rows));
        }
        else {
            consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(suggestions, rows));
        }

        return (consolidatedResults);
    }

}
