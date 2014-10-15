package com.proptiger.app.typeahead.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.app.typeahead.thandlers.RootTHandler;
import com.proptiger.app.typeahead.thandlers.TemplateMap;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import com.proptiger.data.service.BuilderService;
import com.proptiger.data.service.LocalityService;

@Component
public class NLPSuggestionHandler {

    @Autowired
    private LocalityService localityService;

    @Autowired
    private BuilderService  builderService;

    @Autowired
    private TypeaheadDao    typeaheadDao;

    private TemplateMap     templateMap   = new TemplateMap();

    private Logger          logger        = LoggerFactory.getLogger(NLPSuggestionHandler.class);

    private float           scoreTheshold = 5.0f;

    public List<Typeahead> getNlpTemplateBasedResults(String query, String city, int rows) {

        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        QueryResponse response = typeaheadDao.getResponseV4(query, rows, queryFilters);
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
            setTemplateServices(thandler);
            resultsFirstHandler = thandler.getResults(query, templateHits.get(0), city, rows);
        }
        
        /* Populating template score as the score for all suggestions */
        for(Typeahead t : resultsFirstHandler){
        	t.setScore(templateHits.get(0).getScore());
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
        Typeahead topResult;
        for (Typeahead t : templateHits) {
            templateText = t.getTemplateText().trim();
            rootTaTemplate = templateMap.getTemplate(templateText);

            if (rootTaTemplate != null) {
                setTemplateServices(rootTaTemplate);
                topResult = rootTaTemplate.getTopResult(query, t, city);
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

    private void setTemplateServices(RootTHandler rootTHandler) {
        rootTHandler.setLocalityService(localityService);
        rootTHandler.setBuilderService(builderService);
    }
}
