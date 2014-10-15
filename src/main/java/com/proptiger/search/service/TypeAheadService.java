package com.proptiger.search.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.app.typeahead.suggestions.EntitySuggestionHandler;
import com.proptiger.app.typeahead.suggestions.NLPSuggestionHandler;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import com.proptiger.data.util.UtilityClass;

/**
 * @author mukand
 * @author Hemendra
 * 
 * 
 */


public class TypeAheadService {
	@Autowired
    private TypeaheadDao            typeaheadDao;

    @Autowired
    private EntitySuggestionHandler entitySuggestionHandler;

    @Autowired
    private NLPSuggestionHandler    nlpSuggestionHandler;

    /**
     * This method will return the list of typeahead results based on the
     * params.
     * 
     * @param query
     * @param rows
     * @param filterQueries
     * @return List<Typeahead>
     */
    public List<Typeahead> getTypeaheads(String query, int rows, List<String> filterQueries) {
        return typeaheadDao.getTypeaheads(query, rows, filterQueries);
    }

    public List<Typeahead> getExactTypeaheads(String query, int rows, List<String> filterQueries) {
        return typeaheadDao.getExactTypeaheads(query, rows, filterQueries);
    }

    public List<Typeahead> getTypeaheadsV2(String query, int rows, List<String> filterQueries) {
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");
        return typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);
    }

    public List<Typeahead> getTypeaheadsV3(String query, int rows, List<String> filterQueries, String city) {

        /* If any filters were passed in URL, return only normal results */
        if(!filterQueries.isEmpty()){
            return (typeaheadDao.getTypeaheadsV2(query, rows, filterQueries));
        }

        /* Get NLP based results */
        List<Typeahead> nlpResults = nlpSuggestionHandler.getNlpTemplateBasedResults(query, city, rows);

        /* Get Normal Results matching the query String */
        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");
        List<Typeahead> results = typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);

        /* Get recommendations type results */
        List<Typeahead> suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, rows);

        /* Consolidate results */
        List<Typeahead> consolidatedResults = consolidateResults(rows, nlpResults, results, suggestions);

        return consolidatedResults;
    }

    /* Consolidate results fetched using different methods. */
    private List<Typeahead> consolidateResults(
            int rows,
            List<Typeahead> nlpResults,
            List<Typeahead> results,
            List<Typeahead> suggestions) {

        List<Typeahead> consolidatedResults = new ArrayList<Typeahead>();
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(results, rows));
        
        if(suggestions.isEmpty()){
            consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(nlpResults, rows));
        }
        else{
            consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(suggestions, rows));
        }
        
        return (consolidatedResults);
    }
}
