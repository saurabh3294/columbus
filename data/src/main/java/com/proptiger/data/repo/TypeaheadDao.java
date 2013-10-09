/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Typeahead;

/**
 * 
 * @author mukand
 */
@Repository
public class TypeaheadDao {
    private static Logger logger = Logger.getLogger(TypeaheadDao.class);

    @Autowired
    private SolrDao solrDao;

    public List<Typeahead> getTypeaheads(String query, int rows) {
        SolrQuery solrQuery = getSolrQuery(query, rows);

        return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
    }

	private SolrQuery getSolrQuery(String query, int rows) {
		SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery(getQueryParams(query));
        solrQuery.setFilterQueries("DOCUMENT_TYPE:TYPEAHEAD");
        solrQuery.setRows(rows);
		return solrQuery;
	}
    
    public List<Typeahead> getTypeaheadsByTypeAheadType(String query, int rows, String typeAheadType) {
        SolrQuery solrQuery = getSolrQuery(query, rows);
        solrQuery.setFilterQueries("TYPEAHEAD_TYPE:"+typeAheadType);
        return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
    }

    private String getQueryParams(String query) {
        String queryParams = new String();
        queryParams += "TYPEAHEAD_CORE_TEXT_NGRAMS:" + query + " ";
        queryParams += " OR TYPEAHEAD_LABEL_NGRAMS(" + query + ") ";
        queryParams += " OR TYPEAHEAD_CORE_TEXT:(" + query + ")^20 ";
        queryParams += " OR TYPEAHEAD_LABEL:(" + query + ")^20 ";
        queryParams += " OR TYPEAHEAD_CORE_TEXT:(" + query + "~)^10 ";
        queryParams += " OR TYPEAHEAD_LABEL:(" + query + "~)^10";

        return queryParams;
    }
}
