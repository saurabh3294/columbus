/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.app.typeahead.thandlers.TemplateMap;
import com.proptiger.app.typeahead.thandlers.RootTHandler;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.data.enums.Suggestions;

/**
 * @author mukand
 * @author Hemendra
 * 
 * 
 */

@Service
public class TypeaheadService {
    @Autowired
    private TypeaheadDao    typeaheadDao;

    private TemplateMap     templateMap = new TemplateMap();

    private Logger          logger      = LoggerFactory.getLogger(TypeaheadService.class);

    private double suggestionScoreThreshold = 20.0d;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private BuilderService  builderService;

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

    public List<Typeahead> getTypeaheadsV2(String query, int rows, List<String> filterQueries, String city) {

        /* Get NLP based results */
        List<Typeahead> nlpResults = getNlpTemplateBasedResults(query, city, rows);

        // /* If a pattern is hit and sufficient results are returned, we are
        // done. */
        // if (nlpResults.size() >= rows) {
        // return UtilityClass.getFirstNElementsOfList(nlpResults, rows);
        // }

        /* Get Normal Results matching the query String */
        filterQueries.add("-TYPEAHEAD_TYPE:TEMPLATE");
        List<Typeahead> results = typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);

        /* Get recommendations type results */
        List<Typeahead> suggestions = auxilliaryService(results);

        List<Typeahead> consolidatedResults = new ArrayList<Typeahead>();
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(nlpResults, rows / 3));
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(results, rows / 3));
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(suggestions, rows / 3));

        return consolidatedResults;
    }

    /*
     * Select final results to be send from results fetched using different
     * methods
     */
    @SuppressWarnings("unused")
    private List<Typeahead> consolidateResults(int totalRows, List<Typeahead> nlpResults, List<Typeahead> results) {
        if (nlpResults.isEmpty()) {
            return UtilityClass.getFirstNElementsOfList(results, totalRows);
        }
        else {
            nlpResults.addAll(results);
            return UtilityClass.getFirstNElementsOfList(nlpResults, totalRows);
        }
    }

    private List<Typeahead> auxilliaryService(List<Typeahead> results) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        if (results == null || results.isEmpty()) {
            return suggestions;
        }

        Typeahead typeahead = results.get(0);

        if (typeahead.getScore() < suggestionScoreThreshold) {
            return suggestions;
        }

        String text = results.get(0).getId();

        List<String> tokens = new ArrayList<String>();

        Pattern pattern = Pattern.compile("([\\w]+)");
        Matcher matcher = pattern.matcher(text);
        String redirectUrl = results.get(0).getRedirectUrl();
        String label = results.get(0).getLabel();

        while (matcher.find()) {
            tokens.add(matcher.group(1).toUpperCase());
            // 2nd element is docType, 3rd is id
            // number
            System.out.println(matcher.group(1).toString());
        }

        if (tokens != null) {

            switch (tokens.get(1)) {
                case "PROJECT":
                    suggestions = getSuggestionByProjectName(tokens);
                    break;
                case "CITY":
                    suggestions = getSuggestionByCityName(tokens, redirectUrl, label);
                    break;
                case "BUILDER":
                    suggestions = getSuggestionByBuilderName(tokens, redirectUrl, label);
                    break;
                case "LOCALITY":
                    suggestions = getSuggestionByLocalityName(tokens, redirectUrl, label);
                    break;
                default:
                    break;
            }
        }

        return suggestions;
    }

    private List<Typeahead> getSuggestionByProjectName(List<String> tokens) {

        List<Typeahead> recommendation = new ArrayList<Typeahead>();
        List<Property> properties = new ArrayList<Property>();
        List<String> filters = new ArrayList<String>();
        List<Integer> distinctBedrooms = new ArrayList<Integer>();
        List<String> redirectUrls = new ArrayList<String>();
        String projectName = new String();
        String query = new String();
        QueryResponse response = new QueryResponse();

        query = "PROJECT_ID:" + tokens.get(2);
        filters.add("DOCUMENT_TYPE:PROPERTY");

        response = typeaheadDao.getResponseSuggestions(query, 50, filters);
        properties = response.getBeans(Property.class);

        // iterate over all the properties and pick distinct bedrooms along with
        // their urls
        if (properties != null && !properties.isEmpty()) {
            projectName = properties.get(0).getProjectName();
            for (Property p : properties) {
                if (!distinctBedrooms.contains(p.getBedrooms())) {
                    distinctBedrooms.add(p.getBedrooms());
                    redirectUrls.add(p.getURL());
                }
            }
        }

        // Insert them in the Typeahead objects
        for (int i = 0; i < redirectUrls.size(); i++) {
            Typeahead obj = new Typeahead();
            obj.setRedirectUrl(redirectUrls.get(i));
            String displayText = distinctBedrooms.get(i).toString() + Suggestions.PROJECT.getVal1() + projectName;
            obj.setDisplayText(displayText);
            recommendation.add(obj);
        }

        return recommendation;
    }

    private List<Typeahead> getSuggestionByBuilderName(List<String> tokens, String redirectUrl, String builderName) {
        List<Typeahead> recommendation = new ArrayList<Typeahead>();
        Typeahead obj = new Typeahead();
        redirectUrl += "/filters?projectStatus=not%20launched,pre%20launch";
        String displayText = Suggestions.BUILDER.getVal1() + builderName;
        obj.setRedirectUrl(redirectUrl);
        obj.setDisplayText(displayText);
        recommendation.add(obj);
        return recommendation;
    }

    private List<Typeahead> getSuggestionByCityName(List<String> tokens, String redirectUrl, String cityName) {
        List<Typeahead> recommendation = new ArrayList<Typeahead>();
        String newUrl;
        Typeahead obj1 = new Typeahead();
        newUrl = redirectUrl + "/filters?projectStatus=not%20launched,pre%20launch";
        String displayText = Suggestions.CITY.getVal1() + cityName;
        obj1.setRedirectUrl(newUrl);
        obj1.setDisplayText(displayText);
        recommendation.add(obj1);

        Typeahead obj2 = new Typeahead();
        newUrl = redirectUrl + "/filters?budget=0,5000000";
        displayText = Suggestions.LOCALITY.getVal1() + cityName;
        obj2.setRedirectUrl(newUrl);
        obj2.setDisplayText(displayText);
        recommendation.add(obj2);

        Typeahead obj3 = new Typeahead();
        newUrl = redirectUrl + "/filters?budget=10000000,";
        displayText = Suggestions.LOCALITY.getVal2() + cityName;
        obj3.setRedirectUrl(newUrl);
        obj3.setDisplayText(displayText);
        recommendation.add(obj3);

        return recommendation;
    }

    private List<Typeahead> getSuggestionByLocalityName(List<String> tokens, String redirectUrl, String localityName) {
        List<Typeahead> recommendation = new ArrayList<Typeahead>();
        Typeahead obj1 = new Typeahead();
        Typeahead obj2 = new Typeahead();
        String newUrl;

        newUrl = redirectUrl + "/filters?budget=0,5000000";
        String displayText = Suggestions.LOCALITY.getVal1() + localityName;
        obj1.setRedirectUrl(newUrl);
        obj1.setDisplayText(displayText);
        recommendation.add(obj1);

        newUrl = redirectUrl + "/filters?budget=10000000,";
        displayText = Suggestions.LOCALITY.getVal2() + localityName;
        obj2.setRedirectUrl(newUrl);
        obj2.setDisplayText(displayText);
        recommendation.add(obj2);

        return recommendation;
    }

    private List<Typeahead> getNlpTemplateBasedResults(String query, String city, int rows) {

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
        setTemplateServices(thandler);

        List<Typeahead> resultsFirstHandler = new ArrayList<Typeahead>();
        if (thandler != null) {
            resultsFirstHandler = thandler.getResults(templateHits.get(0), city, rows);
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
                setTemplateServices(rootTaTemplate);
                results.add(rootTaTemplate.getTopResult(t, city));
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
