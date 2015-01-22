package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//import com.google.common.base.Joiner;
//import com.proptiger.core.model.cms.City;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.UtilityClass;

/**
 * 
 * @author mukand
 * @author hemendra
 * 
 */

@Repository
public class TypeaheadDao {

    @Autowired
    private SolrDao solrDao;

    public List<Typeahead> getTypeaheadsV2(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = this.getSolrQueryV2(query, rows, filterQueries);
        List<Typeahead> results = getSpellCheckedResponseV2(solrQuery, rows, filterQueries);
        return UtilityClass.getFirstNElementsOfList(results, rows);
    }

    // Add parameters to use the custom requestHandler
    private SolrQuery getSolrQueryV2(String query, int rows, List<String> filterQueries) {

        SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
        solrQuery.setParam("qt", "/payload");
        solrQuery.setParam("defType", "payload");
        solrQuery.setParam("fl", "*,score");

        /* Query time boosting */
        String boostQuery = getBoostQuery(query);
        if (!boostQuery.isEmpty()) {
            solrQuery.setParam("bq", boostQuery);
        }
        return solrQuery;
    }

    private String getBoostQuery(String query) {
        String boostQuery = "";
        StringTokenizer st = new StringTokenizer(query.trim());
        int count = st.countTokens();
        if (count < 2) {
            return boostQuery;
        }

        float boost = TypeaheadConstants.QueryTimeBoostStart;
        /* Boost all-but-last query strings as core-texts */
        for (int i = 0; i < count - 1; i++) {
            boostQuery += "Core_text:" + st.nextToken() + "^" + Math.max(1, boost) + " ";
            boost *= TypeaheadConstants.QueryTimeBoostMultiplier;
        }

        /* Boost last query string as an edgeNGram */
        boostQuery += ("ENGram:" + st.nextToken() + "^" + Math.max(1, boost) + " ");

        // System.out.println("=====>> Q = [" + query + "], BQ = [" + boostQuery
        // + "]");
        return boostQuery;
    }

    private SolrQuery getSimpleSolrQuery(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }

        solrQuery.setRows(rows);
        return solrQuery;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<Typeahead> getSpellCheckedResponseV2(SolrQuery solrQuery, int rows, List<String> filterQueries) {

        List<Typeahead> results = new ArrayList<Typeahead>();
        QueryResponse response = solrDao.executeQuery(solrQuery);
        SpellCheckResponse scr = response.getSpellCheckResponse();
        String spellsuggestion = ((scr != null) ? scr.getCollatedResult() : null);
        results = response.getBeans(Typeahead.class);

        if (spellsuggestion != null && results.size() < 5) {
            SolrQuery newQuery = this.getSolrQueryV2(spellsuggestion.toString(), rows, filterQueries);
            List<Typeahead> suggestResults = new ArrayList<Typeahead>();
            suggestResults = solrDao.executeQuery(newQuery).getBeans(Typeahead.class);
            results.addAll(suggestResults);
            return results;
        }
        else {
            return results;
        }
    }

    // ******* TYPEAHEAD :: VERSION 3 ********

    public List<Typeahead> getTypeaheadsV3(String query, int rows, List<String> filterQueries, String usercity) {
        List<Typeahead> results = getSpellCheckedResponseV3(query, rows, filterQueries, usercity);
        return UtilityClass.getFirstNElementsOfList(results, rows);
    }

    private SolrQuery getSolrQueryV3(String query, int rows, List<String> filterQueries) {

        SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
        solrQuery.setParam("qt", "/payload_v4");
        solrQuery.setParam("defType", "payload");
        solrQuery.setParam("fl", "*,score");
        return solrQuery;
    }

    public QueryResponse getResponseV3(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = getSolrQueryV3(query, rows, filterQueries);
        QueryResponse result = solrDao.executeQuery(solrQuery);
        return result;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<Typeahead> getSpellCheckedResponseV3(
            String query,
            int rows,
            List<String> filterQueries,
            String usercity) {

        /* Fetching more results due to city-context boosting */
        rows = (int) (rows * TypeaheadConstants.DocumentFetchMultiplier);

        /* Fetch results for entered query first */
        SolrQuery solrQuery = this.getSolrQueryV3(query, rows, filterQueries);
        List<Typeahead> resultsOriginal = new ArrayList<Typeahead>();
        QueryResponse response = solrDao.executeQuery(solrQuery);
        resultsOriginal = response.getBeans(Typeahead.class);

        SpellCheckResponse scr = response.getSpellCheckResponse();
        if (scr == null) {
            return resultsOriginal;
        }

        /* If spell-check suggestions are there, get those results as well */

        String spellsuggestion = scr.getCollatedResult();
        List<Typeahead> resultsSuggested = new ArrayList<Typeahead>();
        if (spellsuggestion != null && !spellsuggestion.isEmpty()) {
            SolrQuery newQuery = this.getSolrQueryV3(spellsuggestion.toString(), rows, filterQueries);
            resultsSuggested = solrDao.executeQuery(newQuery).getBeans(Typeahead.class);
        }

        /* Merge results */
        if (!resultsSuggested.isEmpty()) {
            resultsOriginal.addAll(resultsSuggested);
        }

        /*
         * Boost results where city is same as user's selected city if usercity
         * is given
         */
        if (usercity != null && !usercity.isEmpty()) {
            boostByCityContext(resultsOriginal, usercity);
        }

        /* Sort and remove duplicates */
        Collections.sort(resultsOriginal, new Comparator<Typeahead>() {
            @Override
            public int compare(Typeahead o1, Typeahead o2) {
                return o2.getScore().compareTo(o1.getScore());
            }
        });

        List<List<Typeahead>> listOfresults = new ArrayList<List<Typeahead>>();
        listOfresults.add(resultsOriginal);
        List<Typeahead> results = UtilityClass.getMergedListRemoveDuplicates(
                listOfresults,
                new Comparator<Typeahead>() {
                    @Override
                    public int compare(Typeahead o1, Typeahead o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });

        return results;
    }

    /* Boost results where city is same a user's selected city */
    private void boostByCityContext(List<Typeahead> results, String city) {
        for (Typeahead t : results) {
            if (t.getCity().equalsIgnoreCase(city)) {
                t.setScore(getCityBoostedScore(t.getScore()));
            }
        }
    }

    private float getCityBoostedScore(float oldScore) {
        /* Don't boost irrelevant documents */
        if (oldScore <= TypeaheadConstants.CityBoostMinScore) {
            return oldScore;
        }
        return oldScore * TypeaheadConstants.CityBoost;
    }

    // ******* Exact Typeaheads ********

    public List<Typeahead> getExactTypeaheads(String query, int rows, List<String> filterQueries) {
        String[] multiWords = query.split("\\s+");
        int wordsCounter = 0;
        StringBuilder queryStringBuilder = new StringBuilder();
        for (String word : multiWords) {
            if (++wordsCounter < multiWords.length) {
                queryStringBuilder.append("TYPEAHEAD_LABEL_LOWERCASE:" + "*" + word + "*" + " AND ");
            }
            else {
                queryStringBuilder.append("TYPEAHEAD_LABEL_LOWERCASE:" + "*" + word + "*");
            }
        }

        String exactMatchQuery = queryStringBuilder.toString();

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(exactMatchQuery);
        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }
        solrQuery.setRows(rows);
        return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
    }

}
