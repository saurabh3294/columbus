package com.proptiger.columbus.suggestions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TemplateInfoDao;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.thandlers.RootTHandler;
import com.proptiger.columbus.thandlers.TemplateMap;
import com.proptiger.columbus.thandlers.TemplateTypes;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.UtilityClass;

@Component
public class NLPSuggestionHandler {
    @Autowired
    private TypeaheadDao       typeaheadDao;

    @Autowired
    private TemplateMap        templateMap;

    private Logger             logger                           = LoggerFactory.getLogger(NLPSuggestionHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    private float              templateFirstResultScoreTheshold = 20.0f;

    private float              templateResultScoreTheshold      = 7.0f;

    private RootTHandler getTemplateHandler(Typeahead template) {
        String templateText = template.getTemplateText().trim();
        TemplateTypes ttype = templateMap.get(templateText);
        RootTHandler thandler = null;
        try {
            thandler = applicationContext.getBean(ttype.getClazz());
        }
        catch (Exception e) {
            logger.error("No template handler found for template = " + template.getId(), e);
        }
        return thandler;
    }

    public List<Typeahead> getNlpTemplateBasedResults(String query, String city, int cityId, int rows) {

        /* Check for city if it is null or not */
        if (city == null || city.isEmpty()) {
            city = TypeaheadConstants.DEFAULT_CITY_NAME;
            cityId = TypeaheadConstants.DEFAULT_CITY_ID;
        }

        List<Typeahead> results = new ArrayList<Typeahead>();
        List<Typeahead> templateHits = getTemplateHits(query, rows);

        /* If no good-matching templates are found, return empty list. */
        if (templateHits.isEmpty() || templateHits.get(0).getScore() < templateResultScoreTheshold) {
            return results;
        }

        Typeahead firstHit = templateHits.get(0);
        float firstHitScore = firstHit.getScore();

        /* Get All results for first template. */

        List<Typeahead> resultsFirstHandler = getAllResultsForTemplate(firstHit, city, cityId, rows, query);
        if (firstHitScore > templateFirstResultScoreTheshold) {
            return resultsFirstHandler;
        }

        /*
         * Get top result for each template. (as we needed to incorporate
         * multiple template hits)
         */
        for (Typeahead t : templateHits) {
            results.addAll(getTopResultsForTemplate(t, city, cityId, query));
        }

        /*
         * If top results are not enough then populate the list by rest of the
         * results for first template up to a maximum of 'rows'.
         */
        
        resultsFirstHandler.remove(0);
        results.addAll(resultsFirstHandler);
        UtilityClass.getFirstNElementsOfList(results, rows);
        return results;
    }

    List<Typeahead> getTopResultsForTemplate(Typeahead template, String city, int cityId, String query) {
        String templateText = template.getTemplateText().trim();
        RootTHandler thandler = getTemplateHandler(template);
        List<Typeahead> results = new ArrayList<Typeahead>();

        if (thandler == null) {
            logger.warn("No Template Handler found for typeahead template : " + templateText);
            return results;
        }

        Typeahead topResult = thandler.getTopResult(query, template, city, cityId);
        topResult.setScore(template.getScore());
        results.add(topResult);
        return results;
    }

    List<Typeahead> getAllResultsForTemplate(Typeahead template, String city, int cityId, int rows, String query) {

        RootTHandler thandlerFirst = getTemplateHandler(template);
        List<Typeahead> resultsFirstHandler = new ArrayList<Typeahead>();

        if (thandlerFirst != null) {
            resultsFirstHandler = thandlerFirst.getResults(query, template, city, cityId, rows);
        }

        /* Populating template score as the score for all suggestions */
        for (Typeahead t : resultsFirstHandler) {
            t.setScore(template.getScore());
        }
        return resultsFirstHandler;
    }

    private List<Typeahead> getTemplateHits(String query, int rows) {
        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        List<Typeahead> templateHits = typeaheadDao.getResponseV3(query, rows, queryFilters);
        return templateHits;
    }
}