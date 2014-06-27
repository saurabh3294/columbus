package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Joiner;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.CityService;

/**
 * 
 * @author mukand
 * @author hemendra
 * 
 */

@Repository
public class TypeaheadDao {
    @Autowired
    private CityService cityService;

    @Autowired
    private SolrDao     solrDao;

    //Add parameters to use the custom requestHandler
    private SolrQuery getSolrQuery(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = this.getQueryParams(query);

        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }
        solrQuery.setRows(rows);
        solrQuery.setParam("qt","/payload");
        solrQuery.setParam("defType", "payload");

        return solrQuery;
    }

    private SolrQuery getQueryParams(String query) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        return solrQuery;
    }

    //If the query has a typo and can be corrected then new query is generated using the suggestions and executed
    //automatically
    public List<Typeahead> getTypeaheads(String query, int rows, List<String> filterQueries) {

        SolrQuery solrQuery = this.getSolrQuery(query, rows, filterQueries);
        List<Typeahead> results = new ArrayList<Typeahead>();
        QueryResponse response = solrDao.executeQuery(solrQuery);
        String spellsuggestion =response.getSpellCheckResponse().getCollatedResult();
		results = response.getBeans(Typeahead.class);

		if (spellsuggestion !=null){
			SolrQuery newQuery = this.getSolrQuery(spellsuggestion.toString(),rows,filterQueries);
			return solrDao.executeQuery(newQuery).getBeans(Typeahead.class);
		}
		else
			return results;
    }

}
