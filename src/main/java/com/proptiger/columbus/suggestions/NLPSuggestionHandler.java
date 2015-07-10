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
        city = setCity(city);
        cityId = setCityId(cityId, city);
        List<Typeahead> results = new ArrayList<Typeahead>();

        List<Typeahead> templateHits = getTemplateHits(query, rows);

        /* If no good-matching templates are found, return empty list. */
        if (templateHits.isEmpty() || templateHits.get(0).getScore() < templateResultScoreTheshold) {
            return results;
        }

        /* Get All results for first template. */

        RootTHandler thandlerFirst = getTemplateHandler(templateHits.get(0));
        List<Typeahead> resultsFirstHandler = new ArrayList<Typeahead>();

        resultsFirstHandler = getResultForFirstTemplate(
                thandlerFirst,
                resultsFirstHandler,
                templateHits,
                city,
                cityId,
                rows,
                query);
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
        RootTHandler thandler;
        Typeahead topResult;
        for (Typeahead t : templateHits) {
            templateText = t.getTemplateText().trim();
            thandler = getTemplateHandler(t);

            if (thandler != null) {
                topResult = thandler.getTopResult(query, t, city, cityId);
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
        return populateFirstTemplate(results, resultsFirstHandler, size, rows);
    }

    List<Typeahead> populateFirstTemplate(
            List<Typeahead> results,
            List<Typeahead> resultsFirstHandler,
            int size,
            int rows) {
        for (int i = 1; i < size; i++) {
            if (results.size() >= rows) {
                break;
            }
            results.add(resultsFirstHandler.get(i));
        }
        return results;
    }

    public String setCity(String city) {
        if (city == null || city.isEmpty()) {
            /* set Default city */
            city = TypeaheadConstants.DEFAULT_CITY_NAME;
        }
        return city;
    }

    public int setCityId(int cityId, String city) {
        if (city == null || city.isEmpty()) {
            /* set Default cityId */
            cityId = TypeaheadConstants.DEFAULT_CITY_ID;
        }
        return cityId;
    }

    List<Typeahead> getResultForFirstTemplate(
            RootTHandler thandlerFirst,
            List<Typeahead> resultFirstHandler,
            List<Typeahead> templatesHits,
            String city,
            int cityId,
            int rows,
            String query) {
        if (thandlerFirst != null) {
            resultFirstHandler = thandlerFirst.getResults(query, templatesHits.get(0), city, cityId, rows);
        }

        return resultFirstHandler;
    }

    private List<Typeahead> getTemplateHits(String query, int rows) {
        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("DOCUMENT_TYPE:TYPEAHEAD" + " AND " + "TYPEAHEAD_TYPE:TEMPLATE");
        List<Typeahead> templateHits = typeaheadDao.getResponseV3(query, rows, queryFilters);
        return templateHits;
    }
}