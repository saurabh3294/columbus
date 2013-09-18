/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.proptiger.data.model.Typeahead;

/**
 *
 * @author mukand
 */
public class TypeaheadDao {
    private HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8983/solr/");

    public List<Typeahead> getSearchTypeahead(String query, int rows){
        SolrQuery solrQuery = new SolrQuery();
                
        solrQuery.setQuery( getQueryParams(query) );
        solrQuery.setFilterQueries("DOCUMENT_TYPE:TYPEAHEAD");
        solrQuery.setRows(rows);
        
        List<Typeahead> typeaheadList = null;
        try{
            typeaheadList = httpSolrServer.query(solrQuery).getBeans(Typeahead.class);
        }catch(SolrServerException e){
            
        }
        return typeaheadList;
    }
    
    private String getQueryParams(String query){
        String queryParams = new String();
        queryParams += "TYPEAHEAD_CORE_TEXT_NGRAMS:"+query+" ";
        queryParams += " OR TYPEAHEAD_LABEL_NGRAMS("+query+") ";
        queryParams += " OR TYPEAHEAD_CORE_TEXT:("+query+")^20 ";
        queryParams += " OR TYPEAHEAD_LABEL:("+query+")^20 ";
        queryParams += " OR TYPEAHEAD_CORE_TEXT:("+query+"~)^10 ";
        queryParams += " OR TYPEAHEAD_LABEL:("+query+"~)^10";
        
        return queryParams;
    }
}
