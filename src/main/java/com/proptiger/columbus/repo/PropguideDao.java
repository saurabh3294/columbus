package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.UtilityClass;

@Repository
public class PropguideDao {

    public static final String FQ_PGD_CATEGORY = "PGD_ROOT_CATEGORY_ID:(%s)";

    @Autowired
    private SolrDao            solrDao;

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public List<PropguideDocument> getDocumentsV1(String query, String[] categories, int rows) {
        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        results = getResponseV1(query, categories, rows);
        return results;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     * 
     * @param categories
     */
    private List<PropguideDocument> getResponseV1(String query, String[] categories, int rows) {
        int enlargedRows = rows * TypeaheadConstants.PROPGUIDE_POST_TAGS_MULTIPLIER;
        QueryResponse solrResponseOriginal = makeSolrQueryAndGetResponse(query, categories, enlargedRows);
        List<PropguideDocument> resultsOriginal = solrResponseOriginal.getBeans(PropguideDocument.class);

        SpellCheckResponse scr = solrResponseOriginal.getSpellCheckResponse();
        String querySuggested = getSuggestedQuery(scr);
        if (querySuggested != null) {
            QueryResponse solrResponseSpellcheck = makeSolrQueryAndGetResponse(querySuggested, categories, enlargedRows);
            List<PropguideDocument> resultsSpellcheck = solrResponseSpellcheck.getBeans(PropguideDocument.class);
            resultsOriginal = combineOriginalAndSpellcheckResults(resultsOriginal, resultsSpellcheck);
        }

        List<PropguideDocument> resultsFinal = divideResultInTagsPosts(resultsOriginal, rows);

        return resultsFinal;
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

        /* Sort and remove duplicates */
        Collections.sort(results, new TypeaheadUtils.AbstractTypeaheadComparatorScore());
        List<List<PropguideDocument>> listOfresults = new ArrayList<List<PropguideDocument>>();
        listOfresults.add(results);
        List<PropguideDocument> resultsFinal = UtilityClass.getMergedListRemoveDuplicates(
                listOfresults,
                new TypeaheadUtils.AbstractTypeaheadComparatorId());

        return resultsFinal;
    }

    private List<PropguideDocument> divideResultInTagsPosts(List<PropguideDocument> resultsOriginal, int rows) {

        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        if (resultsOriginal == null) {
            return results;
        }

        List<PropguideDocument> tagList = new ArrayList<PropguideDocument>();
        List<PropguideDocument> postList = new ArrayList<PropguideDocument>();

        for (PropguideDocument pgd : resultsOriginal) {
            if (pgd.getPgdType().equals("Suggestion")) {
                tagList.add(pgd);
            }
            else {
                postList.add(pgd);
            }
        }
        int tagCount, postCount;
        if (rows % 2 == 1) {
            tagCount = rows / 2 + 1;
        }
        else {
            tagCount = rows / 2;
        }
        postCount = rows / 2;

        if (tagList.size() < tagCount) {
            postList = UtilityClass.getFirstNElementsOfList(postList, rows - tagList.size());

        }
        else if (postList.size() < postCount) {
            tagList = UtilityClass.getFirstNElementsOfList(tagList, rows - postList.size());
        }
        else {
            postList = UtilityClass.getFirstNElementsOfList(postList, postCount);
            tagList = UtilityClass.getFirstNElementsOfList(tagList, tagCount);
        }
        results.addAll(tagList);
        results.addAll(postList);

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

    private QueryResponse makeSolrQueryAndGetResponse(String query, String[] categories, int rows) {
        List<String> filterQueries = new ArrayList<String>();
        filterQueries.add("DOCUMENT_TYPE:PROPGUIDE");
        if (categories != null) {
            String fq = StringUtils.join(categories, " OR ");
            fq = "(" + String.format(FQ_PGD_CATEGORY, fq) + " OR " + "PGD_TYPE:Suggestion )";
            filterQueries.add(fq);
        }

        SolrQuery solrQuery = getSolrQueryV1(query, filterQueries, rows);
        QueryResponse response = solrDao.executeQuery(solrQuery);
        return response;
    }

    private SolrQuery getSolrQueryV1(String query, List<String> filterQueries, int rows) {
        query = QueryParserUtil.escape(query.toLowerCase());
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setRows(rows);
        solrQuery.setParam("qt", "/propguide");
        if (filterQueries == null) {
            return solrQuery;
        }
        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }
        return solrQuery;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     * 
     * @param types
     */

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    public PaginatedResponse<List<PropguideDocument>> getListingDocumentsV1(
            String query,
            String[] categories,
            int start,
            int rows) {
        QueryResponse solrResponseOriginal = makeSolrQueryAndGetResponseForListing(query, categories, start, rows);
        List<PropguideDocument> resultsOriginal = solrResponseOriginal.getBeans(PropguideDocument.class);
        long totalDocs = solrResponseOriginal.getResults().getNumFound();
        return new PaginatedResponse<List<PropguideDocument>>(resultsOriginal, totalDocs);
    }

    private QueryResponse makeSolrQueryAndGetResponseForListing(String query, String[] categories, int start, int rows) {
        List<String> filterQueries = new ArrayList<String>();

        filterQueries.add("DOCUMENT_TYPE:PROPGUIDE");
        filterQueries.add("!PGD_TYPE:Suggestion");
        if (categories != null) {
            String fq = StringUtils.join(categories, " OR ");
            fq = String.format(FQ_PGD_CATEGORY, fq);
            filterQueries.add(fq);
        }

        SolrQuery solrQuery = getSolrQueryV1(query, filterQueries, rows);
        solrQuery.setStart(start);
        if (StringUtils.trim(query) == "") {
            solrQuery.setSort(new SortClause("PGD_DATE", ORDER.desc));
        }
        QueryResponse response = solrDao.executeQuery(solrQuery);
        return response;
    }

}
