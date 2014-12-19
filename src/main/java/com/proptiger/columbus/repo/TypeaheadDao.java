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

    private float   boostStart      = 10f;
    private float   boostMultiplier = 0.3f;

    // public QueryResponse getResponseSuggestions(String query, int rows,
    // List<String> filterQueries) {
    // SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
    // QueryResponse result = solrDao.executeQuery(solrQuery);
    // return result;
    // }

    // public QueryResponse getResponseV2(String query, int rows, List<String>
    // filterQueries) {
    // SolrQuery solrQuery = getSolrQueryV2(query, rows, filterQueries);
    // QueryResponse result = solrDao.executeQuery(solrQuery);
    // return result;
    // }

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

        float boost = boostStart;
        /* Boost all-but-last query strings as core-texts */
        for (int i = 0; i < count - 1; i++) {
            boostQuery += "Core_text:" + st.nextToken() + "^" + Math.max(1, boost) + " ";
            boost *= boostMultiplier;
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

    // ******* TYPEAHEAD :: VERSION 4 ********

    public List<Typeahead> getTypeaheadsV4(String query, int rows, List<String> filterQueries) {
        List<Typeahead> results = getSpellCheckedResponseV4(query, rows, filterQueries);
        return UtilityClass.getFirstNElementsOfList(results, rows);
    }

    private SolrQuery getSolrQueryV4(String query, int rows, List<String> filterQueries) {

        SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
        solrQuery.setParam("qt", "/payload_v4");
        solrQuery.setParam("defType", "payload");
        solrQuery.setParam("fl", "*,score");
        return solrQuery;
    }

    public QueryResponse getResponseV4(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = getSolrQueryV4(query, rows, filterQueries);
        QueryResponse result = solrDao.executeQuery(solrQuery);
        return result;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<Typeahead> getSpellCheckedResponseV4(String query, int rows, List<String> filterQueries) {

        /* Fetch results for entered query first */
        SolrQuery solrQuery = this.getSolrQueryV4(query, rows, filterQueries);
        List<Typeahead> resultsOriginal = new ArrayList<Typeahead>();
        QueryResponse response = solrDao.executeQuery(solrQuery);
        resultsOriginal = response.getBeans(Typeahead.class);

        SpellCheckResponse scr = response.getSpellCheckResponse();
        if(scr == null){
            return resultsOriginal;
        }
        
        /* If spell-check suggestions are there, get those results as well */

        String spellsuggestion = scr.getCollatedResult();
        List<Typeahead> resultsSuggested = new ArrayList<Typeahead>();
        if (spellsuggestion != null && !spellsuggestion.isEmpty()) {
            SolrQuery newQuery = this.getSolrQueryV4(spellsuggestion.toString(), rows, filterQueries);
            resultsSuggested = solrDao.executeQuery(newQuery).getBeans(Typeahead.class);
        }

        if (resultsSuggested.isEmpty()) {
            return resultsOriginal;
        }

        /* Merge results */

        resultsOriginal.addAll(resultsSuggested);
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

    // ******* TYPEAHEAD :: OLD VERSION FUNCTIONS ********

    // private SolrQuery getSolrQuery(String query, int rows, List<String>
    // filterQueries) {
    // SolrQuery solrQuery = this.getQueryParams(query);
    //
    // for (String fq : filterQueries) {
    // solrQuery.addFilterQuery(fq);
    // }
    //
    // solrQuery.setRows(rows);
    // return solrQuery;
    // }

    // private SolrQuery getQueryParams(String query) {
    // SolrQuery solrQuery = new SolrQuery();
    // List<String> queryTokens = this.tokenizeQuery(query);
    // List<String> cityList = this.findCities(query);
    // query = this.parseCities(query, cityList);
    // String ctq = this.getCityQuery(cityList);
    //
    // double wt = 1.0;
    // if (query.trim().isEmpty())
    // wt = 1.0 / queryTokens.size();
    //
    // solrQuery.setQuery(query);
    // solrQuery.setParam("defType", "edismax");
    // solrQuery.setParam("qf", "tp_engram^5.0 tp_ngram^0.5 tp_phonetic^0.2");
    //
    // List<String> boostList = new ArrayList<String>();
    // for (String qry : queryTokens) {
    // if (qry.split(" ").length == 1) {
    // boostList
    // .add("query({!edismax qf='tp_city tp_locality tp_builder tp_suburb tp_project' tie=0.1 v='$q' boost=''}, "
    // + String
    // .format("%.2f", wt) + ")");
    // }
    // else {
    // boostList
    // .add("query({!edismax qf='tp_city tp_locality tp_builder tp_suburb tp_project' tie=0.1 v='\"$q\"' boost=''}, $wt)");
    // }
    // }
    //
    // String boost = new String();
    // if (boostList.size() > 1) {
    // boost = "sum(" + Joiner.on(",").skipNulls().join(boostList) + ")";
    // }
    // else {
    // boost = boostList.get(0);
    // }
    // boost =
    // "product(map(query({!v='TYPEAHEAD_TYPE:CITY'}),0,0,1,1.5),map(query({!v='TYPEAHEAD_TYPE:BUILDER'}),0,0,1,1.1),map(query({!v='TYPEAHEAD_TYPE:PROJECT'}),0,0,1,1.3),map(query({!v='TYPEAHEAD_TYPE:LOCALITY'}),0,0,1,1.3),map(query({!v='TYPEAHEAD_TYPE:SUBURB'}),0,0,1,1.3),"
    // + boost
    // + ")";
    //
    // if (cityList.size() > 0) {
    // boost = "product(query({!v='" + ctq + "'}, 1.0), " + boost + ")";
    // }
    //
    // solrQuery.setParam("boost", boost);
    // return solrQuery;
    // }

//    private String getCityQuery(List<String> cityList) {
//        List<String> newCityList = new ArrayList<String>();
//
//        for (String city : cityList) {
//            newCityList.add("TYPEAHEAD_CITY:" + city.trim());
//        }
//        String ctq = Joiner.on(" OR ").skipNulls().join(newCityList);
//        return ctq;
//    }

    public List<List<String>> powerset(List<String> list) {
        List<List<String>> ps = new ArrayList<List<String>>();
        ps.add(new ArrayList<String>());

        for (String item : list) {
            List<List<String>> newPs = new ArrayList<List<String>>();

            for (List<String> subset : ps) {
                newPs.add(subset);
                List<String> newSubset = new ArrayList<String>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            ps = newPs;
        }
        return ps;
    }

//    private List<String> tokenizeQuery(String query) {
//        List<String> qList = new ArrayList<String>();
//        List<List<String>> powerset = this.powerset(qList);
//        for (List<String> list : powerset) {
//            String st = Joiner.on(" ").skipNulls().join(list);
//            if (st.trim() != "" && st != null) {
//                qList.add(st);
//            }
//        }
//        return qList;
//    }

    // public List<Typeahead> getTypeaheads(String query, int rows, List<String>
    // filterQueries) {
    //
    // SolrQuery solrQuery = this.getSolrQuery(query, rows, filterQueries);
    // return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
    // }

//    private String parseCities(String query, List<String> queryCities) {
//        String query_new = this.substituteQuery(query, queryCities);
//
//        if (query_new.trim().isEmpty()) {
//            return query;
//        }
//        else {
//            return query_new;
//        }
//    }

    // private List<String> findCities(String query) {
    // // TODO Auto-generated method stub
    // List<City> cityList = cityService.getCityList(null);
    // List<String> cityLabels = new ArrayList<String>();
    //
    // for (City city : cityList) {
    // String label = city.getLabel();
    // if (label != "") {
    // cityLabels.add(city.getLabel());
    // }
    // }
    //
    // List<String> matchedCities = new ArrayList<String>();
    // for (String city : cityLabels) {
    // if (query.matches("(?i).*" + city + ".*")) {
    // if (city != "") {
    // matchedCities.add(city);
    // }
    // }
    // }
    // return matchedCities;
    // }

//    private String substituteQuery(String query, List<String> terms) {
//        // replaces terms in query with terms
//        for (String term : terms) {
//            query = query.replace(term.toLowerCase(), "");
//        }
//        return query.trim();
//    }

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
