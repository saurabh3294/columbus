package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.Constants;
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
    private SolrDao          solrDao;

    private Logger           logger = LoggerFactory.getLogger(TypeaheadDao.class);

    private List<SortClause> sortClauseList;

    @PostConstruct
    private void initialize() {
        sortClauseList = getCustomSortOrderV4();
    }

    // ******* TYPEAHEAD :: VERSION 3 ********

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheadById(String typeaheadId) {
        List<String> queryFilters = new ArrayList<String>();
        queryFilters.add("id:" + typeaheadId);
        SolrQuery solrQuery = getSolrQueryV3("", 1, queryFilters);
        List<Typeahead> results = solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
        return results;
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheadById(List<String> typeaheadIds) {
        List<Typeahead> results = new ArrayList<Typeahead>();
        if (typeaheadIds == null || typeaheadIds.isEmpty()) {
            return results;
        }
        List<String> queryFilters = new ArrayList<String>();
        String filterIds = "";
        for (String id : typeaheadIds) {
            filterIds = filterIds.concat("id:" + id + " OR ");
        }
        filterIds = filterIds.substring(0, filterIds.length() - 3);
        filterIds = "(" + filterIds + ")";
        queryFilters.add(filterIds);
        SolrQuery solrQuery = getSolrQueryV3("", typeaheadIds.size(), queryFilters);
        results = solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
        return results;
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<Typeahead> getTypeaheadsV3(String query, int rows, List<String> filterQueries, String usercity) {
        List<Typeahead> results = new ArrayList<Typeahead>();
        try {
            results = getSpellCheckedResponseV3(query, rows, filterQueries);
        }
        catch (ProAPIException e) {
            if (e.getCause() instanceof RemoteSolrException) {
                logger.warn("Error executing solr query. QueryString = " + query + ", userCity = " + usercity);
            }
            else {
                throw e;
            }
        }
        return results;
    }

    public List<Typeahead> getResponseV3(String query, int rows, List<String> filterQueries) {
        List<Typeahead> results = new ArrayList<Typeahead>();
        SolrQuery solrQuery = getSolrQueryV3(query, rows, filterQueries);
        try {
            results = solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
        }
        catch (ProAPIException e) {
            if (e.getCause() instanceof RemoteSolrException) {
                logger.warn("Error executing solr query. QueryString = " + query
                        + ", SolrQuery = "
                        + String.valueOf(solrQuery));
            }
            else {
                throw e;
            }
        }
        return results;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<Typeahead> getSpellCheckedResponseV3(String query, int rows, List<String> filterQueries) {

        /* Fetch results for entered query first */
        SolrQuery solrQuery = this.getSolrQueryV3(query, rows, filterQueries);
        solrQuery.setSorts(sortClauseList);
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
            newQuery.setSorts(sortClauseList);
            resultsSuggested = solrDao.executeQuery(newQuery).getBeans(Typeahead.class);
        }

        /* Merge results */
        if (!resultsSuggested.isEmpty()) {
            resultsOriginal.addAll(resultsSuggested);
        }

        return resultsOriginal;
    }

    private SolrQuery getSimpleSolrQuery(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = new SolrQuery(QueryParserUtil.escape(query.toLowerCase()));
        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }

        solrQuery.setRows(rows);
        return solrQuery;
    }

    private SolrQuery getSolrQueryV3(String query, int rows, List<String> filterQueries) {

        SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
        solrQuery.setParam("qt", "/payload_v4");
        solrQuery.setParam("defType", "payload");
        solrQuery.setParam("fl", "*,score");
        solrQuery.setParam("spellcheck", "on");
        return solrQuery;
    }

    private List<SortClause> getCustomSortOrderV4() {
        List<SortClause> sortClauseListLocal = new ArrayList<SortClause>();
        sortClauseListLocal.add(new SortClause("score", ORDER.desc));
        sortClauseListLocal.add(new SortClause("TYPEAHEAD_ENTITY_POPULARITY", ORDER.desc));
        return sortClauseListLocal;
    }

    /******************* Legacy Methods ****************************************/

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

        float boost = TypeaheadConstants.QUERY_TIME_BOOST_START;
        /* Boost all-but-last query strings as core-texts */
        for (int i = 0; i < count - 1; i++) {
            boostQuery += "Core_text:" + st.nextToken() + "^" + Math.max(1, boost) + " ";
            boost *= TypeaheadConstants.QUERY_TIME_BOOST_MULTIPLIER;
        }

        /* Boost last query string as an edgeNGram */
        boostQuery += ("ENGram:" + st.nextToken() + "^" + Math.max(1, boost) + " ");

        /**
         * System.out.println("=====>> Q = [" + query + "], BQ = [" + boostQuery
         * + "]");
         */
        return boostQuery;
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

}
