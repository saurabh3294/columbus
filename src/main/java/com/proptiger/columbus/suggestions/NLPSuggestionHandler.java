package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.thandlers.RootTHandler;
import com.proptiger.columbus.thandlers.TemplateMap;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;

@Component
public class NLPSuggestionHandler {
    @Autowired
    private TypeaheadDao    typeaheadDao;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    private TemplateMap     templateMap                      = new TemplateMap();

    private Logger          logger                           = LoggerFactory.getLogger(NLPSuggestionHandler.class);

    private float           templateFirstResultScoreTheshold = 20.0f;

    private float           templateResultScoreTheshold      = 7.0f;

    public List<Typeahead> getNlpTemplateBasedResults(String query, String city, int cityId, int rows) {

        if (city == null || city.isEmpty()) {
            city = TypeaheadConstants.defaultCityName;
            cityId = TypeaheadConstants.defaultCityId;
        }

        List<Typeahead> results = new ArrayList<Typeahead>();

        List<Typeahead> templateHits = getTemplateHits(query, rows);
        
        /* If no good-matching templates are found, return empty list. */
        if (templateHits.size() == 0 || templateHits.get(0).getScore() < templateResultScoreTheshold) {
            return results;
        }

        /* Get All results for first template. */
        RootTHandler thandler = templateMap.getTemplateHandler(templateHits.get(0).getTemplateText().trim());

        List<Typeahead> resultsFirstHandler = new ArrayList<Typeahead>();
        if (thandler != null) {
            thandler.setHttpRequestUtil(httpRequestUtil);
            resultsFirstHandler = thandler.getResults(query, templateHits.get(0), city, cityId, rows);
        }

        /* Populating template score as the score for all suggestions */
        for (Typeahead t : resultsFirstHandler) {
            t.setScore(templateHits.get(0).getScore());
        }

        if (templateHits.get(0).getScore() > templateFirstResultScoreTheshold) {
            return resultsFirstHandler;
        }

        /*
         * Get top result for each template. (as we needed to incorporate
         * multiple template hits)
         */
        String templateText;
        RootTHandler rootTaTemplate;
        Typeahead topResult;
        for (Typeahead t : templateHits) {
            templateText = t.getTemplateText().trim();
            rootTaTemplate = templateMap.getTemplateHandler(templateText);

            if (rootTaTemplate != null) {
                rootTaTemplate.setHttpRequestUtil(httpRequestUtil);
                topResult = rootTaTemplate.getTopResult(query, t, city, cityId);
                topResult.setScore(t.getScore());
                results.add(topResult);
            }
            else {
                logger.warn("No Template Handler found for typeahead template : " + templateText);
            }
        }

        /*
         * If top results are not enough then populate the list by rest of the
         * results for first template up to a maximum of 'rows'.
         */

        int size = resultsFirstHandler.size();
        for (int i = 1; i < size; i++) {
            if (results.size() >= rows) {
                break;
            }
            results.add(resultsFirstHandler.get(i));
        }

        return results;
    }
    
    
    private List<Typeahead> getTemplateHits(String query, int rows){
        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        List<Typeahead> templateHits = typeaheadDao.getResponseV3(query, rows, queryFilters);
        return templateHits;
    }
}