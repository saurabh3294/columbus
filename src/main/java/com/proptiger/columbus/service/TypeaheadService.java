/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.TypeaheadDao;
import com.proptiger.columbus.suggestions.EntitySuggestionHandler;
import com.proptiger.columbus.suggestions.NLPSuggestionHandler;
import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.HttpRequestUtil;
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

    private static Logger           logger              = LoggerFactory.getLogger(TypeaheadService.class);

    @Autowired
    private HttpRequestUtil         httpRequestUtil;

    private Map<String, City>       cityNameToCityObjectMap;

    private int                     MAX_CITY_COUNT      = 1000;

    private String                  domainObjectIdRegex = "^[\\d]{5,6}$";

    private int                     MaX_GP_RESULTS      = 1;
    @Value("${google.place.threshold.score}")
    private int                     googlePlaceThresholdScore;

    @Value("${google.place.top.threshold.score}")
    private int                     googlePlaceTopThresholdScore;

    @Value("${own.results.privileged.slots}")
    private int                     ownResultsPrivilegedSlots;

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
    public List<Typeahead> getTypeaheadsV2(String query, int rows, List<String> filterQueries) {
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");
        return typeaheadDao.getTypeaheadsV2(query, rows, filterQueries);
    }

    public List<Typeahead> getTypeaheadsV3(
            String query,
            int rows,
            List<String> filterQueries,
            String usercity,
            String enhance) {

        if (query == null || query.isEmpty()) {
            return new ArrayList<Typeahead>();
        }

        /* If query is a number of 5-7 digits, return id-based results. */
        if (query.matches(domainObjectIdRegex)) {
            return getResultsByTypeaheadID(Long.parseLong(query));
        }

        /* Handling City filter : url-param-based and query-based */

        boolean fqEmpty = filterQueries.isEmpty();
        String qcity = extractCityNameFromQuery(query);
        String oldQuery = query;
        String templateCity = usercity;
        if (qcity != null) {
            templateCity = qcity;
            query = StringUtils.substringBeforeLast(query, qcity);
            filterQueries.add("TYPEAHEAD_CITY:" + qcity);
        }

        /* If any filters were passed in URL, return only normal results */
        if (!fqEmpty) {
            return (typeaheadDao.getTypeaheadsV3(query, rows, filterQueries, usercity));
        }

        /*
         * Get Normal Results matching the query String. filterQueries if we
         * reach here.
         */
        filterQueries.add("DOCUMENT_TYPE:TYPEAHEAD");
        filterQueries.add("(-TYPEAHEAD_TYPE:TEMPLATE)");

        List<Typeahead> results = typeaheadDao.getTypeaheadsV3(query, rows, filterQueries, usercity);

        /* Sort and remove duplicates */
        results = sortAndRemoveDuplicates(results);

        /* City Context based tweaking. */
        boostByCityContext(results, usercity);

        Collections.sort(results, new TypeaheadUtils.AbstractTypeaheadComparatorScore());

        /*
         * Remove not-so-good results and replace them with google place
         * landmarks.
         */

        results = enhanceWithGooglePlaceResults(oldQuery, null, results, rows, enhance);

        /* Get recommendations (suggestions and templates) */
        List<Typeahead> suggestions = getSuggestionsAndTemplates(results, query, templateCity, rows, null);

        /* Consolidate results */
        List<Typeahead> consolidatedResults = consolidateResults(rows, results, suggestions);
        return consolidatedResults;
    }

    /**
     * @param query
     *            : search query string
     * @param rows
     *            : max number of results needed
     * @param filterQueries
     *            : map of filter params (should not be null)
     * @param usercity
     *            : user context city
     * @param enhance
     *            : external enhancements to be used
     * @return : List of Typeahead objects matching this query
     */
    public List<Typeahead> getTypeaheadsV4(
            String query,
            int rows,
            Map<String, String> filterQueries,
            String usercity,
            String enhance) {

        if (query == null || query.isEmpty()) {
            return new ArrayList<Typeahead>();
        }

        /* If query is a number of 5-7 digits, return id-based results. */
        if (query.matches(domainObjectIdRegex)) {
            return getResultsByTypeaheadID(Long.parseLong(query));
        }

        // Extracting the typeaheadType received in request
        String typeaheadType = null;
        if (filterQueries.containsKey(TypeaheadConstants.TYPEAHEAD_TYPE)) {
            typeaheadType = filterQueries.get(TypeaheadConstants.TYPEAHEAD_TYPE);
        }

        /* Handling City filter : url-param-based and query-based */

        String filterCity = filterQueries.get(TypeaheadConstants.TYPEAHEAD_FIELD_NAME_CITY);
        String queryCity = extractCityNameFromQuery(query);
        String templateCity = usercity;
        City filterCityObject = null;

        String newQuery = query;
        String enhanceQuery = query;

        if (filterCity != null) {
            templateCity = filterCity;
            enhanceQuery = filterCity + " " + enhanceQuery;
            filterCityObject = cityNameToCityObjectMap.get(filterCity);
        }
        else if (queryCity != null) {
            templateCity = queryCity;
            newQuery = StringUtils.substringBeforeLast(query, queryCity);
            filterQueries.put(TypeaheadConstants.TYPEAHEAD_FIELD_NAME_CITY, queryCity);
        }

        List<String> filterQueryList = getFilterQueryListV4(filterQueries);

        /*
         * Fetching more results due to city-context boosting Fetched results
         * (maybe unsorted and can contain duplicates.)
         */
        int newRows = (int) (Math.min(
                rows * TypeaheadConstants.DOCUMENT_FETCH_MULTIPLIER,
                TypeaheadConstants.DOCUMENT_FETCH_LIMIT));

        List<Typeahead> results = typeaheadDao.getTypeaheadsV3(newQuery, newRows, filterQueryList, usercity);

        /* Sort and remove duplicates */
        results = sortAndRemoveDuplicates(results);

        /* City Context based tweaking. */
        boostByCityContext(results, usercity);

        Collections.sort(results, new TypeaheadUtils.AbstractTypeaheadComparatorScore());

        /* Builder-City based tweaking */
        if (filterCity != null && !filterCity.isEmpty()) {
            results = processSpecialRulesForBuilderResults(results, filterCity);
        }

        /*
         * Remove not-so-good results and replace them with third party
         * enhancements
         */

        results = enhanceWithGooglePlaceResults(enhanceQuery, filterCityObject, results, rows, enhance);

        /* Get recommendations (suggestions and templates) */
        List<Typeahead> suggestions = getSuggestionsAndTemplates(
                results,
                query,
                templateCity,
                rows,
                typeaheadType);

        /* Consolidate results */
        List<Typeahead> consolidatedResults = consolidateResults(rows, results, suggestions);

        return consolidatedResults;
    }

    /* Add additional filters */
    public List<String> getFilterQueryListV4(Map<String, String> fqMap) {

        List<String> list = new ArrayList<String>();
        String key, cityName;
        for (Map.Entry<String, String> entry : fqMap.entrySet()) {
            key = entry.getKey();
            cityName = entry.getValue();
            if (key.equals(TypeaheadConstants.TYPEAHEAD_FIELD_NAME_CITY) && cityNameToCityObjectMap
                    .containsKey(cityName)) {
                int cityId = cityNameToCityObjectMap.get(cityName).getId();
                list.add("(" + TypeaheadConstants.TYPEAHEAD_FIELD_NAME_CITY
                        + ":"
                        + cityName
                        + " OR "
                        + "BUILDER_CITY_IDS:"
                        + cityId
                        + ")");
            }
            else {
                list.add(String.valueOf(entry.getKey() + ":" + entry.getValue()));
            }
        }
        list.add("DOCUMENT_TYPE:TYPEAHEAD");
        list.add("(-TYPEAHEAD_TYPE:TEMPLATE)");
        return list;
    }

    private String extractCityNameFromQuery(String query) {
        if (cityNameToCityObjectMap == null || cityNameToCityObjectMap.isEmpty()) {
            populateCityNames();
        }

        String[] qterms = query.split("\\s+");
        String city = null;
        Set<String> cityNames = cityNameToCityObjectMap.keySet();
        if (qterms.length > 1 && cityNames != null && cityNames.contains(qterms[qterms.length - 1].toLowerCase())) {
            city = qterms[qterms.length - 1].toLowerCase();
        }
        return city;
    }

    /**
     * Populate all the city names in cityNames, this should be done only once
     */
    private void populateCityNames() {
        String buildParams = "?" + URLGenerationConstants.SELECTOR + URLGenerationConstants.SELECTOR_GET_ALL_CITIES;
        buildParams = String.format(buildParams, MAX_CITY_COUNT);
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.DAL_API_KEY) + PropertyReader
                                .getRequiredPropertyAsString(CorePropertyKeys.CITY_API_URL) + buildParams).build()
                .encode().toString());
        cityNameToCityObjectMap = new HashMap<String, City>();
        List<City> cities = null;
        try {
            cities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, City.class);
        }
        catch (Exception e) {
            logger.error("Exception while getting cities", e);
            return;
        }
        if (cities == null) {
            logger.error("Error while getting cities : Null list recieved.");
            return;
        }
        for (City c : cities) {
            cityNameToCityObjectMap.put(c.getLabel().toLowerCase(), c);
        }
    }

    /**
     * Boost result score where city is same as user's selected city. Performs
     * in-place boosting in same list. Sort order may get disrupted as scores of
     * only some objects are boosted.
     * 
     * @param results
     *            : list of typeaheads in which some results will be boosted
     * @param usercity
     *            : cityname to boost typeaheads on.
     * 
     */
    private void boostByCityContext(List<Typeahead> results, String usercity) {

        if (usercity == null || usercity.isEmpty()) {
            return;
        }

        if (!cityNameToCityObjectMap.containsKey(usercity)) {
            logger.warn("Invalid usercity recieved : " + usercity);
            return;
        }

        int cityId = cityNameToCityObjectMap.get(usercity).getId();

        for (Typeahead t : results) {
            if (t.getType().equalsIgnoreCase(TypeaheadConstants.TYPEAHEAD_TYPE_BUILDER)) {
                if (t.getBuilderCityIds().contains(cityId)) {
                    t.setScore(getCityBoostedScore(t.getScore()));
                }
            }
            else if (t.getCity().equalsIgnoreCase(usercity)) {
                t.setScore(getCityBoostedScore(t.getScore()));
            }
        }
    }

    private float getCityBoostedScore(float oldScore) {
        /* Don't boost irrelevant documents */
        if (oldScore <= TypeaheadConstants.CITY_BOOST_MIN_SCORE) {
            return oldScore;
        }
        return oldScore * TypeaheadConstants.CITY_BOOST;
    }

    /**
     * If city filter is applied then for builder type results these rules are
     * followed : 1. If city is buidler's HQ, then show builder as well as
     * builder-city result. 2. Otherwise show only builder-city result.
     * 
     * 
     * @return new Typeahead list containing final results
     */
    private List<Typeahead> processSpecialRulesForBuilderResults(List<Typeahead> results, String filterCity) {

        List<Typeahead> newResults = new ArrayList<Typeahead>();
        Typeahead tnew;
        Map<String, String> builderCityMap;
        for (Typeahead t : results) {
            if (t.getType().equalsIgnoreCase(TypeaheadConstants.TYPEAHEAD_TYPE_BUILDER)) {
                builderCityMap = getBuilderCityMap(t.getBuilderCityInfo());
                /*
                 * if builder is operational in filterCity then add only
                 * builder-city buidler-city result otherwise add only builder
                 * document
                 */
                if (builderCityMap.containsKey(filterCity)) {
                    tnew = makeBuilderCityDocument(t, builderCityMap.get(filterCity));
                    newResults.add(tnew);
                }
                else {
                    newResults.add(t);
                }
            }
            else {
                newResults.add(t);
            }
        }
        return newResults;
    }

    /**
     * @param builderCityInfoList
     *            : (SolrField)
     * @return returns a map of [cityName, builderCityInfo]
     */
    private Map<String, String> getBuilderCityMap(List<String> builderCityInfoList) {
        Map<String, String> builderCityMap = new HashMap<String, String>();
        if (builderCityInfoList == null) {
            return builderCityMap;
        }

        String cityName;
        for (String builderCityInfo : builderCityInfoList) {
            cityName = builderCityInfo.split(":")[1].toLowerCase();
            builderCityMap.put(cityName, builderCityInfo);
        }
        return builderCityMap;
    }

    /**
     * @param taBuilder
     *            : typehead object for builder result
     * @param builderCityInfo
     *            : (SolrField) dlim separated string
     *            {cityId:cityName:builderCityUrl}
     * @return Builder-City type typeahead object derived from the given
     *         typeahead object (taBuidler)
     */
    private Typeahead makeBuilderCityDocument(Typeahead taBuilder, String builderCityInfo) {
        String[] tokens = builderCityInfo.split(":");
        String cityId = tokens[0];
        String cityName = tokens[1];
        String url = tokens[2];
        Typeahead taBuilderCity = new Typeahead();
        String id = StringUtils.replace(taBuilder.getId(), "BUILDER", "BUILDERCITY") + "-" + cityId;
        String displayText = taBuilder.getDisplayText() + " - " + StringUtils.capitalize(cityName);
        taBuilderCity.setId(id);
        taBuilderCity.setDisplayText(displayText);
        taBuilderCity.setRedirectUrl(url);
        taBuilderCity.setScore(taBuilder.getScore());
        taBuilderCity.setType("BUILDERCITY");
        return taBuilderCity;
    }

    /**
     * @param domainObjectId
     *            : domain-object-id for (suburb, locality, builder or project)
     * @return The corresponding unique typeahead-object.
     */
    private List<Typeahead> getResultsByTypeaheadID(long domainObjectId) {
        List<Typeahead> results = new ArrayList<Typeahead>();
        DomainObject dObj = DomainObject.getDomainInstance(domainObjectId);
        String typeaheadId = String.format(
                TypeaheadConstants.TYPEAHEAD_ID_PATTERN,
                StringUtils.upperCase(dObj.name()),
                String.valueOf(domainObjectId));
        switch (dObj) {
            case builder:
            case locality:
            case project:
            case suburb:
                results = typeaheadDao.getTypeaheadById(typeaheadId);
                break;
            default:
                break;
        }
        return results;
    }

    /**
     * @param query
     *            : search query tobe fired at gp-api
     * @param results
     *            : original results that need to be enhanced sorted by score.
     * @param totalRows
     *            : total elements needed in final-list
     * @return A new list of typeaheads having sub-par elements of original
     *         results replaced with google-place results
     */
    private List<Typeahead> enhanceWithGooglePlaceResults(
            String query,
            City city,
            List<Typeahead> results,
            int totalRows,
            String enhance) {

        if ((enhance == null || !enhance.equalsIgnoreCase(TypeaheadConstants.EXTERNAL_API_IDENTIFIER_GOOGLE))) {
            return results;
        }

        List<Typeahead> finalResults = new ArrayList<Typeahead>();

        int counter = 0;
        for (Typeahead t : results) {
            if ((t.getScore() > googlePlaceThresholdScore) || (t.getScore() > googlePlaceTopThresholdScore && counter < ownResultsPrivilegedSlots)) {
                finalResults.add(t);
            }
            counter++;
        }

        /* If all results are good then google results are not needed */
        if (finalResults.size() >= totalRows) {
            return finalResults;
        }

        /*
         * Only one google place result is added
         */
        int gpRows = MaX_GP_RESULTS;
        double[] geoCenter = getGeoCenterForCity(city);
        int radius = TypeaheadConstants.CITY_RADIUS;
        List<Typeahead> gpResults = googlePlacesAPIService.getPlacePredictions(query, gpRows, geoCenter, radius);
        finalResults.addAll(gpResults);

        return finalResults;
    }

    private double[] getGeoCenterForCity(City city) {
        double[] geoCenter = null;
        if (city != null && city.getCenterLatitude() != null && city.getCenterLongitude() != null) {
            geoCenter = new double[] { city.getCenterLatitude(), city.getCenterLongitude() };
        }
        return geoCenter;
    }

    /**
     * TODO :: Write description here;
     * 
     * @param results
     * @param cleanedResults
     * @return
     */
    List<Typeahead> getCleanedResults(List<Typeahead> results, List<Typeahead> cleanedResults) {
        int counter = 0;
        for (Typeahead t : results) {
            if ((t.getScore() > googlePlaceThresholdScore) || (t.getScore() > googlePlaceTopThresholdScore && counter < ownResultsPrivilegedSlots)) {
                cleanedResults.add(t);
            }
            counter++;
        }
        return cleanedResults;
    }

    /**
     * 
     * @param results
     *            : results on which suggestion are required sorted by score.
     * @param query
     *            : search query to be fired for template-type-suggestions
     * @param templateCity
     *            : city to be used for template generation
     * @param rows
     *            : limit to number of total suggestions
     * @return A new typeahead List containing suggestions.
     */
    private List<Typeahead> getSuggestionsAndTemplates(
            List<Typeahead> results,
            String query,
            String templateCity,
            int rows,
            String typeaheadType) {

        List<Typeahead> suggestions = new ArrayList<Typeahead>();

        /* No suggestions should be given if typeahead-type is set. */
        if (typeaheadType != null) {
            return suggestions;
        }


        /* Restrict suggestion count */
        int numSuggestions = ((results.size() < rows)
                ? rows + TypeaheadConstants.MAX_SUGGESTION_COUNT - results.size()
                : TypeaheadConstants.MAX_SUGGESTION_COUNT);
        int templateCityId;
        try {
            suggestions = entitySuggestionHandler.getEntityBasedSuggestions(results, numSuggestions);

            if (suggestions == null || suggestions.isEmpty()) {
                templateCityId = ((templateCity == null) ? 0 : cityNameToCityObjectMap.get(templateCity).getId());
                suggestions = nlpSuggestionHandler
                        .getNlpTemplateBasedResults(query, templateCity, templateCityId, rows);
            }
        }
        catch (Exception ex) {
            logger.error("Error while fetching suggestions. Query = " + query + ", templateCity = " + templateCity, ex);
        }

        return suggestions;
    }

    /* Consolidate results fetched using different methods. */
    private List<Typeahead> consolidateResults(int rows, List<Typeahead> results, List<Typeahead> suggestions) {

        List<Typeahead> consolidatedResults = new ArrayList<Typeahead>();
        consolidatedResults.addAll(UtilityClass.getFirstNElementsOfList(results, rows));
        consolidatedResults.addAll(suggestions);
        return (consolidatedResults);
    }

    /**
     * @param resultsOriginal
     *            : typeahead results
     * @return The same list sorted by score and duplicates removed.
     */
    private List<Typeahead> sortAndRemoveDuplicates(List<Typeahead> resultsOriginal) {
        Collections.sort(resultsOriginal, new TypeaheadUtils.AbstractTypeaheadComparatorScore());

        List<List<Typeahead>> listOfresults = new ArrayList<List<Typeahead>>();
        listOfresults.add(resultsOriginal);
        List<Typeahead> resultsFinal = UtilityClass.getMergedListRemoveDuplicates(
                listOfresults,
                new TypeaheadUtils.AbstractTypeaheadComparatorId());

        return resultsFinal;

    }

}
