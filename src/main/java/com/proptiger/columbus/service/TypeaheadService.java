/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.core.util.Constants;
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

    private static Logger           logger = LoggerFactory.getLogger(TypeaheadService.class);

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
    public List<Typeahead> getTypeaheadsV3(String query, int rows, List<String> filterQueries, String usercity) {

        /* If any filters were passed in URL, return only normal results */
        if (!filterQueries.isEmpty()) {
            return (typeaheadDao.getTypeaheadsV3(query, rows, filterQueries, usercity));
        }

        /* Get NLP based results */
        List<Typeahead> nlpResults = new ArrayList<Typeahead>();
        try {
            nlpResults = nlpSuggestionHandler.getNlpTemplateBasedResults(query, usercity, rows);
        }
        catch (Exception ex) {
            logger.error("Error while fetching templates.", ex);
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
        results = incorporateGooglePlaceResults(query, results, rows);

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
