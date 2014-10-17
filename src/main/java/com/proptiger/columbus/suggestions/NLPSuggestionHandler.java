package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.thandlers.RootTHandler;
import com.proptiger.columbus.thandlers.TemplateMap;

@Component
public class NLPSuggestionHandler {
    @Autowired
    private TypeaheadDao    typeaheadDao;

    private TemplateMap     templateMap   = new TemplateMap();

    private Logger          logger        = LoggerFactory.getLogger(NLPSuggestionHandler.class);

    private float           scoreTheshold = 5.0f;

    public List<Typeahead> getNlpTemplateBasedResults(String query, String city, int rows) {

        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        QueryResponse response = typeaheadDao.getResponseV2(query, rows, queryFilters);
        List<Typeahead> templateHits = response.getBeans(Typeahead.class);

        List<Typeahead> results = new ArrayList<Typeahead>();

        /* If no matching templates are found, return empty list. */
        if (templateHits.size() == 0) {
            return results;
        }

        /* Get All results for first template. */
        RootTHandler thandler = templateMap.getTemplate(templateHits.get(0).getTemplateText().trim());

        List<Typeahead> resultsFirstHandler = new ArrayList<Typeahead>();
        if (thandler != null) {
            resultsFirstHandler = thandler.getResults(query, templateHits.get(0), city, rows);
        }

        if (templateHits.get(0).getScore() > scoreTheshold) {
            return resultsFirstHandler;
        }

        /*
         * Get top result for each template. (as we needed to incorporate
         * multiple template hits)
         */
        String templateText;
        RootTHandler rootTaTemplate;
        for (Typeahead t : templateHits) {
            templateText = t.getTemplateText().trim();
            rootTaTemplate = templateMap.getTemplate(templateText);

            if (rootTaTemplate != null) {
                results.add(rootTaTemplate.getTopResult(query, t, city));
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
}