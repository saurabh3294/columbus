package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.UtilityClass;

@Repository
public class PropguideDao {

    @Autowired
    private SolrDao solrDao;

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<PropguideDocument> getDocumentsV1(String query, int rows) {
        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        results = getResponseV1(query, rows);
        return results;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<PropguideDocument> getResponseV1(String query, int rows) {

        QueryResponse solrResponseOriginal = makeSolrQueryAndGetResponse(query, rows);
        List<PropguideDocument> resultsOriginal = solrResponseOriginal.getBeans(PropguideDocument.class);

        SpellCheckResponse scr = solrResponseOriginal.getSpellCheckResponse();
        String querySuggested = getSuggestedQuery(scr);
        if (querySuggested == null) {
            return resultsOriginal;
        }

        QueryResponse solrResponseSpellcheck = makeSolrQueryAndGetResponse(querySuggested, rows);
        List<PropguideDocument> resultsSpellcheck = solrResponseSpellcheck.getBeans(PropguideDocument.class);

        List<PropguideDocument> resultsFinal = combineOriginalAndSpellcheckResults(resultsOriginal, resultsSpellcheck);

        return UtilityClass.getFirstNElementsOfList(resultsFinal, rows);
    }

    private List<PropguideDocument> combineOriginalAndSpellcheckResults(
            List<PropguideDocument> resultsOriginal,
            List<PropguideDocument> resultsSuggested) {

        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        if (resultsOriginal != null) {
            results.addAll(resultsOriginal);
        }
        if (resultsSuggested != null) {
            results.addAll(resultsSuggested);
        }
        return results;
    }

    private String getSuggestedQuery(SpellCheckResponse scr) {
        if (scr == null) {
            return null;
        }
        String spellsuggestion = scr.getCollatedResult();
        if (spellsuggestion == null || spellsuggestion.isEmpty()) {
            return null;
        }
        return spellsuggestion;
    }

    private QueryResponse makeSolrQueryAndGetResponse(String query, int rows) {
        SolrQuery solrQuery = getSolrQueryV1(query, rows);
        QueryResponse response = solrDao.executeQuery(solrQuery);
        return response;
    }

    private SolrQuery getSolrQueryV1(String query, int rows) {
        query = QueryParserUtil.escape(query.toLowerCase());
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setRows(rows);
        solrQuery.setParam("qt", "/propguide");
        return solrQuery;
    }
}
