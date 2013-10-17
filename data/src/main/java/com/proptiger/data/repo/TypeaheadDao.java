
package com.proptiger.data.repo;

import java.util.List;

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

    @Autowired
    private SolrDao solrDao;

    public List<Typeahead> getTypeaheads(String query, int rows,  String typeAheadType, String cityName) {
        SolrQuery solrQuery = getSolrQuery(query, rows, typeAheadType, cityName);
        return solrDao.executeQuery(solrQuery).getBeans(Typeahead.class);
    }

	private SolrQuery getSolrQuery(String query, int rows, String typeAheadType, String cityName) {
		SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery(getQueryParams(query));
        solrQuery.setFilterQueries("DOCUMENT_TYPE:TYPEAHEAD");
        if(typeAheadType != null && !"".equals(typeAheadType)){
        	 solrQuery.addFilterQuery("TYPEAHEAD_TYPE:"+typeAheadType.toUpperCase());
        }
        if(cityName != null && !"".equals(cityName)){
        	solrQuery.addFilterQuery("TYPEAHEAD_CITY:"+cityName.toLowerCase());
        }
        solrQuery.setRows(rows);
		return solrQuery;
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
